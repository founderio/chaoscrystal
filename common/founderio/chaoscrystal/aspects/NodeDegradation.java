package founderio.chaoscrystal.aspects;

import founderio.chaoscrystal.degradation.Aspects;
import founderio.util.ItemUtil;
import net.minecraft.item.ItemStack;

public class NodeDegradation extends Node {

	private int[] aspects;
	private Node parent;
	private ItemStack target;
	private boolean requiresSunlight;
	private boolean requiresAirAbove;
	
	public NodeDegradation(Node parent, int[] aspects, ItemStack target, boolean requiresSunlight, boolean requiresAirAbove) {
		this.parent = parent;
		this.aspects = aspects;
		this.target = target;
		this.requiresSunlight = requiresSunlight;
		this.requiresAirAbove = requiresAirAbove;
	}

	@Override
	public int[] getAspects() {
		return Aspects.addAspects(aspects, parent.getAspects());
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
