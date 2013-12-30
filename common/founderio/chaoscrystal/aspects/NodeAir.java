package founderio.chaoscrystal.aspects;

import founderio.chaoscrystal.degradation.Aspects;
import net.minecraft.item.ItemStack;


public class NodeAir extends Node {

	@Override
	public int[] getAspectDifference() {
		return new int[Aspects.ASPECTS.length];
	}

	@Override
	public int[] getAspects() {
		return new int[Aspects.ASPECTS.length];
	}

	@Override
	public Node[] getParents() {
		return new Node[0];
	}

	@Override
	public boolean matchesItemStack(ItemStack is) {
		return is.itemID == 0;
	}

	@Override
	public ItemStack getDispayItemStack() {
		return new ItemStack(0, 1, 0);
	}

}
