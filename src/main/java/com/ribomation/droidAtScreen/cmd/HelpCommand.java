/*
 * Project:  droidAtScreen
 * File:     HelpCommand.java
 * Modified: 2012-04-11
 *
 * Copyright (C) 2011, Ribomation AB (Jens Riboe).
 * http://blog.ribomation.com/
 *
 * You are free to use this software and the source code as you like.
 * We do appreciate if you attribute were it came from.
 */

package com.ribomation.droidAtScreen.cmd;

import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;

import com.ribomation.droidAtScreen.Application;

/**
 * Shows the help text.
 * 
 * @user Jens
 * @date 2012-04-11, 00:00
 */
public class HelpCommand extends CommandWithURI {

	public HelpCommand() {
		setLabel("Help");
		setTooltip("Views the help text for Droid@Screen");
		setIcon("help");
		setMnemonic('H');
	}

	@Override
	protected Desktop.Action getType() {
		return Desktop.Action.BROWSE;
	}

	@Override
	protected URI getURI(Application app) throws URISyntaxException {
		return new URI(app.getInfo().getHelpUri());
	}

}
