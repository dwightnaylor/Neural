package graphics.basicmodels;

import graphics.basicShapes.Point3D;
import graphics.basicShapes.Polygon3D;
import graphics.fundamentals.World3D;

import java.util.ArrayList;

public class TriangleModel extends Model {

	public TriangleModel(World3D w,  Point3D p1, Point3D p2, Point3D p3) {
		super(w);
		setLocation(p1.x, p1.y, p1.z);
		ArrayList<Point3D> t1 = new ArrayList<Point3D>();
		t1.add(p1);
		t1.add(p2);
		t1.add(p3);
		addPolygon(new Polygon3D(t1));
	}
	public TriangleModel(World3D w,  double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3) {
		super(w);
		//setLocation appears to be wrong
		setLocation((x1+x2+x3)/3, (y1+y2+y3)/3, (z1+z2+z3)/3);
		ArrayList<Point3D> t1 = new ArrayList<Point3D>();
		t1.add(new Point3D(x1,y1,z1));
		t1.add(new Point3D(x2,y2,z2));
		t1.add(new Point3D(x3,y3,z3));
		addPolygon(new Polygon3D(t1));
	}
}
