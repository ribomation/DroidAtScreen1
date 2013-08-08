/*
 * Project:  droidAtScreen
 * File:     Application.java
 * Modified: 2011-10-04
 *
 * Copyright (C) 2011, Ribomation AB (Jens Riboe).
 * http://blog.ribomation.com/
 *
 * You are free to use this software and the source code as you like.
 * We do appreciate if you attribute were it came from.
 */

package com.ribomation.droidAtScreen;

import java.util.List;

import com.ribomation.droidAtScreen.dev.AndroidDevice;
import com.ribomation.droidAtScreen.dev.AndroidDeviceListener;
import com.ribomation.droidAtScreen.dev.AndroidDeviceManager;
import com.ribomation.droidAtScreen.gui.ApplicationFrame;
import com.ribomation.droidAtScreen.gui.DeviceFrame;
import com.ribomation.droidAtScreen.gui.DeviceTableModel;

/**
 * Application interface that provides a set of services for disparate parts of
 * the app.
 *
 * @user jens
 * @date 2010-jan-18 10:06:42
 */
public interface Application {

	ApplicationFrame getAppFrame();

	Settings getSettings();

	Info getInfo();

	AndroidDeviceManager getDeviceManager();

	void addAndroidDeviceListener(AndroidDeviceListener listener);

	/**
	 * Updates the position of the frames on the screen
	 */
	void updateDeviceFramePositionsOnScreen(DeviceFrame newFrame);

	List<DeviceFrame> getDevices();

	/**
	 * Invoked when a new device is detected.
	 *
	 * @param dev
	 *            the new device
	 */
	void connected(AndroidDevice dev);

	/**
	 * Invoked when a device goes offline.
	 *
	 * @param dev
	 *            the defunct device
	 */
	void disconnected(AndroidDevice dev);

	DeviceTableModel getDeviceTableModel();

	void disconnectAll();

	java.util.Timer getTimer();
}
