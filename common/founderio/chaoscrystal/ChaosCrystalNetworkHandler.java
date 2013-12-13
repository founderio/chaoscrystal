package founderio.chaoscrystal;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import founderio.chaoscrystal.entities.DegradationParticles;

public class ChaosCrystalNetworkHandler implements IPacketHandler {

	private Random rnd = new Random();
	
	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		if(packet.channel.equals(Constants.CHANNEL_NAME)) {
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
