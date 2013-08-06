/*
 * Project:  droidAtScreen
 * File:     CommandWithTarget.java
 * Modified: 2012-03-22
 *
 * Copyright (C) 2011, Ribomation AB (Jens Riboe).
 * http://blog.ribomation.com/
 *
 * You are free to use this software and the source code as you like.
 * We do appreciate if you attribute were it came from.
 */

package com.ribomation.droidAtScreen.cmd;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;

import com.ribomation.droidAtScreen.Application;

/**
 * A command that is associated with a target object, which is the subject for
 * the execution of the command.
 * <p/>
 * User: Jens Created: 2012-03-22, 22:13
 */
public abstract class CommandWithTarget<TargetType> extends Command {
	private TargetType target;

	protected CommandWithTarget(TargetType target) {
		this.target = target;
	}

	public TargetType getTarget() {
		return target;
	}

	@Override
	public AbstractButton newButton() {
		final JButton b = new JButton(this);
		b.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		b.setRolloverEnabled(true);
		b.setContentAreaFilled(false);
		b.setFocusPainted(false);
		b.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				b.setBorder(BorderFactory.createEtchedBorder());
			}

			@Override
			public void mouseExited(MouseEvent e) {
				b.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			}
		});
		return b;
	}

	protected abstract void updateButton(TargetType target);

	protected abstract void doExecute(Application app, TargetType target);

	@Override
	final protected void doExecute(Application app) {
		doExecute(app, getTarget());
	}

}
