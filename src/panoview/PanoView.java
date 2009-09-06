package panoview;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import panoview.lens.Lens;
import panoview.lens.RectilinearLens;
import panoview.texture.EquirectangularTextureProjection;
import panoview.texture.Texture;
import panoview.texture.TextureProjection;


public class PanoView {

	private int width;
	private int height;
	private DataBuffer dataBuffer;
	private BufferedImage img;
	private final GraphicsConfiguration gc;
	private Texture texture;
	private TextureProjection textureProjection;
	private Lens lens;
	private int parts;
	private ExecutorService execService;
	private boolean changed;

	/**
	 * Create a new PanoView instance.
	 * @param w the width of the surface the image will be drawn to.
	 * @param h the height of the surface the image will be drawn to.
	 * @param gc the GraphicConfiguration of the surface the image will be drawn to.
	 * @throws IllegalArgumentException if the color model of the GraphicsConfiguration is unsupported.
	 */
	public PanoView(int w, int h, GraphicsConfiguration gc, Texture texture) {
		this.gc = gc;
		this.texture = texture;
		textureProjection = new EquirectangularTextureProjection();
		lens = new RectilinearLens();
		parts = Runtime.getRuntime().availableProcessors();
		System.out.println("Using " + parts + " thread(s)");
		reconfigure(w, h);
		execService = Executors.newFixedThreadPool(parts);
	}
	
	public Lens getLens() {
		return lens;
	}
	
	public void setLens(Lens lens) {
		this.lens = lens;
		changed = true;
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	/**
	 * Tell the renderer that the AWT surface to draw to has changed size.
	 * The next call to {@link #render(Graphics)} will use the new size.
	 * @param w the new width.
	 * @param h the new height.
	 */
	public void reconfigure(int w, int h) {
		if (w != width || h != height) {
			width = w;
			height = h;
			if (img != null) {
				img = null;
				dataBuffer = null;
			}
			ColorModel cm = gc.getColorModel();
			WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
			dataBuffer = raster.getDataBuffer();
			img = new BufferedImage(cm, raster, false, null);
			changed = true;
		}
	}
	
	/**
	 * Render the current frame to an AWT surface.
	 * @param gr the Graphics object to use when rendering.
	 */
	public void render(Graphics gr, boolean force) {
		if (changed || force) {
			long start = System.nanoTime();
			// TODO short data vs. int data
			final int[] data = ((DataBufferInt) dataBuffer).getData();

			Collection<Callable<Void>> tasks = new ArrayList<Callable<Void>>(parts);

			for (int part = 0; part < parts ; part++) {
				final int v0 = height * part / parts;
				final int v1 = height * (part + 1) / parts;

				tasks.add(new Callable<Void>() {

					@Override
					public Void call() throws Exception {
						int i = v0 * width;
						for (int v = v0; v < v1; v++) {
							double y = (((width - height) / 2 + v + 0.5) / width) - 0.5;
							for (int u = 0; u < width; u++) {
								double x = ((u + 0.5) / width) - 0.5;

								int c = lens.sample(textureProjection, texture, x, y);
								// TODO RGB vs. BGR
								data[i] = c;
								i++;
							}
						}
						return null;
					}

				});
			}
			try {
				for (Future<Void> f : execService.invokeAll(tasks)) {
					try {
						f.get();
					} catch (ExecutionException e) {
						e.getCause().printStackTrace();
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			long end = System.nanoTime();
			System.out.println("Time " + (end - start) / 1000000L);
			changed = false;
		}
		gr.drawImage(img, 0, 0, null);
	}

}
