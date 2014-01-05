package founderio.chaoscrystal.aspects;

import java.util.HashMap;
import java.util.Map;


public class AspectBuilder {
	private Map<String, Integer> aspects;

	public AspectBuilder() {
		aspects = new HashMap<String, Integer>();
	}

	public AspectBuilder clear() {
		aspects.clear();
		return this;
	}

	public AspectBuilder addAspect(String aspect, int amount) {
		setAspect(aspect, getAspect(aspect) + amount);
		return this;
	}

	public AspectBuilder setAspect(String aspect, int amount) {
		if (!Aspects.isAspect(aspect)) {
			throw new RuntimeException("Wrong Aspect '" + aspect + "'");
		}
		aspects.put(aspect, amount);
		return this;
	}

	public int getAspect(String aspect) {
		if (Aspects.isAspect(aspect)) {
			if (aspects.containsKey(aspect)) {
				return aspects.get(aspect);
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}

	public int[] toAspectArray() {
		int[] aspectArray = new int[Aspects.ASPECTS.length];
		for (int a = 0; a < Aspects.ASPECTS.length; a++) {
			aspectArray[a] = getAspect(Aspects.ASPECTS[a]);
		}
		return aspectArray;
	}

	public AspectBuilder fromAspectArray(int[] aspectArray) {
		for (int a = 0; a < Aspects.ASPECTS.length; a++) {
			setAspect(Aspects.ASPECTS[a], aspectArray[a]);
		}
		return this;
	}
}
