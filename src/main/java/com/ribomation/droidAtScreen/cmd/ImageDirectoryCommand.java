/*
 * Project:  droidAtScreen
 * File:     ImageDirectoryCommand.java
 * Modified: 2012-01-03
 *
 * Copyright (C) 2011, Ribomation AB (Jens Riboe).
 * http://blog.ribomation.com/
 *
 * You are free to use this software and the source code as you like.
 * We do appreciate if you attribute were it came from.
 */

package com.ribomation.droidAtScreen.cmd;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.ribomation.droidAtScreen.Application;

/**
 * Sets the default image dir, when saving screen-shots.
 * 
 * @user jens
 * @date 2012-01-03 09:20
 */
public class ImageDirectoryCommand extends Command {

	public ImageDirectoryCommand() {
		setLabel("Image Dir");
		setIcon("imgfolder");
		setMnemonic('D');
		updateView(getApplication().getSettings().getImageDirectory());
	}

	protected void updateView(File imageDirectory) {
		setTooltip(String.format("Directory when saving screen-shots (%s)", imageDirectory.getName()));
	}

	@Override
	protected void doExecute(Application app) {
		File imageDirectory = app.getSettings().getImageDirectory();

		final JFileChooser chooser = new JFileChooser(imageDirectory);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int rc = chooser.showSaveDialog(app.getAppFrame());
		if (rc != JFileChooser.APPROVE_OPTION)
			return;

		imageDirectory = chooser.getSelectedFile();
		if (!(imageDirectory.isAbsolute() && imageDirectory.canWrite())) {
			JOptionPane.showMessageDialog(app.getAppFrame(), "Not a writable directory '" + imageDirectory + "'", "Invalid directory", JOptionPane.ERROR_MESSAGE);
			return;
		}

		app.getSettings().setImageDirectory(imageDirectory);
		updateView(imageDirectory);
	}

}
