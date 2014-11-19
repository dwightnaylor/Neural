package graphics.testing;

import graphics.basicShapes.Point3D;
import graphics.basicmodels.LineModel;
import graphics.basicmodels.Model;
import graphics.basicmodels.PointModel;
import graphics.fundamentals.Camera;
import graphics.fundamentals.World3D;
import helpers.DelayHelper;
import helpers.MathHelper;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JFrame;
import javax.swing.JRootPane;

@SuppressWarnings("serial")
public class PointSpacer extends JFrame implements KeyListener, MouseMotionListener, MouseListener, MouseWheelListener {

	private static boolean started = false;

	public static void main(String[] args) {
		new PointSpacer(400, 400, 20).continueRepainting();
	}

	World3D w;
	private Camera camera;

	public static final int DIFF = 40;
	HashSet<Point> ties = new HashSet<Point>();
	Model[] points;
	Point3D[] velocities;
	double desiredDistance = 10;
	double velocityMultiplier = 100;
	private int move = 100;

	Point3D selectedPoint;
	private int selectedIndex;
	ArrayList<Double> velocitySums = new ArrayList<Double>();
	private JFrame graphFrame;

	public PointSpacer(final int width, final int height, final int displayDelay) {
		this.w = new World3D();
		this.setSize(width + DIFF, height + DIFF);
		camera = new Camera(w, 0, -100, 0, 0, 0, width, height);
		makeThings();
		JRootPane customPane = new JRootPane() {
			public void paint(Graphics g) {
				// if (!started) {
				// try {
				// Thread.sleep(10000);
				// } catch (InterruptedException e) {
				// e.printStackTrace();
				// }
				// started = true;
				// }
				long st = System.currentTimeMillis();
				BufferedImage buffer = (BufferedImage) createImage(getWidth(), getHeight());
				Graphics2D bg = (Graphics2D) buffer.getGraphics();
				adjustCamera();
				w.paint(bg, camera);

				bg.setColor(Color.black);
				bg.drawString("WASD keys move forward,left,back, and right. Shift moves down and Spacebar moves up. Click and drag to look around.", 20, 10);
				bg.drawString("Move speed is currently " + move + ". Scroll to change move speed. (Up is faster, down is slower)", 20, 25);

				if (selectedPoint != null) {
					Point3D cameraPoint = selectedPoint.getCameraPoint(camera);
					if (cameraPoint.z > 0) {
						Point drawPoint = cameraPoint.getDirectDrawPoint(camera);
						double sx = MathHelper.round(selectedPoint.x, 3);
						double sy = MathHelper.round(selectedPoint.y, 3);
						double sz = MathHelper.round(selectedPoint.z, 3);
						bg.setColor(Color.red);
						bg.drawString(selectedIndex + ":[" + sx + "," + sy + "," + sz + "]", drawPoint.x, drawPoint.y);
					}
				}

				g.drawImage(buffer, 0, 0, null);
				if (started) {
					for (int i = 0; i < points.length; i++) {
						for (int j = 0; j < points.length; j++) {
							if (i != j) {
								Point3D subtract = points[i].getLocation().subtractFrom(points[j].getLocation());
								subtract.scale(-Math.min(10, 5000 * velocityMultiplier / Math.pow(points[i].getLocation().distance(points[j].getLocation()), 2)));
								velocities[i] = velocities[i].addTo(new Point3D(subtract.x, subtract.y, subtract.z));
							}
						}
					}
					for (Point tie : ties) {
						Point3D subtract = points[tie.x].getLocation().subtractFrom(points[tie.y].getLocation());
						double dist = points[tie.x].getLocation().distance(points[tie.y].getLocation());
						subtract.scale(Math.min(100, (0.001) * velocityMultiplier * dist));
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
				checkForKeyPresses();
				System.out.println(System.currentTimeMillis() - st);
				DelayHelper.delayFor(displayDelay - (System.currentTimeMillis() - st));
			}
		};
		setContentPane(customPane);
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
		graphFrame.setVisible(true);
		getComponent(0).addMouseMotionListener(this);
		getComponent(0).addMouseListener(this);
		getComponent(0).addMouseWheelListener(this);
		addKeyListener(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
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

		// makeSpinExample();
		// makeTwistExample();
		makeCubes(8, 1, false);
		// makeCubes(4, 4, true);
		// makeMultipleDodecahedra(20, 1);
		// tieLine(100);
		// tieLines(100, 2);
		// tieRandomly(200, 0.0075);
		// tieRandomly(6, 1);
		// tieRandomClusters(400, 20, 0.25, true);
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
		points[0] = new PointModel(w, 50, 0, 0);
		points[1] = new PointModel(w, -50, 0, 0);
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
			points[i] = new PointModel(w, randLocValue(), randLocValue(), randLocValue());
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
			points[i] = new PointModel(w, randLocValue(), randLocValue(), randLocValue());
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
		// tied[i][j] = desiredDistance;
		// tied[j][i] = desiredDistance;
		ties.add(new Point(i, j));
		ties.add(new Point(j, i));
		new LineModel(w, points[i].getLocation(), points[j].getLocation());
	}

	private void adjustCamera() {
		// FIXME: Will be removed soon
	}

	public void checkForKeyPresses() {
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == 1) {
			double minDist = Double.MAX_VALUE;
			int minIndex = -1;
			for (int i = 0; i < points.length; i++) {
				Point3D cameraPoint = points[i].getLocation().getCameraPoint(camera);
				if (cameraPoint.z > 0) {
					Point drawPoint = cameraPoint.getDirectDrawPoint(camera);
					double dist = Math.sqrt(Math.pow(drawPoint.x - e.getX(), 2) + Math.pow(drawPoint.y - e.getY(), 2));
					if (dist < minDist) {
						minIndex = i;
						minDist = dist;
					}
				}
			}
			selectedIndex = minIndex;
			selectedPoint = points[minIndex].getLocation();
		}
		if (e.getButton() == 3) {
			selectedPoint = null;
		}

		// for (int i = 0; i < points.length; i++) {
		// if (i != minIndex) {
		// Point3D subtract = points[i].subtractFrom(points[minIndex]);
		// double dist = points[i].getDistanceTo(points[minIndex]);
		// double magnitude = 1000000 * velocityMultiplier / Math.pow(dist, 2);
		// subtract.scale(-magnitude);
		// velocities[i].translate(subtract.x, subtract.y,
		// subtract.z);
		// }
		// }
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent arg0) {
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void continueRepainting() {
		new Thread() {
			public void run() {
				while (true) {
					repaint();
				}
			}
		}.start();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		move -= e.getUnitsToScroll();
		if (move < 0) {
			move = 0;
		}
	}
}
