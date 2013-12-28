package founderio.chaoscrystal.degradation;

public interface IAspectStore {
	int getAspect(String aspect);
	void setAspect(String aspect, int value);
}
