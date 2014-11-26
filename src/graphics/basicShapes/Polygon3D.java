package graphics.basicShapes;

import graphics.fundamentals.Camera;
import graphics.fundamentals.Math3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Immutable 3d polygon class. To perform transforms, the model that uses the
 * polygon should be manipulated.
 * 
 * @author Dwight Naylor
 * @since 11/13/14
 */
public class Polygon3D {

	private final List<Point3D> points;

	// TODO: Make this only accept triangles. Any polygon can be made of
	// triangles. Also makes math easy.

	// **********Constructors**********

	public Polygon3D(Point3D... pointArray) {
		points = Collections.unmodifiableList(Arrays.asList(pointArray));
	}

	public Polygon3D(List<Point3D> pointList) {
		points = Collections.unmodifiableList(pointList);
	}

	// **********Transforms**********

	public Polygon3D transform(Ray3D transform) {
		ArrayList<Point3D> newPoints = new ArrayList<Point3D>();
		for (Point3D point : points) {
			newPoints.add(Math3D.getPointInNewCoordinates(Ray3D.DEFAULT_RAY.rotate(-transform.getZenith(), -transform.getAzimuth()), point).addTo(transform));
		}
		return new Polygon3D(newPoints);
	}

	// **********Graphics**********

	public void draw(Camera camera) {
		if (points.size() == 3) { // Fills triangles
			fill(camera);
			return;
		}

		Point3D[] cameraPoints = new Point3D[points.size()];
		for (int i = 0; i < cameraPoints.length; i++) {
			cameraPoints[i] = points.get(i).getCameraPoint(camera);
		}
		for (int i = 0; i < points.size(); i++) {
			camera.paintLineForCameraPoints(cameraPoints[(i) % points.size()], cameraPoints[(i + 1) % points.size()]);
		}
	}

	// This only works with triangles.
	public void fill(Camera c) {
		if (shouldFill(c)) {
			Point3D[] cPoints = new Point3D[points.size()];
			for (int i = 0; i < cPoints.length; i++) {
				cPoints[i] = points.get(i).getCameraPoint(c);
			}
			c.fillTriangleForCameraPoints(cPoints[0], cPoints[1], cPoints[2]);
			// g.fill(new Polygon(px, py, px.length));TODO:fix this
		}
	}

	private boolean shouldFill(Camera c) {
		for (int i = 0; i < this.points.size(); i++) {
			if (c.getDrawDistFor(points.get(i).getCameraPoint(c)) < 0) {
				return false;
			}
		}
		return true;
	}
}
