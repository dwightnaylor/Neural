package graphics.fundamentals;

import graphics.basicShapes.Ray3D;
import graphics.basicmodels.Rectangle3D;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.WindowConstants;

public class WorldFrame extends JFrame implements MouseMotionListener, MouseListener, KeyListener, ComponentListener, MouseWheelListener {
	private World3D world;
	private Camera camera;
	private Point lastMousePress;
	private Point lastMouseDrag;
	public boolean[] keyPressBuffer = new boolean[1024];
	private long minFrameTime = 20;
	private int moveSpeed = 5;

	public WorldFrame(int width, int height) {
		setWorld(new World3D());
		setCamera(new Camera(getWorld(), 0, 0, 0, 0, 0, width, height));

		setSize(width, height);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setContentPane(new JRootPane() {
			long frameTime = -1;
			int frameNumber = 1;

			public void paint(Graphics g) {
				long frameStartTime = System.currentTimeMillis();
				getWorld().paint(g, getCamera());
				g.setColor(Color.black);
				g.drawString("WASD keys move forward,left,back, and right. Shift moves down and Spacebar moves up. Click and drag to look around.", 3, 12);
				g.drawString("Move speed is currently " + getMoveSpeed() + ". Scroll to change move speed. (Up is faster, down is slower)", 3, 27);
				g.drawString("Frametime is about " + (frameTime / frameNumber++), 3, 42);
				processKeyPresses();
				doPainting(g);
				frameTime += System.currentTimeMillis() - frameStartTime;
				try {
					Thread.sleep(Math.max(0, getMinFrameTime() - frameTime));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		addMouseMotionListener(this);
		addMouseListener(this);
		addKeyListener(this);
		addComponentListener(this);
		addMouseWheelListener(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void doPainting(Graphics g) {
		// no-op
	}

	protected void processKeyPresses() {
		if (this.keyPressBuffer[KeyEvent.VK_W]) {
		    	camera.translate(moveSpeed*Math.sin(camera.getAzimuth()), 0, moveSpeed*Math.cos(camera.getAzimuth()));
		}
		if (this.keyPressBuffer[KeyEvent.VK_S]) {
		    	camera.translate(-moveSpeed*Math.sin(camera.getAzimuth()), 0, -moveSpeed*Math.cos(camera.getAzimuth()));
		}
		if (this.keyPressBuffer[KeyEvent.VK_A]) {
		    	camera.translate(-moveSpeed*Math.cos(camera.getAzimuth()), 0, moveSpeed*Math.sin(camera.getAzimuth()));
		}
		if (this.keyPressBuffer[KeyEvent.VK_D]) {
		    	camera.translate(moveSpeed*Math.cos(camera.getAzimuth()), 0, -moveSpeed*Math.sin(camera.getAzimuth()));
		}
		if (this.keyPressBuffer[KeyEvent.VK_SPACE]) {
			camera.translate(0, moveSpeed, 0);
		}
		if (this.keyPressBuffer[KeyEvent.VK_SHIFT]) {
			camera.translate(0, -moveSpeed, 0);
		}
		// double rot = 0.01;
		// if (this.keyPressBuffer[37]) {
		// this.spin += rot;
		// }
		// if (this.keyPressBuffer[39]) {
		// this.spin -= rot;
		// }
		// if (this.keyPressBuffer[38]) {
		// this.tilt -= rot;
		// }
		// if (this.keyPressBuffer[40]) {
		// this.tilt += rot;
		// }
		// if (this.player != null) {
		// adjustCamera();
		// }
	}

	public World3D getWorld() {
		return world;
	}

	public void setWorld(World3D world) {
		this.world = world;
	}

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (this.lastMouseDrag == null) {
			this.lastMouseDrag = this.lastMousePress;
		}
		double deltaZenith = -((double) (e.getY() - this.lastMouseDrag.y)) / 150;
		double zenithLimit = Math.PI/2.1;
		if (camera.getZenith() + deltaZenith > zenithLimit) {
			deltaZenith = zenithLimit - camera.getZenith();
		}
		if (camera.getZenith() + deltaZenith < -zenithLimit) {
			deltaZenith = -zenithLimit - camera.getZenith();
		}
		camera.rotate(deltaZenith, (double) (e.getX() - this.lastMouseDrag.x) / 150);
		this.lastMouseDrag = new Point(e.getX(), e.getY());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		this.keyPressBuffer[e.getKeyCode()] = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		this.keyPressBuffer[e.getKeyCode()] = false;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		this.lastMousePress = new Point(e.getX(), e.getY());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		this.lastMouseDrag = null;
	}

	public long getMinFrameTime() {
		return minFrameTime;
	}

	public void setMinFrameTime(long minFrameTime) {
		this.minFrameTime = minFrameTime;
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void componentResized(ComponentEvent e) {
		camera.setFieldSize(getWidth(), getHeight());
	}

	@Override
	public void componentShown(ComponentEvent e) {
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		setMoveSpeed(getMoveSpeed() - e.getUnitsToScroll());
		if (getMoveSpeed() < 0) {
			setMoveSpeed(0);
		}
	}

	public int getMoveSpeed() {
		return moveSpeed;
	}

	public void setMoveSpeed(int moveSpeed) {
		this.moveSpeed = moveSpeed;
	}

	public static void main(String[] args) {
		WorldFrame frame = new WorldFrame(400, 400);
		// Rectangle3D r = new Rectangle3D(frame.getWorld(), 0, 0, 400, 100);
		// r.rotate(30, 0);
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				for (int k = 0; k < 2; k++) {
					new Rectangle3D(frame.getWorld(), 30 * i - 15, 30 * j - 15, 100 + k * 30, 20);
				}
			}
		}
		frame.setVisible(true);
		while (true) {
			frame.repaint();
		}
	}
}
