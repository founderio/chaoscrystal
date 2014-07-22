package founderio.chaoscrystal.entities;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.Config;
import founderio.chaoscrystal.aspects.Aspect;
import founderio.chaoscrystal.aspects.DegradationHelper;
import founderio.chaoscrystal.aspects.IAspectStore;
import founderio.util.GeometryHelper;

public class EntityChaosCrystal extends EntityCrystal implements IAspectStore {

	public EntityChaosCrystal(World world) {
		super(world);
		tickInterval = Config.cfgCrystalTickInterval;
	}

	public EntityChaosCrystal(World world, double x, double y, double z,
			float yaw, float pitch) {
		super(world, x, y, z, yaw, pitch);
		tickInterval = Config.cfgCrystalTickInterval;
	}

	@Override
	protected void entityInit() {
		dataWatcher.addObject(9, Byte.valueOf((byte) 0));
		for (int i = 0; i < Aspect.values().length; i++) {
			dataWatcher.addObject(10 + i, Integer.valueOf(0));
		}
	}

	public boolean isInSuckMode() {
		return dataWatcher.getWatchableObjectByte(9) == 1;
	}

	public void setSuckMode(boolean value) {
		dataWatcher.updateObject(9, Byte.valueOf((byte) (value ? 1 : 0)));
	}

	@Override
	public int getAspect(Aspect aspect) {
		return dataWatcher.getWatchableObjectInt(10 + aspect.ordinal());
	}

	@Override
	public void setAspect(Aspect aspect, int value) {
		dataWatcher.updateObject(10 + aspect.ordinal(), Integer.valueOf(value));
	}

	@Override
	public int getSingleAspectCapacity() {
		return Config.cfgCrystalAspectStorage;
	}

	@Override
	public void addAspects(int[] aspectArray) {
		for (int i = 0; i < Aspect.values().length; i++) {
			int aspectCount = dataWatcher.getWatchableObjectInt(10 + i)
					+ aspectArray[i];
			dataWatcher.updateObject(10 + i, Integer.valueOf(aspectCount));
		}
	}

	@Override
	public void subtractAspects(int[] aspectArray) {
		for (int i = 0; i < Aspect.values().length; i++) {
			int aspectCount = dataWatcher.getWatchableObjectInt(10 + i)
					- aspectArray[i];
			dataWatcher.updateObject(10 + i, Integer.valueOf(aspectCount));
		}
	}

	@Override
	public boolean canProvideAspects(int[] aspectArray) {
		for (int i = 0; i < Aspect.values().length; i++) {
			int aspectCount = dataWatcher.getWatchableObjectInt(10 + i);
			if (aspectCount - aspectArray[i] < 0) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean canAcceptAspects(int[] aspectArray) {
		for (int i = 0; i < Aspect.values().length; i++) {
			int aspectCount = dataWatcher.getWatchableObjectInt(10 + i);
			if (aspectCount + aspectArray[i] > getSingleAspectCapacity()) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void logicUpdate() {
		List<Aspect> filterAspects = new ArrayList<Aspect>();
		List<String> filterTargets = new ArrayList<String>();
		double range = Config.cfgCrystalRange;

		for (Object obj : worldObj.loadedEntityList) {
			if (obj instanceof EntityFocusFilter) {
				double tmp_dist = GeometryHelper.entityDistance((Entity) obj,
						this);
				if (tmp_dist < Config.cfgFocusRange) {
					Aspect asp = ((EntityFocusFilter) obj).getAspect();
					if (!filterAspects.contains(asp)) {
						filterAspects.add(asp);
					}
				}
			} else if (obj instanceof EntityFocusFilterTarget) {
				double tmp_dist = GeometryHelper.entityDistance((Entity) obj,
						this);
				if (tmp_dist < Config.cfgFocusRange) {
					String target = ((EntityFocusFilterTarget) obj).getTarget();
					if (!filterTargets.contains(target)) {
						filterTargets.add(target);
					}
				}
			} else if (obj instanceof EntityFocusBorder) {
				double tmp_dist = GeometryHelper.entityDistance((Entity) obj,
						this);
				if (tmp_dist < range) {
					range = tmp_dist;
				}
			}
		}

		DegradationHelper.crystalTick(this, worldObj, posX, posY,
				posZ, filterAspects, filterTargets, range, isInSuckMode());

	}

	@Override
	protected void visualUpdate() {

	}

	@Override
	public void playSpawnSound() {
		playSound("mob.wither.death", 1, .2f);
		playSound("mob.enderdragon.end", 1, .2f);
	}

	private NBTTagCompound getAspectStore() {
		NBTTagCompound comp = new NBTTagCompound();
		for (Aspect aspect : Aspect.values()) {
			comp.setInteger(aspect.stringRep, getAspect(aspect));
		}
		return comp;
	}

	@Override
	public ItemStack buildItemStack() {
		ItemStack is = new ItemStack(ChaosCrystalMain.itemChaosCrystal);
		NBTTagCompound comp = new NBTTagCompound();
		comp.setTag("aspectStore", getAspectStore());

		is.setTagCompound(comp);
		return is;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		NBTTagCompound aspectStore = nbttagcompound
				.getCompoundTag("aspectStore");
		if (aspectStore != null) {
			for (Aspect aspect : Aspect.values()) {
				setAspect(aspect, aspectStore.getInteger(aspect.stringRep));
			}
		}
		setSuckMode(nbttagcompound.getBoolean("suckMode"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setTag("aspectStore", getAspectStore());
		nbttagcompound.setBoolean("suckMode", isInSuckMode());
	}

}
