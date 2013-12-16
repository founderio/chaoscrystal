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
import founderio.chaoscrystal.degradation.Aspects;

public class EntityFocusFilter extends Entity {

	public EntityFocusFilter(World par1World) {
		super(par1World);
        this.setSize(0.75F, 0.75F);
	}
	
	public EntityFocusFilter(World par1World, double par2, double par4,
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
	
	public float lookX;
	public float lookY;
	public float lookZ;
	
	
	public static final int focusRange = 10;

	@Override
	protected void entityInit() {
		this.dataWatcher.addObject(30, Aspects.ASPECTS[0]);
		this.dataWatcher.addObject(10, Float.valueOf(lookX));
		this.dataWatcher.addObject(11, Float.valueOf(lookY));
		this.dataWatcher.addObject(12, Float.valueOf(lookZ));
	}
	
	public String getAspect() {
		return this.dataWatcher.getWatchableObjectString(30);
	}
	
	public void setAspect(String aspect) {
		this.dataWatcher.updateObject(30, aspect);
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
		ItemStack is = new ItemStack(ChaosCrystalMain.itemFocus, 1, 2);
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
        	List<EntityChaosCrystal> crystals = new ArrayList<EntityChaosCrystal>();
    		 
            for(Object obj : this.worldObj.loadedEntityList) {
    			if(obj instanceof EntityChaosCrystal) {
    				double distX = ((EntityChaosCrystal) obj).posX - posX;
    				double distY = ((EntityChaosCrystal) obj).posY - posY;
    				double distZ = ((EntityChaosCrystal) obj).posZ - posZ;
    				double tmp_dist = Math.sqrt(distX*distX + distY*distY + distZ*distZ);
    				if(tmp_dist < focusRange) {
    					crystals.add((EntityChaosCrystal)obj);
    				}
    			}
    		}
            
            if(!crystals.isEmpty()) {
            	EntityChaosCrystal crystal1 = crystals.get(rand.nextInt(crystals.size()));
            	lookX = (float)crystal1.posX;
	    		lookY = (float)(crystal1.boundingBox.minY + crystal1.boundingBox.maxY) / 2.0F;
	    		lookZ = (float)crystal1.posZ;
	    		this.dataWatcher.updateObject(10, Float.valueOf(lookX));
	    		this.dataWatcher.updateObject(11, Float.valueOf(lookY));
	    		this.dataWatcher.updateObject(12, Float.valueOf(lookZ));
            } else if(age % 20 == 0) {
            	lookX = (float)posX + (this.rand.nextFloat() - 0.5f) * 10;
            	lookY = (float)posY;
            	lookZ = (float)posZ + (this.rand.nextFloat() - 0.5f) * 10;
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
//		age = nbttagcompound.getInteger("age");
		lookX = nbttagcompound.getFloat("lookX");
		lookY = nbttagcompound.getFloat("lookY");
		lookZ = nbttagcompound.getFloat("lookZ");
		String aspect = nbttagcompound.getString("aspect");
		if(!Aspects.isAspect(aspect)) {
			aspect = Aspects.ASPECTS[0];
		}
		setAspect(aspect);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
//		nbttagcompound.setInteger("age", age);
		nbttagcompound.setFloat("lookX", lookX);
		nbttagcompound.setFloat("lookY", lookY);
		nbttagcompound.setFloat("lookZ", lookZ);
		nbttagcompound.setString("aspect", getAspect());
	}

}
