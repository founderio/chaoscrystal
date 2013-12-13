package founderio.chaoscrystal.entities;

import net.minecraft.client.particle.EntityPortalFX;
import net.minecraft.world.World;

public class DegradationParticles extends EntityPortalFX {

	public DegradationParticles(World par1World, double par2, double par4,
			double par6, double par8, double par10, double par12, int type) {
		super(par1World, par2, par4, par6, par8, par10, par12);
		float f = this.rand.nextFloat() * 0.6F + 0.4F;
		switch (type) {
		case 1:
			this.particleRed = this.particleGreen = this.particleBlue = 1.0F * f;
			this.particleGreen *= 0.9F;
			this.particleRed *= 0.9F;
			break;
		default:
			this.particleRed = this.particleGreen = this.particleBlue = 1.0F * f;
			this.particleGreen *= 0.3F;
			this.particleRed *= 0.3F;
			break;
		}
	}
}
