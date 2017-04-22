package com.ribomation.droidAtScreen.dev;

import com.ribomation.droidAtScreen.gui.DeviceFrame;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.*;
import java.util.Locale;

public class AndroidDeviceCommands implements MouseListener, MouseWheelListener, KeyListener {
	private DeviceFrame deviceFrame;
	private Point startPoint;
	private Logger log;

	public AndroidDeviceCommands(DeviceFrame deviceFrame) {
		log = Logger.getLogger(this.getClass().getName() + ":" + deviceFrame.getDevice().getName());
		this.deviceFrame = deviceFrame;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		startPoint = getScaledPoint(e.getPoint());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// Move focus on DeviceFrame
		this.deviceFrame.requestFocus();
		Point point = getScaledPoint(e.getPoint());
		if (startPoint.equals(point)) {
			switch (e.getButton()) {
				case MouseEvent.BUTTON1:
					tap(point);
					break;
				case MouseEvent.BUTTON2:
					sendKey(KeyEvent.VK_CONTEXT_MENU);
					break;
				case MouseEvent.BUTTON3:
					tapAndHold(point);
					break;
			}
		} else {
			swipe(startPoint, point);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		Point from = getScaledPoint(new Point(e.getX(), e.getY()));
		// Scroll 10% of the screen
		final int scrollPercentage = 10;
		int scrollStep = deviceFrame.getHeight() * scrollPercentage / deviceFrame.getScale();
		Point to = new Point(from.x, (from.y - scrollStep * e.getWheelRotation()));
		swipe(from, to);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// Most of the ASCII chars http://www.asciitable.com/
		if (e.getKeyChar() >= 33 && e.getKeyChar() <= 126) {
			sendText(e.getKeyChar());
		} else {
			sendKey(e.getKeyChar());
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// Home or End keyboard buttons
		if (e.getKeyCode() == KeyEvent.VK_HOME || e.getKeyCode() == KeyEvent.VK_END) {
			sendKey(e.getKeyCode());
		}
		// Ctrl + V
		if (e.getKeyCode() == KeyEvent.VK_V && e.getModifiers() == KeyEvent.CTRL_MASK) {
			try {
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				String text = (String) clipboard.getData(DataFlavor.stringFlavor);
				sendText(text);
			} catch (Exception ex) {
				log.error(ex);
			}
		}
	}

	private Point getScaledPoint(Point p) {
		log.debug(String.format("mouse: %s", p));
		p = new Point(
				(int) (p.getX() * 100) / deviceFrame.getScale(),
				(int) (p.getY() * 100) / deviceFrame.getScale()
		);
		log.debug(String.format("scaled: %s", p));
		return p;
	}

	private void tap(Point p) {
		String cmd = String.format(Locale.ENGLISH, "input tap %s %s", p.getX(), p.getY());
		deviceFrame.getDevice().sendCommand(cmd);
	}

	private void tapAndHold(Point p) {
		String cmd = String.format(Locale.ENGLISH,
				"input swipe %s %s %s %s 1000", p.getX(), p.getY(), p.getX(), p.getY());
		deviceFrame.getDevice().sendCommand(cmd);
	}

	private void swipe(Point from, Point to) {
		String cmd = String.format(Locale.ENGLISH,
				"input swipe %s %s %s %s", from.getX(), from.getY(), to.getX(), to.getY());
		deviceFrame.getDevice().sendCommand(cmd);
	}

	private void sendText(String text) {
		String cmd = String.format(Locale.ENGLISH, "input text '%s'", text);
		deviceFrame.getDevice().sendCommand(cmd);
	}

	private void sendText(char letter) {
		sendText(String.valueOf(letter));
	}

	private void sendKey(int key) {
		int eventKey = 0;
		switch (key) {
			case KeyEvent.VK_HOME:
				eventKey = AndroidKeyEvent.KEYCODE_HOME;
				break;
			case KeyEvent.VK_END:
				eventKey = AndroidKeyEvent.KEYCODE_POWER;
				break;
			case KeyEvent.VK_CONTEXT_MENU:
				eventKey = AndroidKeyEvent.KEYCODE_MENU;
				break;
			case KeyEvent.VK_ESCAPE:
				eventKey = AndroidKeyEvent.KEYCODE_BACK;
				break;
			case KeyEvent.VK_ENTER:
				eventKey = AndroidKeyEvent.KEYCODE_ENTER;
				break;
			case KeyEvent.VK_DELETE:
				eventKey = AndroidKeyEvent.KEYCODE_FORWARD_DEL;
				break;
			case KeyEvent.VK_BACK_SPACE:
				eventKey = AndroidKeyEvent.KEYCODE_DEL;
				break;
			case KeyEvent.VK_SPACE:
				eventKey = AndroidKeyEvent.KEYCODE_SPACE;
				break;
			case KeyEvent.VK_TAB:
				eventKey = AndroidKeyEvent.KEYCODE_TAB;
				break;
		}
		if (eventKey != 0) {
			String cmd = String.format(Locale.ENGLISH, "input keyevent %s", eventKey);
			deviceFrame.getDevice().sendCommand(cmd);
		}
	}
}
