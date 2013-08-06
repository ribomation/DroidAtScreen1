/*
 * Project:  droidAtScreen
 * File:     UpsideDownCommand.java
 * Modified: 2012-03-24
 *
 * Copyright (C) 2011, Ribomation AB (Jens Riboe).
 * http://blog.ribomation.com/
 *
 * You are free to use this software and the source code as you like.
 * We do appreciate if you attribute were it came from.
 */

package com.ribomation.droidAtScreen.cmd;

import com.ribomation.droidAtScreen.Application;
import com.ribomation.droidAtScreen.gui.DeviceFrame;

/**
 * Flips the device-frame 90 degrees.
 * <p/>
 * User: Jens Created: 2012-03-22, 22:18
 */
public class UpsideDownCommand extends CommandWithTarget<DeviceFrame> {

	public UpsideDownCommand(DeviceFrame deviceFrame) {
		super(deviceFrame);
		updateButton(deviceFrame);
	}

	@Override
	protected void doExecute(Application app, DeviceFrame deviceFrame) {
		deviceFrame.setUpsideDown(!deviceFrame.isUpsideDown());
		updateButton(deviceFrame);
		deviceFrame.repaint();
	}

	@Override
	protected void updateButton(DeviceFrame deviceFrame) {
		setIcon(deviceFrame.isUpsideDown() ? "downsideup" : "upsidedown");
		setTooltip(String.format("Flips the image upside-down [%s]. (Useful for ZTE Blade devices)", deviceFrame.isUpsideDown() ? "UpsideDown" : "Normal"));
	}

}
