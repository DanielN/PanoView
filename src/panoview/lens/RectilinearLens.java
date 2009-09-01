package panoview.lens;

import panoview.texture.Texture;
import panoview.texture.TextureProjection;

public class RectilinearLens implements Lens {

	private double cosTilt;
	private double sinDir;
	private double cosDir;
	private double sinTiltSinDir;
	private double sinTiltCosDir;
	private double zSinTilt;
	private double zCosTiltSinDir;
	private double zCosTiltCosDir;

	@Override
	public void setup(double dir, double tilt, double focalLength) {
		final double sinTilt = Math.sin(tilt);
		cosTilt = Math.cos(tilt);
		sinDir = Math.sin(dir);
		cosDir = Math.cos(dir);
		sinTiltSinDir = sinTilt * sinDir;
		sinTiltCosDir = sinTilt * cosDir;
		zSinTilt = focalLength * sinTilt;
		zCosTiltSinDir = focalLength * cosTilt * sinDir;
		zCosTiltCosDir = focalLength * cosTilt * cosDir;
	}

	@Override
	public int sample(TextureProjection textureProjection, Texture texture,	double u, double v) {
		double ry = v * cosTilt - zSinTilt;
		double rx =   u * cosDir + v * sinTiltSinDir + zCosTiltSinDir;
		double rz = - u * sinDir + v * sinTiltCosDir + zCosTiltCosDir;

		return textureProjection.sample(texture, rx, ry, rz);
	}

}
