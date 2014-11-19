package graphics.junit;

import static org.junit.Assert.*;
import graphics.basicShapes.Point3D;
import graphics.basicShapes.Ray3D;
import graphics.fundamentals.Math3D;

import org.junit.Test;

public class Math3DTests {

	@Test
	public void testGetPointInNewCoordinates() {
		assertEquals(new Point3D(0, 0, 1), Math3D.getPointInNewCoordinates(new Ray3D(0, 0, 0, 0, 0, 1), new Point3D(0, 0, 1)));
		assertEquals(new Point3D(10, 10, 11), Math3D.getPointInNewCoordinates(new Ray3D(0, 0, 0, 0, 0, 1), new Point3D(10, 10, 11)));
		assertEquals(new Point3D(0, 0, Math.sqrt(3)), Math3D.getPointInNewCoordinates(new Ray3D(0, 0, 0, 1, 1, 1), new Point3D(1, 1, 1)));
		assertEquals(new Point3D(10,10, 10), Math3D.getPointInNewCoordinates(new Ray3D(10, 10, 10, 0, 0, 1), new Point3D(20, 20, 20)));
	}

}
