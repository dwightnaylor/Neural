package graphics.basicShapes;

import java.awt.Color;

import graphics.fundamentals.Camera;
import graphics.fundamentals.Math3D;

/**
 * The immutable Point3D class.
 * 
 * @author Dwight Naylor
 * @since 11/13/14
 */
public class Point3D {
	public static final Point3D ORIGIN = new Point3D(0, 0, 0);
	public final double x;
	public final double y;
	public final double z;

	// **********Constructors**********

	public Point3D() {
		x = 0;
		y = 0;
		z = 0;
	}

	public Point3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Point3D(Point3D p) {
		this(p.x, p.y, p.z);
	}

	// **********Book-keeping**********

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Point3D)) {
			return false;
		}
		Point3D point = (Point3D) object;
		return Math3D.equal(x, point.x) && Math3D.equal(y, point.y) && Math3D.equal(z, point.z);
	}

	@Override
	public int hashCode() {
		return (int) (x * y * z);
	}

	public final Point3D clone() {
		return new Point3D(x, y, z);
	}

	public String toString() {
		return "Point3D[" + x + "," + y + "," + z + "]";
	}

	// **********Properties**********

	public Point3D crossProduct(Point3D p) {
		return new Point3D(y * p.z - p.y * z, p.x * z - x * p.z, x * p.y - p.x * y);
	}

	public final double dotProduct(Point3D p) {
		return p.x * x + p.y * y + p.z * z;
	}

	public final double getMagnitude() {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
	}

	public final double distance(Point3D p) {
		return Math.sqrt(Math.pow(x - p.x, 2) + Math.pow(y - p.y, 2) + Math.pow(z - p.z, 2));
	}

	// **********Transforms**********

	public final Point3D addTo(Point3D p) {
		return new Point3D(p.x + x, p.y + y, p.z + z);
	}

	public final Point3D subtractFrom(Point3D p) {
		return new Point3D(p.x - x, p.y - y, p.z - z);
	}

	public final Point3D multiply(double t) {
		return new Point3D(x * t, y * t, z * t);
	}

	public final Point3D divide(double t) {
		return multiply(1.0 / t);
	}

	public final Point3D scale(double magnitude) {
		return multiply(magnitude / getMagnitude());
	}

	public final Point3D getPointPartwayTo(Point3D target, double distanceRatio) {
		return this.addTo(target.subtractFrom(this).multiply(distanceRatio));
	}

	// **********Graphics**********

	public final Point3D getDrawPoint(Camera c) {
		return getCameraPoint(c).getDirectDrawPoint(c);
	}

	public final Point3D getDirectDrawPoint(Camera c) {
		return new Point3D((int) (c.getDrawXFor(this) + 0.5), (int) (c.getDrawYFor(this) + 0.5), z);
	}

	public final Point3D getCameraPoint(Camera camera) {
		return Math3D.getPointInNewCoordinates(camera.getTransform(), this);
	}

	public void draw(Camera c, Color color) {
		Point3D cameraPoint = getCameraPoint(c);
		Point3D drawPoint = cameraPoint.getDirectDrawPoint(c);
		c.drawPoint((int) drawPoint.x, (int) drawPoint.y, cameraPoint.z, color);
	}
}
