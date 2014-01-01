package founderio.chaoscrystal;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class CommonProxy {

	public void registerRenderStuff() {

	}

	public static void spawnParticleEffects(int dimension, int effect,
			float sourceX, float sourceY, float sourceZ) {
		spawnParticleEffects(dimension, effect,
				sourceX + 0.5f, sourceY + 0.7f, sourceZ + 0.5f,
				0, 0, 0, 1.0f);
	}

	public static void spawnParticleEffects(Entity to, int effect) {
		spawnParticleEffects(to.worldObj.provider.dimensionId, effect,
				(float)to.posX, (float)to.posY, (float)to.posZ,
				0, 0, 0, 0.5f);
	}

	public static void spawnParticleEffects(Entity from, TileEntity to, int effect) {
		spawnParticleEffects(from.worldObj.provider.dimensionId, effect,
				to.xCoord, to.yCoord, to.zCoord,
				(float)(from.posX - to.xCoord), (float)(from.posY - to.yCoord), (float)(from.posZ - to.zCoord));
	}

	public static void spawnParticleEffects(Entity from, Entity to, int effect) {
		spawnParticleEffects(from.worldObj.provider.dimensionId, effect,
				(float)to.posX, (float)to.posY, (float)to.posZ,
				(float)(from.posX - to.posX), (float)(from.posY - to.posY), (float)(from.posZ - to.posZ));
	}

	public static void spawnParticleEffects(int dimension, int effect,
			float sourceX, float sourceY, float sourceZ,
			float offX, float offY, float offZ, float variation) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream(Float.SIZE * 7 + Integer.SIZE * 2);
			DataOutputStream dos = new DataOutputStream(bos);

			dos.writeInt(effect);
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
	//TODO: Change to double!!
	public static void spawnParticleEffects(int dimension, int effect,
			float sourceX, float sourceY, float sourceZ,
			float offX, float offY, float offZ) {
		CommonProxy.spawnParticleEffects(dimension, effect, sourceX, sourceY, sourceZ, offX, offY, offZ, 1);
	}

	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {

	}
}
