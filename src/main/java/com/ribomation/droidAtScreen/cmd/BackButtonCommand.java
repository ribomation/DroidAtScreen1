/*
 * Project:  droidAtScreen-NG
 */

package com.ribomation.droidAtScreen.cmd;

import com.ribomation.droidAtScreen.Application;
import com.ribomation.droidAtScreen.gui.DeviceFrame;

/**
  */
public class BackButtonCommand extends CommandWithTarget<DeviceFrame> {

	public BackButtonCommand(DeviceFrame deviceFrame) {
		super(deviceFrame);
		updateButton(deviceFrame);
	}

	@Override
	protected void doExecute(Application app, DeviceFrame target) {
		target.getDevice().key("KEYCODE_BACK");
	}

	@Override
	protected void updateButton(DeviceFrame deviceFrame) {
		setTooltip("Temp");
		setIcon("ic_ab_back_holo_light_am");
	}

}
