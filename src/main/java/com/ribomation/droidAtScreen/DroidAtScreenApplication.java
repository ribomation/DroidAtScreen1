package com.ribomation.droidAtScreen;

import com.ribomation.droidAtScreen.cmd.*;
import com.ribomation.droidAtScreen.dev.AndroidDevice;
import com.ribomation.droidAtScreen.dev.AndroidDeviceListener;
import com.ribomation.droidAtScreen.dev.AndroidDeviceManager;
import com.ribomation.droidAtScreen.gui.ApplicationFrame;
import com.ribomation.droidAtScreen.gui.DeviceFrame;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Main entry point of this application
 *
 * @user jens
 * @date 2010-jan-17 11:00:39
 */
public class DroidAtScreenApplication implements Application, AndroidDeviceListener {
    private Logger                          log = Logger.getLogger(DroidAtScreenApplication.class);
    private AndroidDeviceManager            deviceManager;
    private ApplicationFrame                appFrame;
    private Preferences                     appPreferences;
    private Map<String, DeviceFrame>        devices = new HashMap<String, DeviceFrame>();
    private List<AndroidDeviceListener>     deviceListeners = new ArrayList<AndroidDeviceListener>();
    private final String                    appPropertiesPath = "/META-INF/maven/com.ribomation/droidAtScreen/pom.properties";
    private String                          appName = "Droid@Screen";
    private String                          appVersion = "0.1";


    public static void main(String[] args) {
        DroidAtScreenApplication    app = new DroidAtScreenApplication();
        app.parseArgs(args);
        app.initProperties();
        app.initCommands();
        app.initGUI();
        app.initAndroid();
        app.run();
        app.postStart();
    }

    private void parseArgs(String[] args) {
        log.debug("parseArgs: " + Arrays.toString(args));
    }

    private void initProperties() {
        log.debug("initProperties");
        
        InputStream is = this.getClass().getResourceAsStream(appPropertiesPath);
        if (is != null) {
            try {
                Properties prp = new Properties();
                prp.load(is);
    //            appName    = prp.getProperty("artifactId", appName);
                appVersion = prp.getProperty("version", appVersion);
            } catch (IOException e) {
                log.debug("Missing classpath resource: "+appPropertiesPath, e);
            }
        }

        try {
            log.debug("--- Preferences ---");
            Preferences prefs = getPreferences();
            for (String key : prefs.keys()) {
                log.debug(String.format("%s: %s", key, prefs.get(key, "[none]")));
            }
            log.debug("--- END ---");
        } catch (BackingStoreException e) {log.warn("Failed to list prefs",e);}
    }

    private void initCommands() {
        log.debug("initCommands");
        Command.setApplication(this);
    }

    private void initAndroid() {
        log.debug("initAndroid");
        deviceManager = new AndroidDeviceManager();
        deviceManager.addAndroidDeviceListener(this);
    }

    private void initGUI() {
        log.debug("initGUI");
        appFrame = new ApplicationFrame(this);
        appFrame.initGUI();
    }

    private void run() {
        log.debug("run");        
        getAppFrame().placeInUpperLeftScreen();
        getAppFrame().setVisible(true);
    }

    private void postStart() {
        log.debug("postStart");
        AdbExePathCommand adbPath = Command.find(AdbExePathCommand.class);
        if (adbPath.isNotDefined()) {
            String pathAndroidHome = System.getenv("ANDROID_HOME");
            File adb = new File(pathAndroidHome + "/platform-tools/adb");
            if (adb.isFile()) {
                adbPath.setPreferenceValue(adb.getAbsolutePath());
            } else {
                adbPath.execute();
            }
        } else {
            setAdbExecutablePath( adbPath.getFile() );
        }
    }


    // --------------------------------------------
    // AndroidDeviceManager
    // --------------------------------------------

    @Override
    public void connected(final AndroidDevice dev) {
        log.debug("connected: dev="+dev);

        if (isSkipEmulator() && dev.isEmulator()) {
            return;
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                fireDeviceConnected(dev);
                if (isAutoShow()) {
                    showDevice(dev);
                }
            }
        });
    }

    @Override
    public void disconnected(final AndroidDevice dev) {
        log.debug("disconnected: dev="+dev);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                fireDeviceDisconnected(dev);
                hideDevice(dev);
            }
        });
    }

    @Override
    public void showDevice(AndroidDevice dev) {
        log.debug("showDevice: "+dev);
        try {
            DeviceFrame devFrame = new DeviceFrame(this, dev, isPortrait(), getScale(), getFrameRate());
            ApplicationFrame.placeInCenterScreen(devFrame);
            devFrame.setVisible(true);
            devices.put(devFrame.getFrameName(), devFrame);
        } catch (Exception e) {
            String msg = e.getMessage();
            log.debug("Failed to create DeviceFrame: "+msg);
            if (msg.lastIndexOf("device offline") > 0) {
                JOptionPane.showMessageDialog(getAppFrame(),
                        "The ADB claims the device is offline. Please, unplug/replug the device and/or restart this application.",
                        "Device offline",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(getAppFrame(),
                        "Failed to show device. "+e,
                        "Device failure",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        Command.get("Show").setEnabled(true);
        Command.get("ScreenShot").setEnabled(true);
    }


    @Override
    public void hideDevice(DeviceFrame dev) {
        hideDevice(dev, true);
    }

    @Override
    public void hideDevice(DeviceFrame dev, boolean doDispose) {
        log.debug("hideDevice: "+dev.getFrameName());
        DeviceFrame deviceFrame = devices.remove(dev.getFrameName());
        if (deviceFrame != null) {
            log.debug("Disposing devFrame: " + deviceFrame.getDevice());
            if ( doDispose) {
                deviceFrame.dispose();
            }
        }

        if (devices.isEmpty()) {
            Command.get("ScreenShot").setEnabled(false);
        }
        if (deviceManager.getDevices().isEmpty()) {
            Command.get("Show").setEnabled(false);            
        }
    }

    public void hideDevice(AndroidDevice dev) {
        log.debug("hideDevice: "+dev);

        for (DeviceFrame df : new ArrayList<DeviceFrame>(devices.values())) {
            if (df.getFrameName().startsWith(dev.getName())) {
                hideDevice(df, true);
            }
        }
    }

    public void updateDevice(AndroidDevice dev) {
        log.debug("updateDevice: "+dev);
        hideDevice(dev);
        showDevice(dev);
    }

    @Override
    public AndroidDevice getSelectedDevice() {
        String  devName = (String) getAppFrame().getDeviceList().getSelectedItem();

        if (devName != null) {
            if (devices.containsKey(devName)) {
                return devices.get(devName).getDevice();
            }
            if (deviceManager.getDevices().containsKey(devName)) {
                return deviceManager.getDevices().get(devName);
            }
        }

        return null;
    }

    // --------------------------------------------
    // AndroidDeviceListener
    // --------------------------------------------

    @Override
    public void addAndroidDeviceListener(AndroidDeviceListener listener) {
        deviceListeners.add(listener);
    }

    public void fireDeviceConnected(AndroidDevice dev) {
        for (AndroidDeviceListener listener : deviceListeners) {
            listener.connected(dev);
        }
    }

    public void fireDeviceDisconnected(AndroidDevice dev) {
        for (AndroidDeviceListener listener : deviceListeners) {
            listener.disconnected(dev);
        }
    }


    // --------------------------------------------
    // Application
    // --------------------------------------------

    @Override
    public String getName() {
        return appName;
    }

    @Override
    public String getVersion() {
        return appVersion;
    }

    @Override
    public ApplicationFrame getAppFrame() {
        return appFrame;
    }

    @Override
    public Preferences getPreferences() {
        if (appPreferences == null) {
            appPreferences = Preferences.userNodeForPackage(this.getClass());
        }
        return appPreferences;
    }

    @Override
    public void savePreferences() {
        try {
            getPreferences().flush();
        } catch (BackingStoreException e) {
            log.info("Failed to flush app preferences", e);
        }
    }

    @Override
    public void destroyPreferences() {
        if (appPreferences != null) {
            try {
                appPreferences.removeNode();
                appPreferences = null;
            } catch (BackingStoreException e) {
                log.error("Failed to destroy application properties.", e);
            }
        }
    }

    @Override
    public void setAdbExecutablePath(File value) {
        log.debug("setAdbExecutablePath: " + value);
        deviceManager.setAdbExecutable(value);
    }

    @Override
    public void setSkipEmulator(boolean value) {
        log.debug("setSkipEmulator: " + value);
    }

    @Override
    public void setAutoShow(boolean value) {
        log.debug("setAutoShow: " + value);
    }

    @Override
    public void setScale(int value) {
        log.debug("setScale: " + value);
        updateDevice( getSelectedDevice() );
    }

    @Override
    public void setPortraitMode(boolean value) {
        log.debug("setPortraitMode: " + value);
        updateDevice( getSelectedDevice() );
    }

    @Override
    public void setFrameRate(int value) {
        log.debug("setFrameRate: " + value);
        updateDevice( getSelectedDevice() );
    }




    public boolean isAutoShow() {
        AutoShowCommand cmd = Command.find(AutoShowCommand.class);
        return cmd.isSelected();
    }

    public boolean isSkipEmulator() {
        SkipEmulatorCommand cmd = Command.find(SkipEmulatorCommand.class);
        return cmd.isSelected();
    }

    public boolean isPortrait() {
        PortraitCommand cmd = Command.find(PortraitCommand.class);
        return cmd.isSelected();
    }

    public int  getScale() {
        ScaleCommand cmd = Command.find(ScaleCommand.class);
        return cmd.getScale();
    }

    public int  getFrameRate() {
        FrameRateCommand cmd = Command.find(FrameRateCommand.class);
        return cmd.getRate();
    }

}
