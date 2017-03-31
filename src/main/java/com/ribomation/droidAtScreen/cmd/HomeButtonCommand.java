/*
 * Project:  droidAtScreen-NG
 */

package com.ribomation.droidAtScreen.cmd;

import com.ribomation.droidAtScreen.Application;
import com.ribomation.droidAtScreen.gui.DeviceFrame;

/**
  */
public class HomeButtonCommand extends CommandWithTarget<DeviceFrame> {

	public HomeButtonCommand(DeviceFrame deviceFrame) {
		super(deviceFrame);
		updateButton(deviceFrame);
	}

	@Override
	protected void doExecute(Application app, DeviceFrame target) {
		target.getDevice().key("KEYCODE_HOME");
	}

	@Override
	protected void updateButton(DeviceFrame deviceFrame) {
		setTooltip("Home");
		setIcon("ic_menu_home");
	}

}
