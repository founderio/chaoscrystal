package founderio.chaoscrystal;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import founderio.chaoscrystal.entities.DegradationParticles;
import founderio.chaoscrystal.entities.EntityFocus;

public class ChaosCrystalNetworkHandler implements IPacketHandler {

	private Random rnd = new Random();
	
	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		if(packet.channel.equals(Constants.CHANNEL_NAME_PARTICLES)) {
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(packet.data));
			
			try {
				int type = dis.readInt();
				int posX = dis.readInt();
				int posY = dis.readInt();
				int posZ = dis.readInt();
				int offX = dis.readInt();
				int offY = dis.readInt();
				int offZ = dis.readInt();
				int dimension = dis.readInt();

				for(int i = 0; i < 20 + rnd.nextInt(20); i++) {
					Minecraft.getMinecraft().effectRenderer.addEffect(
							new DegradationParticles(
									DimensionManager.getWorld(dimension),
							posX + rnd.nextDouble(),
							posY + rnd.nextDouble(),
							posZ + rnd.nextDouble(),
							offX + rnd.nextDouble(),
							offY + rnd.nextDouble(),
							offZ + rnd.nextDouble(),
							type));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if(packet.channel.equals(Constants.CHANNEL_NAME_OTHER_VISUAL)) {
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(packet.data));
			try {
				int type = dis.readInt();
				
				if(type==1) {
					// EntityLook
					int dimension = dis.readInt();
					int entity = dis.readInt();
					double posX = dis.readDouble();
					double posY = dis.readDouble();
					double posZ = dis.readDouble();
					
					World w = DimensionManager.getWorld(dimension);
					if(w != null) {
						Entity e = w.getEntityByID(entity);
						
						double d0 = posX - e.posX;
				        double d1 = posY - e.posY;
				        double d2 = posZ - e.posZ;
				        float f3 = MathHelper.sqrt_double(d0 * d0 + d2 * d2);
				        e.rotationYaw = (float)(Math.atan2(d0, d2) * 180.0D / Math.PI);
				        e.rotationPitch = (float)(Math.atan2(d1, (double)f3) * 180.0D / Math.PI);
						
						if(e instanceof EntityFocus) {
							((EntityFocus)e).lookHelper.posX = posX;
							((EntityFocus)e).lookHelper.posY = posY;
							((EntityFocus)e).lookHelper.posZ = posZ;
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
