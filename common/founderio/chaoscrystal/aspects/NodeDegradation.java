package founderio.chaoscrystal.aspects;

import net.minecraft.item.ItemStack;
import founderio.util.ItemUtil;

public class NodeDegradation extends Node {

	private final int[] aspects;
	private final Node parent;
	private final ItemStack target;
	public final boolean requiresSunlight;
	public final boolean requiresAirAbove;

	public NodeDegradation(Node parent, int[] aspects, ItemStack target,
			boolean requiresSunlight, boolean requiresAirAbove) {
		if (parent == null) {
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
		if (parent == null) {
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
		ItemStack is = target.copy();
		is.stackSize = 1;
		return is;
	}

	@Override
	public ItemStack[] getDegradedFrom(ItemStack is) {
		ItemStack result = parent.getDispayItemStack().copy();
		result.stackSize = is.stackSize;
		return new ItemStack[] { result };
	}

}
