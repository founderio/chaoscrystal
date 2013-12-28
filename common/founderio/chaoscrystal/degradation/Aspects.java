package founderio.chaoscrystal.degradation;

public final class Aspects {

	private Aspects() {
		// Util class
	}

	public static final String ASPECT_GROWTH = "growth";
	public static final String ASPECT_EARTH = "earth";
	public static final String ASPECT_WATER = "water";
	public static final String ASPECT_LIGHT = "light";
	public static final String ASPECT_LIVING = "living";
	public static final String ASPECT_HEAT = "heat";
	public static final String ASPECT_CRAFTING = "crafting";
	public static final String ASPECT_STRUCTURE = "structure";
	public static final String ASPECT_WOOD = "wood";
	public static final String ASPECT_METAL = "metal";
	public static final String ASPECT_VALUE = "value";
	public static final String ASPECT_CRYSTAL = "crystal";

	public static final String[] ASPECTS = new String[] { ASPECT_GROWTH,
			ASPECT_EARTH, ASPECT_WATER, ASPECT_LIGHT, ASPECT_LIVING,
			ASPECT_HEAT, ASPECT_CRAFTING, ASPECT_STRUCTURE, ASPECT_WOOD,
			ASPECT_METAL, ASPECT_VALUE, ASPECT_CRYSTAL };

	public static boolean isAspect(String aspect) {
		if(aspect == null) {
			return false;
		}
		for (String asp : ASPECTS) {
			if (asp.equals(aspect)) {
				return true;
			}
		}
		return false;
	}

	public static int getAspectDisplayId(String aspect) {
		if(aspect == null) {
			return -1;
		}
		for (int i = 0; i < ASPECTS.length; i++) {
			if (ASPECTS[i].equals(aspect)) {
				return i;
			}
		}
		return -1;
	}
}
