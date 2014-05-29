package founderio.chaoscrystal.entities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
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
		
		this.sourcePosX = this.posX;
        this.sourcePosY = this.posY;
        this.sourcePosZ = this.posZ;

        this.motionX = targetX-posX;
        this.motionY = targetY-posY;
        this.motionZ = targetZ-posZ;
		
		float colorVariation = this.rand.nextFloat() * 0.6f + 0.4f;
		if(type == 1) { // extract
			this.particleRed = this.particleGreen = this.particleBlue = 1.0F * colorVariation;
			this.particleGreen *= 0.9F;
			this.particleRed *= 0.9F;
		} else { // infuse
			this.particleRed = this.particleGreen = this.particleBlue = 1.0F * colorVariation;
			this.particleGreen *= 0.3F;
			this.particleRed *= 0.3F;
		}

        this.particleMaxAge = 50;
        this.noClip = true;
        this.particleScale = 0.5f;
		
		this.setParticleTextureIndex((int) (Math.random() * 16.0D));
	}
	
	@Override
	public void onUpdate() {
		
		this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
		
        float f = (float)this.particleAge / (float)this.particleMaxAge;
        double px = this.sourcePosX + this.motionX * (double)f;
        double py = this.sourcePosY + this.motionY * (double)f;
        double pz = this.sourcePosZ + this.motionZ * (double)f;
        this.moveEntity(px - prevPosX, py - prevPosY, pz - prevPosZ);
        
        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setDead();
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
