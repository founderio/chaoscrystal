package founderio.chaoscrystal.entities;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.degradation.Aspects;
import founderio.chaoscrystal.degradation.DegradationHelper;

public class EntityChaosCrystal extends Entity {

	
	public EntityChaosCrystal(World par1World) {
		super(par1World);
        this.setSize(0.75F, 0.75F);
	}
	
	public EntityChaosCrystal(World par1World, double par2, double par4,
			double par6) {
		super(par1World);
        this.setSize(0.75F, 0.75F);
        this.setPosition(par2, par4, par6);
	}

	public int age;
	public int lastDegrade = 0;
	public final int degradeInterval = 1;
	
	@Override
	public boolean canBeCollidedWith() {
		return true;
	}
	
	@Override
	protected void entityInit() {
		this.age = 0;
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
	
	public int getAspect(String aspect) {
		return this.dataWatcher.getWatchableObjectInt(10 + Aspects.getAspectDisplayId(aspect));
	}
	
	public void setAspect(String aspect, int value) {
		this.dataWatcher.updateObject(10 + Aspects.getAspectDisplayId(aspect), Integer.valueOf(value));
	}
	
	@Override
	public void onUpdate() {
        this.worldObj.theProfiler.startSection("entityBaseTick");
        this.age++;
        if(!this.worldObj.isRemote) {
        	if(age-lastDegrade > degradeInterval) {
        		
        		List<String> filterAspects = new ArrayList<String>();
        		
        		for(Object obj : this.worldObj.loadedEntityList) {
        			if(obj instanceof EntityFocusFilter) {
        				double distX = ((EntityFocusFilter) obj).posX - posX;
        				double distY = ((EntityFocusFilter) obj).posY - posY;
        				double distZ = ((EntityFocusFilter) obj).posZ - posZ;
        				double tmp_dist = distX*distX + distY*distY + distZ*distZ;
        				if(tmp_dist < EntityFocusFilter.focusRange*EntityFocusFilter.focusRange) {
        					String asp = ((EntityFocusFilter)obj).getAspect();
        					if(!filterAspects.contains(asp)) {
        						filterAspects.add(asp);
        					}
        				}
        			}
        		}
        		
        		double range = DegradationHelper.degradeRange * DegradationHelper.degradeRange * DegradationHelper.degradeRange;
        		
        		for(Object obj : this.worldObj.loadedEntityList) {
        			if(obj instanceof EntityFocusBorder) {
        				double distX = ((EntityFocusBorder) obj).posX - posX;
        				double distY = ((EntityFocusBorder) obj).posY - posY;
        				double distZ = ((EntityFocusBorder) obj).posZ - posZ;
        				double tmp_dist = distX*distX + distY*distY + distZ*distZ;
        				if(tmp_dist < range) {
        					range = tmp_dist;
        				}
        			}
        		}
        		
        		lastDegrade = age;
        		if(isInSuckMode()) {
        			DegradationHelper.suckAspect(this, worldObj, (int)posX, (int)posY, (int)posZ, filterAspects, Math.sqrt(range));
        		} else {
        			DegradationHelper.releaseAspect(this, worldObj, (int)posX, (int)posY, (int)posZ, filterAspects, Math.sqrt(range));
        		}
        		
            }
        }
        
        
        this.worldObj.theProfiler.endSection();
	}
	
	@Override
	public void onStruckByLightning(EntityLightningBolt par1EntityLightningBolt) {
		this.playSound("mob.blaze.death", 1, .2f);
	}
	
	public void playSpawnSound() {
		this.playSound("mob.wither.death", 1, .2f);
		this.playSound("mob.enderdragon.end", 1, .2f);
	}
	
	@Override
	public boolean hitByEntity(Entity par1Entity) {
		
		if(this.worldObj.isRemote) {
			return true;
		}

		this.playSound("mob.blaze.hit", 1, .2f);
		
		EntityItem item = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, buildItemStack());
		item.delayBeforeCanPickup = 0;
		
		this.worldObj.spawnEntityInWorld(item);
		
		this.setDead();
		
		return true;
	}
	
	private NBTTagCompound getAspectStore() {
		NBTTagCompound comp = new NBTTagCompound();
		for(String aspect : Aspects.ASPECTS) {
			comp.setInteger(aspect, getAspect(aspect));
		}
		return comp;
	}

	public ItemStack buildItemStack() {
		ItemStack is = new ItemStack(ChaosCrystalMain.itemChaosCrystal);
		NBTTagCompound comp = new NBTTagCompound();
		comp.setCompoundTag("aspectStore", getAspectStore());
		
		is.setTagCompound(comp);
		return is;
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		NBTTagCompound aspectStore = nbttagcompound.getCompoundTag("aspectStore");
		if(aspectStore != null) {
			if (aspectStore != null) {
				for(String aspect : Aspects.ASPECTS) {
					setAspect(aspect, aspectStore.getInteger(aspect));
				}
			}
		}
		age = nbttagcompound.getInteger("age");
		lastDegrade = nbttagcompound.getInteger("lastDegrade");
		setSuckMode(nbttagcompound.getBoolean("suckMode"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setCompoundTag("aspectStore", getAspectStore());
		nbttagcompound.setInteger("age", age);
		nbttagcompound.setInteger("lastDegrade", lastDegrade);
		nbttagcompound.setBoolean("suckMode", isInSuckMode());
	}

}
