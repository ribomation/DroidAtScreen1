/*
 * Project:  droidAtScreen
 * File:     ImageFormatCommand.java
 * Modified: 2011-10-04
 *
 * Copyright (C) 2011, Ribomation AB (Jens Riboe).
 * http://blog.ribomation.com/
 *
 * You are free to use this software and the source code as you like.
 * We do appreciate if you attribute were it came from.
 */

package com.ribomation.droidAtScreen.cmd;

import javax.swing.JOptionPane;

import com.ribomation.droidAtScreen.Application;

/**
 * Sets the default image format, when saving screen-shots.
 * 
 * @user jens
 * @date 2011-10-01 12:13
 */
public class ImageFormatCommand extends Command {

	public ImageFormatCommand() {
		updateView(getApplication().getSettings().getImageFormat());
		setIcon("imgfmt");
		setMnemonic('F');
		setTooltip("Set the default image-format when saving screen-shots.");
	}

	protected void updateView(String imgFmt) {
		setLabel(String.format("Image Format (%s)", imgFmt));
	}

	@Override
	protected void doExecute(Application app) {
		String[] formats = app.getSettings().getImageFormats();
		int rc = JOptionPane.showOptionDialog(app.getAppFrame(), "Image Formats", "Set default image format", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, formats, app.getSettings().getImageFormat());

		if (0 <= rc && rc < formats.length) {
			app.getSettings().setImageFormat(formats[rc]);
			updateView(formats[rc]);
		}
	}

}
