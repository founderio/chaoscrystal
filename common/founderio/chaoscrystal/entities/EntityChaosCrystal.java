package founderio.chaoscrystal.entities;

import founderio.chaoscrystal.ChaosCrystalMain;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

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

	protected NBTTagCompound aspectStore;
	public int age;
	
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
        //TODO: Extraction, hurting, stuff...
        
        this.worldObj.theProfiler.endSection();
	}
	
	@Override
	public boolean hitByEntity(Entity par1Entity) {
		
		if(this.worldObj.isRemote) {
			return true;
		}
		
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
		comp.setInteger("age", age);
		
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
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setCompoundTag("aspectStore", aspectStore);
		nbttagcompound.setInteger("age", age);
	}

}
