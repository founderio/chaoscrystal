package founderio.chaoscrystal.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityFocus extends Entity {

	public EntityFocus(World par1World) {
		super(par1World);
        this.setSize(0.75F, 0.75F);
        lookHelper = new UniversalEntityLookHelper(this);
	}
	
	public EntityFocus(World par1World, double par2, double par4,
			double par6, float par7, float par8) {
		super(par1World);
        this.setSize(0.75F, 0.75F);
        this.setPosition(par2, par4, par6);
        this.setRotation(par7, par7);
        lookHelper = new UniversalEntityLookHelper(this);
	}
	
	private UniversalEntityLookHelper lookHelper;
	
	/**
	 * 0 = Transfer
	 * 1 = border
	 */
	public int mode = 0;
	public int age = 0;
	public int lastTransfer = 0;
	
	public final int transferRange = 20;
	public final int transferInterval = 30;

	@Override
	protected void entityInit() {
		this.age = 0;
	}
	
	@Override
	public void onUpdate() {
        this.worldObj.theProfiler.startSection("entityBaseTick");
        this.age++;
        if(!this.worldObj.isRemote) {
        	if(mode == 0) {
        		if(age-lastTransfer > transferInterval) {
            		lastTransfer = age;
            		
            		
            		
            		//TODO: find two random ChaosCrystals in vincinity
            		//TODO: look at target
            		//TODO: balance aspects
            		
            		
            		//this.rotationYaw
            		
                }
        	}
        	
        }
        
        
        this.worldObj.theProfiler.endSection();
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		mode = nbttagcompound.getInteger("mode");
		age = nbttagcompound.getInteger("age");
		lastTransfer = nbttagcompound.getInteger("lastTransfer");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setInteger("mode", mode);
		nbttagcompound.setInteger("age", age);
		nbttagcompound.setInteger("lastTransfer", lastTransfer);
	}

}
