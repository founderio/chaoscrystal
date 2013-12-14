package founderio.chaoscrystal;

import founderio.chaoscrystal.blocks.TileEntityApparatus;
import founderio.chaoscrystal.entities.EntityChaosCrystal;
import founderio.chaoscrystal.entities.EntityFocusBorder;
import founderio.chaoscrystal.entities.EntityFocusFilter;
import founderio.chaoscrystal.entities.EntityFocusTransfer;
import founderio.chaoscrystal.rendering.RenderChaosCrystal;
import founderio.chaoscrystal.rendering.RenderFocus;
import founderio.chaoscrystal.rendering.TileEntityRenderer;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
	@Override
	public void registerRenderStuff() {
		RenderingRegistry.registerEntityRenderingHandler(EntityChaosCrystal.class, new RenderChaosCrystal());
		RenderingRegistry.registerEntityRenderingHandler(EntityFocusTransfer.class, new RenderFocus());
		RenderingRegistry.registerEntityRenderingHandler(EntityFocusBorder.class, new RenderFocus());
		RenderingRegistry.registerEntityRenderingHandler(EntityFocusFilter.class, new RenderFocus());
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityApparatus.class, new TileEntityRenderer());
	}
}
