package founderio.chaoscrystal.aspects.modules;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import founderio.chaoscrystal.aspects.AspectBuilder;
import founderio.chaoscrystal.aspects.AspectModule;
import founderio.chaoscrystal.aspects.Node;
import founderio.chaoscrystal.aspects.NodeDegradation;
import founderio.chaoscrystal.aspects.NodeSimpleOre;
import founderio.chaoscrystal.degradation.Aspects;

public class ModuleVanillaWorldgen extends AspectModule {

	/*
	 * "Ignore"-Blocks
	 */
	public static Node AIR;
	public static Node WATER_MOVING;
	

	public static Node WATER_STILL;
	public static Node SAND;
	public static Node GRAVEL;
	public static Node DIRT_DEAD;
	public static Node DIRT;
	public static Node GRASS;
	public static Node MYCELIUM;
	public static Node CLAY;


	public static Node COBBLESTONE;
	public static Node STONE;
	public static Node STONE_BRICK;
	public static Node COBBLESTONE_MOSSY;

	public static Node SILVERFISH_STONE;
	public static Node SILVERFISH_COBBLE;
	public static Node SILVERFISH_BRICK;
	
	public static Node GLASS;
	public static Node GLOWSTONE;
	
	public static Node INGOT_GOLD;
	public static Node INGOT_IRON;
	public static Node INGOT_DIAMOND;
	public static Node INGOT_COAL;
	public static Node INGOT_REDSTONE;
	public static Node INGOT_LAPIS;
	public static Node INGOT_EMERALD;

	public static Node VINE;
	public static Node PLANT_RED;
	public static Node PLANT_YELLOW;
	public static Node TALL_GRASS;
	public static Node WATERLILY;
	public static Node REEDS;
	public static Node CACTUS;
	public static Node DEAD_BUSH;
	public static Node WEB;
	public static Node MUSHROOM_RED;
	public static Node MUSHROOM_BROWN;
	public static Node MUSHROOM_CAP_RED;
	public static Node MUSHROOM_CAP_BROWN;

	public static Node MELON;
	public static Node PUMPKIN;
	
	@Override
	protected void registerNodes() {
		AspectBuilder ab = new AspectBuilder();
		
		/*
		 * "Ignore"-Blocks
		 */
		//TODO: In NonDestructive Mode, use Lifeless Block once added
		nodes.add(AIR = new NodeDegradation(null, ab.toAspectArray(), new ItemStack(0, 1, 0), false, false));
		
		nodes.add(WATER_MOVING = new NodeDegradation(null, ab.toAspectArray(), new ItemStack(Block.waterMoving, 1, 32767), false, false));
		
		/*
		 * Worldgen-Blocks
		 */
		
		ab.setAspect(Aspects.ASPECT_WATER, 5);
		nodes.add(WATER_STILL = new NodeDegradation(AIR, ab.toAspectArray(), new ItemStack(Block.waterStill, 1, 32767), false, false));
		ab.clear();
		
		ab.setAspect(Aspects.ASPECT_EARTH, 5);
		nodes.add(SAND = new NodeDegradation(AIR, ab.toAspectArray(), new ItemStack(Block.sand), false, false));
		ab.clear();
		
		ab.setAspect(Aspects.ASPECT_STRUCTURE, 5).setAspect(Aspects.ASPECT_LIVING, 2);
		nodes.add(DIRT_DEAD = new NodeDegradation(SAND, ab.toAspectArray(), new ItemStack(Block.dirt, 1, 1), false, false));
		ab.clear();
		
		ab.setAspect(Aspects.ASPECT_GROWTH, 5);
		nodes.add(DIRT = new NodeDegradation(DIRT_DEAD, ab.toAspectArray(), new ItemStack(Block.dirt), false, false));
		ab.clear();
		
		ab.setAspect(Aspects.ASPECT_LIVING, 5);
		nodes.add(GRASS = new NodeDegradation(DIRT, ab.toAspectArray(), new ItemStack(Block.grass), false, true));
		nodes.add(MYCELIUM = new NodeDegradation(DIRT, ab.toAspectArray(), new ItemStack(Block.mycelium), false, true));
		ab.clear();
		
		ab.setAspect(Aspects.ASPECT_WATER, 5).setAspect(Aspects.ASPECT_STRUCTURE, 2);
		nodes.add(CLAY = new NodeDegradation(SAND, ab.toAspectArray(), new ItemStack(Block.blockClay), false, false));
		ab.clear();
		
		//TODO: Smelting Recipe
		ab.setAspect(Aspects.ASPECT_HEAT, 5);
		nodes.add(GLASS = new NodeDegradation(SAND, ab.toAspectArray(), new ItemStack(Block.glass), false, false));
		ab.clear();
		
		ab.setAspect(Aspects.ASPECT_CRYSTAL, 15)
		.setAspect(Aspects.ASPECT_VALUE, 5)
		.setAspect(Aspects.ASPECT_STRUCTURE, 5)
		.setAspect(Aspects.ASPECT_LIGHT, 20);
		nodes.add(GLOWSTONE = new NodeDegradation(GLASS, ab.toAspectArray(), new ItemStack(Block.glowStone), false, false));
		ab.clear();
		
		ab.setAspect(Aspects.ASPECT_STRUCTURE, 5);
		nodes.add(GRAVEL = new NodeDegradation(SAND, ab.toAspectArray(), new ItemStack(Block.gravel), false, false));
		nodes.add(COBBLESTONE = new NodeDegradation(STONE, ab.toAspectArray(), new ItemStack(Block.cobblestone), false, false));
		nodes.add(STONE = new NodeDegradation(COBBLESTONE, ab.toAspectArray(), new ItemStack(Block.stone), false, false));
		//TODO: Crafting rec?
		nodes.add(STONE_BRICK = new NodeDegradation(STONE, ab.toAspectArray(), new ItemStack(Block.stoneBrick), false, false));
		ab.clear();
		
		ab.setAspect(Aspects.ASPECT_LIVING, 15);
		nodes.add(COBBLESTONE_MOSSY = new NodeDegradation(COBBLESTONE, ab.toAspectArray(), new ItemStack(Block.cobblestoneMossy), false, false));
		nodes.add(SILVERFISH_STONE = new NodeDegradation(STONE, ab.toAspectArray(), new ItemStack(Block.silverfish, 0, 0), false, false));
		nodes.add(SILVERFISH_COBBLE = new NodeDegradation(COBBLESTONE, ab.toAspectArray(), new ItemStack(Block.silverfish, 0, 1), false, false));
		nodes.add(SILVERFISH_BRICK = new NodeDegradation(STONE_BRICK, ab.toAspectArray(), new ItemStack(Block.silverfish, 0, 2), false, false));
		ab.clear();
		
		
		/*
		 * SUB: Ores
		 */
		
		ab.setAspect(Aspects.ASPECT_METAL, 15)
		.setAspect(Aspects.ASPECT_VALUE, 5);
		nodes.add(INGOT_GOLD = new NodeDegradation(AIR, ab.toAspectArray(), new ItemStack(Item.ingotGold), false, false));
		ab.clear();
		
		ab.setAspect(Aspects.ASPECT_METAL, 50);
		nodes.add(INGOT_IRON = new NodeDegradation(AIR, ab.toAspectArray(), new ItemStack(Item.ingotIron), false, false));
		ab.clear();
		
		ab.setAspect(Aspects.ASPECT_CRYSTAL, 500)
		.setAspect(Aspects.ASPECT_VALUE, 500);
		nodes.add(INGOT_DIAMOND = new NodeDegradation(AIR, ab.toAspectArray(), new ItemStack(Item.diamond), false, false));
		ab.clear();
		
		ab.setAspect(Aspects.ASPECT_HEAT, 15);
		nodes.add(INGOT_COAL = new NodeDegradation(AIR, ab.toAspectArray(), new ItemStack(Item.coal), false, false));
		ab.clear();
		
		ab.setAspect(Aspects.ASPECT_CRYSTAL, 5)
		.setAspect(Aspects.ASPECT_VALUE, 5)
		.setAspect(Aspects.ASPECT_CRAFTING, 10);
		nodes.add(INGOT_REDSTONE = new NodeDegradation(AIR, ab.toAspectArray(), new ItemStack(Item.redstone), false, false));
		ab.clear();
		
		ab.setAspect(Aspects.ASPECT_EARTH, 5)
		.setAspect(Aspects.ASPECT_VALUE, 3);
		nodes.add(INGOT_LAPIS = new NodeDegradation(AIR, ab.toAspectArray(), new ItemStack(Item.dyePowder, 1, 4), false, false));
		ab.clear();
		
		ab.setAspect(Aspects.ASPECT_CRYSTAL, 500)
		.setAspect(Aspects.ASPECT_VALUE, 400);
		nodes.add(INGOT_EMERALD = new NodeDegradation(AIR, ab.toAspectArray(), new ItemStack(Item.emerald), false, false));
		ab.clear();
		
		nodes.add(new NodeSimpleOre(INGOT_GOLD, STONE, 1, new ItemStack(Block.oreGold)));
		nodes.add(new NodeSimpleOre(INGOT_IRON, STONE, 1, new ItemStack(Block.oreIron)));
		nodes.add(new NodeSimpleOre(INGOT_DIAMOND, STONE, 1, new ItemStack(Block.oreDiamond)));
		nodes.add(new NodeSimpleOre(INGOT_COAL, STONE, 1, new ItemStack(Block.oreCoal)));
		nodes.add(new NodeSimpleOre(INGOT_REDSTONE, STONE, 1, new ItemStack(Block.oreRedstone)));
		nodes.add(new NodeSimpleOre(INGOT_REDSTONE, STONE, 1, new ItemStack(Block.oreRedstoneGlowing)));
		nodes.add(new NodeSimpleOre(INGOT_LAPIS, STONE, 4, new ItemStack(Block.oreLapis)));
		nodes.add(new NodeSimpleOre(INGOT_EMERALD, STONE, 1, new ItemStack(Block.oreEmerald)));
		
		/*
		 * SUB: Plants
		 */
		
		ab.setAspect(Aspects.ASPECT_LIVING, 5).setAspect(Aspects.ASPECT_GROWTH, 5);
		nodes.add(VINE = new NodeDegradation(AIR, ab.toAspectArray(), new ItemStack(Block.vine, 1, 32767), false, false));
		ab.clear();
		

		ab.setAspect(Aspects.ASPECT_LIVING, 2).setAspect(Aspects.ASPECT_GROWTH, 2).setAspect(Aspects.ASPECT_WATER, 1);
		nodes.add(PLANT_RED = new NodeDegradation(AIR, ab.toAspectArray(), new ItemStack(Block.plantRed), true, false));
		nodes.add(PLANT_YELLOW = new NodeDegradation(AIR, ab.toAspectArray(), new ItemStack(Block.plantYellow), true, false));
		nodes.add(TALL_GRASS = new NodeDegradation(AIR, ab.toAspectArray(), new ItemStack(Block.tallGrass, 1, 32767), true, false));
		
		ab.setAspect(Aspects.ASPECT_WATER, 3);
		nodes.add(WATERLILY = new NodeDegradation(AIR, ab.toAspectArray(), new ItemStack(Block.waterlily), true, false));
		nodes.add(REEDS = new NodeDegradation(AIR, ab.toAspectArray(), new ItemStack(Block.reed), true, false));
		

		ab.setAspect(Aspects.ASPECT_WATER, 5);
		nodes.add(CACTUS = new NodeDegradation(AIR, ab.toAspectArray(), new ItemStack(Block.cactus), true, false));
		
		ab.clear();
		
		ab.setAspect(Aspects.ASPECT_STRUCTURE, 2);
		nodes.add(DEAD_BUSH = new NodeDegradation(AIR, ab.toAspectArray(), new ItemStack(Block.deadBush), false, false));
		ab.clear();
		
		ab.setAspect(Aspects.ASPECT_STRUCTURE, 5);
		nodes.add(WEB = new NodeDegradation(AIR, ab.toAspectArray(), new ItemStack(Block.web), false, false));
		ab.clear();
		
		ab.setAspect(Aspects.ASPECT_LIVING, 2).setAspect(Aspects.ASPECT_GROWTH, 3).setAspect(Aspects.ASPECT_WATER, 5);
		nodes.add(MUSHROOM_RED = new NodeDegradation(AIR, ab.toAspectArray(), new ItemStack(Block.mushroomRed), false, false));
		nodes.add(MUSHROOM_BROWN = new NodeDegradation(AIR, ab.toAspectArray(), new ItemStack(Block.mushroomBrown), false, false));
		
		ab.setAspect(Aspects.ASPECT_LIVING, 5).setAspect(Aspects.ASPECT_STRUCTURE, 2);
		
		nodes.add(MUSHROOM_CAP_RED = new NodeDegradation(AIR, ab.toAspectArray(), new ItemStack(Block.mushroomCapRed, 1, 32767), false, false));
		nodes.add(MUSHROOM_CAP_BROWN = new NodeDegradation(AIR, ab.toAspectArray(), new ItemStack(Block.mushroomCapBrown, 1, 32767), false, false));
		
		ab.clear();
		
		ab.setAspect(Aspects.ASPECT_LIVING, 5)
		.setAspect(Aspects.ASPECT_GROWTH, 3)
		.setAspect(Aspects.ASPECT_WATER, 5)
		.setAspect(Aspects.ASPECT_STRUCTURE, 2);

		
		nodes.add(MELON = new NodeDegradation(AIR, ab.toAspectArray(), new ItemStack(Block.melon), false, false));
		
		ab.setAspect(Aspects.ASPECT_WATER, 3);
		
		nodes.add(PUMPKIN = new NodeDegradation(AIR, ab.toAspectArray(), new ItemStack(Block.pumpkin), false, false));
		
		ab.clear();
		
	}

}
