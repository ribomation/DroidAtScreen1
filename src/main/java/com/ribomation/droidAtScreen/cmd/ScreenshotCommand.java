/*
 * Project:  droidAtScreen
 * File:     ScreenshotCommand.java
 * Modified: 2012-03-23
 *
 * Copyright (C) 2011, Ribomation AB (Jens Riboe).
 * http://blog.ribomation.com/
 *
 * You are free to use this software and the source code as you like.
 * We do appreciate if you attribute were it came from.
 */

package com.ribomation.droidAtScreen.cmd;

import java.io.File;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.ribomation.droidAtScreen.Application;
import com.ribomation.droidAtScreen.Settings;
import com.ribomation.droidAtScreen.dev.ScreenImage;
import com.ribomation.droidAtScreen.gui.DeviceFrame;

/**
 * DESCRIPTION
 * <p/>
 * User: Jens Created: 2012-03-23, 18:14
 */
public class ScreenshotCommand extends CommandWithTarget<DeviceFrame> {
	public ScreenshotCommand(DeviceFrame target) {
		super(target);
		setIcon("camera");
		setTooltip("Takes a screen-shot and saves it to a file");
	}

	@Override
	protected void doExecute(Application app, DeviceFrame device) {
		ScreenImage image = device.getLastScreenshot().copy();

		if (app.getSettings().isAskBeforeScreenshot()) {
			JFileChooser chooser = createChooser(app.getSettings().getImageDirectory(), suggestFilename(app), app.getSettings().getImageFormats());
			if (chooser.showSaveDialog(app.getAppFrame()) == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				if (!file.exists() || askOverwrite(app, file)) {
					SwingUtilities.invokeLater(new ImageSaver(app, file, image));
				}
			}
		} else {
			SwingUtilities.invokeLater(new ImageSaver(app, suggestFilename(app), image));
		}
	}

	private class ImageSaver implements Runnable {
		private Application app;
		private File file;
		private ScreenImage image;

		private ImageSaver(Application app, File file, ScreenImage image) {
			this.app = app;
			this.file = file;
			this.image = image;
		}

		@Override
		public void run() {
			try {
				ImageIO.write(image.toBufferedImage(), extractFormat(app, file), file);
				app.getAppFrame().getStatusBar().message("Written %s", file.getName());
				getLog().info(String.format("Screenshot file: %s", file.getAbsolutePath()));
			} catch (Exception e) {
				JOptionPane.showMessageDialog(app.getAppFrame(), String.format("Failed to save '%s': %s", file, e.getMessage()), "Failure", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	@Override
	protected void updateButton(DeviceFrame target) {

	}

	private JFileChooser createChooser(File dir, File file, String[] exts) {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(dir);
		chooser.setSelectedFile(file);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("Image Files", exts));
		return chooser;
	}

	private File suggestFilename(Application app) {
		Settings cfg = app.getSettings();
		return new File(cfg.getImageDirectory(), String.format("%s-%d.%s", app.getInfo().getName().toLowerCase(), cfg.nextInt(), cfg.getImageFormat().toLowerCase()));
	}

	private boolean askOverwrite(Application app, File f) {
		return JOptionPane.showConfirmDialog(app.getAppFrame(), String.format("File '%s' already exists. Do you want to overwrite it?", f), "Overwrite?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
	}

	private String extractExt(File f) {
		String n = f.getName();
		int dot = n.lastIndexOf('.');
		if (dot > 0)
			return n.substring(dot + 1);
		return n;
	}

	private String extractFormat(Application app, File f) {
		String[] formats = app.getSettings().getImageFormats();
		String ext = extractExt(f).toUpperCase();
		if (Arrays.asList(formats).contains(ext))
			return ext;
		throw new RuntimeException("Invalid extension: " + f);
	}

}
