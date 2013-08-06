/*
 * Project:  droidAtScreen
 * File:     Command.java
 * Modified: 2011-10-04
 *
 * Copyright (C) 2011, Ribomation AB (Jens Riboe).
 * http://blog.ribomation.com/
 *
 * You are free to use this software and the source code as you like.
 * We do appreciate if you attribute were it came from.
 */

package com.ribomation.droidAtScreen.cmd;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

import com.ribomation.droidAtScreen.Application;
import com.ribomation.droidAtScreen.dev.AndroidDevice;
import com.ribomation.droidAtScreen.dev.AndroidDeviceListener;
import com.ribomation.droidAtScreen.gui.GuiUtil;

/**
 * Base class of all app commands.
 * 
 * @user jens
 * @date 2010-jan-18 09:59:01
 */
public abstract class Command extends AbstractAction implements AndroidDeviceListener {
	private Logger log;
	private static Application application;
	private static Map<String, Command> cmds = new HashMap<String, Command>();
	private String name;
	private boolean enabledOnlyWithDevice = false;

	{
		log = Logger.getLogger(this.getClass());
		getApplication().addAndroidDeviceListener(this);
	}

	protected Command() {
		this.name = extractName();
		cmds.put(this.getName(), this);
	}

	protected Command(String name) {
		this.name = name;
		cmds.put(this.getName(), this);
	}

	public boolean isEnabledOnlyWithDevice() {
		return enabledOnlyWithDevice;
	}

	public void setEnabledOnlyWithDevice(boolean enabledOnlyWithDevice) {
		this.enabledOnlyWithDevice = enabledOnlyWithDevice;
		//        setEnabled(!enabledOnlyWithDevice);
	}

	@Override
	public void connected(AndroidDevice dev) {
	}

	@Override
	public void disconnected(AndroidDevice dev) {
	}

	protected abstract void doExecute(Application app);

	public void execute() {
		try {
			doExecute(getApplication());
		} catch (Exception e) {
			getLog().error("Failed to execute command " + getName(), e);

			JOptionPane.showMessageDialog(null, "Failed to execute command " + getName() + ": " + e, "Application Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		execute();
	}

	@SuppressWarnings("unchecked")
	public static <CmdType extends Command> CmdType find(Class<? extends Command> cmdCls) {
		String name = extractName(cmdCls);
		return (CmdType) get(name);
	}

	public static Command get(String name) {
		Command c = cmds.get(name.toLowerCase());
		if (c != null)
			return c;
		return loadCommand(name);
	}

	protected static Map<String, Command> getCmds() {
		return cmds;
	}

	protected Logger getLog() {
		return log;
	}

	public String getName() {
		return name;
	}

	public String getLabel() {
		return (String) getValue(Action.NAME);
	}

	protected void setLabel(String label) {
		putValue(Action.NAME, label);
	}

	public String getTooltip() {
		return (String) getValue(Action.SHORT_DESCRIPTION);
	}

	protected void setTooltip(String label) {
		putValue(Action.SHORT_DESCRIPTION, label);
	}

	public Icon getIcon() {
		return (Icon) getValue(Action.SMALL_ICON);
	}

	protected void setIcon(Icon ico) {
		putValue(Action.SMALL_ICON, ico);
		putValue(Action.LARGE_ICON_KEY, ico);
	}

	protected void setIcon(String icoName) {
		setIcon(GuiUtil.loadIcon(icoName));
	}

	public String getAccelerator() {
		return (String) getValue(Action.ACCELERATOR_KEY);
	}

	protected void setAccelerator(String key) {
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(key));
	}

	public int getMnemonic() {
		return (Integer) getValue(Action.MNEMONIC_KEY);
	}

	protected void setMnemonic(char ch) {
		putValue(Action.MNEMONIC_KEY, new Integer(ch));
	}

	public JMenuItem createMenuItem() {
		JMenuItem mi = newMenuItem();
		mi.setAction(this);
		return mi;
	}

	protected JMenuItem newMenuItem() {
		return new JMenuItem();
	}

	public AbstractButton createButton() {
		AbstractButton b = newButton();
		b.setAction(this);
		return b;
	}

	protected AbstractButton newButton() {
		JButton b = new JButton();
		b.setVerticalTextPosition(SwingConstants.BOTTOM);
		b.setHorizontalTextPosition(SwingConstants.CENTER);
		return b;
	}

	public JPanel createPane() {
		JPanel p = new JPanel();
		p.add(new JLabel("Empty/Dummy Pane"));
		return p;
	}

	protected Application getApplication() {
		if (application == null) {
			throw new IllegalStateException("Missing application ref. Must invoke Command.setApplication(...) before use.");
		}
		return application;
	}

	public static void setApplication(Application application) {
		Command.application = application;
	}

	protected static Command loadCommand(String name) {
		String clsName = Command.class.getPackage().getName() + "." + toTitleCase(name) + "Command";
		try {
			Class cls = Class.forName(clsName);
			return (Command) cls.newInstance();
		} catch (ClassNotFoundException e) {
			return new DummyCommand(name);
			//            throw new IllegalArgumentException("Command not defined: "+name);
		} catch (InstantiationException e) {
			throw new RuntimeException("Failed to create command " + name + ". ErrMsg=" + e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Not permitted to create command " + name + ". ErrMsg=" + e);
		}
	}

	private String extractName() {
		return extractName(this.getClass());
	}

	private static String extractName(Class cls) {
		return extractName(cls.getSimpleName());
	}

	private static String extractName(String clsName) {
		if (isBlank(clsName))
			return "[NONE]";
		return clsName.substring(0, clsName.lastIndexOf("Command")).toLowerCase();
	}

	private static String toTitleCase(String s) {
		if (isBlank(s) || s.length() == 1)
			return s;
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	private static boolean isBlank(String s) {
		return s == null || s.trim().length() == 0;
	}

	protected String loadResource(String path) {
		InputStream is = this.getClass().getResourceAsStream(path);
		if (is == null) {
			throw new RuntimeException("Failed to load text resource: " + path);
		}

		try {
			StringBuilder buf = new StringBuilder(1000);
			BufferedReader in = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = in.readLine()) != null) {
				buf.append(line);
			}
			in.close();

			return buf.toString();
		} catch (Exception e) {
			throw new RuntimeException("Failed to load text resource: " + path, e);
		}
	}

	protected static class DummyCommand extends Command {
		public DummyCommand(String name) {
			super("dummy-" + name);
			setLabel(name);
		}

		@Override
		protected void doExecute(Application app) {
			JOptionPane.showMessageDialog(null, "Command not yet implemented: " + getLabel(), "Undefined command", JOptionPane.WARNING_MESSAGE);
		}
	}

}
