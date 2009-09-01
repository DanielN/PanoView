package panoview.lens;

import panoview.texture.Texture;
import panoview.texture.TextureProjection;

public interface Lens {

	void setup(double dir, double tilt, double focalLength);
	
	int sample(TextureProjection textureProjection, Texture texture, double u, double v);
	
}
