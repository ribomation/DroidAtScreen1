/*
 * Project:  droidAtScreen
 * File:     RemovePropertiesCommand.java
 * Modified: 2011-10-04
 *
 * Copyright (C) 2011, Ribomation AB (Jens Riboe).
 * http://blog.ribomation.com/
 *
 * You are free to use this software and the source code as you like.
 * We do appreciate if you attribute were it came from.
 */

package com.ribomation.droidAtScreen.cmd;

import javax.swing.JOptionPane;

import com.ribomation.droidAtScreen.Application;

/**
 * DESCRIPTION
 * 
 * @user jens
 * @date 2010-jan-18 10:35:20
 */
public class RemovePropertiesCommand extends Command {
	public RemovePropertiesCommand() {
		setLabel("Remove application properties");
		setTooltip("Removes all saved application properties from this host.");
		setIcon("remove");
	}

	@Override
	protected void doExecute(Application app) {
		int rc = JOptionPane.showConfirmDialog(app.getAppFrame(), "Are you sure you want to remove all persisted properties of this application from this host?", "Remove app properties?", JOptionPane.YES_NO_OPTION);
		if (rc == JOptionPane.YES_OPTION) {
			app.getSettings().destroyPreferences();
		}
	}
}
