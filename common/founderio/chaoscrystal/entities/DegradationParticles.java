package founderio.chaoscrystal.entities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityPortalFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import founderio.chaoscrystal.Constants;

public class DegradationParticles extends EntityPortalFX {

	public static final ResourceLocation chaosParticles = new ResourceLocation(Constants.MOD_ID + ":textures/particle/chaosparticles.png");
	public static final ResourceLocation particleTextures = new ResourceLocation("textures/particle/particles.png");
	 
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
		this.setParticleTextureIndex((int)(Math.random() * 16.0D));
	}
	
	@Override
	public void renderParticle(Tessellator par1Tessellator, float par2,
			float par3, float par4, float par5, float par6, float par7) {
		Minecraft.getMinecraft().renderEngine.bindTexture(chaosParticles);
		super.renderParticle(par1Tessellator, par2, par3, par4, par5, par6, par7);
		Minecraft.getMinecraft().renderEngine.bindTexture(particleTextures);
	}
}
