package founderio.chaoscrystal.entities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import founderio.chaoscrystal.blocks.DamageSourceSentrySnowball;

public class EntityPlayerAwareSnowball extends EntitySnowball {

	public EntityPlayerAwareSnowball(World par1World,
			EntityLivingBase par2EntityLivingBase) {
		super(par1World, par2EntityLivingBase);
	}

	public EntityPlayerAwareSnowball(World par1World, double par2, double par4,
			double par6) {
		super(par1World, par2, par4, par6);
	}

	public EntityPlayerAwareSnowball(World par1World) {
		super(par1World);
	}

	@Override
	protected void onImpact(
			MovingObjectPosition par1MovingObjectPosition) {
		if (par1MovingObjectPosition.entityHit != null)
		{
			float b0 = 0.0000000000001f;

			if (par1MovingObjectPosition.entityHit instanceof EntityBlaze)
			{
				b0 = 3;
			}


			DamageSource dmgSource = new DamageSourceSentrySnowball("thrown", this, getThrower());

			par1MovingObjectPosition.entityHit.attackEntityFrom(dmgSource, b0);
			float i = 0.1f;//knockback factor

			par1MovingObjectPosition.entityHit.addVelocity(motionX * i, motionY * i, motionZ * i);


		}

		for (int i = 0; i < 8; ++i)
		{
			worldObj.spawnParticle("snowballpoof", posX, posY, posZ, 0.0D, 0.0D, 0.0D);
		}

		if (!worldObj.isRemote)
		{
			setDead();
		}
	}

}
