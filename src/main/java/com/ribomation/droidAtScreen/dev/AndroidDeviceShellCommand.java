/**
 * 
 */
package com.ribomation.droidAtScreen.dev;

import java.awt.Point;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IShellOutputReceiver;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.ribomation.droidAtScreen.gui.DeviceFrame;

/**
 * @author tonsense
 * This class executes shell commands using ADB 
 */
public class AndroidDeviceShellCommand {

	private AndroidDevice device;
	private DeviceFrame view;
	private Logger log;
	
	public AndroidDeviceShellCommand(AndroidDevice device, DeviceFrame view) {
		this.device = device;
		this.view = view;
		this.log = Logger.getLogger(DeviceFrame.class.getName() + ":" + device.getName());
	}
	
	/**
	 * Tap on a screen location
	 * @param p Point to tap
	 */
	public void tap(Point p) {
		p = scalePoint(p);
		execute("input tap " + p.getX() + " " + p.getY());
	}
	
	
	
	////////////////// PRIVATE METHODS ////////////////////
	
	/**
	 * Execute shell command
	 * @param cmd String with shell command. Example: "input tap 0 0"
	 */
	private void execute(String cmd) {
		log.debug(cmd);
		try {
			device.getDevice().executeShellCommand(cmd,
					new IShellOutputReceiver() {
				
				@Override
				public boolean isCancelled() {
					return false;
				}
				
				@Override
				public void flush() {
				}
				
				@Override
				public void addOutput(byte[] data, int offset, int length) {
				}
			});
		} catch (TimeoutException e1) {
			e1.printStackTrace();
		} catch (AdbCommandRejectedException e1) {
			e1.printStackTrace();
		} catch (ShellCommandUnresponsiveException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Adjust scaled point back to original device coordinates 
	 * @param p Point to scale back
	 * @return scaled Point
	 */
	private Point scalePoint(Point p) {
		return new Point((int)p.getX() * 100 / view.getScale(),
				(int)p.getY() * 100 / view.getScale());
	}
	
}
