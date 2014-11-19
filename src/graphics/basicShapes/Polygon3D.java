package graphics.basicShapes;

import graphics.fundamentals.Camera;
import graphics.fundamentals.Math3D;

import java.awt.Point;
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
		Point3D[] cameraPoints = new Point3D[points.size()];
		for (int i = 0; i < cameraPoints.length; i++) {
			cameraPoints[i] = points.get(i).getCameraPoint(camera);
		}
		for (int i = 0; i < points.size(); i++) {
			Point3D p1c = cameraPoints[(i) % points.size()];
			Point3D p2c = cameraPoints[(i + 1) % points.size()];
			if (p1c.z > 0 || p2c.z > 0) {
				if (p2c.z <= 0) {
					p2c = p1c.getPointPartwayTo(p2c, (p1c.z - 0.01) / (p2c.z - p1c.z));
				} else if (p1c.z <= 0) {
					p1c = p2c.getPointPartwayTo(p1c, (p2c.z - 0.01) / (p1c.z - p2c.z));
				}
				Point p1d = p1c.getDirectDrawPoint(camera);
				Point p2d = p2c.getDirectDrawPoint(camera);
				// TODO: Fix
				// camera.screenGraphics.setColor(Color.black);
				camera.paintLine(p1d.x, p1d.y, p1c.z, p2d.x, p2d.y, p2c.z);
//				camera.screenGraphics.drawLine(p1d.x, p1d.y, p2d.x, p2d.y);
			}
		}
	}

	public void fill(Camera c) {
		if (shouldFill(c)) {
			int[] px = new int[points.size()];
			int[] py = new int[points.size()];
			for (int i = 0; i < px.length; i++) {
				Point dpf = points.get(i).getDrawPoint(c);
				px[i] = dpf.x;
				py[i] = dpf.y;
			}
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
