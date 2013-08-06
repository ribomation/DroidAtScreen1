/*
 * Project:  droidAtScreen
 * File:     AboutCommand.java
 * Modified: 2011-10-04
 *
 * Copyright (C) 2011, Ribomation AB (Jens Riboe).
 * http://blog.ribomation.com/
 *
 * You are free to use this software and the source code as you like.
 * We do appreciate if you attribute were it came from.
 */

package com.ribomation.droidAtScreen.cmd;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.ribomation.droidAtScreen.Application;
import com.ribomation.droidAtScreen.Info;
import com.ribomation.droidAtScreen.gui.GuiUtil;

/**
 * Shows some info about this app.
 * 
 * @user jens
 * @date 2010-jan-18 10:35:20
 */
public class AboutCommand extends Command {
	public AboutCommand() {
		setLabel("About...");
		setTooltip("Shows info about this application");
		setIcon("about");
		setMnemonic('A');
	}

	@Override
	protected void doExecute(Application app) {
		ImageIcon image = GuiUtil.loadPicture("jens-riboe");
		Info info = app.getInfo();
		String aboutText = loadResource("/about.html");
		String linkText = loadResource("/about-links.html");
		String systemText = String.format(loadResource("/about-system.html"), System.getProperty("os.name"), System.getProperty("os.arch"), System.getProperty("java.vm.name"), System.getProperty("java.runtime.version"));

		JPanel content = new JPanel(new BorderLayout(5, 0));
		content.add(new JLabel(image), BorderLayout.WEST);
		content.add(new JLabel("<html>" + aboutText), BorderLayout.CENTER);
		content.add(new JLabel("<html>" + linkText + systemText), BorderLayout.SOUTH);

		JOptionPane.showMessageDialog(null, content, info.getName() + " - Version " + info.getVersion(), JOptionPane.PLAIN_MESSAGE);
	}

}
