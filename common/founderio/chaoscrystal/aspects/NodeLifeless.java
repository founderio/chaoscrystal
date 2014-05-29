package founderio.chaoscrystal.aspects;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import founderio.chaoscrystal.ChaosCrystalMain;

public class NodeLifeless extends Node {

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
		return Block.getBlockFromItem(is.getItem()) == ChaosCrystalMain.blockLifeless;
	}

	@Override
	public ItemStack getDispayItemStack() {
		return new ItemStack(ChaosCrystalMain.blockLifeless, 1, 0);
	}

	@Override
	public ItemStack[] getDegradedFrom(ItemStack is) {
		if(is == null || is.getItem() == null) {
			return new ItemStack[0];
		} else if(is.getItem() instanceof ItemBlock) {
			return new ItemStack[] { new ItemStack(ChaosCrystalMain.blockLifeless, is.stackSize, 0) };
		} else {
			return new ItemStack[] { new ItemStack(ChaosCrystalMain.itemLifelessShard, is.stackSize, 0) };
		}
	}

}
