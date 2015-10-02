/*
 * Project:  droidAtScreen
 * File:     AndroidDevice.java
 * Modified: 2011-10-04
 *
 * Copyright (C) 2011, Ribomation AB (Jens Riboe).
 * http://blog.ribomation.com/
 *
 * You are free to use this software and the source code as you like.
 * We do appreciate if you attribute were it came from.
 */

package com.ribomation.droidAtScreen.dev;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import com.android.ddmlib.RawImage;
import com.android.ddmlib.TimeoutException;
import java.awt.Point;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Wrapper around an Android device.
 * 
 * @user jens
 * @date 2010-jan-17 12:16:55, 2011-Oct-02
 */
public class AndroidDevice implements Comparable<AndroidDevice> {
	/**
	 * Models the device state
	 */
	public enum ConnectionState {
		booting, offline, online, recovery, timeout, rejected, error
	}

	private final Logger log;
	private final IDevice target;
	private ConnectionState state = ConnectionState.offline;

	public AndroidDevice(IDevice target) {
		this.target = target;
		log = Logger.getLogger(AndroidDevice.class.getName() + ":" + target.getSerialNumber());
	}

	public IDevice getDevice() {
		return target;
	}

	public ScreenImage getScreenImage() {
		try {
			RawImage rawImage = target.getScreenshot();
			if (rawImage == null)
				return null;
			setState(target.getState());
			return new ScreenImage(rawImage);
		} catch (IOException e) {
			setState(ConnectionState.error);
			log.error("Failed to get screenshot: " + e);
		} catch (TimeoutException e) {
			setState(ConnectionState.timeout);
			log.warn("Got timeout");
		} catch (AdbCommandRejectedException e) {
			setState(ConnectionState.rejected);
			log.error("ADB command rejected: OFFLINE=" + e.isDeviceOffline());
		}
		return null;
	}

	protected void shellCommand(String command)
	{
		try {
			log.debug("SEND: "+command);
			target.executeShellCommand(command, new IShellOutputReceiver() {
                @Override
                public void addOutput(byte[] data, int offset, int length) {
                    log.debug(String.format("SHELL: %s", new String(data, offset, length)));
                }
    
                @Override
                public void flush() { }
    
                @Override
                public boolean isCancelled() { return false; }
            });
		} catch (Exception e) {
			log.debug("Failed to send '" + command + "' command to the device", e);
		}
	}

	public void tap(Point p) {
		shellCommand(String.format(Locale.ENGLISH, "input tap %.3f %.3f", p.getX(), p.getY()));
	}

	public void key(String key)
	{
		shellCommand(String.format(Locale.ENGLISH, "input keyevent %s", key));
	}
	
	public ConnectionState getState() {
		return state;
	}

	private void setState(ConnectionState s) {
		state = s;
	}

	private void setState(IDevice.DeviceState s) {
		switch (s) {
		case BOOTLOADER:
			setState(ConnectionState.booting);
			break;
		case OFFLINE:
			setState(ConnectionState.offline);
			break;
		case ONLINE:
			setState(ConnectionState.online);
			break;
		case RECOVERY:
			setState(ConnectionState.recovery);
			break;
		}
	}

	public String getName() {
		String name = isEmulator() ? target.getAvdName() : target.getProperty("ro.product.model");
		return name != null ? name : target.getSerialNumber();
	}

	public Map<String, String> getProperties() {
		return target.getProperties();
	}

	public boolean isEmulator() {
		return target.isEmulator();
	}

	@Override
	public String toString() {
		return getName() + " (" + (isEmulator() ? "emulator" : "device") + ")";
	}

	@Override
	public boolean equals(Object obj) {
		System.out.printf("AndroidDevice.equals: %s == %s%n", this, obj);

		if (this == obj)
			return true;
		if (!(obj instanceof AndroidDevice))
			return false;

		AndroidDevice that = (AndroidDevice) obj;
		return this.getDevice().getSerialNumber().equals(that.getDevice().getSerialNumber());
	}

	@Override
	public int hashCode() {
		return this.getDevice().getSerialNumber().hashCode();
	}

	@Override
	public int compareTo(AndroidDevice that) {
		return this.getDevice().getSerialNumber().compareTo(that.getDevice().getSerialNumber());
	}

}
