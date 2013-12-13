package founderio.chaoscrystal;

import founderio.chaoscrystal.entities.EntityChaosCrystal;
import founderio.chaoscrystal.entities.EntityFocus;
import founderio.chaoscrystal.rendering.RenderChaosCrystal;
import founderio.chaoscrystal.rendering.RenderFocus;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
	@Override
	public void registerRenderStuff() {
		RenderingRegistry.registerEntityRenderingHandler(EntityChaosCrystal.class, new RenderChaosCrystal());
		RenderingRegistry.registerEntityRenderingHandler(EntityFocus.class, new RenderFocus());
	}
}
