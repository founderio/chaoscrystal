package founderio.chaoscrystal;

import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import founderio.chaoscrystal.blocks.TileEntityApparatus;
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
		MinecraftForgeClient.registerItemRenderer(ChaosCrystalMain.itemManual, new RenderItemManual());


		MinecraftForge.EVENT_BUS.register(new OverlayAspectSelector());
		MinecraftForge.EVENT_BUS.register(render);

	}
}
