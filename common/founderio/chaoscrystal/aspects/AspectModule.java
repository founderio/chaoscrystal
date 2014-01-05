package founderio.chaoscrystal.aspects;


public abstract class AspectModule {

	public AspectModule() {
	}

	public abstract void registerNodes(ChaosRegistry degradationStore);
}
