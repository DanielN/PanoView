package panoview;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import panoview.texture.Texture;
import panoview.util.RenderCanvas;


public class Main implements MouseMotionListener {

	public static void main(String[] args) throws InterruptedException {
		if (args.length != 1) {
			System.err.println("Usage: java " + Main.class.getName() + " [image]");
			System.exit(1);
		}
		try {
			Main app = new Main(640, 480, args[0]);
			app.run();
		} catch (IOException e) {
			System.err.println("Error opening image: " + e.getMessage());
			System.exit(1);
		}
	}

	private Frame frame;
	private RenderCanvas canvas;
	private PanoView panoView;
	private long frameTime = 30000000L;
	private int mouseX;
	private int mouseY;
	private double dir;
	private double tilt;
	private double zoom = 1.0;

	public Main(int width, int height, String filename) throws IOException {
		frame = new Frame("PanoView");
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		canvas = new RenderCanvas(new Dimension(width, height));
		canvas.addMouseMotionListener(this);
		canvas.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		frame.add(canvas);
		frame.pack();
		frame.setLocationByPlatform(true);
		panoView = new PanoView(width, height, canvas.getGraphicsConfiguration(), new Texture(filename));
	}
	
	public void run() {
		frame.setVisible(true);
		long t = System.nanoTime();
		try {
			while (true) {
				long delta = System.nanoTime() - t;
				while (delta < frameTime) {
					Thread.sleep(delta / 1000000, (int) (delta % 1000000));
					delta = System.nanoTime() - t;
				}
				panoView.setDir(dir);
				panoView.setTilt(tilt);
				panoView.setFocalLength(zoom);
				int w = canvas.getWidth();
				int h = canvas.getHeight();
				panoView.reconfigure(w, h);
				Graphics2D g = canvas.startRender();
				panoView.render(g);
				canvas.stopRender(g);
				t += frameTime;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		int dx = e.getX() - mouseX;
		int dy = e.getY() - mouseY;
		mouseX = e.getX();
		mouseY = e.getY();
		if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) == MouseEvent.BUTTON1_DOWN_MASK) {
			dir += dx / zoom / 200.0;
			tilt -= dy / zoom / 200.0;
		} else if ((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) == MouseEvent.BUTTON3_DOWN_MASK) {
			zoom += dy / 100.0;
			if (zoom < 0.1) {
				zoom = 0.1;
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

}
