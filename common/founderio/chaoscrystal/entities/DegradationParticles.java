package founderio.chaoscrystal.entities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.Constants;

public class DegradationParticles extends EntityFX {

	public static final ResourceLocation CHAOS_TEX = new ResourceLocation(
			Constants.MOD_ID + ":textures/particle/chaosparticles.png");
	public static final ResourceLocation DEFAULT_TEX = new ResourceLocation(
			"textures/particle/particles.png");


	private final double sourcePosX;
	private final double sourcePosY;
	private final double sourcePosZ;

	public DegradationParticles(World par1World,
			double posX, double posY, double posZ,
			double targetX, double targetY, double targetZ,
			int type) {
		super(par1World, posX, posY, posZ);

		sourcePosX = this.posX;
		sourcePosY = this.posY;
		sourcePosZ = this.posZ;

		motionX = targetX-posX;
		motionY = targetY-posY;
		motionZ = targetZ-posZ;

		float colorVariation = rand.nextFloat() * 0.6f + 0.4f;
		if(type == 1) { // extract
			particleRed = particleGreen = particleBlue = 1.0F * colorVariation;
			particleGreen *= 0.9F;
			particleRed *= 0.9F;
		} else { // infuse
			particleRed = particleGreen = particleBlue = 1.0F * colorVariation;
			particleGreen *= 0.3F;
			particleRed *= 0.3F;
		}

		particleMaxAge = 50;
		noClip = true;
		particleScale = 0.5f;

		setParticleTextureIndex(ChaosCrystalMain.rand.nextInt(17));
	}

	@Override
	public void onUpdate() {

		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;

		float f = (float)particleAge / (float)particleMaxAge;
		double px = sourcePosX + motionX * f;
		double py = sourcePosY + motionY * f;
		double pz = sourcePosZ + motionZ * f;
		moveEntity(px - prevPosX, py - prevPosY, pz - prevPosZ);

		if (particleAge++ >= particleMaxAge)
		{
			setDead();
		}
	}

	@Override
	public void renderParticle(Tessellator tessellator, float par2,
			float par3, float par4, float par5, float par6, float par7) {

		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setBrightness(getBrightnessForRender(par2));
		Minecraft.getMinecraft().renderEngine.bindTexture(CHAOS_TEX);

		super.renderParticle(tessellator, par2, par3, par4, par5, par6, par7);

		tessellator.draw();
		tessellator.startDrawingQuads();

		Minecraft.getMinecraft().renderEngine.bindTexture(DEFAULT_TEX);
	}
}
