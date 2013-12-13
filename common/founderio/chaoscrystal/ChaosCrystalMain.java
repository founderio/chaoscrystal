/**
 * 
 */
package founderio.chaoscrystal;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import founderio.chaoscrystal.degradation.Aspects;
import founderio.chaoscrystal.degradation.DegradationStore;
import founderio.chaoscrystal.entities.EntityChaosCrystal;
import founderio.chaoscrystal.items.ItemChaosCrystal;

/**
 * @author Oliver
 *
 */
@Mod(modid = Constants.MOD_ID, name = Constants.MOD_NAME, version = Constants.MOD_VERSION)
@NetworkMod(clientSideRequired = true, serverSideRequired = false, channels = { Constants.CHANNEL_NAME }, packetHandler = ChaosCrystalNetworkHandler.class)
public class ChaosCrystalMain {
	@Instance(Constants.MOD_ID)
	public static ChaosCrystalMain instance;
	
	@SidedProxy(clientSide = "founderio.chaoscrystal.ClientProxy", serverSide = "founderio.chaoscrystal.CommonProxy")
	public static CommonProxy proxy;
	
	public static DegradationStore degradationStore;
	
	public static Item itemChaosCrystal;
	
	private Configuration config;
	
	private int getItemId(String id, int defaultId) {
		return config.get("Items", id, defaultId).getInt();
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		//TODO: setup stuff if needed, get cfgs
		
		config.save();
		
		itemChaosCrystal = new ItemChaosCrystal(getItemId(Constants.ID_ITEM_CHAOSCRYSTAL, 18200));
		itemChaosCrystal.setUnlocalizedName(Constants.ID_ITEM_CHAOSCRYSTAL);
		//TODO: setup properties
		
		
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
//		LanguageRegistry langReg = LanguageRegistry.instance();
//		langReg.
		
		EntityRegistry.registerModEntity(EntityChaosCrystal.class, Constants.NAME_ENTITY_CHAOSCRYSTAL, 0, this, 75, 1, false);
		proxy.registerRenderStuff();
		
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
				new ItemStack(Block.dirt),
				new String[]{Aspects.ASPECT_GROWTH},
				new int[]{5},
				new ItemStack(Block.dirt, 0, 1));
		degradationStore.registerDegradation(
				new ItemStack(Block.dirt, 0, 1),
				new String[]{Aspects.ASPECT_STRUCTURE},
				new int[]{5},
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
		
		degradationStore.registerDegradation(
				new ItemStack(Block.workbench),
				new String[]{Aspects.ASPECT_CRAFTING},
				new int[]{5},
				new ItemStack(Block.planks, 0, 0));
		//TODO: All-Meta Match
		//TODO: Ore Dict Match
		
		/*
		 * Wood, Leaves
		 */
		for(int meta = 0; meta < 4; meta++) {
			degradationStore.registerDegradation(
					new ItemStack(Block.planks, 0, meta),
					new String[]{Aspects.ASPECT_WOOD},
					new int[]{1},
					new ItemStack(Block.leaves, 0, meta));
			degradationStore.registerDegradation(
					new ItemStack(Block.wood, 0, meta),
					new String[]{Aspects.ASPECT_WOOD, Aspects.ASPECT_GROWTH},
					new int[]{4, 5},
					new ItemStack(Block.planks, 0, meta));
			degradationStore.registerDegradation(
					new ItemStack(Block.leaves, 0, meta),
					new String[]{Aspects.ASPECT_LIVING},
					new int[]{5},
					new ItemStack(0, 0, 0));
		}
		
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
	}
}
