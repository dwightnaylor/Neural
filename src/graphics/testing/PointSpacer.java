package graphics.testing;

import graphics.basicShapes.Point3D;
import graphics.basicmodels.LineModel;
import graphics.basicmodels.Model;
import graphics.basicmodels.PointModel;
import graphics.fundamentals.WorldFrame;
import helpers.dataStructures.UnorderedPair;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class PointSpacer extends WorldFrame {

	private static boolean started = false;

	public static void main(String[] args) {
		PointSpacer frame = new PointSpacer(400, 400, 20);
		frame.drawMovementInstructions = false;
		frame.setVisible(true);
		while (true) {
			frame.repaint();
		}
	}

	public static final int DIFF = 40;
	HashSet<UnorderedPair<Model>> ties = new HashSet<UnorderedPair<Model>>();
	Hashtable<UnorderedPair<Model>, Model> tieModels = new Hashtable<UnorderedPair<Model>, Model>();
	ArrayList<Model> points = new ArrayList<Model>();
	Hashtable<Model, Point3D> velocities = new Hashtable<Model, Point3D>();
	double desiredDistance = 100;
	double velocityMultiplier = 100;

	PointModel selectedPoint;
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
		if (keyPressBuffer[KeyEvent.VK_ESCAPE]) {
			started = false;
		}
		super.processKeyPresses();
	}

	@Override
	public void doPainting(Graphics g) {
		Hashtable<Model, String> reverseNameLookup = new Hashtable<Model, String>();
		for (String name : getWorld().names.keySet()) {
			reverseNameLookup.put(getWorld().names.get(name), name);
		}
		g.setColor(Color.red);
		for (Model point : points) {
			if (!reverseNameLookup.containsKey(point)) {
				continue;
			}
			Point3D cameraPoint = point.getLocation().getCameraPoint(getCamera());
			if (cameraPoint.z > 0) {
				Point3D drawPoint = cameraPoint.getDirectDrawPoint(getCamera());
				g.drawString(reverseNameLookup.get(point), (int) drawPoint.x, (int) drawPoint.y);
			}
		}

		if (started) {
			// if (Math.random() < 0.9) {
			// if (ties.size() > 0) {
			// Iterator<UnorderedPair<Model>> iterator = ties.iterator();
			// int j = (int) (Math.random() * (ties.size() - 1));
			// for (int i = 0; i < j; i++) {
			// iterator.next();
			// }
			// UnorderedPair<Model> next = iterator.next();
			// untie(points.indexOf(next.first()),
			// points.indexOf(next.second()));
			// }
			// }
			// tie((int) (Math.random() * points.size()), (int) (Math.random() *
			// points.size()));

			for (Model curModel : points) {
				for (Model otherModel : points) {
					if (curModel == otherModel) {
						continue;
					}
					Point3D subtract = curModel.getLocation().subtractFrom(otherModel.getLocation());
					subtract = subtract.scale(-Math.min(10, 5000 * velocityMultiplier / Math.pow(subtract.getMagnitude(), 2)));
					setVelocity(curModel, getVelocity(curModel).addTo(subtract));
				}
			}
			for (UnorderedPair<Model> tie : ties) {
				Point3D subtract = tie.first().getLocation().subtractFrom(tie.second().getLocation());
				double dist = subtract.getMagnitude();
				subtract = subtract.scale(Math.min(100, (0.001) * velocityMultiplier * dist));
				if (dist < desiredDistance) {
					subtract.multiply(-1);
				}
				setVelocity(tie.first(), getVelocity(tie.first()).addTo(subtract));
				setVelocity(tie.second(), getVelocity(tie.second()).addTo(subtract.multiply(-1)));
			}
			double velocitySum = 0;
			for (Model curModel : points) {
				curModel.setLocation(curModel.getLocation().addTo(getVelocity(curModel)));
				setVelocity(curModel, getVelocity(curModel).multiply(0.9));
				velocitySum += getVelocity(curModel).getMagnitude();
			}
			velocitySums.add(velocitySum);
			graphFrame.repaint();
		}
	}

	private Point3D getVelocity(Model model) {
		if (!velocities.containsKey(model)) {
			velocities.put(model, Point3D.ORIGIN);
		}
		return velocities.get(model);
	}

	private void setVelocity(Model model, Point3D velocity) {
		velocities.put(model, velocity);
	}

	private int randLocValue() {
		return (int) (Math.random() * 1000) - 500;
	}

	private void makeThings() {
		moveSpeed = 30;

		// makePoints(100);
		// Platonic solids
		// tieRandomly(4, 1);
		 makeCubes(9, 1, false);
		// makeOctahedron();
		// makeMultipleDodecahedra(1, 0);
		// makeIcosahedron();
		// Basic Geometric arrangements
//		 tieLine(64);
		// tieRing(500);
//		 makeSquareGrid(8);
//		 makeCubes(10, 1, false);

		// makeTwistExample();

		// Randomly-generated
		// tieLines(100, 2);
		// tieRandomly(300, 0.02);
		// tieRandomly(100, 0.01);
		// tieRandomly(400, 0.003);

		// Clusters
		// makeCubes(4, 4, true);
		// makeMultipleDodecahedra(20, 1);
		// tieRandomClusters(400, 20, 0.1, true);
		// tieRandomClusters(300, 2, 0.02, true);

		// makeWikiCrawler("http://en.wikipedia.org/wiki/Pollarding");

		// Real data
		// doPairwiseTying("rpidata/BioDepartment");
		// doPairwiseTying("rpidata/PhysicsDepartment");
//		doPairwiseTying("rpidata/CSDepartment");
//		 doPairwiseTying("caltechdata/CSDepartment");
//		 doPairwiseTying("allSchoolsData/csDepartments");
		// doOrderedTying("text/pledgeOfAllegiance");
		// doOrderedTying("text/gettysburgAddress");
	}

	void makeOctahedron() {
		makePoints(6);
		tie(0, 1);
		tie(0, 2);
		tie(0, 3);
		tie(0, 4);
		tie(5, 1);
		tie(5, 2);
		tie(5, 3);
		tie(5, 4);
		tie(1, 2);
		tie(2, 3);
		tie(3, 4);
		tie(4, 1);
	}

	void doOrderedTying(String file) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(file)));
			String line;
			String lastWord = null;
			while ((line = br.readLine()) != null) {
				line = line.replaceAll("[^a-zA-Z ]", "").toLowerCase();
				String[] split = line.split(" ");
				for (String string : split) {
					if (lastWord != null) {
						tie(string, lastWord);
					}
					lastWord = string;
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void doPairwiseTying(String file) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(file)));
			String line;
			while ((line = br.readLine()) != null) {
				tie(line.substring(0, line.indexOf(':')), line.substring(line.indexOf(':') + 1, line.length()));
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void tie(String name1, String name2) {
		if (!getWorld().names.containsKey(name1)) {
			PointModel ap = new PointModel(getWorld(), randLocValue(), randLocValue(), randLocValue());
			points.add(ap);
			getWorld().names.put(name1, ap);
		}
		if (!getWorld().names.containsKey(name2)) {
			PointModel bp = new PointModel(getWorld(), randLocValue(), randLocValue(), randLocValue());
			points.add(bp);
			getWorld().names.put(name2, bp);
		}
		tie(points.indexOf(getWorld().names.get(name1)), points.indexOf(getWorld().names.get(name2)));
	}

	void makeWikiCrawler(final String startURL) {
		HashSet<String> searched = new HashSet<String>();
		Queue<String> toSearch = new LinkedList<String>();
		Queue<Integer> indexOfParent = new LinkedList<Integer>();
		toSearch.add(startURL);
		indexOfParent.add(-1);

		int num = 0;
		while (num < 200 && !toSearch.isEmpty()) {
			String url = toSearch.poll();
			int parentIndex = indexOfParent.poll();
			String title = url.substring(url.lastIndexOf('/') + 1);
			PointModel model = new PointModel(getWorld(), randLocValue(), randLocValue(), randLocValue());
			points.add(model);
			int modelIndex = points.size() - 1;
			if (parentIndex != -1) {
				tie(modelIndex, parentIndex);
			}
			getWorld().names.put(title, model);
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
				String line;
				while ((line = br.readLine()) != null) {
					String ref = "a href=\"/wiki/";
					while (line.contains(ref)) {
						int index = line.indexOf(ref);
						String newURL = "http://en.wikipedia.org" + line.substring(line.indexOf('"', index) + 1, line.indexOf('"', index + ref.length()));
						if (!searched.contains(newURL) && !newURL.contains("File:")) {
							toSearch.add(newURL);
							indexOfParent.add(modelIndex);
							searched.add(newURL);
						}
						line = line.substring(index + ref.length());
					}
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			num++;
		}
	}

	void makeSpinExample() {
		PointModel p1 = new PointModel(getWorld(), 50, 0, 0);
		points.add(p1);
		setVelocity(p1, new Point3D(0, 50, 0));
		PointModel p2 = new PointModel(getWorld(), -50, 0, 0);
		points.add(p2);
		setVelocity(p2, new Point3D(0, -50, 0));
		ties = new HashSet<UnorderedPair<Model>>();
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
		points.get(0).setLocation(dist, dist, 0);
		points.get(1).setLocation(-dist, dist, 0);
		points.get(2).setLocation(-dist, -dist, 0);
		points.get(3).setLocation(dist, -dist, 0);
		tie(0, 1);
		tie(1, 3);
		tie(2, 3);
		tie(2, 0);
	}

	void makeMultipleDodecahedra(int num, int centerPoints) {
		makePoints(20 * num + centerPoints);
		for (int j = 0; j < num; j++) {
			for (int i = 0; i < centerPoints; i++) {
				tie(points.size() - i - 1, j * 20);
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

	void makeIcosahedron() {
		makePoints(12);
		for (int i = 0; i < 5; i++) {
			tie(0, i + 1);
			tie(11, i + 6);
			tie(i + 1, i + 6);
			tie(i + 1, 6 + (i + 1) % 5);
			tie(i + 1, 1 + (i + 1) % 5);
			tie(i + 6, 6 + (i + 1) % 5);
		}
	}

	private void makePoints(int numPoints) {
		for (int i = 0; i < numPoints; i++) {
			points.add(new PointModel(getWorld(), randLocValue(), randLocValue(), randLocValue()));
		}
		ties = new HashSet<UnorderedPair<Model>>();
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
		for (int i = 0; i < cubeVolume * numCubes + (centerTether ? 1 : 0); i++) {
			points.add(new PointModel(getWorld(), randLocValue(), randLocValue(), randLocValue()));
		}
		ties = new HashSet<UnorderedPair<Model>>();
		for (int i = 0; i < numCubes; i++) {
			if (centerTether) {
				tie(i * cubeVolume, points.size() - 1);
			}
			doCubeTying(edgeLength, i * cubeVolume);
		}
	}

	void doCubeTying(int edgeLength, int offset) {
		for (int a = 0; a < edgeLength; a++) {
			for (int b = 0; b < edgeLength; b++) {
				for (int c = 0; c < edgeLength; c++) {
					int cv = offset + a * edgeLength * edgeLength + b * edgeLength + c;
					if (cv < points.size()) {
						int nv = offset + (a - 1) * edgeLength * edgeLength + b * edgeLength + c;
						if (a > 0 && nv >= 0 && nv < points.size()) {
							tie(cv, nv);
						}
						nv = offset + (a + 1) * edgeLength * edgeLength + b * edgeLength + c;
						if (a < edgeLength - 1 && nv >= 0 && nv < points.size()) {
							tie(cv, nv);
						}
						nv = offset + a * edgeLength * edgeLength + (b - 1) * edgeLength + c;
						if (b > 0 && nv >= 0 && nv < points.size()) {
							tie(cv, nv);
						}
						nv = offset + a * edgeLength * edgeLength + (b + 1) * edgeLength + c;
						if (b < edgeLength - 1 && nv >= 0 && nv < points.size()) {
							tie(cv, nv);
						}
						nv = offset + a * edgeLength * edgeLength + b * edgeLength + c - 1;
						if (c > 0 && nv >= 0 && nv < points.size()) {
							tie(cv, nv);
						}
						nv = offset + a * edgeLength * edgeLength + b * edgeLength + c + 1;
						if (c < edgeLength - 1 && nv >= 0 && nv < points.size()) {
							tie(cv, nv);
						}
					}
				}
			}
		}
	}

	void tieRing(int numPoints) {
		makePoints(numPoints);
		for (int i = 0; i < points.size(); i++) {
			tie(i, (i + 1) % points.size());
		}
	}

	void tieLine(int numPoints) {
		makePoints(numPoints);
		for (int i = 0; i < points.size() - 1; i++) {
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
			for (int i = 0; i < numPoints - 1; i++) {
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
				tie(points.size() - 1, i * numPoints / numClusters);
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
		for (int i = 0; i < points.size(); i++) {
			for (int j = 0; j < points.size(); j++) {
				if (i != j) {
					if (Math.random() < probability) {
						tie(i, j);
					}
				}
			}
		}
	}

	private void tie(int i, int j) {
		UnorderedPair<Model> p1 = new UnorderedPair<Model>(points.get(i), points.get(j));
		if (i == j || ties.contains(p1)) {
			return;
		}
		ties.add(p1);
		LineModel model = new LineModel(getWorld(), points.get(i), points.get(j), 1);
		tieModels.put(p1, model);
	}

	void untie(int i, int j) {
		UnorderedPair<Model> p1 = new UnorderedPair<Model>(points.get(i), points.get(j));
		ties.remove(p1);
		Model remove = tieModels.remove(p1);
		int modelIndex = getWorld().models.indexOf(remove);
		getWorld().models.remove(modelIndex);
		getWorld().colors.remove(modelIndex);
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == 1) {
			// double minDist = Double.MAX_VALUE;
			// int minIndex = -1;
			// for (int i = 0; i < points.size(); i++) {
			// Point3D cameraPoint =
			// points.get(i).getLocation().getCameraPoint(getCamera());
			// if (cameraPoint.z > 0) {
			// Point3D drawPoint = cameraPoint.getDirectDrawPoint(getCamera());
			// double dist = Math.sqrt(Math.pow(drawPoint.x - e.getX(), 2) +
			// Math.pow(drawPoint.y - e.getY(), 2));
			// if (dist < minDist) {
			// minIndex = i;
			// minDist = dist;
			// }
			// }
			// }
			// selectedPoint = (PointModel) points.get(minIndex);
		}
		if (e.getButton() == 3) {
			selectedPoint = null;
		}
	}
}
