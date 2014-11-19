package graphics.basicmodels;

import graphics.basicShapes.Point3D;
import graphics.basicShapes.Polygon3D;
import graphics.fundamentals.World3D;

public class LineModel extends Model {

	public LineModel(World3D w, Point3D one, Point3D two) {
		super(w);
		addPolygon((new Polygon3D(one, two)));
	}

}
