package founderio.chaoscrystal.aspects;

import net.minecraft.item.ItemStack;
import founderio.chaoscrystal.ChaosCrystalMain;

public class NodeLifelessShard extends Node {

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
		return is != null && is.getItem() == ChaosCrystalMain.itemLifelessShard;
	}

	@Override
	public ItemStack getDispayItemStack() {
		return new ItemStack(ChaosCrystalMain.itemLifelessShard, 1, 0);
	}

	@Override
	public ItemStack[] getDegradedFrom(ItemStack is) {
		if (is == null || is.getItem() == null) {
			return new ItemStack[0];
		}
		return new ItemStack[] { new ItemStack(
				ChaosCrystalMain.itemLifelessShard, is.stackSize, 0) };
	}

}
