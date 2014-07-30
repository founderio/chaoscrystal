package founderio.chaoscrystal.aspects;

import net.minecraft.util.ResourceLocation;
import founderio.chaoscrystal.Constants;

public final class Targets {
	//TODO: Reformat the same as with Aspects.
	public static final String TARGET_ALL = "all";
	public static final String TARGET_ITEMS = "items";
	public static final String TARGET_BLOCKS = "blocks";
	public static final String TARGET_ENTITY = "entity";
	public static final String TARGET_ENTITY_CREATURES = "entity_creatures";
	public static final String TARGET_ENTITY_MOBS = "entity_mobs";
	public static final String TARGET_ENTITY_ANIMALS = "entity_animals";
	public static final String TARGET_ENTITY_AMBIENT = "entity_ambient";
	public static final String TARGET_ENTITY_PLAYERS = "entity_players";
	public static final String TARGET_GUARD_MODE = "guard_mode";
	public static final String TARGET_GUARD_MODE_L2 = "guard_mode_l2";

	public static final String[] TARGETS = new String[] { TARGET_ALL,
		TARGET_ITEMS, TARGET_BLOCKS, TARGET_ENTITY, TARGET_ENTITY_CREATURES, TARGET_ENTITY_MOBS,
		TARGET_ENTITY_ANIMALS, TARGET_ENTITY_AMBIENT, TARGET_ENTITY_PLAYERS, TARGET_GUARD_MODE, TARGET_GUARD_MODE_L2 };

	public static final ResourceLocation[] RESOURCE_LOCATIONS = new ResourceLocation[TARGETS.length];

	static {
		for (int a = 0; a < TARGETS.length; a++) {
			RESOURCE_LOCATIONS[a] = new ResourceLocation(Constants.MOD_ID + ":" + "textures/hud/target_" + TARGETS[a] + ".png");
		}
	}

	public static int getTargetIndex(String target) {
		if (target == null) {
			return -1;
		}
		for (int i = 0; i < TARGETS.length; i++) {
			if (TARGETS[i].equals(target)) {
				return i;
			}
		}
		return -1;
	}

	public static boolean isTarget(String target) {
		if (target == null) {
			return false;
		}
		for (String asp : TARGETS) {
			if (asp.equals(target)) {
				return true;
			}
		}
		return false;
	}

	private Targets() {
		// Util class
	}
}
