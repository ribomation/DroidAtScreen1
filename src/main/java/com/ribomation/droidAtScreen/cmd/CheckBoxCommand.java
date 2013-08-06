/*
 * Project:  droidAtScreen
 * File:     CheckBoxCommand.java
 * Modified: 2011-10-04
 *
 * Copyright (C) 2011, Ribomation AB (Jens Riboe).
 * http://blog.ribomation.com/
 *
 * You are free to use this software and the source code as you like.
 * We do appreciate if you attribute were it came from.
 */

package com.ribomation.droidAtScreen.cmd;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;

import com.ribomation.droidAtScreen.Application;

/**
 * Abstract command for boolean commands.
 * 
 * @user jens
 * @date 2010-jan-19 10:36:37
 */
public abstract class CheckBoxCommand extends Command {
	@Override
	public JMenuItem newMenuItem() {
		setSelected(getPreferenceValue());
		JCheckBoxMenuItem b = new JCheckBoxMenuItem();
		b.setSelected(getPreferenceValue());
		return b;
	}

	@Override
	public AbstractButton newButton() {
		setSelected(getPreferenceValue());
		AbstractButton b = new JCheckBox();
		b.setSelected(getPreferenceValue());
		return b;
	}

	public void setSelected(boolean selected) {
		putValue(Action.SELECTED_KEY, selected);
	}

	public boolean isSelected() {
		return (Boolean) getValue(Action.SELECTED_KEY);
	}

	@Override
	protected final void doExecute(Application app) {
		boolean selected = !getPreferenceValue();
		setSelected(selected);
		setPreferenceValue(selected);
	}

	protected abstract boolean getPreferenceValue();

	protected abstract void setPreferenceValue(boolean value);

}
