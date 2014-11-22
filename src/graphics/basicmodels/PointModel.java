package graphics.basicmodels;

import graphics.basicShapes.Point3D;
import graphics.basicShapes.Polygon3D;
import graphics.fundamentals.World3D;

public class PointModel extends Model {

	public PointModel(World3D w, double x, double y, double z) {
		this(w, new Point3D(x, y, z));
	}

	public PointModel(World3D w, Point3D point) {
		super(w);
		addPolygon(new Polygon3D(Point3D.ORIGIN));
		setLocation(point.x, point.y, point.z);
	}
}
