/*
 * Project:  droidAtScreen
 * File:     AndroidDeviceListener.java
 * Modified: 2011-10-04
 *
 * Copyright (C) 2011, Ribomation AB (Jens Riboe).
 * http://blog.ribomation.com/
 *
 * You are free to use this software and the source code as you like.
 * We do appreciate if you attribute were it came from.
 */

package com.ribomation.droidAtScreen.dev;

/**
 * Notification of connected and disconnected devices.
 * 
 * @user jens
 * @date 2010-jan-17 15:45:42
 */
public interface AndroidDeviceListener {

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
}
