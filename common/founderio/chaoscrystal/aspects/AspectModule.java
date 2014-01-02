package founderio.chaoscrystal.aspects;

import founderio.chaoscrystal.degradation.DegradationStore;

public abstract class AspectModule {

	public AspectModule(DegradationStore degradationStore) {
		registerNodes(degradationStore);
	}

	protected abstract void registerNodes(DegradationStore degradationStore);
}
