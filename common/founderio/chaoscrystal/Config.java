package founderio.chaoscrystal;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class Config {

	public static Configuration config;

	public static boolean cfgForceBiome = false;
	public static boolean cfgDebugOutput = false;
	public static boolean cfgSneakToShowAspects = true;
	public static boolean cfgDebugGlasses = false;

	public static int cfgBiomeId = 68;
	public static int cfgCrystalAspectStorage = 1000000;
	public static int cfgHitsPerTick = 1;
	public static int cfgMaxTriesPerTick = 80;
	public static int cfgCrystalRange = 10;
	public static int cfgCrystalTickInterval = 1;
	public static int cfgFocusRange = 20;
	public static int cfgFocusTickInterval = 60;

	private static void loadConfig() {

		cfgDebugOutput = config.getBoolean("debug_output", Configuration.CATEGORY_GENERAL, false, "");
		cfgSneakToShowAspects = config.getBoolean("sneak_to_show_aspects", Configuration.CATEGORY_GENERAL, true, "");
		cfgDebugGlasses = config.getBoolean("debug_glasses", Configuration.CATEGORY_GENERAL, false, "Enabled the debug mode on crystal glasses. (Displays additional info.");
				
		cfgCrystalAspectStorage = config.getInt("crystal_aspect", Configuration.CATEGORY_GENERAL, 1000000, 10 , 1000000000, "");
		cfgHitsPerTick = config.getInt("hits_per_tick", Configuration.CATEGORY_GENERAL, 1, 1, 265, "");
		cfgMaxTriesPerTick = config.getInt("max_tries_per_tick", Configuration.CATEGORY_GENERAL, 80, 1, 256, "");
		cfgCrystalRange = config.getInt("crystalrange",  Configuration.CATEGORY_GENERAL, 10, 1, 256, "");
		cfgCrystalTickInterval = config.getInt("crystal_tick_interval", Configuration.CATEGORY_GENERAL, 1, 1, 256, "");
		cfgFocusRange = config.getInt("focus_range", Configuration.CATEGORY_GENERAL, 20, 1, 265, "");
		cfgFocusTickInterval = config.getInt("focus_tick_interval", Configuration.CATEGORY_GENERAL, 60, 1, 265, "");

		if (config.hasChanged()) {
			config.save();
		}
	}

	public static void init(File configFile) {

		if (config == null) {
			config = new Configuration(configFile);
			loadConfig();

		}
	}

	@SubscribeEvent
	public void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent Event) {
		if (Event.modID.equalsIgnoreCase(Constants.MOD_ID))
			;
		{
			loadConfig();
		}
	}

}
