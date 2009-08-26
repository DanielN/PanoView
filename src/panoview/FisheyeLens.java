package panoview;

public class FisheyeLens implements Lens {

	private double inv2f;
	private double sinTilt;
	private double cosTilt;
	private double sinDir;
	private double cosDir;
	private double sinTiltSinDir;
	private double sinTiltCosDir;
	private double cosTiltSinDir;
	private double cosTiltCosDir;

	/*
	 * r = 2fsin(phi / 2)
	 * r / (2f) = sin(phi/2)
	 * asin(r / (2f)) = phi/2
	 * phi = 2asin(r / (2f))
	 * r = sqrt(x^2 + y^2)
	 * a = atan2(x, y)
	 * 
	 * rx = sin(phi) * cos(a)
	 * ry = sin(phi) * sin(a)
	 * rz = cos(phi)
	 */
	
	@Override
	public void setup(double dir, double tilt, double focalLength) {
		inv2f = 1 / (2*focalLength);
		sinTilt = Math.sin(tilt);
		cosTilt = Math.cos(tilt);
		sinDir = Math.sin(dir);
		cosDir = Math.cos(dir);
		sinTiltSinDir = sinTilt * sinDir;
		sinTiltCosDir = sinTilt * cosDir;
		cosTiltSinDir = cosTilt * sinDir;
		cosTiltCosDir = cosTilt * cosDir;
	}

	@Override
	public int sample(TextureProjection textureProjection, Texture texture, double u, double v) {
		// Fisheye projection is defined by: r = 2 * f * sin(phi / 2)
		// We seek the inverse projection: phi = 2 * asin(r / (2 * f))
		// Distance from image center: r = sqrt(u^2 + v^2)
		// Angle from to x axis: a = atan2(y, x)
		// Formulas from polar to x,y,z:
		// x = sin(phi) * cos(a)
		// y = sin(phi) * sin(a)
		// z = cos(phi)
		// Identities used in simplification:
		// sin(2 * asin(r)) = 2 * sqrt(1 - r^2)
		// cos(2 * asin(r)) = 1 - 2 * r^2
		u *= inv2f;
		v *= inv2f;
		double r2 = u*u + v*v;
		if (r2 > 1.0) {
			return 0;
		}
		double d = FixMath.toDouble(2 * FixMath.sqrt(FixMath.toFixed(1 - r2)));
//		double d = 2 * Math.sqrt(1 - r2);
		double x = u * d;
		double y = v * d;
		double z = 1 - 2 * r2;

		double ry = y * cosTilt - z * sinTilt;
		double rx =   x * cosDir + y * sinTiltSinDir + z * cosTiltSinDir;
		double rz = - x * sinDir + y * sinTiltCosDir + z * cosTiltCosDir;
		return textureProjection.sample(texture, rx, ry, rz);
	}

}
