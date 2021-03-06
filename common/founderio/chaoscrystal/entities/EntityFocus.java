package founderio.chaoscrystal.entities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public abstract class EntityFocus extends EntityCrystal {

	public static final float deltaRotation = 15f;

	public float lookX;
	public float lookY;
	public float lookZ;

	public EntityFocus(World world) {
		super(world);
	}

	public EntityFocus(World world, double x, double y, double z, float yaw,
			float pitch) {
		super(world, x, y, z, yaw, pitch);
	}

	@Override
	protected void entityInit() {
		dataWatcher.addObject(10, Float.valueOf(lookX));
		dataWatcher.addObject(11, Float.valueOf(lookY));
		dataWatcher.addObject(12, Float.valueOf(lookZ));
	}

	@Override
	public void playSpawnSound() {
		return;
	}

	@Override
	protected void visualUpdate() {
		lookX = dataWatcher.getWatchableObjectFloat(10);
		lookY = dataWatcher.getWatchableObjectFloat(11);
		lookZ = dataWatcher.getWatchableObjectFloat(12);

		double d0 = lookX - posX;
		double d1 = lookY - posY;
		double d2 = lookZ - posZ;
		float f3 = MathHelper.sqrt_double(d0 * d0 + d2 * d2);
		float targetYaw = (float) (Math.atan2(d0, d2) * 180.0D / Math.PI);
		float targetPitch = (float) (Math.atan2(d1, f3) * 180.0D / Math.PI);

		final float deltaRotation = 3f;
		float offYaw = targetYaw - rotationYaw;
		if (offYaw > deltaRotation) {
			offYaw = deltaRotation;
		}
		if (offYaw < -deltaRotation) {
			offYaw = -deltaRotation;
		}
		float offPitch = targetPitch - rotationPitch;
		if (offPitch > deltaRotation) {
			offPitch = deltaRotation;
		}
		if (offPitch < -deltaRotation) {
			offPitch = -deltaRotation;
		}
		rotationPitch += offPitch;
		rotationYaw += offYaw;
	}

	protected void updateLook() {
		dataWatcher.updateObject(10, Float.valueOf(lookX));
		dataWatcher.updateObject(11, Float.valueOf(lookY));
		dataWatcher.updateObject(12, Float.valueOf(lookZ));
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		age = nbttagcompound.getInteger("age");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setInteger("age", age);
	}

}
