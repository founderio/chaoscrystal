package founderio.chaoscrystal;

public class Constants {
	public static final String MOD_ID = "chaoscrystal";
	public static final String MOD_NAME = "Chaos Crystal";
	public static final String MOD_VERSION = "0.5.1.2";
	public static final String CHANNEL_NAME = "ChaosCrystal";

	public static final String ID_ITEM_CHAOSCRYSTAL = "chaoscrystal.chaoscrystal";
	public static final String ID_ITEM_FOCUS = "chaoscrystal.focus";
	public static final String ID_ITEM_CRYSTALGLASSES = "chaoscrystal.crystalglasses";
	public static final String ID_ITEM_MANUAL = "chaoscrystal.manual";
	public static final String ID_ITEM_LIFELESS_SHARD = "chaoscrystal.lifeless_shard";
	public static final String ID_ITEM_METERIAL = "chaoscrystal.material";
	public static final String[] METALIST_MATERIAL = new String[] {
		"machine_plate",
		"machine_panel"
	};
	
	public static final String ID_BLOCK_SHARD = "chaoscrystal.shard";
	public static final String[] METALIST_SHARD = {
		"crystalline_energy",
		"crystalline_chaos",
		"crystalline_light",
		"crystal_clear",
		"crystalline_energy_glowing",
		"crystalline_chaos_glowing",
		"crystalline_light_glowing",
		"crystal_clear_glowing"
	};
	
	public static final String ID_BLOCK_BASE = "chaoscrystal.base";
	public static final String[] METALIST_BLOCK_BASE = {
		"crystalline",
		"crystal",
		"crystalline_light",
		"crystal_clear",
		
		"crystalline_energy_cracked",
		"crystalline_chaos_cracked",
		"crystalline_light_cracked",
		"crystal_clear_cracked",
		
		"crystalline_energy_sprout",
		"crystalline_chaos_sprout",
		"crystalline_light_sprout",
		"crystal_clear_sprout"
	};

	public static final String ID_BLOCK_APPARATUS = "chaoscrystal.apparatus";
	public static final String ID_BLOCK_APPARATUS_RECONSTRUCTOR = "chaoscrystal.apparatus.reconstructor";
	public static final String ID_BLOCK_APPARATUS_INFUSER = "chaoscrystal.apparatus.creator";
	public static final String ID_BLOCK_APPARATUS_SENTRY = "chaoscrystal.apparatus.sentry";
	public static final String ID_BLOCK_APPARATUS_TICKER = "chaoscrystal.apparatus.ticker";
	public static final String ID_BLOCK_LIFELESS = "chaoscrystal.lifeless";
	
	public static final String ID_BLOCK_CRYSTAL_LIGHT = "chaoscrystal.crystal_light";

	public static final String ID_TILEENTITY_RECONSTRUCTOR = "chaoscrystal.reconstructor";
	public static final String ID_TILEENTITY_INFUSER = "chaoscrystal.infuser";
	/**
	 * Not in use anymore. (Alternative to ID_TILEENTITY_INFUSER)
	 */
	public static final String ID_TILEENTITY_CREATOR = "chaoscrystal.creator";
	public static final String ID_TILEENTITY_SENTRY = "chaoscrystal.sentry";
	public static final String ID_TILEENTITY_TICKER = "chaoscrystal.ticker";
	public static final String ID_TILEENTITY_SHARD = "chaoscrystal.shard";

	public static final String NAME_ENTITY_CHAOSCRYSTAL = "chaoscrystal.chaoscrystal";
	public static final String NAME_ENTITY_FOCUS_TRANSFER = "chaoscrystal.focus.transfer";
	public static final String NAME_ENTITY_FOCUS_BORDER = "chaoscrystal.focus.border";
	public static final String NAME_ENTITY_FOCUS_FILTER = "chaoscrystal.focus.filter";
	public static final String NAME_ENTITY_FOCUS_FILTER_TARGET = "chaoscrystal.focus.filter.target";

	public static final String NAME_BIOME_CRYSTAL = "Crystal Beach";

	public static final String GUI_FACTORY_CLASS = "founderio.chaoscrystal.client.gui.GuiFactory";
}
