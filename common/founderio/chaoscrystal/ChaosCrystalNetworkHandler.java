package founderio.chaoscrystal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityAuraFX;
import net.minecraft.client.particle.EntityReddustFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import founderio.chaoscrystal.entities.DegradationParticles;

public class ChaosCrystalNetworkHandler implements IPacketHandler {

	private Random rnd = new Random();
	
	public static void spawnParticleEffects(int dimension, int effect,
			float sourceX, float sourceY, float sourceZ,
			float offX, float offY, float offZ) {
		spawnParticleEffects(dimension, effect, sourceX, sourceY, sourceZ, offX, offY, offZ, 1);
	}
	
	public static void spawnParticleEffects(int dimension, int effect,
			float sourceX, float sourceY, float sourceZ,
			float offX, float offY, float offZ, float variation) {
		try {
    		ByteArrayOutputStream bos = new ByteArrayOutputStream(Float.SIZE * 7 + Integer.SIZE * 2);
    		DataOutputStream dos = new DataOutputStream(bos);

    		dos.writeInt(effect);
    		dos.writeInt(dimension);
    		dos.writeFloat(sourceX);
    		dos.writeFloat(sourceY);
    		dos.writeFloat(sourceZ);
			dos.writeFloat(offX);
			dos.writeFloat(offY);
    		dos.writeFloat(offZ);
    		dos.writeFloat(variation);
    		
    		Packet250CustomPayload degradationPacket = new Packet250CustomPayload();
    		degradationPacket.channel = Constants.CHANNEL_NAME_PARTICLES;
    		degradationPacket.data = bos.toByteArray();
    		degradationPacket.length = bos.size();

    		dos.close();
    		
    		PacketDispatcher.sendPacketToAllAround(sourceX, sourceY, sourceZ, 128, dimension, degradationPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void spawnParticleEffects(Entity from, Entity to, int effect) {
		spawnParticleEffects(from.worldObj.provider.dimensionId, effect,
				(float)to.posX, (float)to.posY, (float)to.posZ,
				(float)(from.posX - to.posX), (float)(from.posY - to.posY), (float)(from.posZ - to.posZ));
	}
	
	public static void spawnParticleEffects(Entity from, TileEntity to, int effect) {
		spawnParticleEffects(from.worldObj.provider.dimensionId, effect,
				to.xCoord, to.yCoord, to.zCoord,
				(float)(from.posX - to.xCoord), (float)(from.posY - to.yCoord), (float)(from.posZ - to.zCoord));
	}
	
	public static void spawnParticleEffects(Entity to, int effect) {
		spawnParticleEffects(to.worldObj.provider.dimensionId, effect,
				(float)to.posX, (float)to.posY, (float)to.posZ,
				0, 0, 0, 0.5f);
	}
	
	public static void spawnParticleEffects(int dimension, int effect,
			float sourceX, float sourceY, float sourceZ) {
		spawnParticleEffects(dimension, effect,
				sourceX + 0.5f, sourceY + 0.7f, sourceZ + 0.5f,
				0, 0, 0, 1.0f);
	}
	
	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		if(packet.channel.equals(Constants.CHANNEL_NAME_PARTICLES)) {
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(packet.data));
			
			try {
				int type = dis.readInt();
				int dimension = dis.readInt();
				float posX = dis.readFloat();
				float posY = dis.readFloat();
				float posZ = dis.readFloat();
				float offX = dis.readFloat();
				float offY = dis.readFloat();
				float offZ = dis.readFloat();
				float variation = dis.readFloat();

				World w = DimensionManager.getWorld(dimension);
				if(w != null) {
					float varHalf = variation/2;
					if(type == 2) {
						for(int i = 0; i < 5 + rnd.nextInt(20); i++) {
							Minecraft.getMinecraft().effectRenderer.addEffect(
									new EntityAuraFX(
											w,
									posX + rnd.nextDouble()*variation-varHalf,
									posY + rnd.nextDouble()*variation-varHalf,
									posZ + rnd.nextDouble()*variation-varHalf, 1, 1, 1));
						}
					} else {
						for(int i = 0; i < 5 + rnd.nextInt(5); i++) {
							Minecraft.getMinecraft().effectRenderer.addEffect(
									new DegradationParticles(
											w,
									posX + rnd.nextDouble()*variation-varHalf,
									posY + rnd.nextDouble()*variation-varHalf,
									posZ + rnd.nextDouble()*variation-varHalf,
									offX + rnd.nextDouble()*variation-varHalf,
									offY + rnd.nextDouble()*variation-varHalf,
									offZ + rnd.nextDouble()*variation-varHalf,
									type));
						}
					}
				}
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if(packet.channel.equals(Constants.CHANNEL_NAME_OTHER_VISUAL)) {
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(packet.data));
			try {
				int type = dis.readInt();
				
				if(type==1) {
					//Unused
				} else if(type==2) {
					// Update player's selected stack
					int dimension = dis.readInt();
					String playerName = dis.readUTF();
					String aspect = dis.readUTF();

					World w = DimensionManager.getWorld(dimension);
					
					if(w != null) {
						EntityPlayer e = w.getPlayerEntityByName(playerName);
						if(e != null) {
							ItemStack currentItem = e.inventory.getCurrentItem();
							if(currentItem == null || currentItem.itemID != ChaosCrystalMain.itemFocus.itemID) {
								return;
							}
							if(currentItem.getItemDamage() != 2) {
								return;
							}
							NBTTagCompound tags = currentItem.getTagCompound();
							if(tags == null) {
								tags = new NBTTagCompound();
							}
							tags.setString("aspect", aspect);
							currentItem.setTagCompound(tags);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
