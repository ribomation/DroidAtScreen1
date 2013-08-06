/*
 * Project:  droidAtScreen
 * File:     RecordingListener.java
 * Modified: 2011-10-04
 *
 * Copyright (C) 2011, Ribomation AB (Jens Riboe).
 * http://blog.ribomation.com/
 *
 * You are free to use this software and the source code as you like.
 * We do appreciate if you attribute were it came from.
 */

package com.ribomation.droidAtScreen.gui;

import com.ribomation.droidAtScreen.dev.ScreenImage;

/**
 * DESCRIPTION
 * 
 * @user jens
 * @date 2011-10-04 15:16
 */
public interface RecordingListener {
	void record(ScreenImage image);
}
