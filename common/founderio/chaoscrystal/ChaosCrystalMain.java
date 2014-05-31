/**
 * 
 */
package founderio.chaoscrystal;

import java.util.Random;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldType;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.chaoscrystal.aspects.Aspects;
import founderio.chaoscrystal.aspects.ChaosRegistry;
import founderio.chaoscrystal.blocks.BlockApparatus;
import founderio.chaoscrystal.blocks.BlockBase;
import founderio.chaoscrystal.blocks.BlockLifeless;
import founderio.chaoscrystal.blocks.TileEntityCreator;
import founderio.chaoscrystal.blocks.TileEntityReconstructor;
import founderio.chaoscrystal.blocks.TileEntitySentry;
import founderio.chaoscrystal.entities.EntityChaosCrystal;
import founderio.chaoscrystal.entities.EntityFocusBorder;
import founderio.chaoscrystal.entities.EntityFocusFilter;
import founderio.chaoscrystal.entities.EntityFocusFilterTarget;
import founderio.chaoscrystal.entities.EntityFocusTransfer;
import founderio.chaoscrystal.items.ItemBlockApparatus;
import founderio.chaoscrystal.items.ItemBlockBase;
import founderio.chaoscrystal.items.ItemChaosCrystal;
import founderio.chaoscrystal.items.ItemCrystalGlasses;
import founderio.chaoscrystal.items.ItemFocus;
import founderio.chaoscrystal.items.ItemLifelessShard;
import founderio.chaoscrystal.items.ItemManual;
import founderio.chaoscrystal.network.CCPacketPipeline;
import founderio.chaoscrystal.worldgen.BiomeGenCrystal;
import founderio.chaoscrystal.worldgen.GenCrystalPillars;

/**
 * @author Oliver
 *
 */
@Mod(modid = Constants.MOD_ID, name = Constants.MOD_NAME, version = Constants.MOD_VERSION)
public class ChaosCrystalMain {
	@Instance(Constants.MOD_ID)
	public static ChaosCrystalMain instance;

	@SidedProxy(clientSide = "founderio.chaoscrystal.ClientProxy", serverSide = "founderio.chaoscrystal.CommonProxy")
	public static CommonProxy proxy;
	public static final CCPacketPipeline packetPipeline = new CCPacketPipeline();

	public static ChaosRegistry degradationStore;

	public static final Random rand = new Random();

	public static Item itemChaosCrystal;
	public static Item itemFocus;
	public static Item itemCrystalGlasses;
	public static Item itemManual;
	public static Item itemLifelessShard;

	public static BlockBase blockBase;
	public static BlockApparatus blockReconstructor;
	public static BlockApparatus blockCreator;
	public static BlockApparatus blockSentry;
	public static BlockLifeless blockLifeless;

	public static BiomeGenCrystal biomeCrystal;

	public static CreativeTabs creativeTab;

	private Configuration config;

	public static int cfgHitsPerTick = 1;
	public static int cfgMaxTriesPerTick = 80;
	public static int cfgCrystalRange = 10;
	public static int cfgCrystalTickInterval = 1;
	public static int cfgFocusRange = 20;
	public static int cfgFocusTickInterval = 60;

	public static int cfgCrystalAspectStorage = 1000000;
	public static boolean cfgForceBiome = false;
	public static boolean cfgDebugOutput = false;
	public static boolean cfgSneakToShowAspects = false;
	public static boolean cfgDebugGlasses = false;
	

	private int getBiomeId(String id, int defaultId) {
		return config.get("Biomes", id, defaultId).getInt();
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();

		cfgForceBiome = config.get("Settings", "force_biomes", cfgForceBiome).getBoolean(cfgForceBiome);
		cfgDebugOutput = config.get("Settings", "degradation_debug_output", cfgDebugOutput).getBoolean(cfgDebugOutput);
		cfgSneakToShowAspects = config.get("Settings", "sneak_to_show_aspects", true).getBoolean(cfgSneakToShowAspects);
		cfgDebugGlasses = config.get("Settings", "debug_glasses", false, "Enabled the debug mode on crystal glasses. (Displays additional info.)").getBoolean(cfgDebugGlasses);

		cfgCrystalAspectStorage = config.get("Settings", "crystal_aspect_storage", cfgCrystalAspectStorage).getInt();

		if(cfgCrystalAspectStorage < 10) {
			cfgCrystalAspectStorage = 10;
			config.get("Settings", "crystal_aspect_storage", cfgCrystalAspectStorage).set(cfgCrystalAspectStorage);
		}

		cfgHitsPerTick = config.get("Settings", "crystal_hits_per_tick", cfgHitsPerTick).getInt();
		cfgMaxTriesPerTick = config.get("Settings", "crystal_max_tries_per_tick", cfgMaxTriesPerTick).getInt();
		cfgCrystalRange = config.get("Settings", "crystal_range", cfgCrystalRange).getInt();
		cfgCrystalTickInterval = config.get("Settings", "crystal_tick_interval", cfgCrystalTickInterval).getInt();

		cfgFocusRange = config.get("Settings", "focus_range", cfgFocusRange).getInt();
		cfgFocusTickInterval = config.get("Settings", "focus_tick_interval", cfgFocusTickInterval).getInt();



		creativeTab = new CreativeTabs(Constants.MOD_ID) {
			@Override
			@SideOnly(Side.CLIENT)
			public Item getTabIconItem() {
				return itemChaosCrystal;
			}
		};

		itemChaosCrystal = new ItemChaosCrystal();
		itemChaosCrystal.setUnlocalizedName(Constants.ID_ITEM_CHAOSCRYSTAL);
		itemChaosCrystal.setCreativeTab(creativeTab);

		itemFocus = new ItemFocus();
		itemFocus.setUnlocalizedName(Constants.ID_ITEM_FOCUS);
		itemFocus.setCreativeTab(creativeTab);

		itemCrystalGlasses = new ItemCrystalGlasses();
		itemCrystalGlasses.setUnlocalizedName(Constants.ID_ITEM_CRYSTALGLASSES);
		itemCrystalGlasses.setCreativeTab(creativeTab);

		itemManual = new ItemManual();
		itemManual.setUnlocalizedName(Constants.ID_ITEM_MANUAL);
		itemManual.setCreativeTab(creativeTab);

		itemLifelessShard = new ItemLifelessShard();
		itemLifelessShard.setUnlocalizedName(Constants.ID_ITEM_LIFELESS_SHARD);
		itemLifelessShard.setCreativeTab(creativeTab);

		blockBase = new BlockBase();
		blockBase.setBlockName(Constants.ID_BLOCK_BASE);
		blockBase.setCreativeTab(creativeTab);

		blockLifeless = new BlockLifeless();
		blockLifeless.setBlockName(Constants.ID_BLOCK_LIFELESS);
		blockLifeless.setCreativeTab(creativeTab);

		blockReconstructor = new BlockApparatus(0);
		blockReconstructor.setBlockName(Constants.ID_BLOCK_APPARATUS_RECONSTRUCTOR);
		blockReconstructor.setCreativeTab(creativeTab);

		blockCreator = new BlockApparatus(1);
		blockCreator.setBlockName(Constants.ID_BLOCK_APPARATUS_CREATOR);
		blockCreator.setCreativeTab(creativeTab);

		blockSentry = new BlockApparatus(2);
		blockSentry.setBlockName(Constants.ID_BLOCK_APPARATUS_SENTRY);
		blockSentry.setCreativeTab(creativeTab);

		biomeCrystal = new BiomeGenCrystal(getBiomeId(Constants.NAME_BIOME_CRYSTAL, 68));

		config.save();
		GameRegistry.registerItem(itemChaosCrystal, Constants.ID_ITEM_CHAOSCRYSTAL, Constants.MOD_ID);
		GameRegistry.registerItem(itemFocus, Constants.ID_ITEM_FOCUS, Constants.MOD_ID);
		GameRegistry.registerItem(itemCrystalGlasses, Constants.ID_ITEM_CRYSTALGLASSES, Constants.MOD_ID);
		GameRegistry.registerItem(itemManual, Constants.ID_ITEM_MANUAL, Constants.MOD_ID);
		GameRegistry.registerItem(itemLifelessShard, Constants.ID_ITEM_LIFELESS_SHARD, Constants.MOD_ID);
		
		GameRegistry.registerBlock(blockBase, ItemBlockBase.class, Constants.ID_BLOCK_BASE);
		GameRegistry.registerBlock(blockLifeless, Constants.ID_BLOCK_LIFELESS);
		GameRegistry.registerBlock(blockReconstructor, ItemBlockApparatus.class, Constants.ID_BLOCK_APPARATUS_RECONSTRUCTOR);
		GameRegistry.registerBlock(blockCreator, ItemBlockApparatus.class, Constants.ID_BLOCK_APPARATUS_CREATOR);
		GameRegistry.registerBlock(blockSentry, ItemBlockApparatus.class, Constants.ID_BLOCK_APPARATUS_SENTRY);

		GameRegistry.registerTileEntity(TileEntityReconstructor.class, Constants.ID_TILEENTITY_RECONSTRUCTOR);
		GameRegistry.registerTileEntity(TileEntityCreator.class, Constants.ID_TILEENTITY_CREATOR);
		GameRegistry.registerTileEntity(TileEntitySentry.class, Constants.ID_TILEENTITY_SENTRY);

		BiomeDictionary.registerBiomeType(biomeCrystal, Type.BEACH);
		//TODO: add GenLayer or add to GenLayerBiome.field_151620_f etc. (reflection)
//TODO: fix
//		if(cfgForceBiome) {
//			//Just a test: remove all other biomes...
//			for(BiomeGenBase biome : WorldType.DEFAULT.getBiomesForWorldType()) {
//				GameRegistry.
//				GameRegistry.removeBiome(biome);
//			}
//		}

		BiomeManager.addSpawnBiome(biomeCrystal);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {

		packetPipeline.initialise();
		
		EntityRegistry.registerModEntity(EntityChaosCrystal.class, Constants.NAME_ENTITY_CHAOSCRYSTAL, 0, this, 128, 1, false);
		EntityRegistry.registerModEntity(EntityFocusTransfer.class, Constants.NAME_ENTITY_FOCUS_TRANSFER, 1, this, 128, 1, false);
		EntityRegistry.registerModEntity(EntityFocusBorder.class, Constants.NAME_ENTITY_FOCUS_BORDER, 2, this, 128, 1, false);
		EntityRegistry.registerModEntity(EntityFocusFilter.class, Constants.NAME_ENTITY_FOCUS_FILTER, 3, this, 128, 1, false);
		EntityRegistry.registerModEntity(EntityFocusFilterTarget.class, Constants.NAME_ENTITY_FOCUS_FILTER_TARGET, 4, this, 128, 1, false);
		proxy.registerRenderStuff();


		GameRegistry.registerWorldGenerator(new GenCrystalPillars(), 0);
		//GameRegistry.registerWorldGenerator(new GenCrystalFloats(), 0);

		GameRegistry.addRecipe(new ItemStack(itemChaosCrystal, 1), "RDR", "RER", "RDR", 'D', Items.diamond, 'R', new ItemStack(blockBase, 1, 1), 'E', Items.ender_pearl);
		GameRegistry.addRecipe(new ItemStack(itemFocus, 1, 0), "dBd", "BEB", "dBd", 'B', new ItemStack(blockBase, 1, 0), 'E', Items.ender_pearl, 'd', new ItemStack(Items.dye, 1, 4));
		GameRegistry.addRecipe(new ItemStack(itemFocus, 1, 1), "dBd", "BEB", "dBd", 'B', new ItemStack(blockBase, 1, 0), 'E', Items.ender_pearl, 'd', new ItemStack(Items.dye, 1, 10));
		GameRegistry.addRecipe(new ItemStack(itemFocus, 1, 2), "dBd", "BEB", "dBd", 'B', new ItemStack(blockBase, 1, 0), 'E', Items.ender_pearl, 'd', new ItemStack(Items.dye, 1, 5));
		GameRegistry.addRecipe(new ItemStack(itemCrystalGlasses, 1, 0), "B B", "GBG", 'B', new ItemStack(blockBase, 1, 0), 'G', Blocks.glass_pane);

		GameRegistry.addRecipe(new ItemStack(blockReconstructor, 1), "gBg", "OOO", 'g', new ItemStack(Items.dye, 1, 8), 'B', new ItemStack(blockBase, 1, 0), 'O', Blocks.obsidian);
		GameRegistry.addRecipe(new ItemStack(blockCreator, 1), "gYg", "OOO", 'g', new ItemStack(Items.dye, 1, 8), 'Y', new ItemStack(blockBase, 1, 2), 'O', Blocks.obsidian);
		GameRegistry.addRecipe(new ItemStack(blockSentry, 1), "OBO", "gYg", "OOO", 'g', new ItemStack(Items.dye, 1, 8), 'B', new ItemStack(blockBase, 1, 0), 'Y', new ItemStack(blockBase, 1, 2), 'O', Blocks.obsidian);

		GameRegistry.addShapelessRecipe(new ItemStack(itemManual), new ItemStack(blockBase, 1, 32767), Items.map);

		degradationStore = new ChaosRegistry();

		degradationStore.registerRepair(Items.diamond_pickaxe, new String[] { Aspects.ASPECT_CRYSTAL,  Aspects.ASPECT_VALUE }, new int[] { 1, 1 });
		degradationStore.registerRepair(Items.iron_pickaxe, new String[] { Aspects.ASPECT_METAL }, new int[] { 1 });
		degradationStore.registerRepair(Items.golden_pickaxe, new String[] { Aspects.ASPECT_CRYSTAL,  Aspects.ASPECT_VALUE }, new int[] { 1, 1 });
		degradationStore.registerRepair(Items.stone_pickaxe, new String[] { Aspects.ASPECT_EARTH,  Aspects.ASPECT_STRUCTURE }, new int[] { 2, 2 });
		degradationStore.registerRepair(Items.wooden_pickaxe, new String[] { Aspects.ASPECT_WOOD,  Aspects.ASPECT_GROWTH }, new int[] { 3, 2 });

		degradationStore.registerRepair(Items.diamond_axe, new String[] { Aspects.ASPECT_CRYSTAL,  Aspects.ASPECT_VALUE }, new int[] { 1, 1 });
		degradationStore.registerRepair(Items.iron_axe, new String[] { Aspects.ASPECT_METAL }, new int[] { 1 });
		degradationStore.registerRepair(Items.golden_axe, new String[] { Aspects.ASPECT_CRYSTAL,  Aspects.ASPECT_VALUE }, new int[] { 1, 1 });
		degradationStore.registerRepair(Items.stone_axe, new String[] { Aspects.ASPECT_EARTH,  Aspects.ASPECT_STRUCTURE }, new int[] { 2, 2 });
		degradationStore.registerRepair(Items.wooden_axe, new String[] { Aspects.ASPECT_WOOD,  Aspects.ASPECT_GROWTH }, new int[] { 3, 2 });

		degradationStore.registerRepair(Items.diamond_shovel, new String[] { Aspects.ASPECT_CRYSTAL,  Aspects.ASPECT_VALUE }, new int[] { 1, 1 });
		degradationStore.registerRepair(Items.iron_shovel, new String[] { Aspects.ASPECT_METAL }, new int[] { 1 });
		degradationStore.registerRepair(Items.golden_shovel, new String[] { Aspects.ASPECT_CRYSTAL,  Aspects.ASPECT_VALUE }, new int[] { 1, 1 });
		degradationStore.registerRepair(Items.stone_shovel, new String[] { Aspects.ASPECT_EARTH,  Aspects.ASPECT_STRUCTURE }, new int[] { 2, 2 });
		degradationStore.registerRepair(Items.wooden_shovel, new String[] { Aspects.ASPECT_WOOD,  Aspects.ASPECT_GROWTH }, new int[] { 3, 2 });

		degradationStore.registerRepair(Items.diamond_hoe, new String[] { Aspects.ASPECT_CRYSTAL,  Aspects.ASPECT_VALUE }, new int[] { 1, 1 });
		degradationStore.registerRepair(Items.iron_hoe, new String[] { Aspects.ASPECT_METAL }, new int[] { 1 });
		degradationStore.registerRepair(Items.golden_hoe, new String[] { Aspects.ASPECT_CRYSTAL,  Aspects.ASPECT_VALUE }, new int[] { 1, 1 });
		degradationStore.registerRepair(Items.stone_hoe, new String[] { Aspects.ASPECT_EARTH,  Aspects.ASPECT_STRUCTURE }, new int[] { 2, 2 });
		degradationStore.registerRepair(Items.wooden_hoe, new String[] { Aspects.ASPECT_WOOD,  Aspects.ASPECT_GROWTH }, new int[] { 3, 2 });

		degradationStore.registerRepair(Items.diamond_sword, new String[] { Aspects.ASPECT_CRYSTAL,  Aspects.ASPECT_VALUE }, new int[] { 1, 1 });
		degradationStore.registerRepair(Items.iron_sword, new String[] { Aspects.ASPECT_METAL }, new int[] { 1 });
		degradationStore.registerRepair(Items.golden_sword, new String[] { Aspects.ASPECT_CRYSTAL,  Aspects.ASPECT_VALUE }, new int[] { 1, 1 });
		degradationStore.registerRepair(Items.stone_sword, new String[] { Aspects.ASPECT_EARTH,  Aspects.ASPECT_STRUCTURE }, new int[] { 2, 2 });
		degradationStore.registerRepair(Items.wooden_sword, new String[] { Aspects.ASPECT_WOOD,  Aspects.ASPECT_GROWTH }, new int[] { 3, 2 });

		degradationStore.registerRepair(Items.shears, new String[] { Aspects.ASPECT_METAL }, new int[] { 1 });
		
		degradationStore.registerRepair(itemCrystalGlasses, new String[] { Aspects.ASPECT_CRYSTAL }, new int[] { 1 });
		degradationStore.registerRepair(Items.bow, new String[] { Aspects.ASPECT_WOOD, Aspects.ASPECT_STRUCTURE }, new int[] { 2, 1 });


		/*
		 * Grass, Dirt
		 */

		//		degradationStore.registerDegradation(
		//				new ItemStack(Block.tilledField, 0, 0),
		//				new String[]{Aspects.ASPECT_STRUCTURE, Aspects.ASPECT_GROWTH},
		//				new int[]{2, 1},
		//				new ItemStack(Block.dirt, 0, 0));
		//		degradationStore.registerDegradation(
		//				new ItemStack(Block.tilledField, 0, 1),
		//				new String[]{Aspects.ASPECT_WATER},
		//				new int[]{1},
		//				new ItemStack(Block.tilledField, 0, 0));


		/*
		 * Wood, Leaves
		 */
		//		for(int meta = 0; meta < 16; meta++) {//meh, just catch them all.
		//			degradationStore.registerDegradation(
		//					new ItemStack(Block.planks, 0, meta),
		//					new String[]{Aspects.ASPECT_WOOD},
		//					new int[]{1},
		//					new ItemStack(Block.leaves, 0, meta&3));// cap, only need 0,1,2,3
		//			degradationStore.registerDegradation(
		//					new ItemStack(Block.leaves, 0, meta),
		//					new String[]{Aspects.ASPECT_LIVING},
		//					new int[]{5},
		//					nullStack);
		//		}

		//		
		//
		//		degradationStore.registerDegradation(
		//				new ItemStack(Item.arrow),
		//				new String[]{Aspects.ASPECT_STRUCTURE, Aspects.ASPECT_WOOD, Aspects.ASPECT_LIVING},
		//				new int[]{2, 2, 2},
		//				new ItemStack(0, 0, 0));



	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

		packetPipeline.postInitialise();
		
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

		if(cfgDebugOutput) {
			degradationStore.debugOutput();
		}
	}
}
