package founderio.chaoscrystal.aspects.modules;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import founderio.chaoscrystal.aspects.AspectBuilder;
import founderio.chaoscrystal.aspects.AspectModule;
import founderio.chaoscrystal.aspects.ChaosRegistry;
import founderio.chaoscrystal.aspects.Node;

public class ModuleVanillaWorldgen extends AspectModule {


		//TODO: IgnoreList
		//degradationStore.addNode(WATER_MOVING = new NodeDegradation(null, ab.toAspectArray(), new ItemStack(Blocks.waterMoving, 1, 32767), false, false));

//TODO: caps for wildcard match transformation or something (think wood logs)
		
//TODO: Add ores
//		degradationStore.addNode(new NodeSimpleOre(INGOT_GOLD, STONE, 1, new ItemStack(Blocks.gold_ore)));
//		degradationStore.addNode(new NodeSimpleOre(INGOT_IRON, STONE, 1, new ItemStack(Blocks.iron_ore)));
//		degradationStore.addNode(new NodeSimpleOre(INGOT_DIAMOND, STONE, 1, new ItemStack(Blocks.diamond_ore)));
//		degradationStore.addNode(new NodeSimpleOre(INGOT_COAL, STONE, 1, new ItemStack(Blocks.coal_ore)));
//		degradationStore.addNode(new NodeSimpleOre(INGOT_REDSTONE, STONE, 1, new ItemStack(Blocks.redstone_ore)));
//		degradationStore.addNode(new NodeSimpleOre(INGOT_LAPIS, STONE, 4, new ItemStack(Blocks.lapis_ore)));
//		degradationStore.addNode(new NodeSimpleOre(INGOT_EMERALD, STONE, 1, new ItemStack(Blocks.emerald_ore)));

	//TODO: register these again.

			//		chaosRegistry.registerRepair(Items.diamond_pickaxe, new String[] { Aspects.ASPECT_CRYSTAL,  Aspects.ASPECT_VALUE }, new int[] { 1, 1 });
			//		chaosRegistry.registerRepair(Items.iron_pickaxe, new String[] { Aspects.ASPECT_METAL }, new int[] { 1 });
			//		chaosRegistry.registerRepair(Items.golden_pickaxe, new String[] { Aspects.ASPECT_CRYSTAL,  Aspects.ASPECT_VALUE }, new int[] { 1, 1 });
			//		chaosRegistry.registerRepair(Items.stone_pickaxe, new String[] { Aspects.ASPECT_EARTH,  Aspects.ASPECT_STRUCTURE }, new int[] { 2, 2 });
			//		chaosRegistry.registerRepair(Items.wooden_pickaxe, new String[] { Aspects.ASPECT_WOOD,  Aspects.ASPECT_GROWTH }, new int[] { 3, 2 });
			//
			//		chaosRegistry.registerRepair(Items.diamond_axe, new String[] { Aspects.ASPECT_CRYSTAL,  Aspects.ASPECT_VALUE }, new int[] { 1, 1 });
			//		chaosRegistry.registerRepair(Items.iron_axe, new String[] { Aspects.ASPECT_METAL }, new int[] { 1 });
			//		chaosRegistry.registerRepair(Items.golden_axe, new String[] { Aspects.ASPECT_CRYSTAL,  Aspects.ASPECT_VALUE }, new int[] { 1, 1 });
			//		chaosRegistry.registerRepair(Items.stone_axe, new String[] { Aspects.ASPECT_EARTH,  Aspects.ASPECT_STRUCTURE }, new int[] { 2, 2 });
			//		chaosRegistry.registerRepair(Items.wooden_axe, new String[] { Aspects.ASPECT_WOOD,  Aspects.ASPECT_GROWTH }, new int[] { 3, 2 });
			//
			//		chaosRegistry.registerRepair(Items.diamond_shovel, new String[] { Aspects.ASPECT_CRYSTAL,  Aspects.ASPECT_VALUE }, new int[] { 1, 1 });
			//		chaosRegistry.registerRepair(Items.iron_shovel, new String[] { Aspects.ASPECT_METAL }, new int[] { 1 });
			//		chaosRegistry.registerRepair(Items.golden_shovel, new String[] { Aspects.ASPECT_CRYSTAL,  Aspects.ASPECT_VALUE }, new int[] { 1, 1 });
			//		chaosRegistry.registerRepair(Items.stone_shovel, new String[] { Aspects.ASPECT_EARTH,  Aspects.ASPECT_STRUCTURE }, new int[] { 2, 2 });
			//		chaosRegistry.registerRepair(Items.wooden_shovel, new String[] { Aspects.ASPECT_WOOD,  Aspects.ASPECT_GROWTH }, new int[] { 3, 2 });
			//
			//		chaosRegistry.registerRepair(Items.diamond_hoe, new String[] { Aspects.ASPECT_CRYSTAL,  Aspects.ASPECT_VALUE }, new int[] { 1, 1 });
			//		chaosRegistry.registerRepair(Items.iron_hoe, new String[] { Aspects.ASPECT_METAL }, new int[] { 1 });
			//		chaosRegistry.registerRepair(Items.golden_hoe, new String[] { Aspects.ASPECT_CRYSTAL,  Aspects.ASPECT_VALUE }, new int[] { 1, 1 });
			//		chaosRegistry.registerRepair(Items.stone_hoe, new String[] { Aspects.ASPECT_EARTH,  Aspects.ASPECT_STRUCTURE }, new int[] { 2, 2 });
			//		chaosRegistry.registerRepair(Items.wooden_hoe, new String[] { Aspects.ASPECT_WOOD,  Aspects.ASPECT_GROWTH }, new int[] { 3, 2 });
			//
			//		chaosRegistry.registerRepair(Items.diamond_sword, new String[] { Aspects.ASPECT_CRYSTAL,  Aspects.ASPECT_VALUE }, new int[] { 1, 1 });
			//		chaosRegistry.registerRepair(Items.iron_sword, new String[] { Aspects.ASPECT_METAL }, new int[] { 1 });
			//		chaosRegistry.registerRepair(Items.golden_sword, new String[] { Aspects.ASPECT_CRYSTAL,  Aspects.ASPECT_VALUE }, new int[] { 1, 1 });
			//		chaosRegistry.registerRepair(Items.stone_sword, new String[] { Aspects.ASPECT_EARTH,  Aspects.ASPECT_STRUCTURE }, new int[] { 2, 2 });
			//		chaosRegistry.registerRepair(Items.wooden_sword, new String[] { Aspects.ASPECT_WOOD,  Aspects.ASPECT_GROWTH }, new int[] { 3, 2 });
			//
			//		chaosRegistry.registerRepair(Items.shears, new String[] { Aspects.ASPECT_METAL }, new int[] { 1 });
			//
			//		chaosRegistry.registerRepair(itemCrystalGlasses, new String[] { Aspects.ASPECT_CRYSTAL }, new int[] { 1 });
			//		chaosRegistry.registerRepair(Items.bow, new String[] { Aspects.ASPECT_WOOD, Aspects.ASPECT_STRUCTURE }, new int[] { 2, 1 });
//		degradationStore.autoRegisterDegradation(new ItemStack(Block.stoneBrick));
//		degradationStore.autoRegisterDegradation(new ItemStack(Block.sandStone, 1, 0));
//		degradationStore.autoRegisterDegradation(new ItemStack(Block.sandStone, 1, 1));
//		degradationStore.autoRegisterDegradation(new ItemStack(Block.sandStone, 1, 2));
//		degradationStore.autoRegisterDegradation(new ItemStack(Block.dispenser, 1, 32767));
//		degradationStore.autoRegisterDegradation(new ItemStack(Block.music, 1, 32767));
//		degradationStore.autoRegisterDegradation(new ItemStack(Block.rail, 1, 32767));
//		degradationStore.autoRegisterDegradation(new ItemStack(Block.railDetector, 1, 32767));
//		degradationStore.autoRegisterDegradation(new ItemStack(Block.railPowered, 1, 32767));
//		degradationStore.autoRegisterDegradation(new ItemStack(Block.railActivator, 1, 32767));
//		degradationStore.autoRegisterDegradation(new ItemStack(Block.pistonBase, 1, 32767));
//		degradationStore.autoRegisterDegradation(new ItemStack(Block.pistonStickyBase, 1, 32767));
//		degradationStore.autoRegisterDegradation(new ItemStack(Block.furnaceIdle, 1, 32767));
//		degradationStore.autoRegisterDegradation(new ItemStack(Block.stoneButton, 1, 32767));
//		degradationStore.autoRegisterDegradation(new ItemStack(Block.woodenButton, 1, 32767));
//		degradationStore.autoRegisterDegradation(new ItemStack(Block.ladder, 1, 32767));
//		degradationStore.autoRegisterDegradation(new ItemStack(Item.doorWood));
//		degradationStore.autoRegisterDegradation(new ItemStack(Item.doorIron));
//		degradationStore.autoRegisterDegradation(new ItemStack(Item.pickaxeDiamond));
//degradationStore.autoRegisterDegradation(new ItemStack(Item.arrow));
}

