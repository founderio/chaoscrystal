package founderio.chaoscrystal.aspects;

public interface IAspectStore {
	int getAspect(Aspect aspect);

	void setAspect(Aspect aspect, int value);

	int getSingleAspectCapacity();

	void addAspects(int[] aspectArray);

	void subtractAspects(int[] aspectArray);

	boolean canProvideAspects(int[] aspectArray);

	boolean canAcceptAspects(int[] aspectArray);
}
