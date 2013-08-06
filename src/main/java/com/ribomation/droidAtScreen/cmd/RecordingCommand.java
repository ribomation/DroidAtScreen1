/*
 * Project:  droidAtScreen
 * File:     RecordingCommand.java
 * Modified: 2012-03-24
 *
 * Copyright (C) 2011, Ribomation AB (Jens Riboe).
 * http://blog.ribomation.com/
 *
 * You are free to use this software and the source code as you like.
 * We do appreciate if you attribute were it came from.
 */

package com.ribomation.droidAtScreen.cmd;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.ribomation.droidAtScreen.Application;
import com.ribomation.droidAtScreen.dev.ScreenImage;
import com.ribomation.droidAtScreen.gui.DeviceFrame;
import com.ribomation.droidAtScreen.gui.RecordingListener;
import com.ribomation.droidAtScreen.gui.StatusBar;

/**
 * Records a series of screen-shots into a directory.
 * <p/>
 * User: Jens Created: 2012-03-24, 15:50
 */
public class RecordingCommand extends CommandWithTarget<DeviceFrame> implements RecordingListener, Runnable {
	private AtomicInteger next = new AtomicInteger(0);
	private AtomicBoolean capturing = new AtomicBoolean(true);
	private BlockingQueue<ScreenImage> images;
	private File dir;
	private String format = "png";
	private Thread runner;
	private DeviceFrame device;

	public RecordingCommand(DeviceFrame deviceFrame) {
		super(deviceFrame);
		setIcon("record");
		setTooltip("Continuously record screen-shots and save them to a directory, for further processing.");
	}

	@Override
	protected void doExecute(Application app, DeviceFrame deviceFrame) {
		if (capturing.get() && runner != null) {
			capturing.set(false);
			runner.interrupt();
			return;
		}

		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(app.getSettings().getImageDirectory());
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle("Select target directory for the images");
		chooser.setApproveButtonText("Images Dir");
		chooser.setApproveButtonToolTipText("All screen-shots will go into this directory, sequentially numbered.");

		int rc = chooser.showOpenDialog(app.getAppFrame());
		if (rc != JFileChooser.APPROVE_OPTION)
			return;

		dir = chooser.getSelectedFile();
		if (!(dir.isAbsolute() && dir.canWrite())) {
			JOptionPane.showMessageDialog(app.getAppFrame(), "Not a writable directory " + dir, "Invalid directory", JOptionPane.ERROR_MESSAGE);
			return;
		}

		images = new LinkedBlockingQueue<ScreenImage>(120);
		device = deviceFrame;
		device.setRecordingListener(this);
		capturing.set(true);
		runner = new Thread(this);
		runner.start();
		setIcon("recording");
	}

	@Override
	protected void updateButton(DeviceFrame deviceFrame) {

	}

	@Override
	public void record(ScreenImage image) {
		try {
			images.put(image.copy());
		} catch (InterruptedException ignore) {
		}
	}

	@Override
	public void run() {
		final StatusBar statusBar = getApplication().getAppFrame().getStatusBar();
		try {
			do {
				ScreenImage image = images.take();
				File file = nextName();
				ImageIO.write(image.toBufferedImage(), format, file);
				getLog().info("Screenshot saved " + file);
				statusBar.message("Saved %s", file.getName());
			} while (capturing.get());
		} catch (InterruptedException ignore) {
		} catch (IOException e) {
			getLog().warn("Failed to save image: " + e);
		} finally {
			device.setRecordingListener(null);
			capturing.set(false);
			images.clear();
			runner = null;
			images = null;
			device = null;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					setIcon("record");
					statusBar.message("Recording stopped. %d images saved.", next.get());
				}
			});
		}
	}

	File nextName() {
		return new File(dir, String.format("droidAtScreen-%d.%s", next.incrementAndGet(), format));
	}

}
