package graphics.basicmodels;

import graphics.basicShapes.Point3D;
import graphics.basicShapes.Polygon3D;
import graphics.fundamentals.World3D;

import java.util.ArrayList;

public class Rectangle3D extends Model {

	public Rectangle3D(World3D w, double x, double y, double z, double xs) {
		this(w, x, y, z, xs, xs, xs);
	}

	public Rectangle3D(World3D w, double x, double y, double z, double xs, double ys, double zs) {
		super(w);
		setLocation(x, y, z);
		ArrayList<Point3D> topp = new ArrayList<Point3D>();
		topp.add(new Point3D(xs / 2, ys / 2, zs / 2));
		topp.add(new Point3D(-xs / 2, ys / 2, zs / 2));
		topp.add(new Point3D(-xs / 2, ys / 2, -zs / 2));
		topp.add(new Point3D(xs / 2, ys / 2, -zs / 2));
		addPolygon(new Polygon3D(topp));
		ArrayList<Point3D> bottomp = new ArrayList<Point3D>();
		bottomp.add(new Point3D(xs / 2, -ys / 2, zs / 2));
		bottomp.add(new Point3D(-xs / 2, -ys / 2, zs / 2));
		bottomp.add(new Point3D(-xs / 2, -ys / 2, -zs / 2));
		bottomp.add(new Point3D(xs / 2, -ys / 2, -zs / 2));
		addPolygon(new Polygon3D(bottomp));
		ArrayList<Point3D> leftp = new ArrayList<Point3D>();
		leftp.add(new Point3D(-xs / 2, ys / 2, zs / 2));
		leftp.add(new Point3D(-xs / 2, -ys / 2, zs / 2));
		leftp.add(new Point3D(-xs / 2, -ys / 2, -zs / 2));
		leftp.add(new Point3D(-xs / 2, ys / 2, -zs / 2));
		addPolygon(new Polygon3D(leftp));
		ArrayList<Point3D> rightp = new ArrayList<Point3D>();
		rightp.add(new Point3D(xs / 2, ys / 2, zs / 2));
		rightp.add(new Point3D(xs / 2, -ys / 2, zs / 2));
		rightp.add(new Point3D(xs / 2, -ys / 2, -zs / 2));
		rightp.add(new Point3D(xs / 2, ys / 2, -zs / 2));
		addPolygon(new Polygon3D(rightp));
	}
}
