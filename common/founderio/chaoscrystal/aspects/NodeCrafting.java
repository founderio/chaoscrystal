package founderio.chaoscrystal.aspects;

import net.minecraft.item.ItemStack;
import founderio.chaoscrystal.degradation.Aspects;

public class NodeCrafting extends Node {

private static int[] aspectDifference;
	
	static {
		AspectBuilder ab = new AspectBuilder();
		ab.addAspect(Aspects.ASPECT_CRAFTING, 5);
		aspectDifference = ab.toAspectArray();
	}
	
	@Override
	public int[] getAspects() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int[] getAspectDifference() {
		return aspectDifference.clone();
	}

	@Override
	public Node[] getParents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean matchesItemStack(ItemStack is) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ItemStack getDispayItemStack() {
		// TODO Auto-generated method stub
		return null;
	}

}
