package graphics.fundamentals;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import graphics.basicShapes.Point3D;
import graphics.basicShapes.Ray3D;
import graphics.basicmodels.RayModel;
import helpers.MathHelper;

public class Camera extends RayModel {

	private double[][] zraster;
	private BufferedImage screen;
	public Graphics2D screenGraphics;

	public double zmult = 0.01;
	private double distMult = 5.0;

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

	public void drawPoint(int x, int y, double z, Color color) {
		if (x < 0 || y < 0 || x >= getFieldWidth() || y >= getFieldHeight() || zraster[x][y] < z) {
			return;
		}
		zraster[x][y] = z;

		Color colorToUse = color == null ? Color.black : color;
		// double scale = 1 / (z * zmult * 0.05 + 1);
		// double r = 200 - (colorToUse.getRed() + (200 - colorToUse.getRed()) *
		// scale);
		// double g = 200 - (colorToUse.getGreen() + (200 -
		// colorToUse.getGreen()) * scale);
		// double b = 200 - (colorToUse.getBlue() + (200 - colorToUse.getBlue())
		// * scale);
		// Color finalColor = new Color((int) r, (int) g, (int) b);
		Color finalColor = colorToUse;
		screen.setRGB(x, y, finalColor.getRGB());
	}

	public void fillTriangleForCameraPoints(Point3D p1c, Point3D p2c, Point3D p3c, Color color) {
		fillTriangle(p1c.getDirectDrawPoint(this), p2c.getDirectDrawPoint(this), p3c.getDirectDrawPoint(this), color);
	}

	public void fillTriangle(Point3D p1c, Point3D p2c, Point3D p3c, Color color) {
		double minX = MathHelper.min(p1c.x, p2c.x, p3c.x, getFieldWidth());
		double minY = MathHelper.min(p1c.y, p2c.y, p3c.y, getFieldHeight());
		double maxX = MathHelper.max(p1c.x, p2c.x, p3c.x, 0);
		double maxY = MathHelper.max(p1c.y, p2c.y, p3c.y, 0);

		for (int i = (int) minX - 1; i < maxX + 1; i++) {
			for (int j = (int) minY - 1; j < maxY + 1; j++) {
				if (!Math3D.pointIsInTriangle(i, j, p1c, p2c, p3c))
					continue;
				drawPoint(i, j, Math3D.zVal(i, j, p1c, p2c, p3c), color);
			}
		}
	}

	public void drawLineForCameraPoints(Point3D p1c, Point3D p2c, double width, Color color) {
		if (p1c.z > 0 || p2c.z > 0) {
			if (p2c.z <= 0) {
				p2c = p1c.getPointPartwayTo(p2c, (p1c.z - 0.01) / (p2c.z - p1c.z));
			} else if (p1c.z <= 0) {
				p1c = p2c.getPointPartwayTo(p1c, (p2c.z - 0.01) / (p1c.z - p2c.z));
			}

			Point3D p1d = p1c.getDirectDrawPoint(this);
			Point3D p2d = p2c.getDirectDrawPoint(this);
			drawLine((int) p1d.x, (int) p1d.y, p1c.z, (int) p2d.x, (int) p2d.y, p2c.z, width, color);
		}
	}

	public void drawLine(int x1, int y1, double z1, int x2, int y2, double z2, double width, Color color) {
		if (x1 < 0 && x2 < 0 || x1 >= getFieldWidth() && x2 >= getFieldWidth() || y1 < 0 && y2 < 0 || y1 >= getFieldHeight() && y2 >= getFieldHeight()) {
			return;
		}
		int xd = x2 - x1;
		int yd = y2 - y1;
		if (x1 < 0) {
			y1 -= (double) x1 * yd / xd;
			x1 = 0;
		}
		if (x2 < 0) {
			y2 -= (double) x2 * yd / xd;
			x2 = 0;
		}
		if (y1 < 0) {
			x1 -= (double) y1 * xd / yd;
			y1 = 0;
		}
		if (y2 < 0) {
			x2 -= (double) y2 * xd / yd;
			y2 = 0;
		}
		if (x1 >= getFieldWidth()) {
			y1 -= (double) (x1 - getFieldWidth() + 1) * yd / xd;
			x1 = getFieldWidth() - 1;
		}
		if (x2 >= getFieldWidth()) {
			y2 -= (double) (x2 - getFieldWidth() + 1) * yd / xd;
			x2 = getFieldWidth() - 1;
		}
		if (y1 >= getFieldHeight()) {
			x1 -= (double) (y1 - getFieldHeight() + 1) * xd / yd;
			y1 = getFieldHeight() - 1;
		}
		if (y2 >= getFieldHeight()) {
			x2 -= (double) (y2 - getFieldHeight() + 1) * xd / yd;
			y2 = getFieldHeight() - 1;
		}
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
		boolean xm = longest > shortest;
		double d = Math.abs(width / Math.cos(Math.atan2(h, w)));
		if (longest <= shortest) {
			longest = Math.abs(h);
			shortest = Math.abs(w);
			d = Math.abs(width / Math.cos(Math.atan2(w, h)));
			if (h < 0) {
				dy2 = -1;
			} else if (h > 0) {
				dy2 = 1;
			}
			dx2 = 0;
		}
		d = (int) (d + 0.5);
		int numerator = longest >> 1;
		double dz = (z2 - z1) / longest;
		for (int i = 0; i <= longest; i++) {
			if (x1 >= getFieldWidth() || y1 >= getFieldHeight() || x1 < 0 || y1 < 0) {
				return;
			}
			if (xm) {
				for (int j = 1; j <= d; j++) {
					drawPoint(x1, y1 - j, z1, color);
					drawPoint(x1, y1 + j, z1, color);
				}
			} else {
				for (int j = 1; j <= d; j++) {
					drawPoint(x1 - j, y1, z1, color);
					drawPoint(x1 + j, y1, z1, color);
				}
			}
			drawPoint(x1, y1, z1, color);
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

	public double getScaledWidth(double width, double distance) {
		return distance * zmult * distMult;
	}

	public double getDrawDistFor(Point3D p) {
		return p.z * this.zmult;
	}

	public double getDrawXFor(Point3D p) {
		return (p.x / getDrawDistFor(p)) * this.distMult + getFieldWidth() / 2;
	}

	public double getDrawYFor(Point3D p) {
		return (-p.y / getDrawDistFor(p)) * this.distMult + getFieldHeight() / 2;
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