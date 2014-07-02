/*
 * Project:  droidAtScreen
 * File:     AndroidDeviceManager.java
 * Modified: 2011-10-04
 *
 * Copyright (C) 2011, Ribomation AB (Jens Riboe).
 * http://blog.ribomation.com/
 *
 * You are free to use this software and the source code as you like.
 * We do appreciate if you attribute were it came from.
 */

package com.ribomation.droidAtScreen.dev;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.log4j.Logger;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.ribomation.droidAtScreen.Application;

/**
 * A facade to AndroidDebugBridge.
 * 
 * @user jens
 * @date 2010-jan-17 11:13:33
 */
public class AndroidDeviceManager extends Thread implements AndroidDebugBridge.IDeviceChangeListener, AndroidDebugBridge.IDebugBridgeChangeListener {
	private Logger log = Logger.getLogger(this.getClass());
	private File adbExecutable;
	private Application app;

	public AndroidDeviceManager(Application app) {
		this.app = app;
	}

	public void initManager() {
		AndroidDebugBridge.init(false);
		Runtime.getRuntime().addShutdownHook(this);
		AndroidDebugBridge.addDebugBridgeChangeListener(this);
		AndroidDebugBridge.addDeviceChangeListener(this);
	}

	/**
	 * Invoked during JVM shutdown, to close the bridge.
	 */
	@Override
	public void run() {
		try {
			AndroidDebugBridge.disconnectBridge();
			AndroidDebugBridge.terminate();
		} catch (Exception e) {
			System.err.println("Failed to shutdown Android Device Bridge " + e);
		}
	}

	public void setAdbExecutable(File adbExecutable) {
		if (!adbExecutable.isFile()) {
			throw new RuntimeException("ADB executable '" + adbExecutable + "' is not a file");
		}
		if (!adbExecutable.canExecute()) {
			throw new RuntimeException("ADB executable '" + adbExecutable + "' is not executable.");
		}
		this.adbExecutable = adbExecutable;
	}

	public File getAdbExecutable() {
		return adbExecutable;
	}

	/**
	 * Creates the connection to ADB.
	 */
	public void createBridge() {
		if (getAdbExecutable() == null) {
			throw new IllegalArgumentException("Need to set the ADB exe path first, before starting the bridge.");
		}

		try {
			AndroidDebugBridge.createBridge(getAdbExecutable().getCanonicalPath(), true);
			log.info("Connected to ADB via " + getSocketAddress());
		} catch (IOException e) {
			throw new RuntimeException("Failed to created the absolute path to the ADB executable: " + getAdbExecutable());
		}
	}

	/**
	 * Invoked by ADB, when a new device is attached.
	 * 
	 * @param dev
	 *            the device
	 */
	@Override
	public void deviceConnected(IDevice dev) {
		log.info("Device connected: " + dev);
		app.connected(new AndroidDevice(dev));
	}

	/**
	 * Invoked by ADB when a device has detached.
	 * 
	 * @param dev
	 *            the device
	 */
	@Override
	public void deviceDisconnected(IDevice dev) {
		log.info("Device disconnected: " + dev);
		app.disconnected(new AndroidDevice(dev));
	}

	public void reloadDevices() {
		for (IDevice dev : AndroidDebugBridge.getBridge().getDevices()) {
			deviceConnected(dev);
		}
	}

	public boolean restartADB() {
		return AndroidDebugBridge.getBridge().restart();
	}

	public boolean isConnectedToADB() {
		return AndroidDebugBridge.getBridge().isConnected();
	}

	public InetSocketAddress getSocketAddress() {
		return AndroidDebugBridge.getSocketAddress();
	}

	@Override
	public void deviceChanged(IDevice dev, int changeMask) {
		log.debug("Device changed: " + dev + ", mask=" + toMaskString(changeMask));
	}

	@Override
	public void bridgeChanged(AndroidDebugBridge adb) {
		log.info("ADB changed");
	}

	private String toMaskString(int mask) {
		StringBuilder result = new StringBuilder("");
		if ((mask & IDevice.CHANGE_BUILD_INFO) != 0) {
			result.append("CHANGE_BUILD_INFO ");
		}
		if ((mask & IDevice.CHANGE_CLIENT_LIST) != 0) {
			result.append("CHANGE_CLIENT_LIST ");
		}
		if ((mask & IDevice.CHANGE_STATE) != 0) {
			result.append("CHANGE_STATE ");
		}
		return result.toString();
	}

}
