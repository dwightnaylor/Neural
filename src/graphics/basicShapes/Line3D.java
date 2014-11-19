package graphics.basicShapes;

public class Line3D {

	private Point3D pointA;
	private Point3D pointB;

	public Line3D() {
	}

	public Line3D(Point3D a, Point3D b) {
		this.setPointA(a);
		this.setPointB(b);
	}

	public double getDistanceTo(Point3D point) {
		double t = pointA.subtractFrom(point).dotProduct(pointB.subtractFrom(point)) / pointB.subtractFrom(pointA).distance(new Point3D(0, 0, 0));
		if (t < 0) {
			return pointA.distance(point);
		}
		if (t > 1) {
			return pointB.distance(point);
		}
		return pointA.addTo(pointB.subtractFrom(pointA).multiply(t)).distance(point);
	}

	public void setPointA(Point3D pointA) {
		this.pointA = pointA;
	}

	public Point3D getPointA() {
		return pointA;
	}

	public void setPointB(Point3D pointB) {
		this.pointB = pointB;
	}

	public Point3D getPointB() {
		return pointB;
	}
}
