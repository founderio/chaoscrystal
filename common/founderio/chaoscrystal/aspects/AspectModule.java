package founderio.chaoscrystal.aspects;


public abstract class AspectModule {

	public AspectModule(DegradationStore degradationStore) {
		registerNodes(degradationStore);
	}

	protected abstract void registerNodes(DegradationStore degradationStore);
}
