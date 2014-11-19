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
}
