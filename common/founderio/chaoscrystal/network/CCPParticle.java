package founderio.chaoscrystal.network;

import founderio.chaoscrystal.ChaosCrystalMain;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

public class CCPParticle extends CCAbstractPacket {

	int type;
	double posX;
	double posY;
	double posZ;
	double offX;
	double offY;
	double offZ;
	float variation;
	
	public CCPParticle() {
	}
	
	public CCPParticle(int type, double posX, double posY, double posZ,
			double offX, double offY, double offZ, float variation) {
		this.type = type;
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
		this.offX = offX;
		this.offY = offY;
		this.offZ = offZ;
		this.variation = variation;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		buffer.writeInt(type);
		buffer.writeDouble(posX);
		buffer.writeDouble(posY);
		buffer.writeDouble(posZ);
		buffer.writeDouble(offX);
		buffer.writeDouble(offY);
		buffer.writeDouble(offZ);
		buffer.writeFloat(variation);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		type = buffer.readInt();
		posX = buffer.readDouble();
		posY = buffer.readDouble();
		posZ = buffer.readDouble();
		offX = buffer.readDouble();
		offY = buffer.readDouble();
		offZ = buffer.readDouble();
		variation = buffer.readFloat();
	}

	@Override
	public void handleClientSide(EntityPlayer player) {
		ChaosCrystalMain.proxy.spawnParticleEntity(player.worldObj, type, posX, posY, posZ, offX, offY, offZ, variation);
	}

	@Override
	public void handleServerSide(EntityPlayer player) {
		// No Particles on Server!
	}

}
