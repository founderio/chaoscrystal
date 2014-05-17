package founderio.chaoscrystal;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityAuraFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import founderio.chaoscrystal.blocks.TileEntityApparatus;
import founderio.chaoscrystal.entities.DegradationParticles;
import founderio.chaoscrystal.entities.EntityChaosCrystal;
import founderio.chaoscrystal.entities.EntityFocusBorder;
import founderio.chaoscrystal.entities.EntityFocusFilter;
import founderio.chaoscrystal.entities.EntityFocusFilterTarget;
import founderio.chaoscrystal.entities.EntityFocusTransfer;
import founderio.chaoscrystal.rendering.OverlayAspectSelector;
import founderio.chaoscrystal.rendering.RenderApparatus;
import founderio.chaoscrystal.rendering.RenderChaosCrystal;
import founderio.chaoscrystal.rendering.RenderFocus;
import founderio.chaoscrystal.rendering.RenderItemManual;

public class ClientProxy extends CommonProxy {

	public static RenderApparatus render;

	@Override
	public void registerRenderStuff() {
		RenderFocus rf = new RenderFocus();
		RenderingRegistry.registerEntityRenderingHandler(EntityChaosCrystal.class, new RenderChaosCrystal());
		RenderingRegistry.registerEntityRenderingHandler(EntityFocusTransfer.class, rf);
		RenderingRegistry.registerEntityRenderingHandler(EntityFocusBorder.class, rf);
		RenderingRegistry.registerEntityRenderingHandler(EntityFocusFilter.class, rf);
		RenderingRegistry.registerEntityRenderingHandler(EntityFocusFilterTarget.class, rf);

		render = new RenderApparatus();

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityApparatus.class, render);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ChaosCrystalMain.blockReconstructor), render);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ChaosCrystalMain.blockCreator), render);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ChaosCrystalMain.blockSentry), render);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ChaosCrystalMain.itemManual), new RenderItemManual());


		MinecraftForge.EVENT_BUS.register(new OverlayAspectSelector());
		MinecraftForge.EVENT_BUS.register(render);

	}

	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		if(packet.channel.equals(Constants.CHANNEL_NAME_PARTICLES)) {
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(packet.data));

			try {
				int type = dis.readInt();
				float posX = dis.readFloat();
				float posY = dis.readFloat();
				float posZ = dis.readFloat();
				float offX = dis.readFloat();
				float offY = dis.readFloat();
				float offZ = dis.readFloat();
				float variation = dis.readFloat();

				World w = Minecraft.getMinecraft().theWorld;
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
					int mode = dis.readInt();

					World w = DimensionManager.getWorld(dimension);

					if(w != null) {
						EntityPlayer e = w.getPlayerEntityByName(playerName);
						if(e != null) {
							ItemStack currentItem = e.inventory.getCurrentItem();
							if(currentItem == null || !(currentItem.getItem() instanceof IModeChangingItem)) {
								return;
							}
							IModeChangingItem mci = (IModeChangingItem)currentItem.getItem();
							int modeCount = mci.getModeCount(currentItem);
							if(modeCount == 0) {
								return;
							}
							if(mode < 0) {
								mode = 0;
							}
							if(mode >= modeCount) {
								mode = modeCount;
							}
							mci.setSelectedModeForItemStack(currentItem, mode);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
