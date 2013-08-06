/*
 * Project:  droidAtScreen
 * File:     ApplicationFrame.java
 * Modified: 2011-10-04
 *
 * Copyright (C) 2011, Ribomation AB (Jens Riboe).
 * http://blog.ribomation.com/
 *
 * You are free to use this software and the source code as you like.
 * We do appreciate if you attribute were it came from.
 */

package com.ribomation.droidAtScreen.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;

import com.ribomation.droidAtScreen.Application;
import com.ribomation.droidAtScreen.cmd.Command;
import com.ribomation.droidAtScreen.cmd.QuitCommand;

/**
 * The main GUI window.
 * 
 * @user jens
 * @date 2010-jan-18 17:44:12
 */
public class ApplicationFrame extends JFrame {
	private Logger log = Logger.getLogger(ApplicationFrame.class);
	private Application app;
	private StatusBar statusBar;

	private final String[] TOOLBAR = { "ImageDirectory", "-", "AdbRestart", "AdbReloadDevices", "-", "Help", "About", "-", "Quit" };
	private final String[] FILE_MENU = { "Quit" };
	private final String[] IMAGE_MENU = { "ImageDirectory", "ImageFormat", "AskBeforeScreenshot" };
	private final String[] ADB_MENU = { "AdbRestart", "AdbReloadDevices", "AdbExePath" };
	private final String[] OPTIONS_MENU = { "PreferredScale", "-", "HideEmulators", "AskBeforeQuit", "-", "LookAndFeel", "-", "RemoveProperties" };
	private final String[] HELP_MENU = { "Help", "-", "Home", "Mail", "-", "About" };

	public ApplicationFrame(Application app) throws HeadlessException {
		this.app = app;
	}

	public StatusBar getStatusBar() {
		return statusBar;
	}

	public void initGUI() {
		setIconImage(GuiUtil.loadIcon("device").getImage());
		setTitle(app.getInfo().getName() + ", Version " + app.getInfo().getVersion());
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Command.find(QuitCommand.class).execute();
			}
		});

		setJMenuBar(createMenubar());
		add(GuiUtil.createToolbar(TOOLBAR), BorderLayout.NORTH);
		add(createDevicesTable(), BorderLayout.CENTER);
		add(statusBar = new StatusBar(app), BorderLayout.SOUTH);

		pack();
		setLocationByPlatform(true);
	}

	protected JMenuBar createMenubar() {
		JMenuBar mb = new JMenuBar();
		mb.add(GuiUtil.createMenu("File", 'F', FILE_MENU));
		mb.add(GuiUtil.createMenu("Image", 'I', IMAGE_MENU));
		mb.add(GuiUtil.createMenu("ADB", 'A', ADB_MENU));
		mb.add(GuiUtil.createMenu("Options", 'O', OPTIONS_MENU));
		mb.add(GuiUtil.createMenu("Help", 'H', HELP_MENU));
		return mb;
	}

	private JComponent createDevicesTable() {
		JTable tbl = new JTable(app.getDeviceTableModel());
		tbl.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		tbl.setRowSelectionAllowed(true);
		tbl.setShowHorizontalLines(true);
		tbl.setFillsViewportHeight(true);
		tbl.setPreferredScrollableViewportSize(new Dimension(450, 150));

		JScrollPane pane = new JScrollPane(tbl);
		pane.setBorder(BorderFactory.createTitledBorder("Devices"));

		return pane;
	}

}
