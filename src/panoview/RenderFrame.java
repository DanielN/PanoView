/* JAnalogTV, Copyright (c) 2007 Daniel Nilsson <dannil@users.sourceforge.net>
 * 
 * JAnalogTV is a Java port of analogtv from xscreensaver-5.03.
 * 
 * Permission to use, copy, modify, distribute, and sell this software and its
 * documentation for any purpose is hereby granted without fee, provided that
 * the above copyright notice appear in all copies and that both that
 * copyright notice and this permission notice appear in supporting
 * documentation.  No representations are made about the suitability of this
 * software for any purpose.  It is provided "as is" without express or
 * implied warranty.
 */
package panoview;

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
