package founderio.chaoscrystal.entities;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.ChaosCrystalNetworkHandler;
import founderio.chaoscrystal.degradation.Aspects;

public class EntityFocusTransfer extends Entity {

	public EntityFocusTransfer(World par1World) {
		super(par1World);
        this.setSize(0.75F, 0.75F);
	}
	
	public EntityFocusTransfer(World par1World, double par2, double par4,
			double par6, float par7, float par8) {
		super(par1World);
        this.setSize(0.75F, 0.75F);
        this.setPosition(par2, par4, par6);
        this.setRotation(par7, par8);
	}
	
	
	/**
	 * 0 = Transfer
	 * 1 = border
	 */
	public int age = 0;
	public int lastTransfer = 0;
	
	public final int transferRange = 20;
	public final int transferInterval = 60;
	public final float deltaRotation = 15f;
	
	public float lookX;
	public float lookY;
	public float lookZ;

	@Override
	protected void entityInit() {
		this.dataWatcher.addObject(10, Float.valueOf(lookX));
		this.dataWatcher.addObject(11, Float.valueOf(lookY));
		this.dataWatcher.addObject(12, Float.valueOf(lookZ));
	}
	
	@Override
	public boolean canBeCollidedWith() {
		return true;
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
		ItemStack is = new ItemStack(ChaosCrystalMain.itemFocus, 1, 0);
//		NBTTagCompound comp = new NBTTagCompound();
//		comp.setCompoundTag("aspectStore", aspectStore);
//		
//		is.setTagCompound(comp);
		return is;
	}
	
	@Override
	public void onUpdate() {
        this.worldObj.theProfiler.startSection("entityBaseTick");
        this.age++;
        if(!this.worldObj.isRemote) {

    		if(age-lastTransfer > transferInterval) {
        		lastTransfer = age;
        		List<EntityChaosCrystal> ents = new ArrayList<EntityChaosCrystal>();
        		for(Object obj : this.worldObj.loadedEntityList) {
        			if(obj instanceof EntityChaosCrystal) {
        				double distX = ((EntityChaosCrystal) obj).posX - posX;
        				double distY = ((EntityChaosCrystal) obj).posY - posY;
        				double distZ = ((EntityChaosCrystal) obj).posZ - posZ;
        				if(distX*distX + distY*distY + distZ*distZ < transferRange*transferRange) {
        					ents.add((EntityChaosCrystal)obj);
        				}
        			}
        		}
        		if(ents.size() >= 2) {
        			EntityChaosCrystal crystal1 = (EntityChaosCrystal) ents.get(this.rand.nextInt(ents.size()));
            		EntityChaosCrystal crystal2 = (EntityChaosCrystal) ents.get(this.rand.nextInt(ents.size()));
            		
            		lookX = (float)crystal1.posX;
            		lookY = (float)(crystal1.boundingBox.minY + crystal1.boundingBox.maxY) / 2.0f;
            		lookZ = (float)crystal1.posZ;

    	    		this.dataWatcher.updateObject(10, Float.valueOf(lookX));
    	    		this.dataWatcher.updateObject(11, Float.valueOf(lookY));
    	    		this.dataWatcher.updateObject(12, Float.valueOf(lookZ));
            		
    	    		ChaosCrystalNetworkHandler.spawnParticleEffects(this, crystal2, 1);
    	    		ChaosCrystalNetworkHandler.spawnParticleEffects(crystal1, this, 1);
    	    		
    	    		
            		
            		for(String aspect : Aspects.ASPECTS) {
            			int aspects = crystal1.getAspect(aspect) + crystal2.getAspect(aspect);
            			int asp1 = aspects/2;
            			int asp2 = aspects - asp1;
            			crystal1.setAspect(aspect, asp1);
            			crystal2.setAspect(aspect, asp2);
            		}
            		
        		}
        	}
        }
        
        lookX = this.dataWatcher.getWatchableObjectFloat(10);
        lookY = this.dataWatcher.getWatchableObjectFloat(11);
        lookZ = this.dataWatcher.getWatchableObjectFloat(12);

        double d0 = lookX - posX;
        double d1 = lookY - posY;
        double d2 = lookZ - posZ;
        float f3 = MathHelper.sqrt_double(d0 * d0 + d2 * d2);
        float targetYaw = (float)(Math.atan2(d0, d2) * 180.0D / Math.PI);
        float targetPitch = (float)(Math.atan2(d1, (double)f3) * 180.0D / Math.PI);

        final float deltaRotation = 3f;
        float offYaw = targetYaw - rotationYaw;
        if(offYaw > deltaRotation) {
        	offYaw = deltaRotation;
        }
        if(offYaw < -deltaRotation) {
        	offYaw = -deltaRotation;
        }
        float offPitch = targetPitch - rotationPitch;
        if(offPitch > deltaRotation) {
        	offPitch = deltaRotation;
        }
        if(offPitch < -deltaRotation) {
        	offPitch = -deltaRotation;
        }
        this.rotationPitch += offPitch;
        this.rotationYaw += offYaw;
        this.worldObj.theProfiler.endSection();
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		age = nbttagcompound.getInteger("age");
		lastTransfer = nbttagcompound.getInteger("lastTransfer");
		lookX = nbttagcompound.getFloat("lookX");
		lookY = nbttagcompound.getFloat("lookY");
		lookZ = nbttagcompound.getFloat("lookZ");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setInteger("age", age);
		nbttagcompound.setInteger("lastTransfer", lastTransfer);
		nbttagcompound.setFloat("lookX", lookX);
		nbttagcompound.setFloat("lookY", lookY);
		nbttagcompound.setFloat("lookZ", lookZ);
	}

}
