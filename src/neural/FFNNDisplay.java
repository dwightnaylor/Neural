package neural;

import graphics.basicShapes.Line3D;
import graphics.basicShapes.Point3D;
import graphics.basicShapes.Ray3D;
import graphics.basicmodels.RayModel;
import graphics.basicmodels.Rectangle3D;
import graphics.fundamentals.Camera;
import graphics.fundamentals.World3D;
import helpers.DelayHelper;
import helpers.PriorityQueue;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JRootPane;

@SuppressWarnings("serial")
public class FFNNDisplay extends JFrame implements KeyListener, MouseMotionListener, MouseListener {

	World3D w;
	private Camera camera;

	public static final int DIFF = 40;
	private final FFNN network;
	Point3D[][] neuronLocations;

	public FFNNDisplay(final FFNN network, final int width, final int height, final int displayDelay) {
		this.w = new World3D();
		new RayModel(w, new Ray3D(-width, 0, -height, 45, 25));
		this.network = network;
		this.setSize(width + DIFF, height + DIFF);
		neuronLocations = calculateNeuronLocations(100);
		camera = new Camera(w, 0, 0, 0, 0, 0, width, height);
		new Rectangle3D(w, 10, 0, 0, 100);
		new Rectangle3D(w, 10, 0, 0, -100);
		new Rectangle3D(w, 10, 0, 100, 0);
		new Rectangle3D(w, 10, 0, -100, 0);
		new Rectangle3D(w, 10, 100, 0, 0);
		new Rectangle3D(w, 10, -100, 0, 0);
		JRootPane customPane = new JRootPane() {

			PriorityQueue<Double, Object[]> linesToDraw = new PriorityQueue<Double, Object[]>();

			public void paint(Graphics g) {
				long st = System.currentTimeMillis();
				BufferedImage buffer = (BufferedImage) createImage(getWidth(), getHeight());
				Graphics2D bg = (Graphics2D) buffer.getGraphics();
				prepareToDrawNeurons(network, bg, width, height);
				while (!linesToDraw.isEmpty()) {
					Object[] curLine = linesToDraw.poll();
					Point from = ((Point) curLine[1]);
					Point to = ((Point) curLine[2]);
					bg.setColor(new Color((int) curLine[0], 0, 0));
					NeuralUtils.drawLine(bg, (int) from.x, (int) from.y, (int) to.x, (int) to.y, (int) curLine[3]);
				}
				adjustCamera();
				w.paint(bg, camera);

				bg.setColor(Color.black);
				bg.drawString("WASD keys move forward,left,back, and right. Shift moves down and Spacebar moves up. Click and drag to look around.", 20, 10);

				g.drawImage(buffer, 0, 0, null);
				DelayHelper.delayFor(displayDelay - (System.currentTimeMillis() - st));
				checkForKeyPresses();
			}

			private void prepareToDrawNeurons(final FFNN network, Graphics2D bg, int width, int height) {
				bg.setColor(Color.black);
				double mw = findMaxWeight();
				for (int i = 0; i < network.neurons.length; i++) {
					for (int j = 0; j < network.neurons[i].length; j++) {
						Point3D biasLocation = neuronLocations[i][0];
						if (biasLocation.z > 0) {
							bg.fillRect((int) biasLocation.getDrawPoint(camera).x - 4, (int) biasLocation.getDrawPoint(camera).y - 4, 9, 9);
						}
						Point3D endNeuronLocation = neuronLocations[i + 1][j + 1];
						if (endNeuronLocation.z > 0) {
							bg.fillRect((int) endNeuronLocation.getDrawPoint(camera).x - 2, (int) endNeuronLocation.getDrawPoint(camera).y - 2, 5, 5);
						}
						drawLineFor(width, height, mw, biasLocation, endNeuronLocation, network.neurons[i][j].biasWeight);
						for (int k = 0; k < (i == 0 ? network.getNumInputs() : network.neurons[i - 1].length); k++) {
							Point3D startNeuronLocation = neuronLocations[i][k + 1];
							if (startNeuronLocation.z > 0) {
								bg.fillRect((int) startNeuronLocation.getDrawPoint(camera).x - 2, (int) startNeuronLocation.getDrawPoint(camera).y - 2, 5, 5);
							}
							drawLineFor(width, height, mw, startNeuronLocation, endNeuronLocation, network.neurons[i][j].weights[k]);
						}
					}
				}
			}

			private void drawLineFor(int width, int height, double mw, Point3D n1, Point3D n2, double w) {
				Point3D n1c = n1.getCameraPoint(camera);
				Point3D n2c = n2.getCameraPoint(camera);
				if (n1c.z > 0 || n2c.z > 0) {
					if (n2c.z <= 0) {
						n2 = n1.getPointPartwayTo(n2, (n1c.z - 0.1) / (n1c.z - n2c.z));
					} else if (n1c.z <= 0) {
						n1 = n2.getPointPartwayTo(n1, (n2c.z - 0.1) / (n2c.z - n1c.z));
					}
					int r = (int) (128 + w / mw * 127);
					// Tossed in to fix various color-related
					// bugs...shouldn't
					// actually be necessary
					if (r < 0) {
						r = 0;
					}
					if (r > 255) {
						r = 255;
					}
					linesToDraw.put(-new Line3D(n1, n2).getDistanceTo(camera.getLocation()), new Object[] { r, n1.getDrawPoint(camera), n2.getDrawPoint(camera), (int) (Math.abs(w) / mw * 10 + 0.5) });
				}
			}
		};
		setContentPane(customPane);
		getComponent(0).addMouseMotionListener(this);
		getComponent(0).addMouseListener(this);
		addKeyListener(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	private void adjustCamera() {
	}

	private double findMaxWeight() {
		double mw = 10;
		for (int i = 0; i < network.neurons.length; i++) {
			for (int j = 0; j < network.neurons[i].length; j++) {
				mw = Math.max(mw, Math.abs(network.neurons[i][j].biasWeight));
				for (int k = 0; k < (i == 0 ? network.getNumInputs() : network.neurons[i - 1].length); k++) {
					mw = Math.max(mw, Math.abs(network.neurons[i][j].weights[k]));
				}
			}
		}
		return mw;
	}

	private Point3D[][] calculateNeuronLocations(int b) {
		Point3D[][] neuronLocations = new Point3D[network.neurons.length + 1][];
		neuronLocations[0] = new Point3D[network.getNumInputs() + 1];
		neuronLocations[0][0] = new Point3D(getWidth() / 2, 0, 0);
		// Draw the bias weights
		for (int i = 0; i < network.neurons.length; i++) {
			neuronLocations[i + 1] = new Point3D[network.neurons[i].length + 1];
			int d = getWidth() / 2;
			neuronLocations[i + 1][0] = new Point3D(i % 2 == 1 ? d : 0, (int) ((double) (i + 1) / network.neurons.length * (getHeight() - DIFF)), i % 2 == 0 ? d : 0);
		}
		// Draw the input layer to first layer weights
		for (int i = 1; i <= network.getNumInputs(); i++) {
			double d = (double) (i - 1) / (network.getNumInputs() - 1);
			if (i == 1 && network.getNumInputs() == 1) {
				d = 0;
			}
			neuronLocations[0][i] = new Point3D((int) (b + d * (getWidth() - DIFF - b)) - b - getWidth() / 2, 0, 0);
		}
		// Draw all of the actual neuron weights
		for (int i = 1; i <= network.neurons.length; i++) {
			for (int j = 1; j <= network.neurons[i - 1].length; j++) {
				double widthRatio = (double) (j - 1) / (network.neurons[i - 1].length - 1);
				if (network.neurons[i - 1].length == 1) {
					widthRatio = 0;
				}
				int d = (int) (b + widthRatio * (getWidth() - DIFF - b)) - b - getWidth() / 2;
				neuronLocations[i][j] = new Point3D(i % 2 == 0 ? d : 0, (int) ((double) i / network.neurons.length * (getHeight() - DIFF)), i % 2 == 1 ? d : 0);
			}
		}
		return neuronLocations;
	}

	public void checkForKeyPresses() {
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public void mouseClicked(MouseEvent arg0) {
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent arg0) {
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent arg0) {
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
}
