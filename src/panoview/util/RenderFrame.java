package panoview.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics2D;

/**
 * RenderFrame is a {@link Frame} subclass that simplifies synchronous rendering.
 */
@SuppressWarnings("serial")
public class RenderFrame extends Frame {

	/**
	 * Create a new RenderFrame with the given size.
	 * @param size the initial width and height of the window.
	 */
	public RenderFrame(String title, Dimension size) {
		super(title);
		setSize(size);
		setIgnoreRepaint(true);
		setBackground(Color.black);
		setLocationByPlatform(true);
	}
	
	/**
	 * Start rendering a frame to the window.
	 * The returned {@link Graphics2D} object must be passed to {@link #stopRender(Graphics2D)}
	 * when one frame has been rendered. 
	 * @return the object that should be used for rendering.
	 */
	public Graphics2D startRender() {
		return (Graphics2D) getGraphics();
	}
	
	/**
	 * Stop rendering a frame.
	 * @param g the object returned by {@link #startRender()} when the frame was started.
	 */
	public void stopRender(Graphics2D g) {
		g.dispose();
	}

}
