/*
 * Project:  droidAtScreen
 * File:     AskBeforeQuitCommand.java
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
 * Controls if a prompt should be shown before quitting the application.
 * 
 * @user jens
 * @date Fri Sep 30 2011, 10:53:52 CEST
 */
public class AskBeforeQuitCommand extends CheckBoxCommand {
	public AskBeforeQuitCommand() {
		setLabel("Ask Before Quit");
		setTooltip("If you want a prompt before quitting.");
	}

	@Override
	protected boolean getPreferenceValue() {
		return getApplication().getSettings().isAskBeforeQuit();
	}

	@Override
	protected void setPreferenceValue(boolean value) {
		getApplication().getSettings().setAskBeforeQuit(value);
	}
}
