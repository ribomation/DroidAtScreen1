/*
 * Project:  droidAtScreen
 * File:     SkipEmulatorCommand.java
 * Modified: 2011-10-04
 *
 * Copyright (C) 2011, Ribomation AB (Jens Riboe).
 * http://blog.ribomation.com/
 *
 * You are free to use this software and the source code as you like.
 * We do appreciate if you attribute were it came from.
 */

package com.ribomation.droidAtScreen.cmd;

import com.ribomation.droidAtScreen.gui.DeviceFrame;

/**
 * If emulators should popup or not.
 * 
 * @user jens
 * @date 2010-jan-18 10:35:20
 */
public class HideEmulatorsCommand extends CheckBoxCommand {

	public HideEmulatorsCommand() {
		setLabel("Hide Emulators");
		setTooltip("Do not show emulators automatically");
	}

	@Override
	protected boolean getPreferenceValue() {
		return getApplication().getSettings().isHideEmulators();
	}

	@Override
	protected void setPreferenceValue(boolean hide) {
		getApplication().getSettings().setHideEmulators(hide);
		for (DeviceFrame frame : getApplication().getDeviceTableModel().getDevices()) {
			if (frame.getDevice().isEmulator())
				frame.setVisible(!hide);
		}
		getApplication().getDeviceTableModel().refresh();
	}
}
