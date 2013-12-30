package founderio.chaoscrystal.degradation;

public interface IAspectStore {
	int getAspect(String aspect);
	void setAspect(String aspect, int value);
	int getSingleAspectCapacity();
	void addAspects(int[] aspectArray);
	void subtractAspects(int[] aspectArray);
	boolean canProvideAspects(int[] aspectArray);
	boolean canAcceptAspects(int[] aspectArray);
}
