package founderio.chaoscrystal.entities;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.Constants;
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
	
	public double lookX;
	public double lookY;
	public double lookZ;
	
	public String aspect = Aspects.ASPECTS[0];
	
	public static final int focusRange = 10;

	@Override
	protected void entityInit() {
		System.out.println("Spawned with aspect: " + aspect);
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
    				double tmp_dist = distX*distX + distY*distY + distZ*distZ;
    				if(tmp_dist*tmp_dist < focusRange*focusRange*focusRange) {
    					crystals.add((EntityChaosCrystal)obj);
    				}
    			}
    		}
            
            if(!crystals.isEmpty()) {
            	EntityChaosCrystal crystal1 = crystals.get(rand.nextInt(crystals.size()));
            	lookX = crystal1.posX;
	    		lookY = (crystal1.boundingBox.minY + crystal1.boundingBox.maxY) / 2.0D;
	    		lookZ = crystal1.posZ;
	    		sendLookUpdate();
            } else if(age % 20 == 0) {
            	lookX = posX + (this.rand.nextDouble() - 0.5) * 10;
            	lookY = posY;
            	lookZ = posZ + (this.rand.nextDouble() - 0.5) * 10;
        		sendLookUpdate();
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

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
//		age = nbttagcompound.getInteger("age");
		lookX = nbttagcompound.getDouble("lookX");
		lookY = nbttagcompound.getDouble("lookY");
		lookZ = nbttagcompound.getDouble("lookZ");
		aspect = nbttagcompound.getString("aspect");
		if(!Aspects.isAspect(aspect)) {
			aspect = Aspects.ASPECTS[0];
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
//		nbttagcompound.setInteger("age", age);
		nbttagcompound.setDouble("lookX", lookX);
		nbttagcompound.setDouble("lookY", lookY);
		nbttagcompound.setDouble("lookZ", lookZ);
		nbttagcompound.setString("aspect", aspect);
	}

}
