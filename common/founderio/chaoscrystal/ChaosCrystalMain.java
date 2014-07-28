package founderio.chaoscrystal;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.chaoscrystal.aspects.AspectModule;
import founderio.chaoscrystal.aspects.ChaosRegistry;
import founderio.chaoscrystal.blocks.BlockApparatus;
import founderio.chaoscrystal.blocks.BlockBase;
import founderio.chaoscrystal.blocks.BlockLifeless;
import founderio.chaoscrystal.blocks.BlockSproutingCrystal;
import founderio.chaoscrystal.blocks.TileEntityInfuser;
import founderio.chaoscrystal.blocks.TileEntityReconstructor;
import founderio.chaoscrystal.blocks.TileEntitySentry;
import founderio.chaoscrystal.blocks.TileEntityTicker;
import founderio.chaoscrystal.debug.ChaosCrystalAspectUtil;
import founderio.chaoscrystal.entities.EntityChaosCrystal;
import founderio.chaoscrystal.entities.EntityFocusBorder;
import founderio.chaoscrystal.entities.EntityFocusFilter;
import founderio.chaoscrystal.entities.EntityFocusFilterTarget;
import founderio.chaoscrystal.entities.EntityFocusTransfer;
import founderio.chaoscrystal.items.ItemBlockApparatus;
import founderio.chaoscrystal.items.ItemBlockBase;
import founderio.chaoscrystal.items.ItemBlockSproutingCrystal;
import founderio.chaoscrystal.items.ItemChaosCrystal;
import founderio.chaoscrystal.items.ItemCrystalGlasses;
import founderio.chaoscrystal.items.ItemFocus;
import founderio.chaoscrystal.items.ItemLifelessShard;
import founderio.chaoscrystal.items.ItemManual;
import founderio.chaoscrystal.items.ItemShard;
import founderio.chaoscrystal.network.CCPModeItemChanged;
import founderio.chaoscrystal.network.CCPParticle;
import founderio.chaoscrystal.worldgen.GenCrystalSprouts;

/**
 * Chaos Crystal Mod class
 * 
 * @author founderio
 * 
 */
@Mod(modid = Constants.MOD_ID, name = Constants.MOD_NAME, version = Constants.MOD_VERSION, guiFactory = Constants.GUI_FACTORY_CLASS)
public class ChaosCrystalMain {
	@Instance(Constants.MOD_ID)
	public static ChaosCrystalMain instance;

	@SidedProxy(clientSide = "founderio.chaoscrystal.ClientProxy", serverSide = "founderio.chaoscrystal.CommonProxy")
	public static CommonProxy proxy;
	public static SimpleNetworkWrapper network;

	public static ChaosRegistry chaosRegistry;

	public static final Random rand = new Random();

	public static Item itemChaosCrystal;
	public static Item itemFocus;
	public static Item itemCrystalGlasses;
	public static Item itemManual;
	public static Item itemLifelessShard;
	public static Item itemShard;

	public static BlockBase blockBase;
	public static BlockSproutingCrystal blockSproutingCrystal;
	public static BlockApparatus blockReconstructor;
	public static BlockApparatus blockCreator;
	public static BlockApparatus blockSentry;
	public static BlockLifeless blockLifeless;

	//	public static BiomeGenCrystal biomeCrystal;

	public static CreativeTabs creativeTab;

	public static GenCrystalSprouts genCrystalSprouts;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(new Config());

		Config.init(event.getSuggestedConfigurationFile());
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

		itemShard = new ItemShard();
		itemShard.setUnlocalizedName(Constants.ID_ITEM_SHARD);
		itemShard.setCreativeTab(creativeTab);

		blockBase = new BlockBase();
		blockBase.setBlockName(Constants.ID_BLOCK_BASE);
		blockBase.setCreativeTab(creativeTab);

		blockSproutingCrystal = new BlockSproutingCrystal();
		blockSproutingCrystal.setBlockName(Constants.ID_BLOCK_SPROUTING_CRYSTAL);
		blockSproutingCrystal.setCreativeTab(creativeTab);

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

		blockSentry = new BlockApparatus(3);
		blockSentry.setBlockName(Constants.ID_BLOCK_APPARATUS_TICKER);
		blockSentry.setCreativeTab(creativeTab);

		GameRegistry.registerItem(itemChaosCrystal, Constants.ID_ITEM_CHAOSCRYSTAL, Constants.MOD_ID);
		GameRegistry.registerItem(itemFocus, Constants.ID_ITEM_FOCUS, Constants.MOD_ID);
		GameRegistry.registerItem(itemCrystalGlasses, Constants.ID_ITEM_CRYSTALGLASSES, Constants.MOD_ID);
		GameRegistry.registerItem(itemManual, Constants.ID_ITEM_MANUAL, Constants.MOD_ID);
		GameRegistry.registerItem(itemLifelessShard, Constants.ID_ITEM_LIFELESS_SHARD, Constants.MOD_ID);
		GameRegistry.registerItem(itemShard, Constants.ID_ITEM_SHARD, Constants.MOD_ID);

		GameRegistry.registerBlock(blockBase, ItemBlockBase.class, Constants.ID_BLOCK_BASE);
		GameRegistry.registerBlock(blockSproutingCrystal, ItemBlockSproutingCrystal.class, Constants.ID_BLOCK_SPROUTING_CRYSTAL);
		GameRegistry.registerBlock(blockLifeless, Constants.ID_BLOCK_LIFELESS);
		GameRegistry.registerBlock(blockReconstructor, ItemBlockApparatus.class, Constants.ID_BLOCK_APPARATUS_RECONSTRUCTOR);
		GameRegistry.registerBlock(blockCreator, ItemBlockApparatus.class, Constants.ID_BLOCK_APPARATUS_CREATOR);
		GameRegistry.registerBlock(blockSentry, ItemBlockApparatus.class, Constants.ID_BLOCK_APPARATUS_SENTRY);

		GameRegistry.registerTileEntityWithAlternatives(TileEntityReconstructor.class, Constants.ID_TILEENTITY_INFUSER, Constants.ID_TILEENTITY_RECONSTRUCTOR);
		GameRegistry.registerTileEntity(TileEntityInfuser.class, Constants.ID_TILEENTITY_CREATOR);
		GameRegistry.registerTileEntity(TileEntitySentry.class, Constants.ID_TILEENTITY_SENTRY);
		GameRegistry.registerTileEntity(TileEntityTicker.class, Constants.ID_TILEENTITY_TICKER);
		
		//		biomeCrystal = new BiomeGenCrystal(getBiomeId(Constants.NAME_BIOME_CRYSTAL, 68));
		
		genCrystalSprouts = new GenCrystalSprouts();
		GameRegistry.registerWorldGenerator(genCrystalSprouts, 5);

		network = NetworkRegistry.INSTANCE.newSimpleChannel(Constants.CHANNEL_NAME);
		network.registerMessage(CCPModeItemChanged.Handler.class, CCPModeItemChanged.class, 0, Side.CLIENT);
		network.registerMessage(CCPModeItemChanged.Handler.class, CCPModeItemChanged.class, 1, Side.SERVER);
		network.registerMessage(CCPParticle.Handler.class, CCPParticle.class, 2, Side.CLIENT);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {

		EntityRegistry.registerModEntity(EntityChaosCrystal.class, Constants.NAME_ENTITY_CHAOSCRYSTAL, 0, this, 128, 1, false);
		EntityRegistry.registerModEntity(EntityFocusTransfer.class, Constants.NAME_ENTITY_FOCUS_TRANSFER, 1, this, 128, 1, false);
		EntityRegistry.registerModEntity(EntityFocusBorder.class, Constants.NAME_ENTITY_FOCUS_BORDER, 2, this, 128, 1, false);
		EntityRegistry.registerModEntity(EntityFocusFilter.class, Constants.NAME_ENTITY_FOCUS_FILTER, 3, this, 128, 1, false);
		EntityRegistry.registerModEntity(EntityFocusFilterTarget.class, Constants.NAME_ENTITY_FOCUS_FILTER_TARGET, 4, this, 128, 1, false);
		proxy.registerRenderStuff();


		// GameRegistry.registerWorldGenerator(new GenCrystalPillars(), 0);
		// GameRegistry.registerWorldGenerator(new GenCrystalFloats(), 0);

		GameRegistry.addRecipe(new ItemStack(itemChaosCrystal, 1), "RDR", "RER", "RDR", 'D', Items.diamond, 'R', new ItemStack(blockBase, 1, 1), 'E', Items.ender_pearl);
		GameRegistry.addRecipe(new ItemStack(itemFocus, 1, 0), "dBd", "BEB", "dBd", 'B', new ItemStack(blockBase, 1, 0), 'E', Items.ender_pearl, 'd', new ItemStack(Items.dye, 1, 4));
		GameRegistry.addRecipe(new ItemStack(itemFocus, 1, 1), "dBd", "BEB", "dBd", 'B', new ItemStack(blockBase, 1, 0), 'E', Items.ender_pearl, 'd', new ItemStack(Items.dye, 1, 10));
		GameRegistry.addRecipe(new ItemStack(itemFocus, 1, 2), "dBd", "BEB", "dBd", 'B', new ItemStack(blockBase, 1, 0), 'E', Items.ender_pearl, 'd', new ItemStack(Items.dye, 1, 5));
		GameRegistry.addRecipe(new ItemStack(itemCrystalGlasses, 1, 0), "B B", "GBG", 'B', new ItemStack(blockBase, 1, 0), 'G', Blocks.glass_pane);

		GameRegistry.addRecipe(new ItemStack(blockReconstructor, 1), "gBg", "OOO", 'g', new ItemStack(Items.dye, 1, 8), 'B', new ItemStack(blockBase, 1, 0), 'O', Blocks.obsidian);
		GameRegistry.addRecipe(new ItemStack(blockCreator, 1), "gYg", "OOO", 'g', new ItemStack(Items.dye, 1, 8), 'Y', new ItemStack(blockBase, 1, 2), 'O', Blocks.obsidian);
		GameRegistry.addRecipe(new ItemStack(blockSentry, 1), "OBO", "gYg", "OOO", 'g', new ItemStack(Items.dye, 1, 8), 'B', new ItemStack(blockBase, 1, 0), 'Y', new ItemStack(blockBase, 1, 2), 'O', Blocks.obsidian);

		GameRegistry.addShapelessRecipe(new ItemStack(itemManual), new ItemStack(blockBase, 1, 32767), Items.map);

		GameRegistry.addRecipe(new ItemStack(blockLifeless, 1, 0), "sss", "sss", "sss", 's', itemLifelessShard);
		GameRegistry.addShapelessRecipe(new ItemStack(itemLifelessShard, 9, 0), blockLifeless);

		chaosRegistry = new ChaosRegistry();

		Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().setVersion(1).disableHtmlEscaping().create();

		InputStream vanillaIS = ChaosCrystalMain.class.getResourceAsStream("/assets/chaoscrystal/chaosregistry/vanilla.json");
		if(vanillaIS != null) {
			AspectModule vanilla = gson.fromJson(new InputStreamReader(vanillaIS, Charsets.UTF_8), AspectModule.class);
			chaosRegistry.registerAspectModule(vanilla);
		}
		

	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		if(Config.showDebugOutput) {
			chaosRegistry.debugOutput();
		}
		if(Config.showDebugUtil) {
			ChaosCrystalAspectUtil.open(chaosRegistry);
		}
	}
}
