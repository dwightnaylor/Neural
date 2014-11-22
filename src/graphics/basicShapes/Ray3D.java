package graphics.basicShapes;

import graphics.fundamentals.Math3D;

/**
 * The immutable ray class. Internally represented as a Point3D with and added
 * dx,dy,dz (normalized to have a magnitude of 1). This class is intended to
 * serve as a vector, ray, line, etc, to be used for math and graphics.
 * 
 * @author Dwight Naylor
 * @since 11/13/14
 */
public class Ray3D extends Point3D {
	public static final Ray3D DEFAULT_RAY = new Ray3D(0, 0, 0, 0, 0, 1);
	public final double dx;
	public final double dy;
	public final double dz;

	// **********Constructors**********

	/**
	 * Constructs a ray, with the specified dx, dy, and dz.
	 */
	public Ray3D(double x, double y, double z, double dx, double dy, double dz) {
		super(x, y, z);
		double magnitude = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2) + Math.pow(dz, 2));
		if (Math3D.equal(0, magnitude)) {
			this.dx = 0;
			this.dy = 0;
			this.dz = 1;
		} else {
			this.dx = dx / magnitude;
			this.dy = dy / magnitude;
			this.dz = dz / magnitude;
		}
	}

	/**
	 * Constructs a ray at the first point, facing the second point.
	 */
	public Ray3D(Point3D point1, Point3D point2) {
		this(point1.x, point1.y, point1.z, point2.x - point1.x, point2.y - point1.y, point2.z - point1.z);
	}

	/**
	 * Constructs a ray at the first point, facing the second point.
	 */
	public Ray3D(Point3D point1, double zenith, double azimuth) {
		this(point1.x, point1.y, point1.z, zenith, azimuth);
	}

	// FIXME: Convert all angles for 3d stuff to radians (marked "// degrees")
	/**
	 * Constructs a ray, with the specified azimuth and zenith.
	 */
	public Ray3D(double x, double y, double z, double zenith, double azimuth) {
		// degrees
		this(x, y, z, calculateDx(zenith, azimuth), calculateDy(zenith, azimuth), calculateDz(zenith, azimuth));
	}

	// **********Book-keeping**********

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Ray3D)) {
			return false;
		}
		Ray3D ray = (Ray3D) object;
		return super.equals(ray) && parallelTo(ray);
	}

	public String toString() {
		return "Ray3D[x=" + x + "+" + dx + "t" + " , " + "y=" + y + "+" + dy + "t" + " , " + "z=" + z + "+" + dz + "t" + "]";
	}

	@Override
	public int hashCode() {
		return (int) (super.hashCode() * dx * dy * dz);
	}

	// **********Properties**********

	public Ray3D getNegative() {
		return new Ray3D(-x, -y, -z, -dx, -dy, -dz);
	}

	public Point3D getDirection() {
		return new Point3D(dx, dy, dz);
	}

	// degrees
	public double getZenith() {
		return Math.atan2(dy, Math.sqrt(Math.pow(dx, 2) + Math.pow(dz, 2)));
	}

	// degrees
	public double getAzimuth() {
		return Math.atan2(dx, dz);
	}

	public Ray3D getXZPlaneLine() {
		return new Ray3D(x, y, z, dz, 0, -dx);
	}

	public Point3D getPointAtDistance(double distance) {
		return new Point3D(x + distance * dx, y + distance * dy, z + distance * dz);
	}

	public Point3D getPointClosestTo(Point3D p) {
		return getPointAtDistance(subtractFrom(p).dotProduct(getDirection()));
	}

	public boolean parallelTo(Ray3D ray) {
		return Math3D.equal(dx, -ray.dx) && Math3D.equal(dy, -ray.dy) && Math3D.equal(dz, -ray.dz) || Math3D.equal(dx, ray.dx) && Math3D.equal(dy, ray.dy) && Math3D.equal(dz, ray.dz);
	}

	public Ray3D rotate(double deltaZenith, double deltaAzimuth) {
		return new Ray3D(x, y, z, getZenith() + deltaZenith, getAzimuth() + deltaAzimuth);
	}

	// **********Static**********

	public static double calculateDx(double zenith, double azimuth) {
		return Math.cos(zenith) * Math.sin(azimuth);
	}

	public static double calculateDz(double zenith, double azimuth) {
		return Math.cos(zenith) * Math.cos(azimuth);
	}

	public static double calculateDy(double zenith, double azimuth) {
		return Math.sin(zenith);
	}
}
