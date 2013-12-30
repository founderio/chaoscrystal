package founderio.chaoscrystal.aspects;

import java.util.ArrayList;
import java.util.List;

public abstract class AspectModule {
	public final List<Node> nodes;
	
	public AspectModule() {
		this.nodes = new ArrayList<Node>();
	}
	
	protected abstract void registerNodes();
}
