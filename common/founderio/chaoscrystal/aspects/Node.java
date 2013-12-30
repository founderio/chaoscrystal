package founderio.chaoscrystal.aspects;

import net.minecraft.item.ItemStack;

public abstract class Node {
	public abstract int[] getAspectDifference();
	public abstract int[] getAspects();
	public abstract Node[] getParents();
	public abstract boolean matchesItemStack(ItemStack is);
	public abstract ItemStack getDispayItemStack();
}
