/*
 * Project:  droidAtScreen
 * File:     AdbReloadDevicesCommand.java
 * Modified: 2011-10-04
 *
 * Copyright (C) 2011, Ribomation AB (Jens Riboe).
 * http://blog.ribomation.com/
 *
 * You are free to use this software and the source code as you like.
 * We do appreciate if you attribute were it came from.
 */

package com.ribomation.droidAtScreen.cmd;

import javax.swing.SwingUtilities;

import com.ribomation.droidAtScreen.Application;

/**
 * Reloads the devices from ADB.
 * 
 * @user jens
 * @date 2011-10-04 13:00
 */
public class AdbReloadDevicesCommand extends Command {

	public AdbReloadDevicesCommand() {
		setLabel("Reload Devices");
		setIcon("diagram");
		setMnemonic('L');
		setTooltip("Reloads all devices from ADB.");
	}

	@Override
	protected void doExecute(final Application app) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				app.disconnectAll();
				app.getDeviceManager().reloadDevices();
				app.getAppFrame().getStatusBar().message("Android devices reloaded");
			}
		});
	}

}
