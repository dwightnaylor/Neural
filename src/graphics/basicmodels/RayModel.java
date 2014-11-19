package graphics.basicmodels;

import graphics.basicShapes.Polygon3D;
import graphics.basicShapes.Ray3D;
import graphics.fundamentals.World3D;

public class RayModel extends Model {

	public RayModel(World3D w, Ray3D ray) {
		super(w);
		setTransform(ray);
		addPolygon((new Polygon3D(ray)));
	}

//	// TODO: Make ray draw and model properly...
//	@Override
//	public void draw(Camera c) {
//		super.draw(c);
//	}
}
