package founderio.chaoscrystal.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.chaoscrystal.IModeChangingItem;

public class CCPModeItemChanged implements IMessage {

	public static class Handler implements IMessageHandler<CCPModeItemChanged, IMessage> {

		@Override
		public IMessage onMessage(CCPModeItemChanged message, MessageContext ctx) {
			EntityPlayer player;
			if(ctx.side == Side.CLIENT) {
				player = getClientPlayer();
			} else {
				player = ctx.getServerHandler().playerEntity;
			}
			ItemStack currentItemStack = player.inventory.getCurrentItem();
			if(currentItemStack == null || !(currentItemStack.getItem() instanceof IModeChangingItem)) {
				return null;
			}
			IModeChangingItem mci = (IModeChangingItem)currentItemStack.getItem();
			int modeCount = mci.getModeCount(currentItemStack);
			if(modeCount == 0) {
				return null;
			}
			if(message.modeIndex < 0) {
				message.modeIndex = 0;
			}
			if(message.modeIndex >= modeCount) {
				message.modeIndex = modeCount;
			}
			mci.setSelectedModeForItemStack(currentItemStack, message.modeIndex);
			return null;
		}
		
		@SideOnly(Side.CLIENT)
		private EntityPlayer getClientPlayer() {
			return Minecraft.getMinecraft().thePlayer;
		}
	}

	private int modeIndex;

	public CCPModeItemChanged() {

	}

	public CCPModeItemChanged(int modeIndex) {
		this.modeIndex = modeIndex;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		modeIndex = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(modeIndex);
	}

}
