package founderio.chaoscrystal.aspects;

import java.util.Arrays;
import java.util.List;

import net.minecraft.util.ResourceLocation;
import founderio.chaoscrystal.Constants;

public enum Aspect {
	CRAFTING("crafting"),
	CRYSTAL("crystal"),
	EARTH("earth"),
	GROWTH("growth"),
	HEAT("heat"),
	LIGHT("light"),
	LIVING("living"),
	METAL("metal"),
	STRUCTURE("structure"),
	VALUE("value"),
	WATER("water"),
	WOOD("wood");
	
	
	public final String stringRep;
	
	private Aspect(String stringRep) {
		this.stringRep = stringRep;
	}
	
	public static final ResourceLocation[] RESOURCE_LOCATIONS = new ResourceLocation[Aspect.values().length];

	static {
		for (int a = 0; a < Aspect.values().length; a++) {
			RESOURCE_LOCATIONS[a] = new ResourceLocation(Constants.MOD_ID + ":" + "textures/hud/aspect_" + Aspect.values()[a].stringRep + ".png");
		}
	}
	
	public static Aspect findByStringRep(String stringRep) {
		// For now, this works. In the future there is space for aliases here.
		Aspect found = null;
		for(Aspect aspect : values()) {
			if(aspect.stringRep.equalsIgnoreCase(stringRep)) {
				found = aspect;
				break;
			}
		}
		return found;
	}
	
	public static int[] pack(IAspectStore store) {
		return pack(store, new int[Aspect.values().length]);
	}
	
	public static int[] pack(IAspectStore store, int[] aspectShortArray) {
		//TODO: Pack the values from the store into the array
		return aspectShortArray;
	}
	
	public static void unpack(int[] aspectShortArray, IAspectStore store) {
		//TODO: Unpack the array values to the store
	}
	
	public static int getAspectIndex(String aspect) {
		return Aspect.findByStringRep(aspect).ordinal();
	}
	
	public static boolean isAspect(String aspect) {
		return Aspect.findByStringRep(aspect) != null;
	}
	
	public static int[] addAspects(int[] aspectsA, int[] aspectsB) {
		int[] aspectsR = new int[Aspect.values().length];
		for (int a = 0; a < Aspect.values().length; a++) {
			aspectsR[a] = aspectsA[a] + aspectsB[a];
		}
		return aspectsR;
	}

	public static String[] getAspectNames(int[] aspectArray) {
		String[] aspectNames = new String[Aspect.values().length];
		int idx = 0;
		for (int a = 0; a < Aspect.values().length; a++) {
			if (aspectArray[a] > 0) {
				aspectNames[idx] = Aspect.values()[a].stringRep;
				idx++;
			}
		}
		return Arrays.copyOfRange(aspectNames, 0, idx);
	}

	public static Aspect[] getAspects(int[] aspectArray) {
		Aspect[] aspects = new Aspect[Aspect.values().length];
		int idx = 0;
		for (int a = 0; a < Aspect.values().length; a++) {
			if (aspectArray[a] > 0) {
				aspects[idx] = Aspect.values()[a];
				idx++;
			}
		}
		return Arrays.copyOfRange(aspects, 0, idx);
	}
	
	@Deprecated
	public static boolean allAspectsContained(List<Aspect> whitelist, int[] aspects) {
		return whitelist.containsAll(Arrays.asList(getAspects(aspects)));
	}
	
	public static boolean isFilterMatched(List<Aspect> filter, int[] aspects) {
		return filter.isEmpty() || allAspectsContained(filter, aspects);
	}
}