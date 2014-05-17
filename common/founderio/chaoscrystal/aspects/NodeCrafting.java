package founderio.chaoscrystal.aspects;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import founderio.util.ItemUtil;

public class NodeCrafting extends Node {

	private static final int[] aspectDifference;
	
	private final Node[] parents;
	private final ItemStack reference;
	private final ItemStack[] craftingIngredients;
	private final int[] aspects;

	static {
		AspectBuilder ab = new AspectBuilder();
		ab.addAspect(Aspects.ASPECT_CRAFTING, 5);
		aspectDifference = ab.toAspectArray();
	}
	
	public NodeCrafting(ItemStack reference, ItemStack[] craftingIngredients, Node[] parents, int[] aspects) {
		this.parents = parents;
		this.reference = reference;
		this.craftingIngredients = craftingIngredients;
		this.aspects = aspects;
	}

	@Override
	public int[] getAspects() {
		return Aspects.addAspects(aspects, aspectDifference);
	}

	@Override
	public int[] getAspectDifference() {
		return aspectDifference.clone();
	}

	@Override
	public Node[] getParents() {
		return parents.clone();
	}

	@Override
	public boolean matchesItemStack(ItemStack is) {
		return ItemUtil.itemsMatch(reference, is);
	}

	@Override
	public ItemStack getDispayItemStack() {
		ItemStack is = reference.copy();
		is.stackSize = 1;
		return is;
	}

	@Override
	public ItemStack[] getDegradedFrom(ItemStack is) {
		if(is.stackSize == 0) {
			return new ItemStack[] { new ItemStack(Blocks.air, 1, 0) };
		}
		float factor = (float)is.stackSize / (float)reference.stackSize;
		ItemStack[] ingItemStacks = new ItemStack[craftingIngredients.length];
		for(int i = 0; i < craftingIngredients.length; i++) {
			ingItemStacks[i] = craftingIngredients[i].copy();
			ingItemStacks[i].stackSize *= factor;
		}
		return ingItemStacks;
	}

}
