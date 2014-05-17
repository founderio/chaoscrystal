package founderio.chaoscrystal.network;

import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.entities.DegradationParticles;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityAuraFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

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
		World w = player.worldObj;
		if(w != null) {
			float varHalf = variation/2;
			if(type == 2) {
				for(int i = 0; i < 5 + ChaosCrystalMain.rand.nextInt(20); i++) {
					Minecraft.getMinecraft().effectRenderer.addEffect(
							new EntityAuraFX(
									w,
									posX + ChaosCrystalMain.rand.nextDouble()*variation-varHalf,
									posY + ChaosCrystalMain.rand.nextDouble()*variation-varHalf,
									posZ + ChaosCrystalMain.rand.nextDouble()*variation-varHalf, 1, 1, 1));
				}
			} else {
				for(int i = 0; i < 5 + ChaosCrystalMain.rand.nextInt(5); i++) {
					Minecraft.getMinecraft().effectRenderer.addEffect(
							new DegradationParticles(
									w,
									posX + ChaosCrystalMain.rand.nextDouble()*variation-varHalf,
									posY + ChaosCrystalMain.rand.nextDouble()*variation-varHalf,
									posZ + ChaosCrystalMain.rand.nextDouble()*variation-varHalf,
									offX + ChaosCrystalMain.rand.nextDouble()*variation-varHalf,
									offY + ChaosCrystalMain.rand.nextDouble()*variation-varHalf,
									offZ + ChaosCrystalMain.rand.nextDouble()*variation-varHalf,
									type));
				}
			}
		}
	}

	@Override
	public void handleServerSide(EntityPlayer player) {
		// No Particles on Server!
	}

}
