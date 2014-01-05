package founderio.chaoscrystal.aspects;

import founderio.util.ItemUtil;
import net.minecraft.item.ItemStack;

public class NodeSmelting extends Node {

	private static final int[] aspectDifference;
	
	private final Node parent;
	private final ItemStack reference;
	private final ItemStack ingredient;
	private final int[] aspects;
	
	static {
		AspectBuilder ab = new AspectBuilder();
		ab.addAspect(Aspects.ASPECT_HEAT, 5);
		aspectDifference = ab.toAspectArray();
	}
	
	public NodeSmelting(ItemStack reference, ItemStack ingredient, Node parent, int[] aspects) {
		this.parent = parent;
		this.reference = reference;
		this.ingredient = ingredient;
		this.aspects = aspects;
	}
	
	@Override
	public int[] getAspectDifference() {
		return aspectDifference.clone();
	}

	@Override
	public int[] getAspects() {
		return Aspects.addAspects(aspects, aspectDifference);
	}

	@Override
	public Node[] getParents() {
		return new Node[] { parent };
	}

	@Override
	public ItemStack[] getDegradedFrom(ItemStack is) {
		if(is.stackSize == 0) {
			return new ItemStack[] { new ItemStack(0, 1, 0) };
		}
		float factor = (float)is.stackSize / (float)reference.stackSize;
		ItemStack ingItemStack = ingredient.copy();
		ingItemStack.stackSize *= factor;
		return new ItemStack[] { ingItemStack };
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

}
