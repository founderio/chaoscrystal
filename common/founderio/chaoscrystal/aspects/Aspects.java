package founderio.chaoscrystal.aspects;

import java.util.Arrays;
import java.util.List;

import net.minecraft.util.ResourceLocation;
import founderio.chaoscrystal.Constants;

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
	
	public static final ResourceLocation[] RESOURCE_LOCATIONS = new ResourceLocation[ASPECTS.length];

	static {
		for (int a = 0; a < ASPECTS.length; a++) {
			RESOURCE_LOCATIONS[a] = new ResourceLocation(Constants.MOD_ID + ":" + "textures/hud/aspect_" + ASPECTS[a] + ".png");
		}
	}
	
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

	public static String[] getAspectNames(int[] aspectArray) {
		String[] aspectNames = new String[ASPECTS.length];
		int idx = 0;
		for (int a = 0; a < ASPECTS.length; a++) {
			if (aspectArray[a] > 0) {
				aspectNames[idx] = ASPECTS[a];
				idx++;
			}
		}
		return Arrays.copyOfRange(aspectNames, 0, idx);
	}
	
	public static boolean allAspectsContained(List<String> whitelist, int[] aspects) {
		return whitelist.containsAll(Arrays.asList(Aspects.getAspectNames(aspects)));
	}
	
	public static boolean isFilterMatched(List<String> filter, int[] aspects) {
		return filter.isEmpty() || allAspectsContained(filter, aspects);
	}

	private Aspects() {
		// Util class
	}
}
