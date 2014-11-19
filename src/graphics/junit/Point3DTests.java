package graphics.junit;

import static org.junit.Assert.*;
import static graphics.junit.Ray3DTests.*;
import graphics.basicShapes.Point3D;
import graphics.basicShapes.Ray3D;
import graphics.fundamentals.Camera;
import graphics.fundamentals.World3D;

import org.junit.Test;

public class Point3DTests {

	World3D world = new World3D();
	Camera camera = new Camera(world, 0, 0, 0, 0, 0, 400, 400);

	@Test
	public void testGetCameraPoint() {
		Ray3D transformSave = camera.getTransform();

		assertEquals(new Point3D(0, 0, 1), new Point3D(0, 0, 1).getCameraPoint(camera));
		camera.rotate(0, 45);
		assertEquals(new Point3D(1, 1, 1), new Point3D(r2o2 * 2, 1, 0).getCameraPoint(camera));
		assertEquals(new Point3D(-1, -1, -1), new Point3D(-r2o2 * 2, -1, 0).getCameraPoint(camera));
		assertEquals(new Point3D(-r2o2, 0, r2o2), new Point3D(0, 0, 1).getCameraPoint(camera));
		camera.setTransform(new Ray3D(0, 0, 0, 1, 1, 1));
		assertEquals(new Point3D(-r2o2, 0, r2o2), new Point3D(0, 0, 1).getCameraPoint(camera));
		camera.setTransform(new Ray3D(0, 0, 0, 1, 0, 0));
		assertEquals(new Point3D(-1, 0, 0), new Point3D(0, 0, 1).getCameraPoint(camera));

		camera.setTransform(transformSave);
	}

}
