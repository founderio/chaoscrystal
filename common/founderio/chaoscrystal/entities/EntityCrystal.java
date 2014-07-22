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
		setSize(0.75F, 0.75F);
		isImmuneToFire = true;
	}

	public EntityCrystal(World world, double x, double y, double z, float yaw,
			float pitch) {
		this(world);
		setPosition(x, y, z);
		setRotation(yaw, pitch);
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
		if (worldObj.isRemote) {
			return true;
		}

		playSound("mob.blaze.hit", 1, .2f);

		EntityItem item = new EntityItem(worldObj, posX, posY,
				posZ, buildItemStack());
		item.delayBeforeCanPickup = 0;

		worldObj.spawnEntityInWorld(item);

		setDead();

		return true;
	}

	@Override
	public void onStruckByLightning(EntityLightningBolt par1EntityLightningBolt) {
		if (worldObj.isRemote) {
			return;
		}

		playSound("mob.blaze.death", 1, .2f);
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

		worldObj.theProfiler.endSection();
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
