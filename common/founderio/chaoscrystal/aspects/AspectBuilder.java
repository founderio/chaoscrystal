package founderio.chaoscrystal.aspects;

import java.util.HashMap;
import java.util.Map;


public class AspectBuilder {
	private Map<Aspect, Integer> aspects;

	public AspectBuilder() {
		aspects = new HashMap<Aspect, Integer>();
	}

	public AspectBuilder clear() {
		aspects.clear();
		return this;
	}

	public AspectBuilder addAspect(Aspect aspect, int amount) {
		setAspect(aspect, getAspect(aspect) + amount);
		return this;
	}

	public AspectBuilder setAspect(Aspect aspect, int amount) {
		aspects.put(aspect, amount);
		return this;
	}

	public int getAspect(Aspect aspect) {
		if (aspects.containsKey(aspect)) {
			return aspects.get(aspect);
		} else {
			return 0;
		}
	}

	public int[] toAspectArray() {
		int[] aspectArray = new int[Aspect.values().length];
		for (int a = 0; a < Aspect.values().length; a++) {
			aspectArray[a] = getAspect(Aspect.values()[a]);
		}
		return aspectArray;
	}

	public AspectBuilder fromAspectArray(int[] aspectArray) {
		for (int a = 0; a < Aspect.values().length; a++) {
			setAspect(Aspect.values()[a], aspectArray[a]);
		}
		return this;
	}
}
