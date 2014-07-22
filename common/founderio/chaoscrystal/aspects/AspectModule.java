package founderio.chaoscrystal.aspects;

import java.util.ArrayList;
import java.util.List;

public class AspectModule {

	public AspectModule() {
		nodes = new ArrayList<Node>();
		name = toString();
	}

	private String name;
	private List<Node> nodes;

	public List<Node> getNodes() {
		return nodes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
