package founderio.chaoscrystal.aspects.modules;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import founderio.chaoscrystal.aspects.AspectBuilder;
import founderio.chaoscrystal.aspects.AspectModule;
import founderio.chaoscrystal.aspects.ChaosRegistry;
import founderio.chaoscrystal.aspects.Node;

public class ModuleVanillaWorldgen extends AspectModule {

	/*
	 * "Ignore"-Blocks
	 */
	public static Node LIFELESS_BLOCK;
	public static Node LIFELESS_SHARD;
	public static Node WATER_MOVING;


	public static Node WATER_STILL;
	public static Node SAND;
	public static Node GRAVEL;
	public static Node DIRT_DEAD;
	public static Node DIRT;
	public static Node GRASS;
	public static Node MYCELIUM;
	public static Node PODZOL;
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

	public static Node WHEAT_SEEDS;
	public static Node WHEAT_0;
	public static Node WHEAT_1;
	public static Node WHEAT_2;
	public static Node WHEAT_3;
	public static Node WHEAT_4;
	public static Node WHEAT_5;
	public static Node WHEAT_6;
	public static Node WHEAT_7;
	
	public static Node MELON;
	public static Node PUMPKIN;


	public static Node LEATHER;
	public static Node ROTTEN_FLESH;
	public static Node FIRE;

	@Override
	public void registerNodes(ChaosRegistry degradationStore) {
		AspectBuilder ab = new AspectBuilder();

		/*
		 * "Ignore"-Blocks
		 */
		degradationStore.addNode(LIFELESS_BLOCK = new NodeLifelessBlock());
		degradationStore.addNode(LIFELESS_SHARD = new NodeLifelessShard());
		//TODO: IgnoreList
		//degradationStore.addNode(WATER_MOVING = new NodeDegradation(null, ab.toAspectArray(), new ItemStack(Blocks.waterMoving, 1, 32767), false, false));

		/*
		 * Worldgen-Blocks
		 */
		ab.setAspect(Aspects.ASPECT_WATER, 5);
		degradationStore.addNode(WATER_STILL = new NodeDegradation(LIFELESS_BLOCK, ab.toAspectArray(), new ItemStack(Blocks.water, 1, 32767), false, false));
		ab.clear();

		ab.setAspect(Aspects.ASPECT_EARTH, 5);
		degradationStore.addNode(SAND = new NodeDegradation(LIFELESS_BLOCK, ab.toAspectArray(), new ItemStack(Blocks.sand), false, false));
		degradationStore.addNode(new NodeDegradation(LIFELESS_BLOCK, ab.toAspectArray(), new ItemStack(Blocks.sand, 1, 1), false, false));
		ab.clear();

		ab.setAspect(Aspects.ASPECT_STRUCTURE, 5).setAspect(Aspects.ASPECT_LIVING, 2);
		degradationStore.addNode(DIRT_DEAD = new NodeDegradation(SAND, ab.toAspectArray(), new ItemStack(Blocks.dirt, 1, 1), false, false));
		ab.clear();

		ab.setAspect(Aspects.ASPECT_GROWTH, 5);
		degradationStore.addNode(DIRT = new NodeDegradation(DIRT_DEAD, ab.toAspectArray(), new ItemStack(Blocks.dirt), false, false));
		ab.clear();

		ab.setAspect(Aspects.ASPECT_LIVING, 5);
		degradationStore.addNode(GRASS = new NodeDegradation(DIRT, ab.toAspectArray(), new ItemStack(Blocks.grass), false, true));
		degradationStore.addNode(MYCELIUM = new NodeDegradation(DIRT, ab.toAspectArray(), new ItemStack(Blocks.mycelium), false, true));
		degradationStore.addNode(PODZOL = new NodeDegradation(SAND, ab.toAspectArray(), new ItemStack(Blocks.dirt, 1, 2), false, false));
		ab.clear();

		ab.setAspect(Aspects.ASPECT_WATER, 5).setAspect(Aspects.ASPECT_STRUCTURE, 2);
		degradationStore.addNode(CLAY = new NodeDegradation(SAND, ab.toAspectArray(), new ItemStack(Blocks.clay), false, false));
		ab.clear();

		//TODO: Smelting Recipe
		ab.setAspect(Aspects.ASPECT_HEAT, 5);
		degradationStore.addNode(GLASS = new NodeDegradation(SAND, ab.toAspectArray(), new ItemStack(Blocks.glass), false, false));
		ab.clear();

		ab.setAspect(Aspects.ASPECT_CRYSTAL, 15)
		.setAspect(Aspects.ASPECT_VALUE, 5)
		.setAspect(Aspects.ASPECT_STRUCTURE, 5)
		.setAspect(Aspects.ASPECT_LIGHT, 20);
		degradationStore.addNode(GLOWSTONE = new NodeDegradation(GLASS, ab.toAspectArray(), new ItemStack(Blocks.glowstone), false, false));
		ab.clear();

		ab.setAspect(Aspects.ASPECT_STRUCTURE, 5);
		degradationStore.addNode(GRAVEL = new NodeDegradation(SAND, ab.toAspectArray(), new ItemStack(Blocks.gravel), false, false));
		degradationStore.addNode(COBBLESTONE = new NodeDegradation(GRAVEL, ab.toAspectArray(), new ItemStack(Blocks.cobblestone), false, false));
		degradationStore.addNode(STONE = new NodeDegradation(COBBLESTONE, ab.toAspectArray(), new ItemStack(Blocks.stone), false, false));
		//TODO: Crafting rec?
		degradationStore.addNode(STONE_BRICK = degradationStore.autoRegisterDegradation(new ItemStack(Blocks.stonebrick)));
		ab.clear();

		ab.setAspect(Aspects.ASPECT_LIVING, 15);
		degradationStore.addNode(COBBLESTONE_MOSSY = new NodeDegradation(COBBLESTONE, ab.toAspectArray(), new ItemStack(Blocks.mossy_cobblestone), false, false));
		degradationStore.addNode(SILVERFISH_STONE = new NodeDegradation(STONE, ab.toAspectArray(), new ItemStack(Blocks.monster_egg, 1, 0), false, false));
		degradationStore.addNode(SILVERFISH_COBBLE = new NodeDegradation(COBBLESTONE, ab.toAspectArray(), new ItemStack(Blocks.monster_egg, 1, 1), false, false));
		degradationStore.addNode(SILVERFISH_BRICK = new NodeDegradation(STONE_BRICK, ab.toAspectArray(), new ItemStack(Blocks.monster_egg, 1, 2), false, false));
		ab.clear();


		/*
		 * SUB: Ores
		 */

		ab.setAspect(Aspects.ASPECT_METAL, 15)
		.setAspect(Aspects.ASPECT_VALUE, 5);
		degradationStore.addNode(INGOT_GOLD = new NodeDegradation(LIFELESS_SHARD, ab.toAspectArray(), new ItemStack(Items.gold_ingot), false, false));
		ab.clear();

		ab.setAspect(Aspects.ASPECT_METAL, 50);
		degradationStore.addNode(INGOT_IRON = new NodeDegradation(LIFELESS_SHARD, ab.toAspectArray(), new ItemStack(Items.iron_ingot), false, false));
		ab.clear();

		ab.setAspect(Aspects.ASPECT_CRYSTAL, 500)
		.setAspect(Aspects.ASPECT_VALUE, 500);
		degradationStore.addNode(INGOT_DIAMOND = new NodeDegradation(LIFELESS_SHARD, ab.toAspectArray(), new ItemStack(Items.diamond), false, false));
		ab.clear();

		ab.setAspect(Aspects.ASPECT_HEAT, 15);
		degradationStore.addNode(INGOT_COAL = new NodeDegradation(LIFELESS_SHARD, ab.toAspectArray(), new ItemStack(Items.coal), false, false));
		ab.clear();

		ab.setAspect(Aspects.ASPECT_CRYSTAL, 5)
		.setAspect(Aspects.ASPECT_VALUE, 5)
		.setAspect(Aspects.ASPECT_CRAFTING, 10);
		degradationStore.addNode(INGOT_REDSTONE = new NodeDegradation(LIFELESS_SHARD, ab.toAspectArray(), new ItemStack(Items.redstone), false, false));
		ab.clear();

		ab.setAspect(Aspects.ASPECT_EARTH, 5)
		.setAspect(Aspects.ASPECT_VALUE, 3);
		degradationStore.addNode(INGOT_LAPIS = new NodeDegradation(LIFELESS_SHARD, ab.toAspectArray(), new ItemStack(Items.dye, 1, 4), false, false));
		ab.clear();

		ab.setAspect(Aspects.ASPECT_CRYSTAL, 500)
		.setAspect(Aspects.ASPECT_VALUE, 400);
		degradationStore.addNode(INGOT_EMERALD = new NodeDegradation(LIFELESS_SHARD, ab.toAspectArray(), new ItemStack(Items.emerald), false, false));
		ab.clear();

		degradationStore.addNode(new NodeSimpleOre(INGOT_GOLD, STONE, 1, new ItemStack(Blocks.gold_ore)));
		degradationStore.addNode(new NodeSimpleOre(INGOT_IRON, STONE, 1, new ItemStack(Blocks.iron_ore)));
		degradationStore.addNode(new NodeSimpleOre(INGOT_DIAMOND, STONE, 1, new ItemStack(Blocks.diamond_ore)));
		degradationStore.addNode(new NodeSimpleOre(INGOT_COAL, STONE, 1, new ItemStack(Blocks.coal_ore)));
		degradationStore.addNode(new NodeSimpleOre(INGOT_REDSTONE, STONE, 1, new ItemStack(Blocks.redstone_ore)));
		degradationStore.addNode(new NodeSimpleOre(INGOT_LAPIS, STONE, 4, new ItemStack(Blocks.lapis_ore)));
		degradationStore.addNode(new NodeSimpleOre(INGOT_EMERALD, STONE, 1, new ItemStack(Blocks.emerald_ore)));

		/*
		 * SUB: Plants
		 */

		ab.setAspect(Aspects.ASPECT_LIVING, 5).setAspect(Aspects.ASPECT_GROWTH, 5);
		degradationStore.addNode(VINE = new NodeDegradation(LIFELESS_SHARD, ab.toAspectArray(), new ItemStack(Blocks.vine, 1, 32767), false, false));
		ab.clear();

		
		ab.setAspect(Aspects.ASPECT_LIVING, 2).setAspect(Aspects.ASPECT_GROWTH, 2).setAspect(Aspects.ASPECT_WATER, 1);
		degradationStore.addNode(PLANT_RED = new NodeDegradation(LIFELESS_SHARD, ab.toAspectArray(), new ItemStack(Blocks.red_flower), true, false));
		degradationStore.addNode(PLANT_YELLOW = new NodeDegradation(LIFELESS_SHARD, ab.toAspectArray(), new ItemStack(Blocks.yellow_flower), true, false));
		degradationStore.addNode(TALL_GRASS = new NodeDegradation(LIFELESS_SHARD, ab.toAspectArray(), new ItemStack(Blocks.tallgrass, 1, 32767), true, false));
		degradationStore.addNode(WHEAT_SEEDS = new NodeDegradation(LIFELESS_SHARD, ab.toAspectArray(), new ItemStack(Items.wheat_seeds, 1, 32767), true, false));
		ab.clear();
		
		ab.setAspect(Aspects.ASPECT_GROWTH, 2).setAspect(Aspects.ASPECT_WATER, 1);
		degradationStore.addNode(WHEAT_0 = new NodeDegradation(WHEAT_SEEDS, ab.toAspectArray(), new ItemStack(Blocks.wheat, 1, 0), true, false));
		degradationStore.addNode(WHEAT_1 = new NodeDegradation(WHEAT_0, ab.toAspectArray(), new ItemStack(Blocks.wheat, 1, 1), true, false));
		degradationStore.addNode(WHEAT_2 = new NodeDegradation(WHEAT_1, ab.toAspectArray(), new ItemStack(Blocks.wheat, 1, 2), true, false));
		degradationStore.addNode(WHEAT_3 = new NodeDegradation(WHEAT_2, ab.toAspectArray(), new ItemStack(Blocks.wheat, 1, 3), true, false));
		degradationStore.addNode(WHEAT_4 = new NodeDegradation(WHEAT_3, ab.toAspectArray(), new ItemStack(Blocks.wheat, 1, 4), true, false));
		degradationStore.addNode(WHEAT_5 = new NodeDegradation(WHEAT_4, ab.toAspectArray(), new ItemStack(Blocks.wheat, 1, 5), true, false));
		degradationStore.addNode(WHEAT_6 = new NodeDegradation(WHEAT_5, ab.toAspectArray(), new ItemStack(Blocks.wheat, 1, 6), true, false));
		degradationStore.addNode(WHEAT_7 = new NodeDegradation(WHEAT_6, ab.toAspectArray(), new ItemStack(Blocks.wheat, 1, 7), true, false));
		degradationStore.addNode(new NodeDegradation(WHEAT_7, ab.toAspectArray(), new ItemStack(Blocks.wheat, 1, 8), true, false));
		
		ab.setAspect(Aspects.ASPECT_GROWTH, 16).setAspect(Aspects.ASPECT_WATER, 8);
		degradationStore.addNode(new NodeDegradation(WHEAT_SEEDS, ab.toAspectArray(), new ItemStack(Items.wheat, 1, 32767), true, false));
		
		ab.setAspect(Aspects.ASPECT_LIVING, 2).setAspect(Aspects.ASPECT_GROWTH, 2).setAspect(Aspects.ASPECT_WATER, 3);
		degradationStore.addNode(WATERLILY = new NodeDegradation(LIFELESS_BLOCK, ab.toAspectArray(), new ItemStack(Blocks.waterlily), true, false));
		degradationStore.addNode(REEDS = new NodeDegradation(LIFELESS_BLOCK, ab.toAspectArray(), new ItemStack(Blocks.reeds), true, false));


		ab.setAspect(Aspects.ASPECT_WATER, 5);
		degradationStore.addNode(CACTUS = new NodeDegradation(LIFELESS_BLOCK, ab.toAspectArray(), new ItemStack(Blocks.cactus), true, false));

		ab.clear();

		ab.setAspect(Aspects.ASPECT_STRUCTURE, 2);
		degradationStore.addNode(DEAD_BUSH = new NodeDegradation(LIFELESS_BLOCK, ab.toAspectArray(), new ItemStack(Blocks.deadbush), false, false));
		ab.clear();

		ab.setAspect(Aspects.ASPECT_STRUCTURE, 5);
		degradationStore.addNode(WEB = new NodeDegradation(LIFELESS_BLOCK, ab.toAspectArray(), new ItemStack(Blocks.web), false, false));
		ab.clear();

		ab.setAspect(Aspects.ASPECT_LIVING, 2).setAspect(Aspects.ASPECT_GROWTH, 3).setAspect(Aspects.ASPECT_WATER, 5);
		degradationStore.addNode(MUSHROOM_RED = new NodeDegradation(LIFELESS_BLOCK, ab.toAspectArray(), new ItemStack(Blocks.red_mushroom), false, false));
		degradationStore.addNode(MUSHROOM_BROWN = new NodeDegradation(LIFELESS_BLOCK, ab.toAspectArray(), new ItemStack(Blocks.brown_mushroom), false, false));

		ab.setAspect(Aspects.ASPECT_LIVING, 5).setAspect(Aspects.ASPECT_STRUCTURE, 2);

		degradationStore.addNode(MUSHROOM_CAP_RED = new NodeDegradation(LIFELESS_BLOCK, ab.toAspectArray(), new ItemStack(Blocks.red_mushroom_block, 1, 32767), false, false));
		degradationStore.addNode(MUSHROOM_CAP_BROWN = new NodeDegradation(LIFELESS_BLOCK, ab.toAspectArray(), new ItemStack(Blocks.brown_mushroom_block, 1, 32767), false, false));
		
		ab.clear();

		ab.setAspect(Aspects.ASPECT_LIVING, 5)
		.setAspect(Aspects.ASPECT_GROWTH, 3)
		.setAspect(Aspects.ASPECT_WATER, 5)
		.setAspect(Aspects.ASPECT_STRUCTURE, 2);


		degradationStore.addNode(MELON = new NodeDegradation(LIFELESS_BLOCK, ab.toAspectArray(), new ItemStack(Blocks.melon_block), false, false));

		ab.setAspect(Aspects.ASPECT_WATER, 3);

		degradationStore.addNode(PUMPKIN = new NodeDegradation(LIFELESS_BLOCK, ab.toAspectArray(), new ItemStack(Blocks.pumpkin), false, false));

		ab.clear();

		ab.setAspect(Aspects.ASPECT_STRUCTURE, 3).setAspect(Aspects.ASPECT_GROWTH, 3);
		degradationStore.addNode(ROTTEN_FLESH = new NodeDegradation(LIFELESS_BLOCK, ab.toAspectArray(), new ItemStack(Items.rotten_flesh), false, false));
		ab.setAspect(Aspects.ASPECT_STRUCTURE, 5).setAspect(Aspects.ASPECT_GROWTH, 5).setAspect(Aspects.ASPECT_LIVING, 5).setAspect(Aspects.ASPECT_VALUE, 2);
		degradationStore.addNode(LEATHER = new NodeDegradation(ROTTEN_FLESH, ab.toAspectArray(), new ItemStack(Items.leather), false, false));
		ab.clear();

		ab.setAspect(Aspects.ASPECT_HEAT, 1);
		degradationStore.addNode(FIRE = new NodeDegradation(LIFELESS_BLOCK, ab.toAspectArray(), new ItemStack(Blocks.fire), false, false));
		ab.clear();

	}

}
