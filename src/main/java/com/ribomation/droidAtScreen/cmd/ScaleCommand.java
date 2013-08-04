/*
 * Project:  droidAtScreen
 * File:     ScaleCommand.java
 * Modified: 2012-03-22
 *
 * Copyright (C) 2011, Ribomation AB (Jens Riboe).
 * http://blog.ribomation.com/
 *
 * You are free to use this software and the source code as you like.
 * We do appreciate if you attribute were it came from.
 */

package com.ribomation.droidAtScreen.cmd;

import javax.swing.JDialog;

import com.ribomation.droidAtScreen.Application;
import com.ribomation.droidAtScreen.gui.DeviceFrame;

/**
 * Set the device frame projection scale, as a percentage.
 * <p/>
 * User: Jens
 * Created: 2012-03-22, 22:18
 */
public class ScaleCommand extends CommandWithTarget<DeviceFrame> {
    public ScaleCommand(DeviceFrame deviceFrame) {
        super(deviceFrame);
        setIcon("scale");
        updateButton(deviceFrame);
    }

    @Override
    protected void doExecute(Application app, final DeviceFrame deviceFrame) {
        final JDialog dlg = PreferredScaleCommand.createScaleDialog(app, deviceFrame.getScale(),
                new PreferredScaleCommand.OnScaleUpdatedListener() {
                    @Override
                    public void onScaleUpdated(int value) {
                        deviceFrame.setScale(value);
                        deviceFrame.pack();
                        deviceFrame.invalidate();
                        deviceFrame.validate();
                        deviceFrame.repaint();
                        updateButton(deviceFrame);
                    }
                });
        dlg.setLocationRelativeTo(deviceFrame);
        dlg.setVisible(true);
    }

    @Override
    protected void updateButton(DeviceFrame deviceFrame) {
        setTooltip(String.format("Current scale (%d%%)", deviceFrame.getScale()));
    }
}
