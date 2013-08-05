/*
 * Project:  droidAtScreen
 * File:     OrientationCommand.java
 * Modified: 2012-03-22
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
 * User: Jens
 * Created: 2012-03-22, 22:18
 */
public class OrientationCommand extends CommandWithTarget<DeviceFrame> {

    public OrientationCommand(DeviceFrame deviceFrame) {
        super(deviceFrame);
        updateButton(deviceFrame);
    }

    @Override
    protected void doExecute(Application app, DeviceFrame deviceFrame) {
        deviceFrame.setLandscapeMode(!deviceFrame.isLandscapeMode());
        updateButton(deviceFrame);
        deviceFrame.pack();
        deviceFrame.invalidate();
        deviceFrame.validate();
        deviceFrame.repaint();
    }

    protected void updateButton(DeviceFrame deviceFrame) {
        String orientation = deviceFrame.isLandscapeMode() ? "Landscape" : "Portrait";
        setTooltip(String.format("Flip the orientation (%s)", orientation));
        setIcon("orientation-" + orientation.toLowerCase().trim());
    }
    
}
