package founderio.chaoscrystal.aspects.modules;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import founderio.chaoscrystal.aspects.AspectBuilder;
import founderio.chaoscrystal.aspects.AspectModule;
import founderio.chaoscrystal.aspects.Node;
import founderio.chaoscrystal.aspects.NodeDegradation;
import founderio.chaoscrystal.degradation.Aspects;

public class ModuleVanillaWorldgen extends AspectModule {

	/*
	 * "Ignore"-Blocks
	 */
	public static Node AIR;
	public static Node WATER_MOVING;
	

	public static Node WATER_STILL;
	public static Node SAND;
	public static Node DIRT_DEAD;
	public static Node DIRT;
	public static Node GRASS;
	public static Node MYCELIUM;
	public static Node CLAY;

	public static Node VINE;
	
	@Override
	protected void registerNodes() {
		AspectBuilder ab = new AspectBuilder();
		
		/*
		 * "Ignore"-Blocks
		 */
		//TODO: In NonDestructive Mode, use Lifeless Block once added
		AIR = new NodeDegradation(null, ab.toAspectArray(), new ItemStack(0, 1, 0), false, false);
		nodes.add(AIR);
		
		WATER_MOVING = new NodeDegradation(null, ab.toAspectArray(), new ItemStack(Block.waterMoving, 0, 32767), false, false);
		nodes.add(WATER_MOVING);
		
		/*
		 * Worldgen-Blocks
		 */
		
		ab.setAspect(Aspects.ASPECT_WATER, 5);
		WATER_STILL = new NodeDegradation(AIR, ab.toAspectArray(), new ItemStack(Block.waterStill, 0, 32767), false, false);
		nodes.add(WATER_STILL);
		ab.clear();
		
		ab.setAspect(Aspects.ASPECT_EARTH, 5);
		SAND = new NodeDegradation(AIR, ab.toAspectArray(), new ItemStack(Block.sand), false, false);
		nodes.add(SAND);
		ab.clear();
		
		ab.setAspect(Aspects.ASPECT_STRUCTURE, 5).setAspect(Aspects.ASPECT_LIVING, 2);
		DIRT_DEAD = new NodeDegradation(SAND, ab.toAspectArray(), new ItemStack(Block.dirt, 0, 1), false, false);
		nodes.add(DIRT_DEAD);
		ab.clear();
		
		ab.setAspect(Aspects.ASPECT_GROWTH, 5);
		DIRT = new NodeDegradation(DIRT_DEAD, ab.toAspectArray(), new ItemStack(Block.dirt), false, false);
		nodes.add(DIRT);
		ab.clear();
		
		ab.setAspect(Aspects.ASPECT_LIVING, 5);
		GRASS = new NodeDegradation(DIRT, ab.toAspectArray(), new ItemStack(Block.grass), false, true);
		nodes.add(GRASS);
		ab.clear();
		
		ab.setAspect(Aspects.ASPECT_LIVING, 5);
		MYCELIUM = new NodeDegradation(DIRT, ab.toAspectArray(), new ItemStack(Block.mycelium), false, true);
		nodes.add(MYCELIUM);
		ab.clear();
		
		ab.setAspect(Aspects.ASPECT_WATER, 5).setAspect(Aspects.ASPECT_STRUCTURE, 2);
		CLAY = new NodeDegradation(SAND, ab.toAspectArray(), new ItemStack(Block.blockClay), false, true);
		nodes.add(VINE);
		ab.clear();
		
		ab.setAspect(Aspects.ASPECT_LIVING, 5).setAspect(Aspects.ASPECT_GROWTH, 5);
		VINE = new NodeDegradation(AIR, ab.toAspectArray(), new ItemStack(Block.vine, 0, 32767), false, false);
		nodes.add(VINE);
		ab.clear();
	}

}
