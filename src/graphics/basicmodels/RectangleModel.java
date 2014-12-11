package graphics.basicmodels;

import graphics.basicShapes.Point3D;
import graphics.basicShapes.Polygon3D;
import graphics.fundamentals.World3D;

import java.util.ArrayList;

public class RectangleModel extends Model {

	public RectangleModel(World3D w, double x, double y, double z, double xs) {
		this(w, x, y, z, xs, xs, xs);
	}

	public RectangleModel(World3D w, double x, double y, double z, double xs, double ys, double zs) {
		super(w);
		setLocation(x, y, z);
//		Point3D[] p = new Point3D[8];
//		int n = 0;
//		for (int i = -1; i <= 1; i += 2) {
//			for (int j = -1; j <= 1; j += 2) {
//				for (int k = -1; k <= 1; k += 2) {
//					p[n] = new Point3D(xs / 2 * i, ys / 2 * j, zs / 2 * k);
//					n++;
//				}
//			}
//		}
//		ArrayList<Point3D> a = new ArrayList<Point3D>();
//		a.add(p[0]);
//		a.add(p[3]);
//		a.add(p[6]);
//		addPolygon(new Polygon3D(a));
//		ArrayList<Point3D> b = new ArrayList<Point3D>();
//		b.add(p[1]);
//		b.add(p[4]);
//		b.add(p[7]);
//		addPolygon(new Polygon3D(b));
//		ArrayList<Point3D> c = new ArrayList<Point3D>();
//		c.add(p[2]);
//		c.add(p[5]);
//		c.add(p[6]);
//		addPolygon(new Polygon3D(c));

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
