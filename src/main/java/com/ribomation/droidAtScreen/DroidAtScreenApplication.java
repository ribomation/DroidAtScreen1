/*
 * Project:  droidAtScreen
 * File:     DroidAtScreenApplication.java
 * Modified: 2011-10-04
 *
 * Copyright (C) 2011, Ribomation AB (Jens Riboe).
 * http://blog.ribomation.com/
 *
 * You are free to use this software and the source code as you like.
 * We do appreciate if you attribute were it came from.
 */

package com.ribomation.droidAtScreen;

import com.ribomation.droidAtScreen.cmd.*;
import com.ribomation.droidAtScreen.dev.AndroidDevice;
import com.ribomation.droidAtScreen.dev.AndroidDeviceListener;
import com.ribomation.droidAtScreen.dev.AndroidDeviceManager;
import com.ribomation.droidAtScreen.gui.ApplicationFrame;
import com.ribomation.droidAtScreen.gui.DeviceFrame;
import com.ribomation.droidAtScreen.gui.DeviceTableModel;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Timer;

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
    private List<AndroidDeviceListener>     deviceListeners = new ArrayList<AndroidDeviceListener>();
    private Settings                        settings;
    private Properties                      appProperties;
    private DeviceTableModel                deviceTableModel = new DeviceTableModel();
    private Timer                           timer;
    private String                          deviceUDID;

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
        deviceUDID = args[0];
    }

    private void initProperties() {
        log.debug("initProperties");
        InputStream is = this.getClass().getResourceAsStream("/app.properties");
        if (is != null) {
            try {
                appProperties = new Properties();
                appProperties.load(is);
            } catch (IOException e) {
                log.debug("Missing classpath resource: /app.properties", e);
            }
        }

        settings = new Settings();
        settings.dump();
    }

    private void initCommands() {
        log.debug("initCommands");
        Command.setApplication(this);
    }

    private void initAndroid() {
        log.debug("initAndroid");
        deviceManager = new AndroidDeviceManager(this);
        deviceManager.initManager();
        timer = new Timer("Screenshot Retrievers");
    }

    private void initGUI() {
        log.debug("initGUI");
        appFrame = new ApplicationFrame(this);
        appFrame.initGUI();
    }

    private void run() {
        log.debug("run");        
        getAppFrame().setVisible(true);
    }

    private void postStart() {
        log.debug("postStart");

        File adbExePath = getSettings().getAdbExecutable();
        if (adbExePath == null) {
            adbExePath = isExe("ANDROID_HOME");
        }
        if (adbExePath == null) {
            adbExePath = isExe("ANDROID_SDK_HOME");
        }
        if (adbExePath == null) {
            Command.find(AdbExePathCommand.class).execute();
        } else {
            getSettings().setAdbExecutable(adbExePath);
            getDeviceManager().setAdbExecutable(adbExePath);
            getDeviceManager().createBridge();
        }
    }

    private File isExe(String envName) {
        String env = System.getenv(envName);
        log.debug("isExe: env=" + env);
        if (env == null) return null;

        String  ext = System.getProperty("os.name", "").toLowerCase().startsWith("windows") ? ".exe" : "";
        File    androidHome   = new File(env);
        File    platformTools = new File(androidHome, "platform-tools");
        File    file          = new File(platformTools, "adb" + ext);
        log.debug("isExe: file=" + file.getAbsolutePath());
        
        if (file.isFile() && file.canExecute()) return file;
        return null;
    }
    

    // --------------------------------------------
    // App getters
    // --------------------------------------------
    
    @Override
    public List<DeviceFrame> getDevices() {
        return getDeviceTableModel().getDevices();
    }

    @Override
    public AndroidDeviceManager getDeviceManager() {
        return deviceManager;
    }


    public ApplicationFrame getAppFrame() {
        return appFrame;
    }

    public Settings getSettings() {
        return settings;
    }

    public String getDeviceUDID() {
        return deviceUDID;
    }

    @Override
    public Info getInfo() {
        return new Info() {
            @Override
            public String getName() {
                return appProperties.getProperty("app.name", "no-name");
            }

            @Override
            public String getVersion() {
                return appProperties.getProperty("app.version", "0.0");
            }
            
            @Override
            public String getAppUri() {
                return appProperties.getProperty("app.uri", "");
            }
            
            @Override
            public String getHelpUri() {
                return appProperties.getProperty("help.uri", "");
            }
            
            @Override
            public String getMailUri() {
                return appProperties.getProperty("mail.uri", "");
            }

            @Override
            public Date getBuildDate() {
                try {
                    return new SimpleDateFormat("yyyy-MM-dd").parse(appProperties.getProperty("build.date", "2011-01-01"));
                } catch (ParseException e) { return new Date(); }
            }
        };
    }


    @Override
    public DeviceTableModel getDeviceTableModel() {
        return deviceTableModel;
    }

    @Override
    public Timer getTimer() {
        return timer;
    }

    // --------------------------------------------
    // AndroidDeviceManager
    // --------------------------------------------

    @Override
    public void connected(final AndroidDevice dev) {
        log.debug("connected: dev="+dev);
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getAppFrame().getStatusBar().message("Connected to " + dev.getName());

                DeviceFrame frame = new DeviceFrame(DroidAtScreenApplication.this, dev);
                deviceTableModel.add(frame);
                fireDeviceConnected(dev);
                
                frame.setLocationRelativeTo(getAppFrame());
                frame.setVisible(!getSettings().isHideEmulators() || !dev.isEmulator());
            }
        });
    }

    @Override
    public void disconnected(final AndroidDevice dev) {
        log.debug("disconnected: dev="+dev);
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getAppFrame().getStatusBar().message("Disconnected from " + dev.getName());
                
                DeviceFrame frame = deviceTableModel.getDevice(dev.getName());
                if (frame == null) return;
                
                deviceTableModel.remove(frame);
                fireDeviceDisconnected(dev);
                frame.stopRetriever();
                frame.setVisible(false);
                frame.dispose();
            }
        });
    }
    
    @Override
    public void disconnectAll() {
        for (DeviceFrame frame : new ArrayList<DeviceFrame>(deviceTableModel.getDevices())) {
            disconnected(frame.getDevice());
        }
    }


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

}
