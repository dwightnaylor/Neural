package graphics.testing;

import graphics.basicShapes.Point3D;
import graphics.basicmodels.LineModel;
import graphics.basicmodels.Model;
import graphics.basicmodels.PointModel;
import graphics.fundamentals.WorldFrame;
import helpers.MathHelper;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class PointSpacer extends WorldFrame {

	private static boolean started = false;

	public static void main(String[] args) {
		PointSpacer frame = new PointSpacer(400, 400, 20);
		frame.setVisible(true);
		while (true) {
			frame.repaint();
		}
	}

	public static final int DIFF = 40;
	HashSet<Point> ties = new HashSet<Point>();
	Hashtable<Point, Model> tieModels = new Hashtable<Point, Model>();
	Model[] points;
	Point3D[] velocities;
	double desiredDistance = 10;
	double velocityMultiplier = 100;

	PointModel selectedPoint;
	private int selectedIndex;
	ArrayList<Double> velocitySums = new ArrayList<Double>();
	private JFrame graphFrame;

	public PointSpacer(final int width, final int height, final int displayDelay) {
		super(width, height);
		makeThings();
		graphFrame = new JFrame();
		graphFrame.setLocation(getX() + getWidth(), 0);
		graphFrame.setSize(500, 500);
		graphFrame.setContentPane(new Container() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				double rm = 0;
				for (int i = 0; i < velocitySums.size(); i++) {
					rm = Math.max(rm, velocitySums.get(i));
				}
				g.setColor(Color.red);
				for (int i = 0; i < velocitySums.size(); i++) {
					int x = (int) ((double) i / velocitySums.size() * getWidth());
					g.drawLine(x, getHeight() - 1, x, getHeight() - 1 - (int) (velocitySums.get(i) / rm * getHeight()));
				}
			}
		});
		getCamera().setLocation(getCamera().getTransform().x, getCamera().getTransform().y, -1000);
		moveSpeed = 100;
	}

	@Override
	public void setVisible(boolean b) {
		graphFrame.setVisible(b);
		super.setVisible(b);
	}

	protected void processKeyPresses() {
		if (keyPressBuffer[KeyEvent.VK_ENTER]) {
			started = true;
		}
		super.processKeyPresses();
	}

	@Override
	public void doPainting(Graphics g) {

		if (selectedPoint != null) {
			Point3D cameraPoint = selectedPoint.getLocation().getCameraPoint(getCamera());
			if (cameraPoint.z > 0) {
				Point drawPoint = cameraPoint.getDirectDrawPoint(getCamera());
				double sx = MathHelper.round(selectedPoint.getLocation().x, 3);
				double sy = MathHelper.round(selectedPoint.getLocation().y, 3);
				double sz = MathHelper.round(selectedPoint.getLocation().z, 3);
				g.setColor(Color.red);
				g.drawString(selectedIndex + ":[" + sx + "," + sy + "," + sz + "]", drawPoint.x, drawPoint.y);
			}
		}

		if (started) {
			// if (Math.random() < 0.9) {
			// if (ties.size() > 0) {
			// Iterator<Point> iterator = ties.iterator();
			// int j = (int) (Math.random() * (ties.size() - 1));
			// for (int i = 0; i < j; i++) {
			// iterator.next();
			// }
			// Point next = iterator.next();
			// untie(next.x, next.y);
			// }
			// }
			// tie((int) (Math.random() * points.length), (int) (Math.random() *
			// points.length));

			for (int i = 0; i < points.length; i++) {
				for (int j = 0; j < points.length; j++) {
					if (i != j) {
						Point3D subtract = points[i].getLocation().subtractFrom(points[j].getLocation());
						subtract = subtract.scale(-Math.min(10, 5000 * velocityMultiplier / Math.pow(points[i].getLocation().distance(points[j].getLocation()), 2)));
						velocities[i] = velocities[i].addTo(new Point3D(subtract.x, subtract.y, subtract.z));
					}
				}
			}
			for (Point tie : ties) {
				Point3D subtract = points[tie.x].getLocation().subtractFrom(points[tie.y].getLocation());
				double dist = points[tie.x].getLocation().distance(points[tie.y].getLocation());
				subtract = subtract.scale(Math.min(100, (0.001) * velocityMultiplier * dist));
				if (dist < desiredDistance) {
					subtract.scale(-1);
				}
				velocities[tie.x] = velocities[tie.x].addTo(new Point3D(subtract.x, subtract.y, subtract.z));
			}
			double velocitySum = 0;
			for (int i = 0; i < points.length; i++) {
				points[i].setLocation(points[i].getLocation().addTo(velocities[i]));
				velocities[i] = velocities[i].multiply(0.9);
				velocitySum += velocities[i].getMagnitude();
			}
			velocitySums.add(velocitySum);
			graphFrame.repaint();
		}
	}

	private int randLocValue() {
		return (int) (Math.random() * 1000) - 500;
	}

	private void makeThings() {
		// points = new Model[512];
		// for (int i = 0; i < points.length; i++) {
		// points[i] = new PointModel(w, (int) (Math.random() * 1000), (int)
		// (Math.random() * 1000), (int) (Math.random() * 1000));
		// }
		// tied = new double[points.length][points.length];

		// makePoints(100);
		// makeSpinExample();
		// makeTwistExample();
		// makeCubes(8, 1, false);
		// makeCubes(4, 4, true);
//		makeMultipleDodecahedra(20, 1);
//		 tieLine(100);
		 tieLines(100, 8);
		// tieRandomly(200, 0.0075);
		// tieRandomly(400, 0.01);
		// tieRandomly(6, 1);
		// tieRandomClusters(400, 20, 0.25, true);
		// tieRandomClusters(400, 2, 0.025, true);
		// tieRing(500);
		// makeSquareGrid(20);
		if (velocities == null) {
			velocities = new Point3D[points.length];
			for (int i = 0; i < points.length; i++) {
				velocities[i] = new Point3D();
			}
		}
	}

	void makeSpinExample() {
		points = new Model[2];
		points[0] = new PointModel(getWorld(), 50, 0, 0);
		points[1] = new PointModel(getWorld(), -50, 0, 0);
		velocities = new Point3D[2];
		velocities[0] = new Point3D(0, 50, 0);
		velocities[1] = new Point3D(0, -50, 0);
		ties = new HashSet<Point>();
		tie(0, 1);
	}

	void makeSquareGrid(int sideLength) {
		makePoints(sideLength * sideLength);
		for (int i = 0; i < sideLength; i++) {
			for (int j = 0; j < sideLength; j++) {
				int cv = i * sideLength + j;
				if (i > 0) {
					tie(cv, (i - 1) * sideLength + j);
				}
				if (i < sideLength - 1) {
					tie(cv, (i + 1) * sideLength + j);
				}
				if (j > 0) {
					tie(cv, i * sideLength + j - 1);
				}
				if (j < sideLength - 1) {
					tie(cv, i * sideLength + j + 1);
				}
			}
		}
	}

	void makeTwistExample() {
		makePoints(4);
		int dist = 50;
		points[0].setLocation(dist, dist, 0);
		points[1].setLocation(-dist, dist, 0);
		points[2].setLocation(-dist, -dist, 0);
		points[3].setLocation(dist, -dist, 0);
		tie(0, 1);
		tie(1, 3);
		tie(2, 3);
		tie(2, 0);
	}

	void makeMultipleDodecahedra(int num, int centerPoints) {
		makePoints(20 * num + centerPoints);
		for (int j = 0; j < num; j++) {
			for (int i = 0; i < centerPoints; i++) {
				tie(points.length - i - 1, j * 20);
			}
			for (int i = 0; i < 5; i++) {
				tie(j * 20 + i, j * 20 + (i + 1) % 5);
				tie(j * 20 + i, j * 20 + i + 5);
				tie(j * 20 + i + 5, j * 20 + i + 10);
				tie(j * 20 + (i + 1) % 5 + 5, j * 20 + i + 10);
				tie(j * 20 + i + 10, j * 20 + i + 15);
				tie(j * 20 + i + 15, j * 20 + (i + 1) % 5 + 15);
			}
		}
	}

	private void makePoints(int numPoints) {
		points = new Model[numPoints];
		for (int i = 0; i < points.length; i++) {
			points[i] = new PointModel(getWorld(), randLocValue(), randLocValue(), randLocValue());
		}
		ties = new HashSet<Point>();
	}

	void makeDodecahedron() {
		makePoints(20);
		for (int i = 0; i < 5; i++) {
			tie(i, (i + 1) % 5);
			tie(i, i + 5);
			tie(i + 5, i + 10);
			tie((i + 1) % 5 + 5, i + 10);
			tie(i + 10, i + 15);
			tie(i + 15, (i + 1) % 5 + 15);
		}
	}

	void makeCubes(int edgeLength, int numCubes, boolean centerTether) {
		int cubeVolume = (int) Math.pow(edgeLength, 3);
		if (centerTether) {
			points = new Model[cubeVolume * numCubes + 1];
		} else {
			points = new Model[cubeVolume * numCubes];
		}
		for (int i = 0; i < points.length; i++) {
			points[i] = new PointModel(getWorld(), randLocValue(), randLocValue(), randLocValue());
		}
		ties = new HashSet<Point>();
		for (int i = 0; i < numCubes; i++) {
			if (centerTether) {
				tie(i * cubeVolume, points.length - 1);
			}
			doCubeTying(edgeLength, i * cubeVolume);
		}
	}

	void doCubeTying(int edgeLength, int offset) {
		for (int a = 0; a < edgeLength; a++) {
			for (int b = 0; b < edgeLength; b++) {
				for (int c = 0; c < edgeLength; c++) {
					int cv = offset + a * edgeLength * edgeLength + b * edgeLength + c;
					if (cv < points.length) {
						int nv = offset + (a - 1) * edgeLength * edgeLength + b * edgeLength + c;
						if (a > 0 && nv >= 0 && nv < points.length) {
							tie(cv, nv);
						}
						nv = offset + (a + 1) * edgeLength * edgeLength + b * edgeLength + c;
						if (a < edgeLength - 1 && nv >= 0 && nv < points.length) {
							tie(cv, nv);
						}
						nv = offset + a * edgeLength * edgeLength + (b - 1) * edgeLength + c;
						if (b > 0 && nv >= 0 && nv < points.length) {
							tie(cv, nv);
						}
						nv = offset + a * edgeLength * edgeLength + (b + 1) * edgeLength + c;
						if (b < edgeLength - 1 && nv >= 0 && nv < points.length) {
							tie(cv, nv);
						}
						nv = offset + a * edgeLength * edgeLength + b * edgeLength + c - 1;
						if (c > 0 && nv >= 0 && nv < points.length) {
							tie(cv, nv);
						}
						nv = offset + a * edgeLength * edgeLength + b * edgeLength + c + 1;
						if (c < edgeLength - 1 && nv >= 0 && nv < points.length) {
							tie(cv, nv);
						}
					}
				}
			}
		}
	}

	void tieRing(int numPoints) {
		makePoints(numPoints);
		for (int i = 0; i < points.length; i++) {
			tie(i, (i + 1) % points.length);
		}
	}

	void tieLine(int numPoints) {
		makePoints(numPoints);
		for (int i = 0; i < points.length - 1; i++) {
			tie(i, i + 1);
		}
	}

	void tieLines(int numPoints, int numLines) {
		makePoints(numPoints);
		for (int l = 0; l < numLines; l++) {
			int[] nums = new int[numPoints];
			for (int i = 0; i < numPoints; i++) {
				nums[i] = i;
			}
			int max = numPoints;
			int lastIndex = (int) (Math.random() * max);
			int num = nums[lastIndex];
			nums[lastIndex] = nums[--max];
			for (int i = 0; i < points.length - 1; i++) {
				int index = (int) (Math.random() * max);
				tie(num, nums[index]);
				num = nums[index];
				nums[index] = nums[--max];
				lastIndex = index;
			}
		}
	}

	void tieRandomClusters(int numPoints, int numClusters, double innerClusterProbability, boolean tethered) {
		makePoints(tethered ? numPoints + 1 : numPoints);
		if (tethered) {
			for (int i = 0; i < numClusters; i++) {
				tie(points.length - 1, i * numPoints / numClusters);
			}
		}
		for (int i = 0; i < numPoints; i++) {
			for (int j = 0; j < numPoints / numClusters; j++) {
				int add = (int) (i / ((int) numPoints / numClusters) * (int) (numPoints / numClusters));
				if (i != j + add && j + add < numPoints) {
					if (Math.random() < innerClusterProbability) {
						tie(i, j + add);
					}
				}
			}
		}
	}

	void tieRandomly(int numPoints, double probability) {
		makePoints(numPoints);
		for (int i = 0; i < points.length; i++) {
			for (int j = 0; j < points.length; j++) {
				if (i != j) {
					if (Math.random() < probability) {
						tie(i, j);
					}
				}
			}
		}
	}

	private void tie(int i, int j) {
		Point p1 = new Point(i, j);
		if (i == j || ties.contains(p1)) {
			return;
		}
		// tied[i][j] = desiredDistance;
		// tied[j][i] = desiredDistance;
		ties.add(p1);
		Point p1r = new Point(j, i);
		ties.add(p1r);
		LineModel model = new LineModel(getWorld(), points[i], points[j]);
		tieModels.put(p1, model);
		tieModels.put(p1r, model);
	}

	void untie(int i, int j) {
		Point p1 = new Point(i, j);
		Point p1r = new Point(j, i);
		ties.remove(p1);
		ties.remove(p1r);
		Model remove = tieModels.remove(p1);
		tieModels.remove(p1r);
		int modelIndex = getWorld().models.indexOf(remove);
		getWorld().models.remove(modelIndex);
		getWorld().colors.remove(modelIndex);
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == 1) {
			double minDist = Double.MAX_VALUE;
			int minIndex = -1;
			for (int i = 0; i < points.length; i++) {
				Point3D cameraPoint = points[i].getLocation().getCameraPoint(getCamera());
				if (cameraPoint.z > 0) {
					Point drawPoint = cameraPoint.getDirectDrawPoint(getCamera());
					double dist = Math.sqrt(Math.pow(drawPoint.x - e.getX(), 2) + Math.pow(drawPoint.y - e.getY(), 2));
					if (dist < minDist) {
						minIndex = i;
						minDist = dist;
					}
				}
			}
			selectedIndex = minIndex;
			selectedPoint = (PointModel) points[minIndex];
		}
		if (e.getButton() == 3) {
			selectedPoint = null;
		}
	}
}
