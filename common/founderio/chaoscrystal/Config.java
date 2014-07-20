package founderio.chaoscrystal;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class Config {

	public static Configuration config;

	public static boolean cfgDebugOutput = false;
	public static boolean cfgSneakToShowAspects = true;
	public static boolean cfgDebugGlasses = false;

	public static boolean generateCrystalSprouts = true;
	public static boolean generateCrystalSproutsOverworldOnly = false;
	public static int spikeMaxHeight = 50;
	public static int spikeMaxRadius = 5;
	public static boolean enableInstaGrowth = false;
	public static boolean enableClickTick = false;
	
	public static boolean crackCrystalsOnCollition = true;
	public static boolean crackCrystalsOnHarvest = true;
	public static boolean crackCrystalsOnExplosion = true;
	public static boolean crackCrystalsLessLikelyWithSilkTouch = true;
	public static boolean crackCrystalsLessLikelyWithFeatherFalling = true;
	
	public static int cfgCrystalAspectStorage = 1000000;
	public static int cfgHitsPerTick = 1;
	public static int cfgMaxTriesPerTick = 80;
	public static int cfgCrystalRange = 10;
	public static int cfgCrystalTickInterval = 1;
	public static int cfgFocusRange = 20;
	public static int cfgFocusTickInterval = 60;

	private static void loadConfig() {

		generateCrystalSprouts = config.getBoolean("generate_crystal_sprouts", "worldgen", true, "Enable generation of crystal sprouts (growing crystals) in the world and other dimensions.");
		generateCrystalSproutsOverworldOnly = config.getBoolean("generate_crystal_sprouts_overworld_only", "worldgen", false, "If enabled, ONLY generate growing crystals in the overworld.");
		spikeMaxHeight = config.getInt("spike_max_height", "worldgen", 50, 2, 150, "Maximum height of a spike of the generated crystal sprouts.");
		spikeMaxRadius = config.getInt("spike_max_radius", "worldgen", 5, 2, 150, "Maximum radius (width/2) of a spike of the generated crystal sprouts.");
		enableInstaGrowth = config.getBoolean("enable_insta_growth", "worldgen", false, "Will grow crystals to their maximum size in one tick (even in pregenerated chunks). WARNING! DEBUG ONLY! This will probably grind your game to a halt with bigger crystal sizes!");
		enableClickTick = config.getBoolean("enable_click_tick", "worldgen", false, "Will issue a tick on the crystal sprouts when they are hit by a player.");
		
		crackCrystalsOnCollition = config.getBoolean("crack_crystals_on_collidion", "physics", true, "Crack / Destroy surrounding crystals when an entity is colliding too fast.");
		crackCrystalsOnHarvest = config.getBoolean("crack_crystals_on_harvest", "physics", true, "Crack / Destroy surrounding crystals when mining a crystal block.");
		crackCrystalsOnExplosion = config.getBoolean("crack_crystals_on_explosion", "physics", true, "Crack / Destroy surrounding crystals when a crystal block is destroyed by an explosion.");
		crackCrystalsLessLikelyWithSilkTouch = config.getBoolean("crack_crystals_less_likely_with_silk_touch", "physics", true, "Crack / Destroy surrounding crystals when mining is less likely when using silk touch.");
		crackCrystalsLessLikelyWithFeatherFalling = config.getBoolean("crack_crystals_less_likely_with_feather_falling", "physics", true, "Crack / Destroy surrounding crystals when colliding with an entity is less likely when the boots have feather falling.");
		
		cfgDebugOutput = config.getBoolean("debug_output", Configuration.CATEGORY_GENERAL, false, "Enable aspect registration debug output.");
		cfgSneakToShowAspects = config.getBoolean("sneak_to_show_aspects", Configuration.CATEGORY_GENERAL, true, "Require sneaking to display aspects in the crystal glasses.");
		cfgDebugGlasses = config.getBoolean("debug_glasses", Configuration.CATEGORY_GENERAL, false, "Enable the debug mode on crystal glasses. (Displays additional info, mostly useless for regular gameplay)");
				
		cfgCrystalAspectStorage = config.getInt("crystal_aspect", Configuration.CATEGORY_GENERAL, 1000000, 10 , 1000000000, "Configure the maximum capacity of a chaos crystal. This is the maximum amount of ALL aspects together.");
		
		cfgHitsPerTick = config.getInt("hits_per_tick", Configuration.CATEGORY_GENERAL, 1, 1, 265, "Maximum number of blocks or entities extracted per tick.");
		cfgMaxTriesPerTick = config.getInt("max_tries_per_tick", Configuration.CATEGORY_GENERAL, 80, 1, 256, "Maximum number of tries to find an extraction target per tick.");
		
		cfgCrystalRange = config.getInt("crystalrange",  Configuration.CATEGORY_GENERAL, 10, 1, 256, "Maximum range of the chaos crystal in blocks.");
		cfgCrystalTickInterval = config.getInt("crystal_tick_interval", Configuration.CATEGORY_GENERAL, 1, 1, 256, "Interval (in ticks) the chaos crystal updates. (That means direct extraction or infusion, not foci ticks.)");
		
		cfgFocusRange = config.getInt("focus_range", Configuration.CATEGORY_GENERAL, 20, 1, 265, "Maximum range of the foci in blocks.");
		cfgFocusTickInterval = config.getInt("focus_tick_interval", Configuration.CATEGORY_GENERAL, 60, 1, 265, "Interval (in ticks) the foci update. (That means e.g. crystal<>crystal or crystal<>machine transfer)");

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
		{
			loadConfig();
		}
	}

}
