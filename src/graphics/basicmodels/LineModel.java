package graphics.basicmodels;

import graphics.fundamentals.Camera;
import graphics.fundamentals.World3D;

public class LineModel extends Model {

	private Model a;
	private Model b;

	public LineModel(World3D w, Model a, Model b) {
		super(w);
		this.a = a;
		this.b = b;
	}

	@Override
	public void draw(Camera c) {
		super.draw(c);
		c.paintLineForCameraPoints(a.getTransform().getCameraPoint(c), b.getTransform().getCameraPoint(c));
	}
}
