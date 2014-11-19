package graphics.fundamentals;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import graphics.basicShapes.Point3D;
import graphics.basicShapes.Ray3D;
import graphics.basicmodels.RayModel;

public class Camera extends RayModel {

	private double[][] zraster;
	private BufferedImage screen;
	public Graphics2D screenGraphics;

	public double zmult = 0.01;
	private double distmult = 5.0;

	public Camera(World3D world, double x, double y, double z, double zen, double azi, int width, int height) {
		super(world, new Ray3D(x, y, z, zen, azi));
		setFieldSize(width, height);
	}

	void resetScreen() {
		screenGraphics.setColor(Color.white);
		screenGraphics.fillRect(0, 0, getFieldWidth(), getFieldHeight());
		for (int i = 0; i < getFieldWidth(); i++) {
			for (int j = 0; j < getFieldHeight(); j++) {
				zraster[i][j] = Double.MAX_VALUE;
			}
		}
	}

	public void paintPoint(int x, int y, double z) {
		if (x < 0 || y < 0 || x >= getFieldWidth() || y >= getFieldHeight() || zraster[x][y] < z) {
			return;
		}
		zraster[x][y] = z;
		int r = (int) (z / 200.0 * 256);
		if (r > 255) {
			r = 255;
		}
		Color colorToUse = new Color(r, r, r);
		screen.setRGB(x, y, colorToUse.getRGB());
	}

	public void paintLine(int x1, int y1, double z1, int x2, int y2, double z2) {
		if (x1 < 0 && x2 < 0 || x1 >= getFieldWidth() && x2 >= getFieldWidth() || y1 < 0 && y2 < 0 || y1 >= getFieldHeight() && y2 >= getFieldHeight()) {
			return;
		}
		int xd = x2 - x1;
		int yd = y2 - y1;
		if (x1 < 0) {
			y1 += -xd / x1 * yd;
			x1 = 0;
		}
		if (y1 < 0) {
			x1 += -yd / y1 * xd;
			y1 = 0;
		}
//		if (x2 >= getFieldWidth()) {
//			y2 -= xd / (x2 - (getFieldWidth() - 1)) * yd;
//			x2 = getFieldWidth() - 1;
//		}
//		if (y2 >= getFieldHeight()) {
//			x2 -= yd / (y2 - (getFieldHeight() - 1)) * xd;
//			y2 = getFieldHeight() - 1;
//		}
		int w = x2 - x1;
		int h = y2 - y1;
		int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;
		if (w < 0) {
			dx1 = -1;
		} else if (w > 0) {
			dx1 = 1;
		}
		if (h < 0) {
			dy1 = -1;
		} else if (h > 0) {
			dy1 = 1;
		}
		if (w < 0) {
			dx2 = -1;
		} else if (w > 0) {
			dx2 = 1;
		}
		int longest = Math.abs(w);
		int shortest = Math.abs(h);
		if (longest <= shortest) {
			longest = Math.abs(h);
			shortest = Math.abs(w);
			if (h < 0) {
				dy2 = -1;
			} else if (h > 0) {
				dy2 = 1;
			}
			dx2 = 0;
		}
		int numerator = longest >> 1;
		double dz = (z2 - z1) / longest;
		for (int i = 0; i <= longest; i++) {
			for (int a = -3; a <= 3; a++) {
				paintPoint(x1 + a, y1, z1);
				paintPoint(x1 - a, y1, z1);
				paintPoint(x1, y1 + a, z1);
				paintPoint(x1, y1 - a, z1);
			}
			z1 += dz;
			numerator += shortest;
			if (numerator >= longest) {
				numerator -= longest;
				x1 += dx1;
				y1 += dy1;
			} else {
				x1 += dx2;
				y1 += dy2;
			}
		}
	}

	public double getDrawDistFor(Point3D p) {
		return p.z * this.zmult;
	}

	public double getDrawXFor(Point3D p) {
		return (p.x / getDrawDistFor(p)) * this.distmult + getFieldWidth() / 2;
	}

	public double getDrawYFor(Point3D p) {
		return (-p.y / getDrawDistFor(p)) * this.distmult + getFieldHeight() / 2;
	}

	public int getFieldHeight() {
		return screen.getHeight();
	}

	public int getFieldWidth() {
		return screen.getWidth();
	}

	public void setFieldSize(int width, int height) {
		screen = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		screenGraphics = (Graphics2D) screen.getGraphics();
		zraster = new double[width][height];
	}

	Image getScreen() {
		return screen;
	}
}