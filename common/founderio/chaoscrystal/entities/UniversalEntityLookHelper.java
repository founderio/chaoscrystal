package founderio.chaoscrystal.entities;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import cpw.mods.fml.common.network.PacketDispatcher;
import founderio.chaoscrystal.Constants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.MathHelper;

public class UniversalEntityLookHelper
{
	//More or less similar to net.minecraft.entity.ai.EntityLookHelper
    private Entity entity;

    /**
     * The amount of change that is made each update for an entity facing a direction.
     */
    private float deltaLookYaw = .1f;

    /**
     * The amount of change that is made each update for an entity facing a direction.
     */
    private float deltaLookPitch = .1f;

    public double posX;
    public double posY;
    public double posZ;

    public UniversalEntityLookHelper(Entity par1EntityLiving)
    {
        this.entity = par1EntityLiving;
    }

    /**
     * Sets position to look at using entity
     */
    public void setLookPositionWithEntity(Entity par1Entity)
    {
        this.posX = par1Entity.posX;

        if (par1Entity instanceof EntityLivingBase)
        {
            this.posY = par1Entity.posY + (double)par1Entity.getEyeHeight();
        }
        else
        {
            this.posY = (par1Entity.boundingBox.minY + par1Entity.boundingBox.maxY) / 2.0D;
        }

        this.posZ = par1Entity.posZ;
        sendLookUpdate();
    }

    /**
     * Sets position to look at
     */
    public void setLookPosition(double par1, double par3, double par5)
    {
        this.posX = par1;
        this.posY = par3;
        this.posZ = par5;
        sendLookUpdate();
    }
    
    public void sendLookUpdate() {
    	try {
    		ByteArrayOutputStream bos = new ByteArrayOutputStream(Integer.SIZE * 3 + Double.SIZE * 3);
    		DataOutputStream dos = new DataOutputStream(bos);

    		dos.writeInt(1);
    		dos.writeInt(entity.worldObj.provider.dimensionId);
    		dos.writeInt(entity.entityId);
    		dos.writeDouble(posX);
    		dos.writeDouble(posY);
    		dos.writeDouble(posZ);
    		
    		Packet250CustomPayload degradationPacket = new Packet250CustomPayload();
    		degradationPacket.channel = Constants.CHANNEL_NAME_OTHER_VISUAL;
    		degradationPacket.data = bos.toByteArray();
    		degradationPacket.length = bos.size();

    		dos.close();
    		
    		PacketDispatcher.sendPacketToAllAround(posX, posY, posZ, 128, entity.worldObj.provider.dimensionId, degradationPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
