/*
 * Project:  droidAtScreen
 * File:     GuiUtil.java
 * Modified: 2011-10-04
 *
 * Copyright (C) 2011, Ribomation AB (Jens Riboe).
 * http://blog.ribomation.com/
 *
 * You are free to use this software and the source code as you like.
 * We do appreciate if you attribute were it came from.
 */

package com.ribomation.droidAtScreen.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JToolBar;

import com.ribomation.droidAtScreen.cmd.Command;

/**
 * Util class with a collection of GUI helper methods.
 *
 * @user jens
 * @date 2011-10-01 12:30
 */
public class GuiUtil {

	public static void placeInCenterScreen(Window win) {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frame = win.getSize();
		win.setLocation((screen.width - frame.width) / 2, (screen.height - frame.height) / 2);
	}

	public static void placeInUpperLeftScreen(Window win) {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frame = win.getSize();
		win.setLocation(screen.width / 4 - frame.width / 2, screen.height / 4 - frame.height / 2);
	}

	public static JMenu createMenu(String name, char mnemonicChar, String... commandNames) {
		JMenu m = new JMenu(name);
		m.setMnemonic(mnemonicChar);

		for (String cmdName : commandNames) {
			if (cmdName.equals("-")) {
				m.addSeparator();
			} else {
				m.add(Command.get(cmdName).createMenuItem());
			}
		}

		return m;
	}

	public static JToolBar createToolbar(String... commandNames) {
		JToolBar tb = new JToolBar();
		for (String cmdName : commandNames) {
			if (cmdName.equals("-")) {
				tb.addSeparator();
			} else {
				tb.add(Command.get(cmdName).createButton());
			}
		}
		return tb;
	}

	public static ImageIcon loadIcon(String name) {
		return loadImage(name, "png");
	}

	public static ImageIcon loadPicture(String name) {
		return loadImage(name, "jpg");
	}

	public static ImageIcon loadSkin(String skinName) {
		String path = "/skins/" + skinName + "/frame.png";
		return loadImageFromPath(path);
	}

	public static ImageIcon loadImage(String name, String ext) {
		String path = "/img/" + name + "." + ext.toLowerCase();
		return loadImageFromPath(path);
	}

	private static ImageIcon loadImageFromPath(String path) {
		URL url = GuiUtil.class.getResource(path);
		if (url != null) {
			return new ImageIcon(url);
		}
		throw new IllegalArgumentException("Image not found: " + path);
	}

}
