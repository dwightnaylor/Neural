package graphics.basicmodels;

import graphics.basicShapes.Point3D;
import graphics.basicShapes.Polygon3D;
import graphics.basicShapes.Ray3D;
import graphics.fundamentals.Camera;
import graphics.fundamentals.World3D;

import java.awt.Color;
import java.util.ArrayList;

public class Model implements Comparable<Model> {

	public ArrayList<Polygon3D> polygons;
	private Ray3D transform;
	private World3D world;

	private Color color;

	public Model() {
		polygons = new ArrayList<Polygon3D>();
		transform = Ray3D.DEFAULT_RAY;
	}

	public Model(World3D world) {
		polygons = new ArrayList<Polygon3D>();
		this.setWorld(world);
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

	public void draw(Camera camera) {
		for (int i = 0; i < polygons.size(); i++) {
			polygons.get(i).transform(transform).draw(camera, color);
		}
	}

	@Override
	public int compareTo(Model model) {
		return ((Integer) hashCode()).compareTo(model.hashCode());
	}

	public World3D getWorld() {
		return world;
	}

	public void setWorld(World3D world) {
		this.world = world;
		if (world != null) {
			world.models.add(this);
		}
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

}
