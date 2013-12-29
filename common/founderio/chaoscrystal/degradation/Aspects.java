package founderio.chaoscrystal.degradation;

public final class Aspects {

	public static final String ASPECT_CRAFTING = "crafting";
	public static final String ASPECT_CRYSTAL = "crystal";
	public static final String ASPECT_EARTH = "earth";
	public static final String ASPECT_GROWTH = "growth";
	public static final String ASPECT_HEAT = "heat";
	public static final String ASPECT_LIGHT = "light";
	public static final String ASPECT_LIVING = "living";
	public static final String ASPECT_METAL = "metal";
	public static final String ASPECT_STRUCTURE = "structure";
	public static final String ASPECT_VALUE = "value";
	public static final String ASPECT_WATER = "water";
	public static final String ASPECT_WOOD = "wood";
	
	public static final String[] ASPECTS = new String[] { ASPECT_EARTH,
			ASPECT_WATER, ASPECT_WOOD, ASPECT_METAL, ASPECT_CRYSTAL,
			ASPECT_GROWTH, ASPECT_LIVING, ASPECT_VALUE, ASPECT_LIGHT,
			ASPECT_HEAT, ASPECT_CRAFTING, ASPECT_STRUCTURE };

	public static int getAspectIndex(String aspect) {
		if (aspect == null) {
			return -1;
		}
		for (int i = 0; i < ASPECTS.length; i++) {
			if (ASPECTS[i].equals(aspect)) {
				return i;
			}
		}
		return -1;
	}

	public static boolean isAspect(String aspect) {
		if (aspect == null) {
			return false;
		}
		for (String asp : ASPECTS) {
			if (asp.equals(aspect)) {
				return true;
			}
		}
		return false;
	}
	
	public static int[] addAspects(int[] aspectsA, int[] aspectsB) {
		int[] aspectsR = new int[ASPECTS.length];
		for (int a = 0; a < Aspects.ASPECTS.length; a++) {
			aspectsR[a] = aspectsA[a] + aspectsB[a];
		}
		return aspectsR;
	}

	private Aspects() {
		// Util class
	}
}
