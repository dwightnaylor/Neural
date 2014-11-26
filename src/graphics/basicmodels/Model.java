package graphics.basicmodels;

import graphics.basicShapes.Point3D;
import graphics.basicShapes.Polygon3D;
import graphics.basicShapes.Ray3D;
import graphics.fundamentals.Camera;
import graphics.fundamentals.World3D;

import java.util.ArrayList;

public class Model implements Comparable<Model> {

	public ArrayList<Polygon3D> polygons;
	private Ray3D transform;
	World3D w;

	public Model(World3D w) {
		polygons = new ArrayList<Polygon3D>();
		this.w = w;
		w.models.add(this);
		transform = Ray3D.DEFAULT_RAY;
	}

	// **********Accessors**********

	public Point3D getLocation() {
		return getTransform();
	}

	public double getZenith() {
		return getTransform().getZenith();
	}

	public double getAzimuth() {
		return getTransform().getAzimuth();
	}

	public Ray3D getTransform() {
		return transform;
	}

	// **********Modifiers**********

	public void setZenith(double zenith) {
		transform = new Ray3D(transform, zenith, transform.getAzimuth());
	}

	public void setAzimuth(double azimuth) {
		transform = new Ray3D(transform, transform.getZenith(), azimuth);
	}

	public void translate(double dx, double dy, double dz) {
		setLocation(transform.x + dx, transform.y + dy, transform.z + dz);
	}

	public void setTransform(Ray3D transform) {
		this.transform = transform;
	}

	public void rotate(double deltaZenith, double deltaAzimuth) {
		setTransform(transform.rotate(deltaZenith, deltaAzimuth));
	}

	public void addPolygon(Polygon3D poly) {
		polygons.add(poly);
	}

	public void setLocation(double x, double y, double z) {
		setTransform(new Ray3D(x, y, z, transform.dx, transform.dy, transform.dz));
	}

	public void setLocation(Point3D location) {
		setLocation(location.x, location.y, location.z);
	}

	// **********Graphics**********

	public void draw(Camera c) {
		for (int i = 0; i < polygons.size(); i++) {
			polygons.get(i).transform(transform).draw(c);
		}
	}

	@Override
	public int compareTo(Model o) {
		return ((Integer) hashCode()).compareTo(o.hashCode());
	}

}
