package founderio.chaoscrystal;

import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import founderio.chaoscrystal.blocks.TileEntityApparatus;
import founderio.chaoscrystal.entities.EntityChaosCrystal;
import founderio.chaoscrystal.entities.EntityFocusBorder;
import founderio.chaoscrystal.entities.EntityFocusFilter;
import founderio.chaoscrystal.entities.EntityFocusTransfer;
import founderio.chaoscrystal.rendering.OverlayAspectSelector;
import founderio.chaoscrystal.rendering.RenderChaosCrystal;
import founderio.chaoscrystal.rendering.RenderFocus;
import founderio.chaoscrystal.rendering.RenderItemManual;
import founderio.chaoscrystal.rendering.RenderApparatus;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {
	
	public static RenderApparatus render;
	
	@Override
	public void registerRenderStuff() {
		RenderingRegistry.registerEntityRenderingHandler(EntityChaosCrystal.class, new RenderChaosCrystal());
		RenderingRegistry.registerEntityRenderingHandler(EntityFocusTransfer.class, new RenderFocus());
		RenderingRegistry.registerEntityRenderingHandler(EntityFocusBorder.class, new RenderFocus());
		RenderingRegistry.registerEntityRenderingHandler(EntityFocusFilter.class, new RenderFocus());
		
		render = new RenderApparatus();
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityApparatus.class, render);
		MinecraftForgeClient.registerItemRenderer(ChaosCrystalMain.blockReconstructor.blockID, render);
		MinecraftForgeClient.registerItemRenderer(ChaosCrystalMain.blockCreator.blockID, render);
		MinecraftForgeClient.registerItemRenderer(ChaosCrystalMain.blockSentry.blockID, render);
		MinecraftForgeClient.registerItemRenderer(ChaosCrystalMain.itemManual.itemID, new RenderItemManual());
		

		MinecraftForge.EVENT_BUS.register(new OverlayAspectSelector());
		

		TickRegistry.registerTickHandler(render, Side.CLIENT);
	}
}
