package panoview;

public interface Lens {

	void setup(double dir, double tilt, double focalLength);
	
	int sample(TextureProjection textureProjection, Texture texture, double u, double v);
	
}
