package graphics.basicShapes;

import graphics.fundamentals.Camera;
import graphics.fundamentals.Math3D;

import java.awt.Color;
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
public class Triangle3D {

	private final List<Point3D> points;

	// **********Constructors**********

	public Triangle3D(Point3D p1, Point3D p2, Point3D p3) {
		Point3D[] pointArray = { p1, p2, p3 };
		points = Collections.unmodifiableList(Arrays.asList(pointArray));
	}

	public Triangle3D(List<Point3D> pointList) {
		if (pointList.size() != 3) {
			System.err.println("wrongPointSize");
		}
		points = Collections.unmodifiableList(pointList);
	}

	// **********Transforms**********

	public Triangle3D transform(Ray3D transform) {
		ArrayList<Point3D> newPoints = new ArrayList<Point3D>();
		for (Point3D point : points) {
			newPoints.add(Math3D.getPointInNewCoordinates(Ray3D.DEFAULT_RAY.rotate(-transform.getZenith(), -transform.getAzimuth()), point).addTo(transform));
		}
		return new Triangle3D(newPoints);
	}

	// **********Graphics**********

	public void draw(Camera camera, Color color) {
		if (points.size() == 3) {
			fill(camera, color);
			return;
		}

		Point3D[] cameraPoints = new Point3D[points.size()];
		for (int i = 0; i < cameraPoints.length; i++) {
			cameraPoints[i] = points.get(i).getCameraPoint(camera);
		}
		for (int i = 0; i < points.size(); i++) {
			camera.drawLineForCameraPoints(cameraPoints[(i) % points.size()], cameraPoints[(i + 1) % points.size()], 1, color);
		}
	}

	public void fill(Camera c, Color color) {
		if (shouldFill(c)) {
			Point3D[] cPoints = new Point3D[points.size()];
			for (int i = 0; i < cPoints.length; i++) {
				cPoints[i] = points.get(i).getCameraPoint(c);
			}
			c.fillTriangleForCameraPoints(cPoints[0], cPoints[1], cPoints[2], color);
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
