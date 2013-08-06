/*
 * Project:  droidAtScreen
 * File:     Settings.java
 * Modified: 2011-10-04
 *
 * Copyright (C) 2011, Ribomation AB (Jens Riboe).
 * http://blog.ribomation.com/
 *
 * You are free to use this software and the source code as you like.
 * We do appreciate if you attribute were it came from.
 */

package com.ribomation.droidAtScreen;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;

/**
 * Facad for all the application settings.
 * 
 * @user jens
 * @date 2011-10-03 22:58
 */
public class Settings {
	private final Preferences applicationPreferences;
	private final Logger log;
	private PropertyChangeSupport propSupport;
	private final AtomicInteger next = new AtomicInteger(1);

	public Settings() {
		applicationPreferences = Preferences.userNodeForPackage(Settings.class);
		log = Logger.getLogger(Settings.class);
		propSupport = new PropertyChangeSupport(this);
	}

	public void addListener(String name, PropertyChangeListener listener) {
		propSupport.addPropertyChangeListener(name, listener);
	}

	public void removeListener(String name, PropertyChangeListener listener) {
		propSupport.removePropertyChangeListener(name, listener);
	}

	public void destroyPreferences() {
		try {
			getPreferences().removeNode();
		} catch (BackingStoreException e) {
			log.error("Failed to destroy app settings", e);
		}
	}

	public void dump() {
		log.debug("--- Application Settings ---");
		try {
			for (String key : applicationPreferences.keys()) {
				log.debug(String.format("  %s: %s", key, applicationPreferences.get(key, "[none]")));
			}
		} catch (BackingStoreException e) {
			log.warn("Failed to dump the app settings", e);
		}
	}

	public Preferences getPreferences() {
		return applicationPreferences;
	}

	protected void savePreferences() {
		try {
			getPreferences().flush();
		} catch (BackingStoreException e) {
			log.error("Failed to flush app settings", e);
		}
	}

	// ----------------------------------------------------------
	// --- Settings
	// ----------------------------------------------------------

	public File getAdbExecutable() {
		String f = applicationPreferences.get("adbExecutable", null);
		if (f != null)
			return new File(f);
		return null;
	}

	public void setAdbExecutable(File value) {
		File oldExe = getAdbExecutable();
		applicationPreferences.put("adbExecutable", value.getAbsolutePath());
		savePreferences();
		propSupport.firePropertyChange("adbExecutable", oldExe, value);
	}

	public boolean isAskBeforeQuit() {
		try {
			return applicationPreferences.getBoolean("askBeforeQuit", true);
		} catch (Exception e) {
			return false;
		}
	}

	public void setAskBeforeQuit(boolean value) {
		set("askBeforeQuit", value);
	}

	public int nextInt() {
		return next.getAndIncrement();
	}

	private static final String[] IMG_FMTS = { "PNG", "JPG" };

	public String[] getImageFormats() {
		return IMG_FMTS;
	}

	public String getImageFormat() {
		return applicationPreferences.get("imageFormat", "PNG");
	}

	public void setImageFormat(String value) {
		set("imageFormat", value);
	}

	public File getImageDirectory() {
		String dir = applicationPreferences.get("imageDirectory", null);
		if (dir != null)
			return new File(dir);
		return new File(".");
	}

	public void setImageDirectory(File value) {
		File oldDir = getImageDirectory();
		applicationPreferences.put("imageDirectory", value.getAbsolutePath());
		savePreferences();
		propSupport.firePropertyChange("imageDirectory", oldDir, value);
	}

	public boolean isAskBeforeScreenshot() {
		try {
			return applicationPreferences.getBoolean("askBeforeScreenshot", true);
		} catch (Exception e) {
			return false;
		}
	}

	public void setAskBeforeScreenshot(boolean value) {
		set("askBeforeScreenshot", value);
	}

	public int getPreferredScale() {
		return applicationPreferences.getInt("scale", 100);
	}

	public void setPreferredScale(int value) {
		set("scale", value);
	}

	public boolean isHideEmulators() {
		return applicationPreferences.getBoolean("hideEmulators", false);
	}

	public void setHideEmulators(boolean value) {
		set("hideEmulators", value);
	}

	public boolean isUpsideDown() {
		return applicationPreferences.getBoolean("upsideDown", false);
	}

	public void setUpsideDown(boolean value) {
		set("upsideDown", value);
	}

	public boolean isLandscape() {
		return applicationPreferences.getBoolean("landscape", false);
	}

	public void setLandscape(boolean value) {
		set("landscape", value);
	}

	public boolean isAutoShow() {
		return applicationPreferences.getBoolean("autoShow", false);
	}

	public void setAutoShow(boolean value) {
		set("autoShow", value);
	}

	private void set(String name, String value) {
		String old = applicationPreferences.get(name, "");
		applicationPreferences.put(name, value);
		savePreferences();
		propSupport.firePropertyChange(name, old, value);
	}

	private void set(String name, int value) {
		int old = applicationPreferences.getInt(name, 0);
		applicationPreferences.putInt(name, value);
		savePreferences();
		propSupport.firePropertyChange(name, old, value);
	}

	private void set(String name, boolean value) {
		boolean old = applicationPreferences.getBoolean(name, false);
		applicationPreferences.putBoolean(name, value);
		savePreferences();
		propSupport.firePropertyChange(name, old, value);
	}

}
