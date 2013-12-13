package founderio.chaoscrystal.entities;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import cpw.mods.fml.common.network.PacketDispatcher;

import net.minecraft.client.particle.EntityPortalFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.Constants;
import founderio.chaoscrystal.degradation.Degradation;

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
	public final int degradeInterval = 5;
	public final int degradeRange = 10;
	public final int maxTries = 5;
	
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
        		boolean hit = false;
        		int tries = 0;
        		do {
            	int offX = this.rand.nextInt(degradeRange*2)-degradeRange;
            	int offY = this.rand.nextInt(degradeRange*2)-degradeRange;
            	int offZ = this.rand.nextInt(degradeRange*2)-degradeRange;
            	
            	int id = this.worldObj.getBlockId((int)this.posX + offX, (int)this.posY + offY, (int)this.posZ + offZ);
            	
            	if(id != 0) {// We can't extract air...
            		hit = true;
            		int meta = this.worldObj.getBlockMetadata((int)this.posX + offX, (int)this.posY + offY, (int)this.posZ + offZ);
                	
                	Degradation degradation = ChaosCrystalMain.degradationStore.getDegradation(id, meta);
                	if(degradation != null) {
                		this.worldObj.setBlock((int)this.posX + offX, (int)this.posY + offY, (int)this.posZ + offZ, degradation.degraded.itemID, degradation.degraded.getItemDamage(), 1 + 2);
                		
                		for (int i = 0; i < degradation.aspects.length; i++) {
                    		int aspect = this.aspectStore.getInteger(degradation.aspects[i]);
                    		aspect += degradation.amounts[i];
                    		this.aspectStore.setInteger(degradation.aspects[i], aspect);
        				}
                		
                		try {
                    		ByteArrayOutputStream bos = new ByteArrayOutputStream(Integer.SIZE * 7);
                    		DataOutputStream dos = new DataOutputStream(bos);
                    		
							dos.writeInt((int)this.posX);
							dos.writeInt((int)this.posY);
	                		dos.writeInt((int)this.posZ);
	                		dos.writeInt(offX);
	                		dos.writeInt(offY);
	                		dos.writeInt(offZ);
	                		dos.writeInt(this.worldObj.provider.dimensionId);
	                		
	                		Packet250CustomPayload degradationPacket = new Packet250CustomPayload();
	                		degradationPacket.channel = Constants.CHANNEL_NAME;
	                		degradationPacket.data = bos.toByteArray();
	                		degradationPacket.length = bos.size();

	                		dos.close();
	                		
	                		PacketDispatcher.sendPacketToAllAround(this.posX, this.posY, this.posZ, 128, this.worldObj.provider.dimensionId, degradationPacket);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                		
                		
                    	this.worldObj.spawnEntityInWorld(new EntityPortalFX(this.worldObj, (int)this.posX + offX, (int)this.posY + offY, (int)this.posZ + offZ, 0, 0, 0));
                	} else {
                		this.worldObj.setBlock((int)this.posX + offX, (int)this.posY + offY, (int)this.posZ + offZ, 0, 0, 1 + 2);
                		this.worldObj.createExplosion(this, (int)this.posX + offX, (int)this.posY + offY, (int)this.posZ + offZ, 1, false);
                	}
            	}

    			tries++;
        		} while(!hit && tries < maxTries);
            	
            	
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
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setCompoundTag("aspectStore", aspectStore);
		nbttagcompound.setInteger("age", age);
	}

}
