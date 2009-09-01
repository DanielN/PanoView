package panoview.texture;

import panoview.util.FixMath;

public class EquirectangularTextureProjection implements TextureProjection {

	@Override
	public int sample(Texture texture, double x, double y, double z) {
		int d = FixMath.sqrt(FixMath.toFixed(x * x + z * z));
		int s = FixMath.atan2(FixMath.toFixed(x), FixMath.toFixed(z)) / 2 + FixMath.ONE/2;
		int t = FixMath.atan2(FixMath.toFixed(y), d) + FixMath.ONE/2;

		return texture.sample(s, t);
	}

}
