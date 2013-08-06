/*
 * Project:  droidAtScreen
 * File:     CommandWithURI.java
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

import javax.swing.JOptionPane;

import com.ribomation.droidAtScreen.Application;

/**
 * Abstract helper command for URI based action commands.
 * 
 * @user Jens
 * @date 2012-04-11, 11:11
 */
public abstract class CommandWithURI extends Command {

	@Override
	protected final void doExecute(Application app) {
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			if (desktop.isSupported(getType())) {
				try {
					switch (getType()) {
					case BROWSE:
						desktop.browse(getURI(app));
						return;
					case MAIL:
						desktop.mail(getURI(app));
						return;
					}
				} catch (Exception e) {
					getLog().error("Invalid URI", e);
				}
			}
		}

		JOptionPane.showMessageDialog(app.getAppFrame(), String.format("Action %s is not supported", getType()), "", JOptionPane.WARNING_MESSAGE);
	}

	protected abstract Desktop.Action getType();

	protected abstract URI getURI(Application app) throws URISyntaxException;

}
