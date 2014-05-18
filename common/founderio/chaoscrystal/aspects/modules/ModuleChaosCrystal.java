package founderio.chaoscrystal.aspects.modules;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import founderio.chaoscrystal.aspects.AspectBuilder;
import founderio.chaoscrystal.aspects.AspectModule;
import founderio.chaoscrystal.aspects.Aspects;
import founderio.chaoscrystal.aspects.ChaosRegistry;
import founderio.chaoscrystal.aspects.Node;
import founderio.chaoscrystal.aspects.NodeDegradation;

public class ModuleChaosCrystal extends AspectModule {

	public static Node CRYSTALLINE;
	public static Node CRYSTAL;
	public static Node CRYSTALLINE_LIGHT;
	

	@Override
	public void registerNodes(ChaosRegistry degradationStore) {
		AspectBuilder ab = new AspectBuilder();

		ab.setAspect(Aspects.ASPECT_CRYSTAL, 5).setAspect(Aspects.ASPECT_VALUE, 2).setAspect(Aspects.ASPECT_HEAT, 4);
		degradationStore.addNode(CRYSTALLINE = new NodeDegradation(
				ModuleVanillaWorldgen.GLASS,
				ab.toAspectArray(),
				new ItemStack(Blocks.glass, 1, 0), false, false));
		ab.clear();
		
		ab.setAspect(Aspects.ASPECT_CRYSTAL, 15).setAspect(Aspects.ASPECT_VALUE, 5).setAspect(Aspects.ASPECT_STRUCTURE, 5);
		degradationStore.addNode(CRYSTAL = new NodeDegradation(
				ModuleVanillaWorldgen.GLASS,
				ab.toAspectArray(),
				new ItemStack(Blocks.glass, 1, 0), false, false));
		ab.clear();
		
		ab.setAspect(Aspects.ASPECT_CRYSTAL, 15).setAspect(Aspects.ASPECT_VALUE, 5).setAspect(Aspects.ASPECT_STRUCTURE, 5).setAspect(Aspects.ASPECT_LIGHT, 150);
		degradationStore.addNode(CRYSTALLINE_LIGHT = new NodeDegradation(
				ModuleVanillaWorldgen.GLASS,
				ab.toAspectArray(),
				new ItemStack(Blocks.glass, 1, 0), false, false));
		ab.clear();

	}

}
