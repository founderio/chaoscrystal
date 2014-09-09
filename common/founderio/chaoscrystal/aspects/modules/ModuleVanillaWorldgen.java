package founderio.chaoscrystal.aspects.modules;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.aspects.Aspect;
import founderio.chaoscrystal.aspects.AspectBuilder;
import founderio.chaoscrystal.aspects.AspectModule;
import founderio.chaoscrystal.aspects.ChaosRegistry;
import founderio.chaoscrystal.aspects.Node;

public class ModuleVanillaWorldgen {

	public static void registerRepairs(ChaosRegistry chaosRegistry) {
		AspectBuilder builder = new AspectBuilder();
		
		builder.clear().setAspect(Aspect.CRYSTAL, 1).setAspect(Aspect.VALUE, 1);
		chaosRegistry.registerRepair(Items.diamond_pickaxe, builder.toAspectArray());
		builder.clear().setAspect(Aspect.METAL, 1);
		chaosRegistry.registerRepair(Items.iron_pickaxe, builder.toAspectArray());
		builder.clear().setAspect(Aspect.METAL, 1).setAspect(Aspect.VALUE, 1);
		chaosRegistry.registerRepair(Items.golden_pickaxe, builder.toAspectArray());
		builder.clear().setAspect(Aspect.EARTH, 2).setAspect(Aspect.STRUCTURE, 2);
		chaosRegistry.registerRepair(Items.stone_pickaxe, builder.toAspectArray());
		builder.clear().setAspect(Aspect.WOOD, 3).setAspect(Aspect.GROWTH, 2);
		chaosRegistry.registerRepair(Items.wooden_pickaxe, builder.toAspectArray());

		builder.clear().setAspect(Aspect.CRYSTAL, 1).setAspect(Aspect.VALUE, 1);
		chaosRegistry.registerRepair(Items.diamond_axe, builder.toAspectArray());
		builder.clear().setAspect(Aspect.METAL, 1);
		chaosRegistry.registerRepair(Items.iron_axe, builder.toAspectArray());
		builder.clear().setAspect(Aspect.METAL, 1).setAspect(Aspect.VALUE, 1);
		chaosRegistry.registerRepair(Items.golden_axe, builder.toAspectArray());
		builder.clear().setAspect(Aspect.EARTH, 2).setAspect(Aspect.STRUCTURE, 2);
		chaosRegistry.registerRepair(Items.stone_axe, builder.toAspectArray());
		builder.clear().setAspect(Aspect.WOOD, 3).setAspect(Aspect.GROWTH, 2);
		chaosRegistry.registerRepair(Items.wooden_axe, builder.toAspectArray());

		builder.clear().setAspect(Aspect.CRYSTAL, 1).setAspect(Aspect.VALUE, 1);
		chaosRegistry.registerRepair(Items.diamond_shovel, builder.toAspectArray());
		builder.clear().setAspect(Aspect.METAL, 1);
		chaosRegistry.registerRepair(Items.iron_shovel, builder.toAspectArray());
		builder.clear().setAspect(Aspect.METAL, 1).setAspect(Aspect.VALUE, 1);
		chaosRegistry.registerRepair(Items.golden_shovel, builder.toAspectArray());
		builder.clear().setAspect(Aspect.EARTH, 2).setAspect(Aspect.STRUCTURE, 2);
		chaosRegistry.registerRepair(Items.stone_shovel, builder.toAspectArray());
		builder.clear().setAspect(Aspect.WOOD, 3).setAspect(Aspect.GROWTH, 2);
		chaosRegistry.registerRepair(Items.wooden_shovel, builder.toAspectArray());

		builder.clear().setAspect(Aspect.CRYSTAL, 1).setAspect(Aspect.VALUE, 1);
		chaosRegistry.registerRepair(Items.diamond_hoe, builder.toAspectArray());
		builder.clear().setAspect(Aspect.METAL, 1);
		chaosRegistry.registerRepair(Items.iron_hoe, builder.toAspectArray());
		builder.clear().setAspect(Aspect.METAL, 1).setAspect(Aspect.VALUE, 1);
		chaosRegistry.registerRepair(Items.golden_hoe, builder.toAspectArray());
		builder.clear().setAspect(Aspect.EARTH, 2).setAspect(Aspect.STRUCTURE, 2);
		chaosRegistry.registerRepair(Items.stone_hoe, builder.toAspectArray());
		builder.clear().setAspect(Aspect.WOOD, 3).setAspect(Aspect.GROWTH, 2);
		chaosRegistry.registerRepair(Items.wooden_hoe, builder.toAspectArray());

		builder.clear().setAspect(Aspect.CRYSTAL, 1).setAspect(Aspect.VALUE, 1);
		chaosRegistry.registerRepair(Items.diamond_sword, builder.toAspectArray());
		builder.clear().setAspect(Aspect.METAL, 1);
		chaosRegistry.registerRepair(Items.iron_sword, builder.toAspectArray());
		builder.clear().setAspect(Aspect.METAL, 1).setAspect(Aspect.VALUE, 1);
		chaosRegistry.registerRepair(Items.golden_sword, builder.toAspectArray());
		builder.clear().setAspect(Aspect.EARTH, 2).setAspect(Aspect.STRUCTURE, 2);
		chaosRegistry.registerRepair(Items.stone_sword, builder.toAspectArray());
		builder.clear().setAspect(Aspect.WOOD, 3).setAspect(Aspect.GROWTH, 2);
		chaosRegistry.registerRepair(Items.wooden_sword, builder.toAspectArray());

		builder.clear().setAspect(Aspect.METAL, 1);
		chaosRegistry.registerRepair(Items.shears, builder.toAspectArray());

		builder.clear().setAspect(Aspect.CRYSTAL, 1);
		chaosRegistry.registerRepair(ChaosCrystalMain.itemCrystalGlasses, builder.toAspectArray());
		builder.clear().setAspect(Aspect.WOOD, 2).setAspect(Aspect.STRUCTURE, 1);
		chaosRegistry.registerRepair(Items.bow, builder.toAspectArray());
	}

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

