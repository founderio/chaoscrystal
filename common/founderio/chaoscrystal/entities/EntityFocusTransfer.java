package founderio.chaoscrystal.entities;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.PacketDispatcher;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.Constants;
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
	
	public double lookX;
	public double lookY;
	public double lookZ;

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
            		
            		lookX = crystal1.posX;
            		lookY = (crystal1.boundingBox.minY + crystal1.boundingBox.maxY) / 2.0D;
            		lookZ = crystal1.posZ;
            		sendLookUpdate();
            		
            		try {
                		ByteArrayOutputStream bos = new ByteArrayOutputStream(Integer.SIZE * 7);
                		DataOutputStream dos = new DataOutputStream(bos);

	            		dos.writeInt(1);
                		dos.writeInt((int)posX);
                		dos.writeInt((int)posY);
                		dos.writeInt((int)posZ);
    					dos.writeInt((int)(crystal2.posX - posX));
    					dos.writeInt((int)(crystal2.posY - posY));
                		dos.writeInt((int)(crystal2.posZ - posZ));
                		dos.writeInt(this.worldObj.provider.dimensionId);
                		
                		Packet250CustomPayload degradationPacket = new Packet250CustomPayload();
                		degradationPacket.channel = Constants.CHANNEL_NAME_PARTICLES;
                		degradationPacket.data = bos.toByteArray();
                		degradationPacket.length = bos.size();

                		dos.close();
                		
                		PacketDispatcher.sendPacketToAllAround(posX, posY, posZ, 128, this.worldObj.provider.dimensionId, degradationPacket);
    				} catch (IOException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
            		//TODO: Make Wrapper for this function :-/
            		
            		try {
                		ByteArrayOutputStream bos = new ByteArrayOutputStream(Integer.SIZE * 7);
                		DataOutputStream dos = new DataOutputStream(bos);

	            		dos.writeInt(1);
                		dos.writeInt((int)crystal1.posX);
                		dos.writeInt((int)crystal1.posY);
                		dos.writeInt((int)crystal1.posZ);
    					dos.writeInt((int)(posX - crystal1.posX));
    					dos.writeInt((int)(posY - crystal1.posY));
    					dos.writeInt((int)(posZ - crystal1.posZ));
                		dos.writeInt(this.worldObj.provider.dimensionId);
                		
                		Packet250CustomPayload degradationPacket = new Packet250CustomPayload();
                		degradationPacket.channel = Constants.CHANNEL_NAME_PARTICLES;
                		degradationPacket.data = bos.toByteArray();
                		degradationPacket.length = bos.size();

                		dos.close();
                		
                		PacketDispatcher.sendPacketToAllAround(posX, posY, posZ, 128, this.worldObj.provider.dimensionId, degradationPacket);
    				} catch (IOException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
            		
            		for(String aspect : Aspects.ASPECTS) {
            			int aspects = crystal1.aspectStore.getInteger(aspect) + crystal2.aspectStore.getInteger(aspect);
            			int asp1 = aspects/2;
            			int asp2 = aspects - asp1;
            			crystal1.aspectStore.setInteger(aspect, asp1);
            			crystal2.aspectStore.setInteger(aspect, asp2);
            		}
            		
        		}
        	}
        }
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

	public void sendLookUpdate() {
    	try {
    		ByteArrayOutputStream bos = new ByteArrayOutputStream(Integer.SIZE * 3 + Double.SIZE * 3);
    		DataOutputStream dos = new DataOutputStream(bos);

    		dos.writeInt(1);
    		dos.writeInt(worldObj.provider.dimensionId);
    		dos.writeInt(entityId);
    		dos.writeDouble(lookX);
    		dos.writeDouble(lookY);
    		dos.writeDouble(lookZ);
    		
    		Packet250CustomPayload degradationPacket = new Packet250CustomPayload();
    		degradationPacket.channel = Constants.CHANNEL_NAME_OTHER_VISUAL;
    		degradationPacket.data = bos.toByteArray();
    		degradationPacket.length = bos.size();

    		dos.close();
    		
    		PacketDispatcher.sendPacketToAllAround(posX, posY, posZ, 128, worldObj.provider.dimensionId, degradationPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		age = nbttagcompound.getInteger("age");
		lastTransfer = nbttagcompound.getInteger("lastTransfer");
		lookX = nbttagcompound.getDouble("lookX");
		lookY = nbttagcompound.getDouble("lookY");
		lookZ = nbttagcompound.getDouble("lookZ");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setInteger("age", age);
		nbttagcompound.setInteger("lastTransfer", lastTransfer);
		nbttagcompound.setDouble("lookX", lookX);
		nbttagcompound.setDouble("lookY", lookY);
		nbttagcompound.setDouble("lookZ", lookZ);
	}

}