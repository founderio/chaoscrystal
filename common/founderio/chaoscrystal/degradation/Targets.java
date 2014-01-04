package founderio.chaoscrystal.degradation;

public final class Targets {

	public static final String TARGET_ALL = "all";
	public static final String TARGET_ITEMS = "items";
	public static final String TARGET_BLOCKS = "blocks";
	public static final String TARGET_ENTITY = "entity";
	public static final String TARGET_ENTITY_MOBS = "entity_mobs";
	public static final String TARGET_ENTITY_HOSTILE = "entity_hostile";
	public static final String TARGET_ENTITY_FRIENDLY = "entity_friendly";
	public static final String TARGET_ENTITY_PLAYERS = "entity_players";
	public static final String TARGET_GUARD_MODE = "guard_mode";

	public static final String[] TARGETS = new String[] { TARGET_ALL,
			TARGET_ITEMS, TARGET_BLOCKS, TARGET_ENTITY, TARGET_ENTITY_MOBS, TARGET_ENTITY_HOSTILE,
			TARGET_ENTITY_FRIENDLY, TARGET_ENTITY_PLAYERS, TARGET_GUARD_MODE };

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
