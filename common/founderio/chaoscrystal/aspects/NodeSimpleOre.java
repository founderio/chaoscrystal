package founderio.chaoscrystal.aspects;

import net.minecraft.item.ItemStack;
import founderio.chaoscrystal.degradation.Aspects;
import founderio.util.ItemUtil;

public class NodeSimpleOre extends Node {

	private int[] aspects;
	private Node ingotBase;
	private Node rockBase;
	private ItemStack target;
	
	public NodeSimpleOre(Node ingotBase, Node rockBase, int count, ItemStack target) {
		this.target = target;
		this.rockBase = rockBase;
		this.ingotBase = ingotBase;
		AspectBuilder ab = new AspectBuilder();
		
		int[] ra = rockBase.getAspects();
		int[] ia = ingotBase.getAspects();
		
		ab.fromAspectArray(ra);
		
		for(int a = 0; a < Aspects.ASPECTS.length; a++) {
			ab.addAspect(Aspects.ASPECTS[a], ia[a] * count);
		}
		
		aspects = ab.toAspectArray();
	}
	
	@Override
	public int[] getAspects() {
		return aspects.clone();
	}

	@Override
	public Node[] getParents() {
		return new Node[] {rockBase, ingotBase}; 
	}

	@Override
	public boolean matchesItemStack(ItemStack is) {
		return ItemUtil.itemsMatch(target, is);
	}

}
