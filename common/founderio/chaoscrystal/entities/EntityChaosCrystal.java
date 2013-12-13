package founderio.chaoscrystal.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import founderio.chaoscrystal.ChaosCrystalMain;
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

	public NBTTagCompound aspectStore;
	public int age;
	public int lastDegrade = 0;
	public final int degradeInterval = 1;
	public boolean isInSuckMode = true;
	
	@Override
	public boolean canBeCollidedWith() {
		return true;
	}
	
	@Override
	protected void entityInit() {
		this.age = 0;
		this.aspectStore = new NBTTagCompound();
	}
	
	@Override
	public void onUpdate() {
        this.worldObj.theProfiler.startSection("entityBaseTick");
        this.age++;
        if(!this.worldObj.isRemote) {
        	if(age-lastDegrade > degradeInterval) {
        		lastDegrade = age;
        		if(isInSuckMode) {
        			DegradationHelper.suckAspect(this, worldObj, aspectStore, (int)posX, (int)posY, (int)posZ);
        		} else {
        			DegradationHelper.releaseAspect(this, worldObj, aspectStore, (int)posX, (int)posY, (int)posZ);
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

	public ItemStack buildItemStack() {
		ItemStack is = new ItemStack(ChaosCrystalMain.itemChaosCrystal);
		NBTTagCompound comp = new NBTTagCompound();
		comp.setCompoundTag("aspectStore", aspectStore);
		
		is.setTagCompound(comp);
		return is;
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		aspectStore = nbttagcompound.getCompoundTag("aspectStore");
		if(aspectStore == null) {
			aspectStore = new NBTTagCompound();
		}
		age = nbttagcompound.getInteger("age");
		lastDegrade = nbttagcompound.getInteger("lastDegrade");
		isInSuckMode = nbttagcompound.getBoolean("suckMode");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setCompoundTag("aspectStore", aspectStore);
		nbttagcompound.setInteger("age", age);
		nbttagcompound.setInteger("lastDegrade", lastDegrade);
		nbttagcompound.setBoolean("suckMode", isInSuckMode);
	}

}
