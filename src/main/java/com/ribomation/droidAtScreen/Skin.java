package com.ribomation.droidAtScreen;

import java.awt.Point;

import javax.swing.ImageIcon;

public class Skin {

	private ImageIcon frame;
	private Point screenXYCoord;

	public Skin() {
	}

	public ImageIcon getFrame() {
		return frame;
	}

	public void setFrame(ImageIcon imageIcon) {
		this.frame = imageIcon;
	}

	public Point getScreenXYCoord() {
		return screenXYCoord;
	}

	public void setScreenXYCoord(Point screenXYCoord) {
		this.screenXYCoord = screenXYCoord;
	}
}
