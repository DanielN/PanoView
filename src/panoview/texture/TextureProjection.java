package panoview.texture;

public interface TextureProjection {

	/**
	 * Sample a texture by projecting a vector onto it.
	 * @param texture the texture to sample.
	 * @param x X-component of the vector.
	 * @param y Y-component of the vector.
	 * @param z Z-component of the vector.
	 * @return the sampled color (packed RGB).
	 */
	int sample(Texture texture, double x, double y, double z);
	
}
