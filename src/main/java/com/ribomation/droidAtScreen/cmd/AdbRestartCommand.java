/*
 * Project:  droidAtScreen
 * File:     AdbRestartCommand.java
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
import com.ribomation.droidAtScreen.gui.StatusBar;

/**
 * Restarts the ADB server.
 *
 * @user jens
 * @date 2011-10-04 13:00
 */
public class AdbRestartCommand extends Command {

    public AdbRestartCommand() {
        configure();
    }

    @Override
    protected void doExecute(final Application app) {
        final StatusBar statusBar = app.getAppFrame().getStatusBar();
        statusBar.message(getString("restar_adb_statusbar_loading"));

        SwingUtilities.invokeLater(() -> {
            app.disconnectAll();
            boolean succeeded = app.getDeviceManager().restartADB();
            statusBar.message(getString("restar_adb_statusbar_done") + (succeeded ? 
                    getString("succeeded") : getString("failed")));
        });
    }

    private void configure() {
        setLabel(getString("restar_adb"));
        setIcon("sync");
        setMnemonic('R');
        setTooltip(getString("restar_adb_tooltip"));
    }
}
