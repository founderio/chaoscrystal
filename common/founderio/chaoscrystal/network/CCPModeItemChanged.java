package founderio.chaoscrystal.network;

import founderio.chaoscrystal.IModeChangingItem;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CCPModeItemChanged extends CCAbstractPacket {

	private int modeIndex;

	public CCPModeItemChanged() {

	}

	public CCPModeItemChanged(int modeIndex) {
		this.modeIndex = modeIndex;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		buffer.writeInt(modeIndex);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		modeIndex = buffer.readInt();
	}

	@Override
	public void handleClientSide(EntityPlayer player) {
		handle(player);
	}

	@Override
	public void handleServerSide(EntityPlayer player) {
		handle(player);
	}
	
	private void handle(EntityPlayer player) {
		ItemStack currentItemStack = Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem();
		if(currentItemStack == null) {
			return;
		}
		if(currentItemStack == null || !(currentItemStack.getItem() instanceof IModeChangingItem)) {
			return;
		}
		IModeChangingItem mci = (IModeChangingItem)currentItemStack.getItem();
		int modeCount = mci.getModeCount(currentItemStack);
		if(modeCount == 0) {
			return;
		}
		if(modeIndex < 0) {
			modeIndex = 0;
		}
		if(modeIndex >= modeCount) {
			modeIndex = modeCount;
		}
		mci.setSelectedModeForItemStack(currentItemStack, modeIndex);
	}

}
