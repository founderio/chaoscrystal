package founderio.chaoscrystal.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import founderio.chaoscrystal.ChaosCrystalMain;

public class CCPParticle implements IMessage {

	public static final class Handler implements IMessageHandler<CCPParticle, IMessage> {

		@Override
		public IMessage onMessage(CCPParticle message, MessageContext ctx) {
			if(ctx.side == Side.CLIENT) {
				ChaosCrystalMain.proxy.spawnParticleEntity(Minecraft.getMinecraft().thePlayer.worldObj,
						message.type, message.posX, message.posY, message.posZ, message.offX, message.offY, message.offZ, message.variation);
			}
			return null;
		}

	}

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
	public void fromBytes(ByteBuf buf) {
		type = buf.readInt();
		posX = buf.readDouble();
		posY = buf.readDouble();
		posZ = buf.readDouble();
		offX = buf.readDouble();
		offY = buf.readDouble();
		offZ = buf.readDouble();
		variation = buf.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(type);
		buf.writeDouble(posX);
		buf.writeDouble(posY);
		buf.writeDouble(posZ);
		buf.writeDouble(offX);
		buf.writeDouble(offY);
		buf.writeDouble(offZ);
		buf.writeFloat(variation);
	}

}
