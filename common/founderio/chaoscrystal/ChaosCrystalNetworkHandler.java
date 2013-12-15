package founderio.chaoscrystal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

import net.minecraft.client.Minecraft;
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
	
	public static void spawnParticleEffect(int dimension, int effect,
			int sourceX, int sourceY, int sourceZ,
			int offX, int offY, int offZ) {
		try {
    		ByteArrayOutputStream bos = new ByteArrayOutputStream(Integer.SIZE * 7);
    		DataOutputStream dos = new DataOutputStream(bos);

    		dos.writeInt(effect);
    		dos.writeInt(dimension);
    		dos.writeInt(sourceX);
    		dos.writeInt(sourceY);
    		dos.writeInt(sourceZ);
			dos.writeInt(offX);
			dos.writeInt(offY);
    		dos.writeInt(offZ);
    		
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
		spawnParticleEffect(from.worldObj.provider.dimensionId, effect,
				(int)to.posX, (int)to.posY, (int)to.posZ,
				(int)(from.posX - to.posX), (int)(from.posY - to.posY), (int)(from.posZ - to.posZ));
	}
	
	public static void spawnParticleEffects(Entity from, TileEntity to, int effect) {
		spawnParticleEffect(from.worldObj.provider.dimensionId, effect,
				to.xCoord, to.yCoord, to.zCoord,
				(int)(from.posX - to.xCoord), (int)(from.posY - to.yCoord), (int)(from.posZ - to.zCoord));
	}
	
	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		if(packet.channel.equals(Constants.CHANNEL_NAME_PARTICLES)) {
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(packet.data));
			
			try {
				int type = dis.readInt();
				int dimension = dis.readInt();
				int posX = dis.readInt();
				int posY = dis.readInt();
				int posZ = dis.readInt();
				int offX = dis.readInt();
				int offY = dis.readInt();
				int offZ = dis.readInt();

				World w = DimensionManager.getWorld(dimension);
				if(w != null) {
					for(int i = 0; i < 5 + rnd.nextInt(5); i++) {
						Minecraft.getMinecraft().effectRenderer.addEffect(
								new DegradationParticles(
										w,
								posX + rnd.nextDouble(),
								posY + rnd.nextDouble(),
								posZ + rnd.nextDouble(),
								offX + rnd.nextDouble(),
								offY + rnd.nextDouble(),
								offZ + rnd.nextDouble(),
								type));
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
