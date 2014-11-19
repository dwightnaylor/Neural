package graphics.junit;

import static org.junit.Assert.*;
import graphics.basicShapes.Point3D;
import graphics.basicShapes.Ray3D;
import graphics.fundamentals.Math3D;

import org.junit.Test;

public class Ray3DTests {

	static final double r2o2 = Math.sqrt(2) / 2;

	@Test
	public void testConstructors() {
		assertEquals(new Ray3D(0, 0, 0, 0, 0, 1), new Ray3D(0, 0, 0, 0, 0));
		assertEquals(new Ray3D(0, 0, 0, 1, 0, 0), new Ray3D(0, 0, 0, 0, 90));
		assertEquals(new Ray3D(0, 0, 0, 0, 0, -1), new Ray3D(0, 0, 0, 0, 180));
		assertEquals(new Ray3D(0, 0, 0, -1, 0, 0), new Ray3D(0, 0, 0, 0, 270));
		assertEquals(new Ray3D(0, 0, 0, r2o2, 0, r2o2), new Ray3D(0, 0, 0, 0, 45));
	}

	@Test
	public void testCalculateDx() {
		assertEquals(0, Ray3D.calculateDx(0, 0), Math3D.EPSILON);
		assertEquals(r2o2, Ray3D.calculateDx(0, 45), Math3D.EPSILON);
		assertEquals(1, Ray3D.calculateDx(0, 90), Math3D.EPSILON);
		assertEquals(r2o2, Ray3D.calculateDx(0, 135), Math3D.EPSILON);
		assertEquals(0, Ray3D.calculateDx(0, 180), Math3D.EPSILON);
		assertEquals(-r2o2, Ray3D.calculateDx(0, 225), Math3D.EPSILON);
		assertEquals(-1, Ray3D.calculateDx(0, 270), Math3D.EPSILON);
		assertEquals(-r2o2, Ray3D.calculateDx(0, 315), Math3D.EPSILON);
	}

	@Test
	public void testGetAzimuth() {
		// Test for special cases
		assertEquals(0, new Ray3D(0, 0, 0, 0, 1, 0).getAzimuth(), Math3D.EPSILON);
		assertEquals(0, new Ray3D(0, 0, 0, 0, -1, 0).getAzimuth(), Math3D.EPSILON);

		assertEquals(0, new Ray3D(0, 0, 0, 0, 0, 1).getAzimuth(), Math3D.EPSILON);
		assertEquals(45, new Ray3D(0, 0, 0, r2o2, 0, r2o2).getAzimuth(), Math3D.EPSILON);
		assertEquals(90, new Ray3D(0, 0, 0, 1, 0, 0).getAzimuth(), Math3D.EPSILON);
		assertEquals(135, new Ray3D(0, 0, 0, r2o2, 0, -r2o2).getAzimuth(), Math3D.EPSILON);
		assertEquals(180, new Ray3D(0, 0, 0, 0, 0, -1).getAzimuth(), Math3D.EPSILON);
		assertEquals(-135, new Ray3D(0, 0, 0, -r2o2, 0, -r2o2).getAzimuth(), Math3D.EPSILON);
		assertEquals(-90, new Ray3D(0, 0, 0, -1, 0, 0).getAzimuth(), Math3D.EPSILON);
		assertEquals(-45, new Ray3D(0, 0, 0, -r2o2, 0, r2o2).getAzimuth(), Math3D.EPSILON);
	}

	@Test
	public void testGetXZPlaneLine() {
		assertEquals(new Ray3D(0, 0, 0, 1, 0, 0), new Ray3D(0, 0, 0, 0, r2o2, r2o2).getXZPlaneLine());
	}

	@Test
	public void testGetPointClosestTo() {
		Ray3D ray = new Ray3D(0, 0, 0, 1, 0, 0);
		assertEquals(new Point3D(1, 0, 0), ray.getPointClosestTo(new Point3D(1, 1, 1)));
		assertEquals(new Point3D(124, 0, 0), ray.getPointClosestTo(new Point3D(124, 2134, 12143)));
		ray = new Ray3D(0, 0, 0, 0, 1, 0);
		assertEquals(new Point3D(0, 1, 0), ray.getPointClosestTo(new Point3D(1, 1, 1)));
		assertEquals(new Point3D(0, 2134, 0), ray.getPointClosestTo(new Point3D(124, 2134, 12143)));
		ray = new Ray3D(0, 0, 0, -1, -1, -1);
		assertEquals(new Point3D(-639, -639, -639), ray.getPointClosestTo(new Point3D(-123, -452, -1342)));
	}
}
