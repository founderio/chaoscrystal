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
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import founderio.chaoscrystal.blocks.BlockBase;
import founderio.chaoscrystal.degradation.Aspects;
import founderio.chaoscrystal.degradation.DegradationStore;
import founderio.chaoscrystal.entities.EntityChaosCrystal;
import founderio.chaoscrystal.entities.EntityFocusBorder;
import founderio.chaoscrystal.entities.EntityFocusTransfer;
import founderio.chaoscrystal.items.ItemBlockBase;
import founderio.chaoscrystal.items.ItemChaosCrystal;
import founderio.chaoscrystal.items.ItemFocus;
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
	
	public static BlockBase blockBase;
	
	public static BiomeGenCrystal biomeCrystal;
	
	private Configuration config;
	
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
		
		boolean forceBiome = config.get("Settings", "force_biomes", false).getBoolean(false);
		
		//TODO: Get cfgs for ranges etc. of foci, crystals
		
		
		itemChaosCrystal = new ItemChaosCrystal(getItemId(Constants.ID_ITEM_CHAOSCRYSTAL, 18200));
		itemChaosCrystal.setUnlocalizedName(Constants.ID_ITEM_CHAOSCRYSTAL);
		itemChaosCrystal.setMaxStackSize(1);
		
		itemFocus = new ItemFocus(getItemId(Constants.ID_ITEM_FOCUS, 18201));
		itemFocus.setUnlocalizedName(Constants.ID_ITEM_FOCUS);
		
		blockBase = new BlockBase(getBlockId(Constants.ID_BLOCK_BASE, 230), Material.glass);
		blockBase.setUnlocalizedName(Constants.ID_BLOCK_BASE);
		blockBase.setCreativeTab(CreativeTabs.tabBlock);
		blockBase.setHardness(4);
		blockBase.setLightValue(0.2f);
		blockBase.setResistance(0);
		blockBase.setStepSound(Block.soundGlassFootstep);
		
		biomeCrystal = new BiomeGenCrystal(getBiomeId(Constants.ID_BIOME_CRYSTAL, 68));
		
		config.save();
		GameRegistry.registerItem(itemChaosCrystal, Constants.ID_ITEM_CHAOSCRYSTAL, Constants.MOD_ID);
		GameRegistry.registerItem(itemFocus, Constants.ID_ITEM_FOCUS, Constants.MOD_ID);
		GameRegistry.registerBlock(blockBase, ItemBlockBase.class, Constants.ID_BLOCK_BASE, Constants.MOD_ID);
		
		if(forceBiome) {
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
//		LanguageRegistry langReg = LanguageRegistry.instance();
//		langReg.
		
		EntityRegistry.registerModEntity(EntityChaosCrystal.class, Constants.NAME_ENTITY_CHAOSCRYSTAL, 0, this, 128, 1, false);
		EntityRegistry.registerModEntity(EntityFocusTransfer.class, Constants.NAME_ENTITY_FOCUS_TRANSFER, 1, this, 128, 1, false);
		EntityRegistry.registerModEntity(EntityFocusBorder.class, Constants.NAME_ENTITY_FOCUS_BORDER, 2, this, 128, 1, false);
		proxy.registerRenderStuff();
		
		MinecraftForge.EVENT_BUS.register(new OverlayAspectSelector());
		
		GameRegistry.registerWorldGenerator(new GenCrystalPillars());
		GameRegistry.registerWorldGenerator(new GenCrystalFloats());
		
		GameRegistry.addRecipe(new ItemStack(itemChaosCrystal, 1), " D ", "D D", " D ", 'D', Item.diamond);
		
		degradationStore = new DegradationStore();
		
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
				new ItemStack(Block.tilledField, 0, 1));
		degradationStore.registerDegradation(
				new ItemStack(Block.dirt, 0, 1),
				new String[]{Aspects.ASPECT_STRUCTURE, Aspects.ASPECT_LIVING},
				new int[]{5, 2},
				new ItemStack(Block.sand));
		
		degradationStore.registerDegradation(
				new ItemStack(Block.sand),
				new String[]{Aspects.ASPECT_EARTH},
				new int[]{5},
				new ItemStack(0, 0, 0));
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
		for(int meta = 0; meta < 16; meta++) {//meh, just catch them all.
			degradationStore.registerDegradation(
					new ItemStack(Block.waterStill, 0, meta),
					new String[]{},
					new int[]{},
					new ItemStack(0, 0, 0));
			degradationStore.registerDegradation(
					new ItemStack(Block.waterMoving, 0, meta),
					new String[]{},
					new int[]{},
					new ItemStack(0, 0, 0));
		}
		degradationStore.registerDegradation(
				new ItemStack(Block.glass),
				new String[]{Aspects.ASPECT_HEAT, Aspects.ASPECT_STRUCTURE},
				new int[]{5, 2},
				new ItemStack(Block.sand, 0, 0));
		
		//TODO: All-Meta Match
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
					new ItemStack(0, 0, 0));
			degradationStore.registerDegradation(
					new ItemStack(Block.vine, 0, meta),
					new String[]{Aspects.ASPECT_LIVING, Aspects.ASPECT_GROWTH},
					new int[]{3, 3},
					new ItemStack(0, 0, 0));
		}
		for(int meta = 0; meta < 4; meta++) {
			degradationStore.registerDegradation(
					new ItemStack(Block.tallGrass, 0, meta),
					new String[]{Aspects.ASPECT_LIVING, Aspects.ASPECT_GROWTH, Aspects.ASPECT_WATER},
					new int[]{3, 3, 2},
					new ItemStack(0, 0, 0));
		}
		degradationStore.registerDegradation(
				new ItemStack(Block.waterlily),
				new String[]{Aspects.ASPECT_LIVING, Aspects.ASPECT_GROWTH, Aspects.ASPECT_WATER},
				new int[]{3, 3, 2},
				new ItemStack(0, 0, 0));
		degradationStore.registerDegradation(
				new ItemStack(Block.melon),
				new String[]{Aspects.ASPECT_LIVING, Aspects.ASPECT_GROWTH, Aspects.ASPECT_STRUCTURE, Aspects.ASPECT_WATER},
				new int[]{5, 5, 5, 2},
				new ItemStack(0, 0, 0));
		degradationStore.registerDegradation(
				new ItemStack(Block.pumpkin),
				new String[]{Aspects.ASPECT_LIVING, Aspects.ASPECT_GROWTH, Aspects.ASPECT_STRUCTURE, Aspects.ASPECT_WATER},
				new int[]{5, 5, 5, 1},
				new ItemStack(0, 0, 0));
		degradationStore.registerDegradation(
				new ItemStack(Block.plantYellow),
				new String[]{Aspects.ASPECT_LIVING, Aspects.ASPECT_GROWTH, Aspects.ASPECT_WATER},
				new int[]{2, 2, 1},
				new ItemStack(0, 0, 0));
		degradationStore.registerDegradation(
				new ItemStack(Block.plantRed),
				new String[]{Aspects.ASPECT_LIVING, Aspects.ASPECT_GROWTH, Aspects.ASPECT_WATER},
				new int[]{2, 2, 1},
				new ItemStack(0, 0, 0));
		degradationStore.registerDegradation(
				new ItemStack(Block.mushroomCapBrown),
				new String[]{Aspects.ASPECT_LIVING, Aspects.ASPECT_STRUCTURE},
				new int[]{2, 5},
				new ItemStack(0, 0, 0));
		degradationStore.registerDegradation(
				new ItemStack(Block.mushroomCapRed),
				new String[]{Aspects.ASPECT_LIVING, Aspects.ASPECT_STRUCTURE},
				new int[]{2, 5},
				new ItemStack(0, 0, 0));
		degradationStore.registerDegradation(
				new ItemStack(Block.reed),
				new String[]{Aspects.ASPECT_LIVING, Aspects.ASPECT_STRUCTURE, Aspects.ASPECT_WATER},
				new int[]{3, 5, 2},
				new ItemStack(0, 0, 0));
		degradationStore.registerDegradation(
				new ItemStack(Block.cactus),
				new String[]{Aspects.ASPECT_LIVING, Aspects.ASPECT_STRUCTURE, Aspects.ASPECT_WATER},
				new int[]{3, 5, 5},
				new ItemStack(0, 0, 0));
		
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
				new String[]{Aspects.ASPECT_METAL, Aspects.ASPECT_VALUE},
				new int[]{15, 1},
				new ItemStack(Block.stone));
		degradationStore.registerDegradation(
				new ItemStack(Block.oreDiamond, 0, 0),
				new String[]{Aspects.ASPECT_CRYSTAL, Aspects.ASPECT_VALUE},
				new int[]{15, 15},
				new ItemStack(Block.stone));
		degradationStore.registerDegradation(
				new ItemStack(Block.oreEmerald, 0, 0),
				new String[]{Aspects.ASPECT_CRYSTAL, Aspects.ASPECT_VALUE},
				new int[]{15, 10},
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
				new String[]{Aspects.ASPECT_METAL, Aspects.ASPECT_VALUE, Aspects.ASPECT_STRUCTURE},
				new int[]{15*8, 1*8, 5},
				new ItemStack(Block.oreIron));
		degradationStore.registerDegradation(
				new ItemStack(Block.blockDiamond, 0, 0),
				new String[]{Aspects.ASPECT_CRYSTAL, Aspects.ASPECT_VALUE, Aspects.ASPECT_STRUCTURE},
				new int[]{15*8, 15*8, 5},
				new ItemStack(Block.oreDiamond));
		degradationStore.registerDegradation(
				new ItemStack(Block.blockEmerald, 0, 0),
				new String[]{Aspects.ASPECT_CRYSTAL, Aspects.ASPECT_VALUE, Aspects.ASPECT_STRUCTURE},
				new int[]{15*8, 10*8, 5},
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
		
		degradationStore.debugOutput();
		System.out.println();
		
	}
}
