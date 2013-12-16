/**
 * 
 */
package founderio.chaoscrystal;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import founderio.chaoscrystal.blocks.BlockApparatus;
import founderio.chaoscrystal.blocks.BlockBase;
import founderio.chaoscrystal.blocks.TileEntityCreator;
import founderio.chaoscrystal.blocks.TileEntityReconstructor;
import founderio.chaoscrystal.degradation.Aspects;
import founderio.chaoscrystal.degradation.DegradationStore;
import founderio.chaoscrystal.entities.EntityChaosCrystal;
import founderio.chaoscrystal.entities.EntityFocusBorder;
import founderio.chaoscrystal.entities.EntityFocusFilter;
import founderio.chaoscrystal.entities.EntityFocusTransfer;
import founderio.chaoscrystal.items.ItemBlockApparatus;
import founderio.chaoscrystal.items.ItemBlockBase;
import founderio.chaoscrystal.items.ItemChaosCrystal;
import founderio.chaoscrystal.items.ItemCrystalGlasses;
import founderio.chaoscrystal.items.ItemFocus;
import founderio.chaoscrystal.items.ItemManual;
import founderio.chaoscrystal.rendering.OverlayAspectSelector;
import founderio.chaoscrystal.worldgen.BiomeGenCrystal;
import founderio.chaoscrystal.worldgen.GenCrystalFloats;
import founderio.chaoscrystal.worldgen.GenCrystalPillars;

/**
 * @author Oliver
 *
 */
@Mod(modid = Constants.MOD_ID, name = Constants.MOD_NAME, version = Constants.MOD_VERSION)
@NetworkMod(clientSideRequired = true, serverSideRequired = false, channels = { Constants.CHANNEL_NAME_PARTICLES, Constants.CHANNEL_NAME_OTHER_VISUAL }, packetHandler = ChaosCrystalNetworkHandler.class)
public class ChaosCrystalMain {
	@Instance(Constants.MOD_ID)
	public static ChaosCrystalMain instance;
	
	@SidedProxy(clientSide = "founderio.chaoscrystal.ClientProxy", serverSide = "founderio.chaoscrystal.CommonProxy")
	public static CommonProxy proxy;
	
	public static DegradationStore degradationStore;
	
	public static Item itemChaosCrystal;
	public static Item itemFocus;
	public static Item itemCrystalGlasses;
	public static Item itemManual;
	
	public static BlockBase blockBase;
	public static BlockApparatus blockReconstructor;
	public static BlockApparatus blockCreator;
	
	public static BiomeGenCrystal biomeCrystal;
	
	private Configuration config;
	
	public static int cfg_maxAspectStorage = 1000000;
	public static boolean cfg_forceBiome = false;
	public static boolean cfg_debugOutput = false;
	public static boolean cfg_nonDestructive = true;
	public static boolean cfg_sneakToShowAspects = false;
	
	private int getItemId(String id, int defaultId) {
		return config.get("Items", id, defaultId).getInt();
	}
	
	private int getBlockId(String id, int defaultId) {
		return config.get("Blocks", id, defaultId).getInt();
	}
	
	private int getBiomeId(String id, int defaultId) {
		return config.get("Biomes", id, defaultId).getInt();
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		cfg_forceBiome = config.get("Settings", "force_biomes", cfg_forceBiome).getBoolean(cfg_forceBiome);
		cfg_debugOutput = config.get("Settings", "degradation_debug_output", cfg_debugOutput).getBoolean(cfg_debugOutput);
		cfg_maxAspectStorage = config.get("Settings", "max_aspect_storage", cfg_maxAspectStorage).getInt(cfg_maxAspectStorage);
		if(cfg_maxAspectStorage < 10) {
			cfg_maxAspectStorage = 10;
			config.get("Settings", "max_spect_storage", cfg_maxAspectStorage).set(cfg_maxAspectStorage);
		}
		cfg_nonDestructive = config.get("Settings", "non_destructive", cfg_nonDestructive).getBoolean(cfg_nonDestructive);
		cfg_sneakToShowAspects = config.get("Settings", "sneak_to_show_aspects", false).getBoolean(false);
		//TODO: Get cfgs for ranges etc. of foci, crystals
		
		
		itemChaosCrystal = new ItemChaosCrystal(getItemId(Constants.ID_ITEM_CHAOSCRYSTAL, 18200));
		itemChaosCrystal.setUnlocalizedName(Constants.ID_ITEM_CHAOSCRYSTAL);
		itemChaosCrystal.setMaxStackSize(1);
		
		itemFocus = new ItemFocus(getItemId(Constants.ID_ITEM_FOCUS, 18201));
		itemFocus.setUnlocalizedName(Constants.ID_ITEM_FOCUS);
		
		itemCrystalGlasses = new ItemCrystalGlasses(getItemId(Constants.ID_ITEM_CRYSTALGLASSES, 18202));
		itemCrystalGlasses.setUnlocalizedName(Constants.ID_ITEM_CRYSTALGLASSES);
		
		itemManual = new ItemManual(getItemId(Constants.ID_ITEM_MANUAL, 18203));
		itemManual.setUnlocalizedName(Constants.ID_ITEM_MANUAL);
		
		blockBase = new BlockBase(getBlockId(Constants.ID_BLOCK_BASE, 230), Material.glass);
		blockBase.setUnlocalizedName(Constants.ID_BLOCK_BASE);
		blockBase.setCreativeTab(CreativeTabs.tabBlock);
		blockBase.setHardness(4);
		blockBase.setLightValue(0.2f);
		blockBase.setResistance(1.5f);
		blockBase.setStepSound(Block.soundGlassFootstep);
		MinecraftForge.setBlockHarvestLevel(blockBase, "pickaxe", 1);
		
		blockReconstructor = new BlockApparatus(getBlockId(Constants.ID_BLOCK_APPARATUS_RECONSTRUCTOR, 231), Material.rock, 0);
		blockReconstructor.setUnlocalizedName(Constants.ID_BLOCK_APPARATUS_RECONSTRUCTOR);
		blockReconstructor.setCreativeTab(CreativeTabs.tabBlock);
		blockReconstructor.setHardness(2);
		blockReconstructor.setLightValue(0.2f);
		blockReconstructor.setResistance(6f);
		blockReconstructor.setStepSound(Block.soundStoneFootstep);
		MinecraftForge.setBlockHarvestLevel(blockReconstructor, "pickaxe", 0);

		blockCreator = new BlockApparatus(getBlockId(Constants.ID_BLOCK_APPARATUS_CREATOR, 232), Material.rock, 1);
		blockCreator.setUnlocalizedName(Constants.ID_BLOCK_APPARATUS_CREATOR);
		blockCreator.setCreativeTab(CreativeTabs.tabBlock);
		blockCreator.setHardness(2);
		blockCreator.setLightValue(0.2f);
		blockCreator.setResistance(6f);
		blockCreator.setStepSound(Block.soundStoneFootstep);
		MinecraftForge.setBlockHarvestLevel(blockCreator, "pickaxe", 0);
		
		biomeCrystal = new BiomeGenCrystal(getBiomeId(Constants.ID_BIOME_CRYSTAL, 68));
		
		config.save();
		GameRegistry.registerItem(itemChaosCrystal, Constants.ID_ITEM_CHAOSCRYSTAL, Constants.MOD_ID);
		GameRegistry.registerItem(itemFocus, Constants.ID_ITEM_FOCUS, Constants.MOD_ID);
		GameRegistry.registerItem(itemCrystalGlasses, Constants.ID_ITEM_CRYSTALGLASSES, Constants.MOD_ID);
		GameRegistry.registerBlock(blockBase, ItemBlockBase.class, Constants.ID_BLOCK_BASE, Constants.MOD_ID);
		GameRegistry.registerBlock(blockReconstructor, ItemBlockApparatus.class, Constants.ID_BLOCK_APPARATUS_RECONSTRUCTOR, Constants.MOD_ID);
		GameRegistry.registerBlock(blockCreator, ItemBlockApparatus.class, Constants.ID_BLOCK_APPARATUS_CREATOR, Constants.MOD_ID);
		
		GameRegistry.registerTileEntity(TileEntityReconstructor.class, Constants.ID_TILEENTITY_REENACTOR);
		GameRegistry.registerTileEntity(TileEntityCreator.class, Constants.ID_TILEENTITY_CREATOR);
		
		if(cfg_forceBiome) {
			//Just a test: remove all other biomes...
			for(BiomeGenBase biome : WorldType.DEFAULT.getBiomesForWorldType()) {
				GameRegistry.removeBiome(biome);
			}
		}
		
		GameRegistry.addBiome(biomeCrystal);
		BiomeManager.addSpawnBiome(biomeCrystal);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {

		EntityRegistry.registerModEntity(EntityChaosCrystal.class, Constants.NAME_ENTITY_CHAOSCRYSTAL, 0, this, 128, 1, false);
		EntityRegistry.registerModEntity(EntityFocusTransfer.class, Constants.NAME_ENTITY_FOCUS_TRANSFER, 1, this, 128, 1, false);
		EntityRegistry.registerModEntity(EntityFocusBorder.class, Constants.NAME_ENTITY_FOCUS_BORDER, 2, this, 128, 1, false);
		EntityRegistry.registerModEntity(EntityFocusFilter.class, Constants.NAME_ENTITY_FOCUS_FILTER, 3, this, 128, 1, false);
		proxy.registerRenderStuff();
		
		MinecraftForge.EVENT_BUS.register(new OverlayAspectSelector());
		
		GameRegistry.registerWorldGenerator(new GenCrystalPillars());
		GameRegistry.registerWorldGenerator(new GenCrystalFloats());
		
		GameRegistry.addRecipe(new ItemStack(itemChaosCrystal, 1), "RDR", "RER", "RDR", 'D', Item.diamond, 'R', new ItemStack(blockBase, 1, 1), 'E', Item.enderPearl);
		GameRegistry.addRecipe(new ItemStack(itemFocus, 1, 0), "dBd", "BEB", "dBd", 'B', new ItemStack(blockBase, 1, 0), 'E', Item.enderPearl, 'd', new ItemStack(Item.dyePowder, 1, 4));
		GameRegistry.addRecipe(new ItemStack(itemFocus, 1, 1), "dBd", "BEB", "dBd", 'B', new ItemStack(blockBase, 1, 0), 'E', Item.enderPearl, 'd', new ItemStack(Item.dyePowder, 1, 10));
		GameRegistry.addRecipe(new ItemStack(itemFocus, 1, 2), "dBd", "BEB", "dBd", 'B', new ItemStack(blockBase, 1, 0), 'E', Item.enderPearl, 'd', new ItemStack(Item.dyePowder, 1, 5));
		GameRegistry.addRecipe(new ItemStack(itemCrystalGlasses, 1, 0), "BBB", "BGB", " B ", 'B', new ItemStack(blockBase, 1, 0), 'G', Block.thinGlass);
		
		degradationStore = new DegradationStore();
		
		degradationStore.registerRepair(Item.pickaxeDiamond.itemID, new String[] { Aspects.ASPECT_CRYSTAL,  Aspects.ASPECT_VALUE }, new int[] { 1, 1 });
		degradationStore.registerRepair(Item.pickaxeIron.itemID, new String[] { Aspects.ASPECT_METAL }, new int[] { 1 });
		degradationStore.registerRepair(Item.pickaxeGold.itemID, new String[] { Aspects.ASPECT_CRYSTAL,  Aspects.ASPECT_VALUE }, new int[] { 1, 1 });
		degradationStore.registerRepair(Item.pickaxeStone.itemID, new String[] { Aspects.ASPECT_EARTH,  Aspects.ASPECT_STRUCTURE }, new int[] { 2, 2 });
		degradationStore.registerRepair(Item.pickaxeWood.itemID, new String[] { Aspects.ASPECT_WOOD,  Aspects.ASPECT_GROWTH }, new int[] { 3, 2 });
		
		degradationStore.registerRepair(Item.axeDiamond.itemID, new String[] { Aspects.ASPECT_CRYSTAL,  Aspects.ASPECT_VALUE }, new int[] { 1, 1 });
		degradationStore.registerRepair(Item.axeIron.itemID, new String[] { Aspects.ASPECT_METAL }, new int[] { 1 });
		degradationStore.registerRepair(Item.axeGold.itemID, new String[] { Aspects.ASPECT_CRYSTAL,  Aspects.ASPECT_VALUE }, new int[] { 1, 1 });
		degradationStore.registerRepair(Item.axeStone.itemID, new String[] { Aspects.ASPECT_EARTH,  Aspects.ASPECT_STRUCTURE }, new int[] { 2, 2 });
		degradationStore.registerRepair(Item.axeWood.itemID, new String[] { Aspects.ASPECT_WOOD,  Aspects.ASPECT_GROWTH }, new int[] { 3, 2 });
		
		degradationStore.registerRepair(Item.shovelDiamond.itemID, new String[] { Aspects.ASPECT_CRYSTAL,  Aspects.ASPECT_VALUE }, new int[] { 1, 1 });
		degradationStore.registerRepair(Item.shovelIron.itemID, new String[] { Aspects.ASPECT_METAL }, new int[] { 1 });
		degradationStore.registerRepair(Item.shovelGold.itemID, new String[] { Aspects.ASPECT_CRYSTAL,  Aspects.ASPECT_VALUE }, new int[] { 1, 1 });
		degradationStore.registerRepair(Item.shovelStone.itemID, new String[] { Aspects.ASPECT_EARTH,  Aspects.ASPECT_STRUCTURE }, new int[] { 2, 2 });
		degradationStore.registerRepair(Item.shovelWood.itemID, new String[] { Aspects.ASPECT_WOOD,  Aspects.ASPECT_GROWTH }, new int[] { 3, 2 });

		degradationStore.registerRepair(Item.hoeDiamond.itemID, new String[] { Aspects.ASPECT_CRYSTAL,  Aspects.ASPECT_VALUE }, new int[] { 1, 1 });
		degradationStore.registerRepair(Item.hoeIron.itemID, new String[] { Aspects.ASPECT_METAL }, new int[] { 1 });
		degradationStore.registerRepair(Item.hoeGold.itemID, new String[] { Aspects.ASPECT_CRYSTAL,  Aspects.ASPECT_VALUE }, new int[] { 1, 1 });
		degradationStore.registerRepair(Item.hoeStone.itemID, new String[] { Aspects.ASPECT_EARTH,  Aspects.ASPECT_STRUCTURE }, new int[] { 2, 2 });
		degradationStore.registerRepair(Item.hoeWood.itemID, new String[] { Aspects.ASPECT_WOOD,  Aspects.ASPECT_GROWTH }, new int[] { 3, 2 });

		degradationStore.registerRepair(Item.swordDiamond.itemID, new String[] { Aspects.ASPECT_CRYSTAL,  Aspects.ASPECT_VALUE }, new int[] { 1, 1 });
		degradationStore.registerRepair(Item.swordIron.itemID, new String[] { Aspects.ASPECT_METAL }, new int[] { 1 });
		degradationStore.registerRepair(Item.swordGold.itemID, new String[] { Aspects.ASPECT_CRYSTAL,  Aspects.ASPECT_VALUE }, new int[] { 1, 1 });
		degradationStore.registerRepair(Item.swordStone.itemID, new String[] { Aspects.ASPECT_EARTH,  Aspects.ASPECT_STRUCTURE }, new int[] { 2, 2 });
		degradationStore.registerRepair(Item.swordWood.itemID, new String[] { Aspects.ASPECT_WOOD,  Aspects.ASPECT_GROWTH }, new int[] { 3, 2 });
		
		degradationStore.registerRepair(itemCrystalGlasses.itemID, new String[] { Aspects.ASPECT_CRYSTAL }, new int[] { 1 });
		
		ItemStack nullStack = new ItemStack(0, 0, 0);
		
		/*
		 * Grass, Dirt
		 */
		degradationStore.registerDegradation(
				new ItemStack(Block.grass),
				new String[]{Aspects.ASPECT_LIVING},
				new int[]{5},
				new ItemStack(Block.dirt));
		degradationStore.registerDegradation(
				new ItemStack(Block.mycelium),
				new String[]{Aspects.ASPECT_LIVING},
				new int[]{5},
				new ItemStack(Block.dirt));
		degradationStore.registerDegradation(
				new ItemStack(Block.dirt, 0, 0),
				new String[]{Aspects.ASPECT_GROWTH},
				new int[]{5},
				new ItemStack(Block.dirt, 0, 1));
		degradationStore.registerDegradation(
				new ItemStack(Block.tilledField, 0, 0),
				new String[]{Aspects.ASPECT_STRUCTURE, Aspects.ASPECT_GROWTH},
				new int[]{2, 1},
				new ItemStack(Block.dirt, 0, 0));
		degradationStore.registerDegradation(
				new ItemStack(Block.tilledField, 0, 1),
				new String[]{Aspects.ASPECT_WATER},
				new int[]{1},
				new ItemStack(Block.tilledField, 0, 0));
		degradationStore.registerDegradation(
				new ItemStack(Block.dirt, 0, 1),
				new String[]{Aspects.ASPECT_STRUCTURE, Aspects.ASPECT_LIVING},
				new int[]{5, 2},
				new ItemStack(Block.sand));
		
		degradationStore.registerDegradation(
				new ItemStack(Block.sand),
				new String[]{Aspects.ASPECT_EARTH},
				new int[]{5},
				nullStack);
		degradationStore.registerDegradation(
				new ItemStack(Block.blockClay),
				new String[]{Aspects.ASPECT_WATER, Aspects.ASPECT_STRUCTURE},
				new int[]{5, 2},
				new ItemStack(Block.sand));
		/*
		 * Misc/Structures
		 */
		degradationStore.registerDegradation(
				new ItemStack(Block.workbench),
				new String[]{Aspects.ASPECT_CRAFTING},
				new int[]{5},
				new ItemStack(Block.planks, 0, 0));
		
		degradationStore.registerDegradation(
				new ItemStack(Block.waterStill, 0, 32767),
				new String[]{Aspects.ASPECT_WATER},
				new int[]{5},
				nullStack);
		degradationStore.registerDegradation(
				new ItemStack(Block.waterMoving, 0, 32767),
				new String[]{},
				new int[]{},
				new ItemStack[0]);
		degradationStore.registerDegradation(
				new ItemStack(Block.glass),
				new String[]{Aspects.ASPECT_HEAT, Aspects.ASPECT_STRUCTURE},
				new int[]{5, 2},
				new ItemStack(Block.sand, 0, 0));
		
		//TODO: Ore Dict Match
		
		/*
		 * Wood, Leaves
		 */
		for(int meta = 0; meta < 16; meta++) {//meh, just catch them all.
			degradationStore.registerDegradation(
					new ItemStack(Block.planks, 0, meta),
					new String[]{Aspects.ASPECT_WOOD},
					new int[]{1},
					new ItemStack(Block.leaves, 0, meta&3));// cap, only need 0,1,2,3
			degradationStore.registerDegradation(
					new ItemStack(Block.wood, 0, meta),
					new String[]{Aspects.ASPECT_WOOD, Aspects.ASPECT_GROWTH},
					new int[]{4, 5},
					new ItemStack(Block.planks, 0, meta&3));
			degradationStore.registerDegradation(
					new ItemStack(Block.leaves, 0, meta),
					new String[]{Aspects.ASPECT_LIVING},
					new int[]{5},
					nullStack);
			degradationStore.registerDegradation(
					new ItemStack(Block.vine, 0, meta),
					new String[]{Aspects.ASPECT_LIVING, Aspects.ASPECT_GROWTH},
					new int[]{3, 3},
					nullStack);
		}
		for(int meta = 0; meta < 4; meta++) {
			degradationStore.registerDegradation(
					new ItemStack(Block.tallGrass, 0, meta),
					new String[]{Aspects.ASPECT_LIVING, Aspects.ASPECT_GROWTH, Aspects.ASPECT_WATER},
					new int[]{3, 3, 2},
					nullStack);
		}
		degradationStore.registerDegradation(
				new ItemStack(Block.waterlily),
				new String[]{Aspects.ASPECT_LIVING, Aspects.ASPECT_GROWTH, Aspects.ASPECT_WATER},
				new int[]{3, 3, 2},
				nullStack);
		degradationStore.registerDegradation(
				new ItemStack(Block.melon),
				new String[]{Aspects.ASPECT_LIVING, Aspects.ASPECT_GROWTH, Aspects.ASPECT_STRUCTURE, Aspects.ASPECT_WATER},
				new int[]{5, 5, 5, 2},
				nullStack);
		degradationStore.registerDegradation(
				new ItemStack(Block.pumpkin),
				new String[]{Aspects.ASPECT_LIVING, Aspects.ASPECT_GROWTH, Aspects.ASPECT_STRUCTURE, Aspects.ASPECT_WATER},
				new int[]{5, 5, 5, 1},
				nullStack);
		degradationStore.registerDegradation(
				new ItemStack(Block.plantYellow),
				new String[]{Aspects.ASPECT_LIVING, Aspects.ASPECT_GROWTH, Aspects.ASPECT_WATER},
				new int[]{2, 2, 1},
				nullStack);
		degradationStore.registerDegradation(
				new ItemStack(Block.plantRed),
				new String[]{Aspects.ASPECT_LIVING, Aspects.ASPECT_GROWTH, Aspects.ASPECT_WATER},
				new int[]{2, 2, 1},
				nullStack);
		degradationStore.registerDegradation(
				new ItemStack(Block.mushroomCapBrown),
				new String[]{Aspects.ASPECT_LIVING, Aspects.ASPECT_STRUCTURE},
				new int[]{2, 5},
				nullStack);
		degradationStore.registerDegradation(
				new ItemStack(Block.deadBush),
				new String[]{Aspects.ASPECT_STRUCTURE},
				new int[]{5},
				nullStack);
		degradationStore.registerDegradation(
				new ItemStack(Block.mushroomCapRed),
				new String[]{Aspects.ASPECT_LIVING, Aspects.ASPECT_STRUCTURE},
				new int[]{2, 5},
				nullStack);
		degradationStore.registerDegradation(
				new ItemStack(Block.mushroomBrown),
				new String[]{Aspects.ASPECT_LIVING, Aspects.ASPECT_STRUCTURE, Aspects.ASPECT_GROWTH},
				new int[]{2, 5, 5},
				nullStack);
		degradationStore.registerDegradation(
				new ItemStack(Block.mushroomRed),
				new String[]{Aspects.ASPECT_LIVING, Aspects.ASPECT_STRUCTURE, Aspects.ASPECT_GROWTH},
				new int[]{2, 5, 5},
				nullStack);
		degradationStore.registerDegradation(
				new ItemStack(Block.reed),
				new String[]{Aspects.ASPECT_LIVING, Aspects.ASPECT_STRUCTURE, Aspects.ASPECT_WATER},
				new int[]{3, 5, 2},
				nullStack);
		degradationStore.registerDegradation(
				new ItemStack(Block.cactus),
				new String[]{Aspects.ASPECT_LIVING, Aspects.ASPECT_STRUCTURE, Aspects.ASPECT_WATER},
				new int[]{3, 5, 5},
				nullStack);
		
		/*
		 * Stone
		 */
		degradationStore.registerDegradation(
				new ItemStack(Block.stone),
				new String[]{Aspects.ASPECT_STRUCTURE},
				new int[]{5},
				new ItemStack(Block.cobblestone));
		degradationStore.registerDegradation(
				new ItemStack(Block.cobblestoneMossy),
				new String[]{Aspects.ASPECT_LIVING},
				new int[]{5},
				new ItemStack(Block.cobblestone));
		degradationStore.registerDegradation(
				new ItemStack(Block.cobblestone),
				new String[]{Aspects.ASPECT_STRUCTURE},
				new int[]{5},
				new ItemStack(Block.gravel));
		degradationStore.registerDegradation(
				new ItemStack(Block.gravel),
				new String[]{Aspects.ASPECT_STRUCTURE},
				new int[]{5},
				new ItemStack(Block.sand));
		degradationStore.registerDegradation(
				new ItemStack(Block.silverfish, 0, 0),
				new String[]{Aspects.ASPECT_LIVING},
				new int[]{15},
				new ItemStack(Block.stone));
		degradationStore.registerDegradation(
				new ItemStack(Block.silverfish, 0, 1),
				new String[]{Aspects.ASPECT_LIVING},
				new int[]{15},
				new ItemStack(Block.cobblestone));
		degradationStore.registerDegradation(
				new ItemStack(Block.silverfish, 0, 2),
				new String[]{Aspects.ASPECT_LIVING},
				new int[]{15},
				new ItemStack(Block.stoneBrick));
		
		/*
		 * Ores/Metals
		 */

		degradationStore.registerDegradation(
				new ItemStack(Block.oreCoal, 0, 0),
				new String[]{Aspects.ASPECT_HEAT},
				new int[]{15},
				new ItemStack(Block.stone));
		degradationStore.registerDegradation(
				new ItemStack(Block.oreGold, 0, 0),
				new String[]{Aspects.ASPECT_METAL, Aspects.ASPECT_VALUE},
				new int[]{15, 5},
				new ItemStack(Block.stone));
		degradationStore.registerDegradation(
				new ItemStack(Block.oreIron, 0, 0),
				new String[]{Aspects.ASPECT_METAL},
				new int[]{80},
				new ItemStack(Block.stone));
		degradationStore.registerDegradation(
				new ItemStack(Block.oreDiamond, 0, 0),
				new String[]{Aspects.ASPECT_CRYSTAL, Aspects.ASPECT_VALUE},
				new int[]{500, 500},
				new ItemStack(Block.stone));
		degradationStore.registerDegradation(
				new ItemStack(Block.oreEmerald, 0, 0),
				new String[]{Aspects.ASPECT_CRYSTAL, Aspects.ASPECT_VALUE},
				new int[]{500, 400},
				new ItemStack(Block.stone));
		degradationStore.registerDegradation(
				new ItemStack(Block.oreRedstone, 0, 0),
				new String[]{Aspects.ASPECT_CRYSTAL, Aspects.ASPECT_VALUE, Aspects.ASPECT_CRAFTING},
				new int[]{5, 5, 10},
				new ItemStack(Block.stone));
		degradationStore.registerDegradation(
				new ItemStack(Block.oreRedstoneGlowing, 0, 0),
				new String[]{Aspects.ASPECT_CRYSTAL, Aspects.ASPECT_VALUE, Aspects.ASPECT_CRAFTING},
				new int[]{5, 5, 10},
				new ItemStack(Block.stone));
		degradationStore.registerDegradation(
				new ItemStack(Block.oreLapis, 0, 0),
				new String[]{Aspects.ASPECT_EARTH, Aspects.ASPECT_VALUE},
				new int[]{5, 3},
				new ItemStack(Block.stone));
		
		degradationStore.registerDegradation(
				new ItemStack(Block.coalBlock, 0, 0),
				new String[]{Aspects.ASPECT_HEAT, Aspects.ASPECT_STRUCTURE},
				new int[]{15*8, 5},
				new ItemStack(Block.oreCoal));
		degradationStore.registerDegradation(
				new ItemStack(Block.blockGold, 0, 0),
				new String[]{Aspects.ASPECT_METAL, Aspects.ASPECT_VALUE, Aspects.ASPECT_STRUCTURE},
				new int[]{15*8, 5*8, 5},
				new ItemStack(Block.oreGold));
		degradationStore.registerDegradation(
				new ItemStack(Block.blockIron, 0, 0),
				new String[]{Aspects.ASPECT_METAL, Aspects.ASPECT_STRUCTURE},
				new int[]{80*8, 5},
				new ItemStack(Block.oreIron));
		degradationStore.registerDegradation(
				new ItemStack(Block.blockDiamond, 0, 0),
				new String[]{Aspects.ASPECT_CRYSTAL, Aspects.ASPECT_VALUE, Aspects.ASPECT_STRUCTURE},
				new int[]{500*8, 500*8, 5},
				new ItemStack(Block.oreDiamond));
		degradationStore.registerDegradation(
				new ItemStack(Block.blockEmerald, 0, 0),
				new String[]{Aspects.ASPECT_CRYSTAL, Aspects.ASPECT_VALUE, Aspects.ASPECT_STRUCTURE},
				new int[]{500*8, 400*8, 5},
				new ItemStack(Block.oreEmerald));
		degradationStore.registerDegradation(
				new ItemStack(Block.blockRedstone, 0, 0),
				new String[]{Aspects.ASPECT_CRYSTAL, Aspects.ASPECT_VALUE, Aspects.ASPECT_CRAFTING, Aspects.ASPECT_STRUCTURE},
				new int[]{5*2, 5*2, 10, 5},
				new ItemStack(Block.oreRedstone));
		degradationStore.registerDegradation(
				new ItemStack(Block.blockLapis, 0, 0),
				new String[]{Aspects.ASPECT_EARTH, Aspects.ASPECT_VALUE, Aspects.ASPECT_STRUCTURE},
				new int[]{5*2, 3*2, 5},
				new ItemStack(Block.oreLapis));

		degradationStore.registerDegradation(
				new ItemStack(ChaosCrystalMain.blockBase, 0, 0),
				new String[]{Aspects.ASPECT_CRYSTAL, Aspects.ASPECT_VALUE, Aspects.ASPECT_HEAT},
				new int[]{5, 2, 4},
				new ItemStack(ChaosCrystalMain.blockBase, 0, 1));
		degradationStore.registerDegradation(
				new ItemStack(ChaosCrystalMain.blockBase, 0, 1),
				new String[]{Aspects.ASPECT_CRYSTAL, Aspects.ASPECT_VALUE, Aspects.ASPECT_STRUCTURE},
				new int[]{15, 5, 5},
				new ItemStack(Block.glass, 0, 0));
		degradationStore.registerDegradation(
				new ItemStack(ChaosCrystalMain.blockBase, 0, 2),
				new String[]{Aspects.ASPECT_CRYSTAL, Aspects.ASPECT_VALUE, Aspects.ASPECT_STRUCTURE, Aspects.ASPECT_LIGHT},
				new int[]{15, 5, 5, 150},
				new ItemStack(Block.glass, 0, 0));
		

		degradationStore.registerDegradation(
				new ItemStack(Block.glowStone),
				new String[]{Aspects.ASPECT_CRYSTAL, Aspects.ASPECT_VALUE, Aspects.ASPECT_STRUCTURE, Aspects.ASPECT_LIGHT},
				new int[]{15, 5, 5, 20},
				new ItemStack(Block.glass, 0, 0));
		
		degradationStore.registerDegradation(
				new ItemStack(Block.web),
				new String[]{Aspects.ASPECT_STRUCTURE},
				new int[]{5},
				new ItemStack(0, 0, 0));
		
		
		
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		degradationStore.autoRegisterDegradation(new ItemStack(Block.stoneBrick));
		degradationStore.autoRegisterDegradation(new ItemStack(Block.sandStone, 1, 0));
		degradationStore.autoRegisterDegradation(new ItemStack(Block.sandStone, 1, 1));
		degradationStore.autoRegisterDegradation(new ItemStack(Block.sandStone, 1, 2));
		degradationStore.autoRegisterDegradation(new ItemStack(Block.dispenser, 1, 32767));
		degradationStore.autoRegisterDegradation(new ItemStack(Block.music, 1, 32767));
		degradationStore.autoRegisterDegradation(new ItemStack(Block.rail, 1, 32767));
		degradationStore.autoRegisterDegradation(new ItemStack(Block.railDetector, 1, 32767));
		degradationStore.autoRegisterDegradation(new ItemStack(Block.railPowered, 1, 32767));
		degradationStore.autoRegisterDegradation(new ItemStack(Block.railActivator, 1, 32767));
		degradationStore.autoRegisterDegradation(new ItemStack(Block.pistonBase, 1, 32767));
		degradationStore.autoRegisterDegradation(new ItemStack(Block.pistonStickyBase, 1, 32767));
		degradationStore.autoRegisterDegradation(new ItemStack(Block.furnaceIdle, 1, 32767));
		degradationStore.autoRegisterDegradation(new ItemStack(Block.stoneButton, 1, 32767));
		degradationStore.autoRegisterDegradation(new ItemStack(Block.woodenButton, 1, 32767));
		degradationStore.autoRegisterDegradation(new ItemStack(Block.ladder, 1, 32767));
		degradationStore.autoRegisterDegradation(new ItemStack(Item.doorWood));
		degradationStore.autoRegisterDegradation(new ItemStack(Item.doorIron));
		degradationStore.autoRegisterDegradation(new ItemStack(Item.pickaxeDiamond));
		
		if(cfg_debugOutput) {
			degradationStore.debugOutput();
		}
	}
}
