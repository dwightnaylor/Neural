package graphics.basicmodels;

import graphics.fundamentals.Camera;
import graphics.fundamentals.World3D;

public class LineModel extends Model {

	private Model a;
	private Model b;
	private double width;

	public LineModel(World3D w, Model a, Model b) {
		super(w);
		this.a = a;
		this.b = b;
	}

	public LineModel(World3D w, Model a, Model b, double width) {
		super(w);
		this.a = a;
		this.b = b;
		this.width = width;
	}

	@Override
	public void draw(Camera c) {
		super.draw(c);
		c.drawLineForCameraPoints(a.getTransform().getCameraPoint(c), b.getTransform().getCameraPoint(c), getWidth(), getColor());
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}
}
