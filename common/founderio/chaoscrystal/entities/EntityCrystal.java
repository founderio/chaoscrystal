package founderio.chaoscrystal.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public abstract class EntityCrystal extends Entity {

	public int age = 0;
	public int tickInterval = 1;

	public EntityCrystal(World world) {
		super(world);
		this.setSize(0.75F, 0.75F);
		this.isImmuneToFire = true;
	}

	public EntityCrystal(World world, double x, double y, double z, float yaw,
			float pitch) {
		this(world);
		this.setPosition(x, y, z);
		this.setRotation(yaw, pitch);
	}

	protected abstract void logicUpdate();

	protected abstract void visualUpdate();

	public abstract ItemStack buildItemStack();

	public abstract void playSpawnSound();

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public boolean hitByEntity(Entity par1Entity) {
		if (this.worldObj.isRemote) {
			return true;
		}

		this.playSound("mob.blaze.hit", 1, .2f);

		EntityItem item = new EntityItem(this.worldObj, this.posX, this.posY,
				this.posZ, buildItemStack());
		item.delayBeforeCanPickup = 0;

		this.worldObj.spawnEntityInWorld(item);

		this.setDead();

		return true;
	}

	@Override
	public void onStruckByLightning(EntityLightningBolt par1EntityLightningBolt) {
		if (this.worldObj.isRemote) {
			return;
		}

		this.playSound("mob.blaze.death", 1, .2f);
	}

	@Override
	public void onUpdate() {
		worldObj.theProfiler.startSection("entityBaseTick");

		age++;
		if (worldObj.isRemote) {
			// Render Tick
			age %= 360;
		} else {
			// Logic Tick
			if (age > tickInterval) {
				age -= tickInterval;
				logicUpdate();
			}
		}

		visualUpdate();

		this.worldObj.theProfiler.endSection();
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		age = nbttagcompound.getInteger("age");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setInteger("age", age);
	}

}
