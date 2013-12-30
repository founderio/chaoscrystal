package founderio.chaoscrystal.aspects;

import founderio.chaoscrystal.degradation.Aspects;
import founderio.util.ItemUtil;
import net.minecraft.item.ItemStack;

public class NodeDegradation extends Node {

	private final int[] aspects;
	private final Node parent;
	private final ItemStack target;
	private final boolean requiresSunlight;
	private final boolean requiresAirAbove;
	
	public NodeDegradation(Node parent, int[] aspects, ItemStack target, boolean requiresSunlight, boolean requiresAirAbove) {
		if(parent == null) {
			throw new RuntimeException("BUGBUGBUG!");
		}
		this.parent = parent;
		this.aspects = aspects;
		this.target = target;
		this.requiresSunlight = requiresSunlight;
		this.requiresAirAbove = requiresAirAbove;
	}

	@Override
	public int[] getAspects() {
		if(parent == null) {
			return aspects.clone();
		} else {
			return Aspects.addAspects(aspects, parent.getAspects());
		}
	}
	
	@Override
	public int[] getAspectDifference() {
		return aspects.clone();
	}

	@Override
	public Node[] getParents() {
		return new Node[] { parent };
	}

	@Override
	public boolean matchesItemStack(ItemStack is) {
		return ItemUtil.itemsMatch(target, is);
	}

	@Override
	public ItemStack getDispayItemStack() {
		return target;
	}

}
