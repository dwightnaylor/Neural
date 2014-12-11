package graphics.fundamentals;

import graphics.basicShapes.*;

public class Math3D {
	public static final double EPSILON = 0.0000001;

	public static boolean equal(double v1, double v2) {
		return Math.abs(v1 - v2) < EPSILON;
	}

	public static Point3D getPointInNewCoordinates(Ray3D orig, Point3D p) {
		if (orig.equals(p)) {
			return p;
		}
		Point3D newX = orig.getXZPlaneLine().getDirection();
		Point3D newY = orig.getDirection().crossProduct(newX);
		Point3D newZ = orig.getDirection();
		Point3D subtracted = orig.subtractFrom(p);
		return new Point3D(newX.dotProduct(subtracted), newY.dotProduct(subtracted), newZ.dotProduct(subtracted));
	}

	// Probably should recheck math
	// What the fucking fuck is this shit holy fucking shit -DN
	public static double zVal(int x, int y, Point3D p1, Point3D p2, Point3D p3) {
		double x1 = p1.x, x2 = p2.x, x3 = p3.x;
		double y1 = p1.y, y2 = p2.y, y3 = p3.y;
		double z1 = p1.z, z2 = p2.z, z3 = p3.z;
		return (z3 * (x - x1) * (y - y2) + z1 * (x - x2) * (y - y3) + z2 * (x - x3) * (y - y1) - z2 * (x - x1) * (y - y3) - z3 * (x - x2) * (y - y1) - z1 * (x - x3) * (y - y2)) / ((x - x1) * (y - y2) + (x - x2) * (y - y3) + (x - x3) * (y - y1) - (x - x1) * (y - y3) - (x - x2) * (y - y1) - (x - x3) * (y - y2));
	}

	// Found at:
	// http://stackoverflow.com/questions/2049582/how-to-determine-a-point-in-a-triangle
	// Apparently answer 2 is more efficient
	public static double sign(double x, double y, Point3D p2, Point3D p3) {
		return (x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (y - p3.y);
	}

	public static boolean pointIsInTriangle(double x, double y, Point3D p1, Point3D p2, Point3D p3) {
		boolean b1, b2, b3;

		b1 = sign(x, y, p1, p2) < 0;
		b2 = sign(x, y, p2, p3) < 0;
		b3 = sign(x, y, p3, p1) < 0;

		return ((b1 == b2) && (b2 == b3));
	}
}
