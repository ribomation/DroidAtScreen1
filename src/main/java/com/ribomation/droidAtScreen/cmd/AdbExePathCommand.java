/*
 * Project:  droidAtScreen
 * File:     AdbExePathCommand.java
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.ribomation.droidAtScreen.Application;

/**
 * DESCRIPTION
 * 
 * @user jens
 * @date 2010-jan-18 10:35:20
 */
public class AdbExePathCommand extends Command {

	public AdbExePathCommand() {
		setLabel("ADB Executable Path");
		setTooltip("Sets the path to the Android Device Debugger (ADB) executable");
		setIcon("app");
	}

	@Override
	protected void doExecute(Application app) {
		JOptionPane.showMessageDialog(app.getAppFrame(), createPane(), "ADB Executable", JOptionPane.QUESTION_MESSAGE);
	}

	@Override
	public JPanel createPane() {
		JPanel pane = new JPanel(new BorderLayout(0, 5));
		pane.setBorder(BorderFactory.createTitledBorder("Path to ADB Executable"));

		pane.add(createInfoPane(), BorderLayout.CENTER);
		pane.add(createPathPane(), BorderLayout.SOUTH);

		return pane;
	}

	private JPanel createPathPane() {
		final File adbExecutable = getApplication().getSettings().getAdbExecutable();
		final JTextField pathField = new JTextField(adbExecutable != null ? adbExecutable.getAbsolutePath() : "");
		final JButton open = new JButton("...");

		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createFileDialog(pathField, adbExecutable);
			}
		});

		JPanel pathPane = new JPanel(new BorderLayout(5, 0));
		pathPane.add(pathField, BorderLayout.CENTER);
		pathPane.add(open, BorderLayout.EAST);
		return pathPane;
	}

	private void createFileDialog(final JTextField txtField, File exe) {
		JFileChooser chooser = new JFileChooser(exe != null ? exe.getParentFile() : null);
		int rc = chooser.showOpenDialog(getApplication().getAppFrame());

		if (rc == JFileChooser.APPROVE_OPTION) {
			final File file = chooser.getSelectedFile();
			getLog().info("chosen file: " + file.getAbsolutePath());
			if (file.canRead()) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						getLog().info("setting file: " + file.getAbsolutePath());
						txtField.setText(file.getAbsolutePath());
						getApplication().getSettings().setAdbExecutable(file);
						getApplication().getDeviceManager().setAdbExecutable(file);
						getApplication().getDeviceManager().createBridge();
					}
				});
				//                if (exe != null && exe.exists()) {
				//                    JOptionPane.showMessageDialog(getApplication().getAppFrame(),
				//                            "The change of ADB path will take change the next time you start Droid@Screen",
				//                            "", JOptionPane.WARNING_MESSAGE);
				//                }
			} else {
				JOptionPane.showMessageDialog(getApplication().getAppFrame(), "Cannot read the file: " + file.getAbsolutePath(), "Not readable", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private JLabel createInfoPane() {
		JLabel txt = new JLabel(loadResource("/adb-exe-info.html"));
		return txt;
	}

}
