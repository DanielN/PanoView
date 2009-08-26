package panoview;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;


public class Texture {

	private byte[] data;
	private int width;
	private int height;
	
	/**
	 * For test purposes only!
	 */
	public Texture() {
		try {
			BufferedImage image = ImageIO.read(getClass().getResourceAsStream("panorama.jpg"));
			DataBufferByte buffer = (DataBufferByte) image.getData().getDataBuffer();
			data = buffer.getData();
			width = image.getWidth();
			height = image.getHeight();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Texture(String filename) throws IOException {
		BufferedImage image = ImageIO.read(new FileInputStream(filename));
		DataBufferByte buffer = (DataBufferByte) image.getData().getDataBuffer();
		data = buffer.getData();
		width = image.getWidth();
		height = image.getHeight();
	}
	
	public int sample(int ss, int tt) {
		int s = width * ss - FixMath.ONE/2;		// TODO this limits image size to 32768 pixels, use long?
		int t = height * tt - FixMath.ONE/2;
		
		if (s < 0) s += FixMath.toFixed(width);
		
		int si = FixMath.toInt(s);
		int ti = FixMath.toInt(t);
		int sw = (s >> 8) & 0xFF;		// TODO This depends on FIXED_BITS being 16
		int tw = (t >> 8) & 0xFF;

		int i;
		int r = 0;
		int g = 0;
		int b = 0;
		try {
			i = 3 * (Math.max(ti, 0) * width + si);
			r += (256-sw) * (256-tw) * (data[i+2] & 0xFF);
			g += (256-sw) * (256-tw) * (data[i+1] & 0xFF);
			b += (256-sw) * (256-tw) * (data[i+0] & 0xFF);
			i = 3 * (Math.max(ti, 0) * width + (si + 1) % width);
			r += sw * (256-tw) * (data[i+2] & 0xFF);
			g += sw * (256-tw) * (data[i+1] & 0xFF);
			b += sw * (256-tw) * (data[i+0] & 0xFF);
			i = 3 * (Math.min(ti + 1, height-1) * width + si);
			r += (256-sw) * tw * (data[i+2] & 0xFF);
			g += (256-sw) * tw * (data[i+1] & 0xFF);
			b += (256-sw) * tw * (data[i+0] & 0xFF);
			i = 3 * (Math.min(ti + 1, height-1) * width + (si + 1) % width);
			r += sw * tw * (data[i+2] & 0xFF);
			g += sw * tw * (data[i+1] & 0xFF);
			b += sw * tw * (data[i+0] & 0xFF);
			r >>= 16;
			g >>= 16;
			b >>= 16;
		} catch (IndexOutOfBoundsException e) {
			System.err.println("AIOOB: " + e.getMessage() + " ti: " + ti + " si:" + si + " limit: " + data.length + " tt:" + tt + " ss:" + ss);
		}
		return b | (g << 8) | (r << 16);
	}

}
