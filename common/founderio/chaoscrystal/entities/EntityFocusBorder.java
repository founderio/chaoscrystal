package founderio.chaoscrystal.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import founderio.chaoscrystal.ChaosCrystalMain;

public class EntityFocusBorder extends Entity {

	public EntityFocusBorder(World par1World) {
		super(par1World);
        this.setSize(0.75F, 0.75F);
	}
	
	public EntityFocusBorder(World par1World, double par2, double par4,
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
//	public int age = 0;

	@Override
	protected void entityInit() {
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
		ItemStack is = new ItemStack(ChaosCrystalMain.itemFocus, 1, 1);
//		NBTTagCompound comp = new NBTTagCompound();
//		comp.setCompoundTag("aspectStore", aspectStore);
//		
//		is.setTagCompound(comp);
		return is;
	}
	
	@Override
	public void onUpdate() {
        this.worldObj.theProfiler.startSection("entityBaseTick");
//        this.age++;
//        if(!this.worldObj.isRemote) {
//        }
//        
//        double d0 = lookX - posX;
//        double d1 = lookY - posY;
//        double d2 = lookZ - posZ;
//        float f3 = MathHelper.sqrt_double(d0 * d0 + d2 * d2);
//        rotationYaw = (float)(Math.atan2(d0, d2) * 180.0D / Math.PI);
//        rotationPitch = (float)(Math.atan2(d1, (double)f3) * 180.0D / Math.PI);
//        
       // float offYaw = targetYaw - rotationYaw;
//        if(offYaw > deltaRotation) {
//        	offYaw = deltaRotation;
//        }
//        if(offYaw < -deltaRotation) {
//        	offYaw = -deltaRotation;
//        }
       // float offPitch = targetPitch - rotationPitch;
//        if(offPitch > deltaRotation) {
//        	offPitch = deltaRotation;
//        }
//        if(offPitch < -deltaRotation) {
//        	offPitch = -deltaRotation;
//        }
       // this.rotationPitch = targetPitch;
       // this.rotationYaw = targetYaw;
        this.worldObj.theProfiler.endSection();
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
//		age = nbttagcompound.getInteger("age");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
//		nbttagcompound.setInteger("age", age);
	}

}
