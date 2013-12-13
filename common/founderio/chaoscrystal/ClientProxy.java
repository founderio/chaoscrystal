package founderio.chaoscrystal;

import founderio.chaoscrystal.entities.EntityChaosCrystal;
import founderio.chaoscrystal.rendering.RenderChaosCrystal;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
	@Override
	public void registerRenderStuff() {
		RenderingRegistry.registerEntityRenderingHandler(EntityChaosCrystal.class, new RenderChaosCrystal());
	}
}
