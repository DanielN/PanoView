package panoview;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class Test {

	public static void main(String[] args) throws InterruptedException {
		RenderFrame frame = new RenderFrame("PanoView Test", new Dimension(800, 800));
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		PanoView panoView = new PanoView(800, 800, frame.getGraphicsConfiguration(), new Texture());
		frame.setVisible(true);
		long t = System.nanoTime();
		for (int i = -1800; i < 1800; i++) {
			panoView.setDir(i * Math.PI / 1800);
			panoView.setTilt(-Math.sin(i * Math.PI / 1800));
			panoView.setFocalLength(0.8 + 0.5 * Math.sin(i * Math.PI / 1800));
			Graphics2D g = frame.startRender();
			panoView.render(g);
			frame.stopRender(g);
		}
		t = System.nanoTime() - t;
		System.out.println("Time: " + t / 1000000000.0);
		System.out.println("FPS: " + 3600 * 1000000000.0 / t);
		System.exit(0);
	}
}
