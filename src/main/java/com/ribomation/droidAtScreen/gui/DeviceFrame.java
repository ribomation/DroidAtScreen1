/*
 * Project:  droidAtScreen
 * File:     DeviceFrame.java
 * Modified: 2011-10-04
 *
 * Copyright (C) 2011, Ribomation AB (Jens Riboe).
 * http://blog.ribomation.com/
 *
 * You are free to use this software and the source code as you like.
 * We do appreciate if you attribute were it came from.
 */

package com.ribomation.droidAtScreen.gui;

import com.ribomation.droidAtScreen.Application;
import com.ribomation.droidAtScreen.Settings;
import com.ribomation.droidAtScreen.Skin;
import com.ribomation.droidAtScreen.cmd.BackButtonCommand;
import com.ribomation.droidAtScreen.cmd.HomeButtonCommand;
import com.ribomation.droidAtScreen.cmd.OrientationCommand;
import com.ribomation.droidAtScreen.cmd.PropertiesCommand;
import com.ribomation.droidAtScreen.cmd.RecordingCommand;
import com.ribomation.droidAtScreen.cmd.ScaleCommand;
import com.ribomation.droidAtScreen.cmd.ScreenshotCommand;
import com.ribomation.droidAtScreen.cmd.UpsideDownCommand;
import com.ribomation.droidAtScreen.dev.AndroidDevice;
import com.ribomation.droidAtScreen.dev.ScreenImage;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.TimerTask;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import org.apache.log4j.Logger;

/**
 * Frame holder for the device image.
 *
 * @user jens
 * @date 2010-jan-17 22:13:20
 */
public class DeviceFrame extends JFrame implements Comparable<DeviceFrame> {
	private final static RenderingHints HINTS = new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

	private final Application app;
	private final AndroidDevice device;
	private Logger log;

	private int scalePercentage = 100;
	private boolean landscapeMode = false;
	private boolean upsideDown = false;

	private ImageCanvas canvas;
	private JScrollPane canvasScrollable;
	private JComponent toolBar;
	private RecordingListener recordingListener;
	private TimerTask retriever;
	private InfoPane infoPane;

	private final class AnimationActionListener implements ActionListener {

		private int x = 0;
		private int y = 0;
		private final int velocity = 15;

		public void setLocation(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Point location = DeviceFrame.this.getLocation();
			Point to = new Point(location);
			if (Math.abs(to.x - this.x) < velocity) {
				to.x = this.x;
			} else {
				if (to.x < this.x) {
					to.x += velocity;
				} else if (to.x > this.x) {
					to.x -= velocity;
				}
			}
			if (Math.abs(to.y - this.y) < velocity) {
				to.y = this.y;
			} else {
				if (to.y < this.y) {
					to.y += velocity;
				} else if (to.y > this.y) {
					to.y = this.y;
				}
			}

			DeviceFrame.this.setLocation(to);

			if (to.equals(location)) {
				((Timer) e.getSource()).stop();
			}
		}
	}

	private final class AnimationTimer extends Timer {

		private static final long serialVersionUID = 6541909613675931639L;

		public AnimationTimer(int delay, ActionListener listener) {
			super(delay, listener);
		}
	}

	AnimationActionListener animationActionListener = new AnimationActionListener();
	AnimationTimer timer = new AnimationTimer(1, animationActionListener);

	public DeviceFrame(Application app, final AndroidDevice device) {
		this.app = app;
		this.device = device;
		this.log = Logger.getLogger(DeviceFrame.class.getName() + ":" + device.getName());
		log.debug(String.format("DeviceFrame(device=%s)", device));

		Settings cfg = app.getSettings();
		setScale(cfg.getPreferredScale());
		setLandscapeMode(cfg.isLandscape());

		setTitle(device.getName());
		setIconImage(GuiUtil.loadIcon("device").getImage());
		setResizable(true);

		JComponent c = (JComponent) getContentPane();
		c.setBorder(BorderFactory.createEmptyBorder());

		canvas = new ImageCanvas();
		canvasScrollable = new JScrollPane(canvas);
		
		add(canvasScrollable, BorderLayout.CENTER);
		add(toolBar = createToolBar(), BorderLayout.WEST);
		add(infoPane = new InfoPane(), BorderLayout.SOUTH);

		canvas.setBorder(BorderFactory.createEmptyBorder());

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				log.debug("windowClosing");
				stopRetriever();
				DeviceFrame.this.setVisible(false);
				DeviceFrame.this.app.getDeviceTableModel().refresh();
			}
		});
		
		canvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Point p = e.getPoint();
				log.debug(String.format("mouse: %s", p));
				p = new Point(
						(int) (p.getX() * 100) / getScale(),
						(int) (p.getY() * 100) / getScale()
				);
				log.debug(String.format("scaled: %s", p));
				device.tap(p);
			}
		});

		startRetriever();
		pack();
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension	frame  = super.getPreferredSize();
		Insets 		fb  = super.getInsets();
		Dimension 	screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getSize();
		Dimension canvasSize = canvas.getPreferredSize();
		Dimension infoPaneSize = infoPane.getSize();
		Dimension toolBarSize = toolBar.getSize();

		log.debug(String.format("getPreferredSize: screen=%s, canvas=%s, frame=%s",
				screen, canvasSize, frame
		));

		if (frame.height > screen.height) {
			frame.height = (int) (0.9 * screen.height);
		}
		if (frame.width > screen.width) {
			frame.width = (int) (0.9*screen.width);
		}

		JScrollBar vsb = canvasScrollable.getVerticalScrollBar();
		int vsbW = vsb.getWidth(); //vsb.isShowing() ? vsb.getWidth() : 0;
		JScrollBar hsb = canvasScrollable.getHorizontalScrollBar();
		int hsbH = hsb.getHeight(); //hsb.isShowing() ? hsb.getHeight() : 0;
		
		int W = fb.left + toolBarSize.width + canvasSize.width + vsbW + fb.right;
		int H = fb.top + infoPaneSize.height + canvasSize.height + hsbH + fb.bottom;
		if (frame.width > W) {
			frame.width = W;
		}
		if (frame.height > H) {
			frame.height = H;
		}
		log.debug(String.format("getPreferredSize: frame2=%s", frame));

		return frame;
	}


	private void applySkin() {
		Skin skin = null;
		try {
			skin = SkinUtil.loadSkin(device.getName().toLowerCase());
        } catch (Exception ignore) {
		}
		if (skin != null) {
			// Apply skin
			canvas.setSkin(skin);
			pack();
			boolean wasVisible = isVisible();
			dispose();
			setUndecorated(true);
			setBackground(new Color(1.0f, 1.0f, 1.0f, 0.0f));
			toolBar.setVisible(false);
			infoPane.setVisible(false);
			setVisible(wasVisible);
			forceRepaint();
		}
	}

	public void startRetriever() {
		retriever = new Retriever();
		app.getTimer().schedule(retriever, 0, 500);
	}

	public void stopRetriever() {
		retriever.cancel();
	}

	class Retriever extends TimerTask {
		@Override
		public void run() {
			long start = System.currentTimeMillis();
			ScreenImage image = device.getScreenImage();
			long elapsed = System.currentTimeMillis() - start;
			infoPane.setElapsed(elapsed, image);
			infoPane.setStatus(device.getState().name().toUpperCase());
			//			log.debug(String.format("Got screenshot %s, elapsed %d ms", image, elapsed));

			if (image == null) {
				return;
			}
			boolean fresh = canvas.getScreenshot() == null;
			if (recordingListener != null) {
				recordingListener.record(image);
			}
			canvas.setScreenshot(image);
			infoPane.setSizeInfo(canvas);

			if (fresh) {
				log = Logger.getLogger(DeviceFrame.class.getName() + ":" + device.getName());
				setTitle(device.getName());
				pack();
				applySkin();
				app.getDeviceTableModel().refresh();
				app.updateDeviceFramePositionsOnScreen(null);
			}
		}
	}

	protected JComponent createToolBar() {
		JPanel buttons = new JPanel(new GridLayout(9, 1, 0, 8));
		buttons.add(new OrientationCommand(this).newButton());
		buttons.add(new UpsideDownCommand(this).newButton());
		buttons.add(new ScaleCommand(this).newButton());
		buttons.add(new ScreenshotCommand(this).newButton());
		buttons.add(new RecordingCommand(this).newButton());
		buttons.add(new PropertiesCommand(this).newButton());
		buttons.add(new BackButtonCommand(this).newButton());
		buttons.add(new HomeButtonCommand(this).newButton());

		JPanel tb = new JPanel(new FlowLayout());
		tb.setBorder(BorderFactory.createEmptyBorder());
		tb.add(buttons);

		return tb;
	}

	public class InfoPane extends JPanel {
		JLabel size, status, elapsed;

		InfoPane() {
			super(new GridLayout(1, 2, 3, 0));
			setBorder(BorderFactory.createEmptyBorder());

			Font font = getFont().deriveFont(Font.PLAIN, 12.0F);
			status = new JLabel("UNKNOWN");
			status.setFont(font);
			status.setHorizontalAlignment(SwingConstants.LEADING);
			status.setToolTipText("Device status");

			size = new JLabel("? x ?");
			size.setFont(font);
			size.setHorizontalAlignment(SwingConstants.CENTER);
			size.setToolTipText("Image dimension and size");

			elapsed = new JLabel("");
			elapsed.setFont(font);
			elapsed.setHorizontalAlignment(SwingConstants.RIGHT);
			elapsed.setToolTipText("Elapsed time and rate of last screenshot");

			this.add(status);
			this.add(size);
			this.add(elapsed);
		}

		void setSizeInfo(ImageCanvas img) {
			Dimension sz = img.getPreferredSize();
			size.setText(String.format("%dx%d (%s)", sz.width, sz.height, new Unit(img.getScreenshot().getRawImage().size).toString()));
		}

		public void setStatus(String devStatus) {
			status.setText(devStatus);
		}

		public void setElapsed(long time, ScreenImage img) {
			int sz = (img != null ? (int) (img.getRawImage().size / (time / 1000.0)) : 0);
			elapsed.setText(String.format("%d ms (%s/s)", time, new Unit(sz).toString()));
		}
	}

	class Unit {
		final int K = 1024;
		final int M = K * K;
		final int G = K * M;
		long value;

		Unit(long value) {
			this.value = value;
		}

		String unit() {
			if (value / G > 0) {
				return "Gb";
			}
			if (value / M > 0) {
				return "Mb";
			}
			if (value / K > 0) {
				return "Kb";
			}
			return "bytes";
		}

		float value() {
			if (value / G > 0) {
				return (float) value / G;
			}
			if (value / M > 0) {
				return (float) value / M;
			}
			if (value / K > 0) {
				return (float) value / K;
			}
			return value;
		}

		@Override
		public String toString() {
			return String.format("%.1f %s", value(), unit());
		}
	}

	class ImageCanvas extends JComponent {
		private ScreenImage image;
		private Image skinBackgroundImage = null;
		private Point skinScreenXYPoint;

		public ImageCanvas() {
			setBorder(BorderFactory.createLoweredBevelBorder());
		}

		public void setSkin(Skin skin) {
			this.skinScreenXYPoint = skin.getScreenXYCoord();
			BufferedImage bi = new BufferedImage(skin.getFrame().getIconWidth(), skin.getFrame().getIconHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics g = bi.createGraphics();
			// paint the Icon to the BufferedImage.
			skin.getFrame().paintIcon(null, g, 0, 0);
			g.dispose();
			this.skinBackgroundImage = bi;
			repaint();
		}

		public void setScreenshot(ScreenImage image) {
			this.image = image;
			repaint();
		}

		public ScreenImage getScreenshot() {
			return image;
		}

		@Override
		protected void paintComponent(Graphics g) {
			if (image != null && g instanceof Graphics2D) {
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
				g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
				g2.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

				AffineTransform TX = new AffineTransform();
				BufferedImage bufImg = image.toBufferedImage();

				if (landscapeMode) {
					bufImg = toLandscape(bufImg);
				}
				if (scalePercentage != 100) {
					double scale = scalePercentage / 100.0;
					TX.concatenate(AffineTransform.getScaleInstance(scale, scale));
				}
				if (upsideDown) {
					int w = image.getWidth();
					int h = image.getHeight();
					double x = (landscapeMode ? h : w) / 2;
					double y = (landscapeMode ? w : h) / 2;
					TX.concatenate(AffineTransform.getQuadrantRotateInstance(2, x, y));
				}

				if (skinBackgroundImage != null) {
					g2.drawImage(skinBackgroundImage, TX, null);
					TX.translate(this.skinScreenXYPoint.x, this.skinScreenXYPoint.y);
				}
				g2.drawImage(bufImg, TX, null);
			} else {
				g.setColor(Color.RED);
				g.setFont(getFont().deriveFont(16.0F));
				g.drawString("No screenshot yet", 10, 25);
			}
		}

		BufferedImage toLandscape(BufferedImage img) {
			return rotate(3, img);
		}

		BufferedImage rotate(int quadrants, BufferedImage img) {
			int w = img.getWidth();
			int h = img.getHeight();
			int x = (quadrants == 2 || quadrants == 3) ? w : 0;
			int y = (quadrants == 1 || quadrants == 2) ? h : 0;
			Point2D origo = AffineTransform.getQuadrantRotateInstance(quadrants, 0, 0).transform(new Point(x, y), null);

			BufferedImage result = new BufferedImage(h, w, img.getType());
			Graphics2D g = result.createGraphics();
			g.translate(0 - origo.getX(), 0 - origo.getY());
			g.transform(AffineTransform.getQuadrantRotateInstance(quadrants, 0, 0));
			g.drawRenderedImage(img, null);

			return result;
		}

		@Override
		public Dimension getPreferredSize() {
			if (image == null) {
				return new Dimension(200, 300);
			}
			if (landscapeMode) {
				if (skinBackgroundImage != null) {
					return new Dimension(scale(skinBackgroundImage.getHeight(null)), scale(skinBackgroundImage.getWidth(null)));
				}
				return new Dimension(scale(image.getHeight()), scale(image.getWidth()));
			}
			if (skinBackgroundImage != null) {
				return new Dimension(scale(skinBackgroundImage.getWidth(null)), scale(skinBackgroundImage.getHeight(null)));
			}
			return new Dimension(scale(image.getWidth()), scale(image.getHeight()));
		}

		@Override
		public Dimension getMinimumSize() {
			return getPreferredSize();
		}
	}

	public void setLandscapeMode(boolean landscape) {
		this.landscapeMode = landscape;
		//        if (landscape) {
		//            ScreenImage image = getLastScreenshot();
		//            if (image != null) {
		//                double w = image.getWidth();
		//                double h = image.getHeight();
		//                double x = w / 2;
		//                double y = h / 2;
		//                landscapeTX = AffineTransform.getQuadrantRotateInstance(-1, x, y);
		//            }
		//        } else {
		//            landscapeTX = null;
		//        }
	}

	public void setScale(int scalePercentage) {
		this.scalePercentage = scalePercentage;
	}

	public void updateScale(int scalePercentage) {
		setScale(scalePercentage);
		canvas.repaint();
	}

	public void setUpsideDown(boolean upsideDown) {
		this.upsideDown = upsideDown;
		//        ScreenImage lastScreenshot = getLastScreenshot();
		//        if (upsideDown) {
		//            if (lastScreenshot != null) {
		//                double x = lastScreenshot.getWidth() / 2;
		//                double y = lastScreenshot.getHeight() / 2;
		//                upsideDownTX = AffineTransform.getQuadrantRotateInstance(2, x, y);
		//            }
		//        } else {
		//            upsideDownTX = null;
		//        }
	}

	public void setRecordingListener(RecordingListener recordingListener) {
		this.recordingListener = recordingListener;
	}

	public ScreenImage getLastScreenshot() {
		return canvas.getScreenshot();
	}

	public InfoPane getInfoPane() {
		return infoPane;
	}

	public AndroidDevice getDevice() {
		return device;
	}

	@Override
	public String getName() {
		return device.getName();
	}

	public boolean isLandscapeMode() {
		return landscapeMode;
	}

	public int getScale() {
		return scalePercentage;
	}

	public boolean isUpsideDown() {
		return upsideDown;
	}

	private int scale(int value) {
		double factor = getScaleFactor();
		if (factor != 1) {
			scalePercentage = (int) (factor * 100);
		}
		if (scalePercentage == 100) {
			return value;
		}
		return (int) Math.round(value * scalePercentage / 100.0);
	}

	private double getScaleFactor() {
		final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		final double screenHeight = screen.getHeight();
		final double screenWidth = screen.getWidth();
		double factor = 1;
		if (screenHeight <= getHeight()) {
			factor = screenHeight / getHeight();
			if (factor > 0.75) {
				factor = 0.75;
			} else if (factor > 0.50) {
				factor = 0.50;
			} else if (factor > 0.25) {
				factor = 0.25;
			}
		}
		return factor;
	}

	private void forceRepaint() {
		pack();
		invalidate();
		validate();
		repaint();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		DeviceFrame that = (DeviceFrame) o;
		return this.device.getName().equals(that.device.getName());
	}

	@Override
	public int hashCode() {
		return device.getName().hashCode();
	}

	@Override
	public int compareTo(DeviceFrame that) {
		return this.getName().compareTo(that.getName());
	}

	public void setLocation(int x, int y, boolean animate) {
		if (animate) {
			timer.stop();
			animationActionListener.setLocation(x, y);
			timer.setRepeats(true);
			timer.setCoalesce(true);
			timer.start();
		} else {
			super.setLocation(x, y);
		}
	}
}
