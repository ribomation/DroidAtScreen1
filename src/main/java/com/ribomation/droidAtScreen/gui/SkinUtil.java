package com.ribomation.droidAtScreen.gui;

import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.ImageIcon;

import com.ribomation.droidAtScreen.Skin;

public class SkinUtil {

	public static Skin loadSkin(String skinName) {
		// create and load default properties
		Properties skinProperties = new Properties();
		InputStream in = null;
		ImageIcon skinImage = null;
		try {
			in = SkinUtil.class.getResourceAsStream("/skins/" + skinName + "/frame.cfg");
			skinProperties.load(in);
			skinImage = GuiUtil.loadSkin(skinName);
		} catch (Exception ignore) {
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (skinImage == null) {
			return null;
		}

		Skin skin = new Skin();
		skin.setFrame(skinImage);
		String property = skinProperties.getProperty("screen.x");
		int x = property == null ? 0 : Integer.parseInt(property);
		property = skinProperties.getProperty("screen.y");
		int y = property == null ? 0 : Integer.parseInt(property);
		skin.setScreenXYCoord(new Point(x, y));
		return skin;
	}
}
