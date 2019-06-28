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

/**
 * If emulators should popup or not.
 *
 * @user jens
 * @date 2010-jan-18 10:35:20
 */
public class HideEmulatorsCommand extends CheckBoxCommand {

    public HideEmulatorsCommand() {
        configure();
    }

    @Override
    protected boolean getPreferenceValue() {
        return getApplication().getSettings().isHideEmulators();
    }

    @Override
    protected void setPreferenceValue(boolean hide) {
        getApplication().getSettings().setHideEmulators(hide);
        getApplication().getDeviceTableModel().getDevices().stream().filter((frame) -> (frame.getDevice().isEmulator())).forEachOrdered((frame) -> {
            frame.setVisible(!hide);
        });
        getApplication().getDeviceTableModel().refresh();
    }

    private void configure() {
        setLabel("Hide Emulators");
        setTooltip("Do not show emulators automatically");
    }
}
