package founderio.chaoscrystal.entities;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.degradation.Aspects;
import founderio.chaoscrystal.degradation.DegradationHelper;
import founderio.chaoscrystal.degradation.IAspectStore;
import founderio.util.GeometryHelper;

public class EntityChaosCrystal extends EntityCrystal implements IAspectStore {

	public EntityChaosCrystal(World world) {
		super(world);
        this.tickInterval = ChaosCrystalMain.cfgCrystalTickInterval;
	}

	public EntityChaosCrystal(World world, double x, double y,
			double z, float yaw, float pitch) {
		super(world, x, y, z, yaw, pitch);
        this.tickInterval = ChaosCrystalMain.cfgCrystalTickInterval;
	}
	
	@Override
	protected void entityInit() {
		this.dataWatcher.addObject(9, Byte.valueOf((byte) 0));
		for(int i = 0; i < Aspects.ASPECTS.length; i++) {
			this.dataWatcher.addObject(10 + i, Integer.valueOf(0));
		}
	}
	
	public boolean isInSuckMode() {
		return this.dataWatcher.getWatchableObjectByte(9) == 1;
	}
	
	public void setSuckMode(boolean value) {
		this.dataWatcher.updateObject(9, Byte.valueOf((byte)(value ? 1 : 0)));
	}

	@Override
	public int getAspect(String aspect) {
		return this.dataWatcher.getWatchableObjectInt(10 + Aspects.getAspectIndex(aspect));
	}

	@Override
	public void setAspect(String aspect, int value) {
		this.dataWatcher.updateObject(10 + Aspects.getAspectIndex(aspect), Integer.valueOf(value));
	}
	
	@Override
	public int getSingleAspectCapacity() {
		return ChaosCrystalMain.cfgCrystalAspectStorage;
	}
	
	@Override
	public void addAspects(int[] aspectArray) {
		for(int i = 0; i < Aspects.ASPECTS.length; i++) {
			int aspectCount = this.dataWatcher.getWatchableObjectInt(10 + i) + aspectArray[i];
			this.dataWatcher.updateObject(10 + i, Integer.valueOf(aspectCount));
		}
	}

	@Override
	public void subtractAspects(int[] aspectArray) {
		for(int i = 0; i < Aspects.ASPECTS.length; i++) {
			int aspectCount = this.dataWatcher.getWatchableObjectInt(10 + i) - aspectArray[i];
			this.dataWatcher.updateObject(10 + i, Integer.valueOf(aspectCount));
		}
	}

	@Override
	public boolean canProvideAspects(int[] aspectArray) {
		for(int i = 0; i < Aspects.ASPECTS.length; i++) {
			int aspectCount = this.dataWatcher.getWatchableObjectInt(10 + i);
			if(aspectCount - aspectArray[i] < 0) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean canAcceptAspects(int[] aspectArray) {
		for(int i = 0; i < Aspects.ASPECTS.length; i++) {
			int aspectCount = this.dataWatcher.getWatchableObjectInt(10 + i);
			if(aspectCount + aspectArray[i] > getSingleAspectCapacity()) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	protected void logicUpdate() {
		List<String> filterAspects = new ArrayList<String>();
		
		for(Object obj : this.worldObj.loadedEntityList) {
			if(obj instanceof EntityFocusFilter) {
				double tmp_dist = GeometryHelper.entityDistance((Entity)obj, this);
				if(tmp_dist < EntityFocusFilter.focusRange) {
					String asp = ((EntityFocusFilter)obj).getAspect();
					if(!filterAspects.contains(asp)) {
						filterAspects.add(asp);
					}
				}
			}
		}
		
		double range = ChaosCrystalMain.cfgCrystalRange;
		
		for(Object obj : this.worldObj.loadedEntityList) {
			if(obj instanceof EntityFocusBorder) {
				double tmp_dist = GeometryHelper.entityDistance((Entity)obj, this);
				if(tmp_dist < range) {
					range = tmp_dist;
				}
			}
		}
		
		DegradationHelper.crystalTick(this, worldObj, (int)posX, (int)posY, (int)posZ, filterAspects, range, !isInSuckMode());

	}

	@Override
	protected void visualUpdate() {
		
	}
	
	@Override
	public void playSpawnSound() {
		this.playSound("mob.wither.death", 1, .2f);
		this.playSound("mob.enderdragon.end", 1, .2f);
	}
	
	private NBTTagCompound getAspectStore() {
		NBTTagCompound comp = new NBTTagCompound();
		for(String aspect : Aspects.ASPECTS) {
			comp.setInteger(aspect, getAspect(aspect));
		}
		return comp;
	}

	@Override
	public ItemStack buildItemStack() {
		ItemStack is = new ItemStack(ChaosCrystalMain.itemChaosCrystal);
		NBTTagCompound comp = new NBTTagCompound();
		comp.setCompoundTag("aspectStore", getAspectStore());
		
		is.setTagCompound(comp);
		return is;
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		NBTTagCompound aspectStore = nbttagcompound.getCompoundTag("aspectStore");
		if(aspectStore != null) {
			for(String aspect : Aspects.ASPECTS) {
				setAspect(aspect, aspectStore.getInteger(aspect));
			}
		}
		setSuckMode(nbttagcompound.getBoolean("suckMode"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setCompoundTag("aspectStore", getAspectStore());
		nbttagcompound.setBoolean("suckMode", isInSuckMode());
	}

	

}
