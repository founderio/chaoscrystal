package founderio.chaoscrystal.aspects;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
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
		return Block.getBlockFromItem(is.getItem()) == Blocks.air;
	}

	@Override
	public ItemStack getDispayItemStack() {
		return new ItemStack(Blocks.air, 1, 0);
	}

	@Override
	public ItemStack[] getDegradedFrom(ItemStack is) {
		return new ItemStack[] { new ItemStack(Blocks.air, 1, 0) };
	}

}
