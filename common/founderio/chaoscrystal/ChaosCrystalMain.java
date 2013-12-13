/**
 * 
 */
package founderio.chaoscrystal;

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
import founderio.chaoscrystal.entities.EntityChaosCrystal;
import founderio.chaoscrystal.items.ItemChaosCrystal;

/**
 * @author Oliver
 *
 */
@Mod(modid = Constants.MOD_ID, name = Constants.MOD_NAME, version = Constants.MOD_VERSION)
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class ChaosCrystalMain {
	@Instance(Constants.MOD_ID)
	public static ChaosCrystalMain instance;
	
	@SidedProxy(clientSide = "founderio.chaoscrystal.ClientProxy", serverSide = "founderio.chaoscrystal.CommonProxy")
	public static CommonProxy proxy;
	
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
		
		GameRegistry.addRecipe(new ItemStack(itemChaosCrystal, 1), " D ", "D D", " D ", 'D', Item.diamond);
	}
}
