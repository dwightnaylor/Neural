package graphics.testing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JRootPane;

public class LineTesting {
	@SuppressWarnings("serial")
	public static void main(String[] args) {
		final BufferedImage image = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);
		JFrame frame = new JFrame();
		frame.setSize(image.getWidth(), image.getHeight());
		frame.setContentPane(new JRootPane() {
			public void paint(Graphics g) {
				Graphics imageGraphics = image.getGraphics();
				imageGraphics.setColor(Color.white);
				imageGraphics.fillRect(0, 0, image.getWidth(), image.getHeight());
				line(image, image.getWidth() / 2, image.getHeight() / 2, MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y, Color.black.getRGB());
				g.drawImage(image, 0, 0, null);
			}
		});
		frame.setVisible(true);
		while (true) {
			frame.repaint();
		}
	}

	public static void line(BufferedImage image, int x, int y, int x2, int y2, int color) {
		int w = x2 - x;
		int h = y2 - y;
		int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;
		if (w < 0)
			dx1 = -1;
		else if (w > 0)
			dx1 = 1;
		if (h < 0)
			dy1 = -1;
		else if (h > 0)
			dy1 = 1;
		if (w < 0)
			dx2 = -1;
		else if (w > 0)
			dx2 = 1;
		int longest = Math.abs(w);
		int shortest = Math.abs(h);
		if (longest <= shortest) {
			longest = Math.abs(h);
			shortest = Math.abs(w);
			if (h < 0)
				dy2 = -1;
			else if (h > 0)
				dy2 = 1;
			dx2 = 0;
		}
		int numerator = longest >> 1;
		for (int i = 0; i <= longest; i++) {
			image.setRGB(x, y, color);
			numerator += shortest;
			if (!(numerator < longest)) {
				numerator -= longest;
				x += dx1;
				y += dy1;
			} else {
				x += dx2;
				y += dy2;
			}
		}
	}
}
