package graphics.fundamentals;

import graphics.basicmodels.Model;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class World3D {
	public ArrayList<Model> models = new ArrayList<Model>();
	private ArrayList<Color> colors = new ArrayList<Color>();

	public void paint(Graphics g, Camera c) {
		c.resetScreen();
		for (int i = 0; i < models.size(); i++) {
			if (colors.size() <= i) {
				colors.add(new Color((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256)));
			}
			c.screenGraphics.setColor(colors.get(i));
			models.get(i).draw(c);
		}
		g.drawImage(c.getScreen(), 0, 0, null);
	}
}