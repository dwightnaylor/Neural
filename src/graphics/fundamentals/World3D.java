package graphics.fundamentals;

import graphics.basicmodels.Model;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Hashtable;

public class World3D {
	public static final String MODEL_NAME = "MODEL_NAME";

	public ArrayList<Model> models = new ArrayList<Model>();
	public Hashtable<Model, Color> colors = new Hashtable<Model, Color>();
	public Hashtable<String, Model> names = new Hashtable<String, Model>();

	private boolean inFrame = false;

	public void draw(Graphics g, Camera c) {
		inFrame = true;
		c.resetScreen();
		for (Model model : models) {
			c.screenGraphics.setColor(colors.containsKey(model) ? colors.get(model) : Color.black);
			model.draw(c);
		}
		g.drawImage(c.getScreen(), 0, 0, null);
		inFrame = false;
	}

	public boolean isInFrame() {
		return inFrame;
	}
}