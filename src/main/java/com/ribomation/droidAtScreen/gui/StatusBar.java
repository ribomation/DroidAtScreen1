/*
 * Project:  droidAtScreen
 * File:     StatusBar.java
 * Modified: 2011-10-04
 *
 * Copyright (C) 2011, Ribomation AB (Jens Riboe).
 * http://blog.ribomation.com/
 *
 * You are free to use this software and the source code as you like.
 * We do appreciate if you attribute were it came from.
 */

package com.ribomation.droidAtScreen.gui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.ribomation.droidAtScreen.Application;

/**
 * Place for status messages.
 * 
 * @user jens
 * @date 2011-10-02 11:17
 */
public class StatusBar extends JPanel {
	private JLabel message;

	public StatusBar(Application app) {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		setBorder(BorderFactory.createLoweredBevelBorder());

		message = new JLabel("", SwingConstants.LEFT);
		Font font = message.getFont();
		message.setFont(font.deriveFont(Font.PLAIN, (float) (font.getSize() * 0.90)));
		message.setForeground(Color.DARK_GRAY);
		add(message);

		message(app.getInfo().getName() + ", V" + app.getInfo().getVersion());
	}

	public void message(String txt) {
		message.setText(txt);
	}

	public void message(String fmt, Object... args) {
		message(String.format(fmt, args));
	}

	public void clear() {
		message("");
	}

}
