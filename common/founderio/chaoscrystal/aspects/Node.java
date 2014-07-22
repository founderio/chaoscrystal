package founderio.chaoscrystal.aspects;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Node {
	private NodePoint lesser;
	private NodePoint greater;
	private int[] aspects;
	private boolean infusionSunlightRequired;
	private boolean infusionAirAboveRequired;

	public Node() {
		lesser = new NodePoint();
		greater = new NodePoint();
		aspects = new int[Aspect.values().length];
	}

	public int[] getAspects() {
		return Arrays.copyOf(aspects, aspects.length);
	}

	public NodePoint getLesser() {
		return lesser;
	}

	public NodePoint getGreater() {
		return greater;
	}

	public void setLesser(NodePoint lesser) {
		this.lesser = lesser;
	}

	public void setGreater(NodePoint greater) {
		this.greater = greater;
	}

	public void setAspects(int[] aspects) {
		//TODO: validate array length
		this.aspects = Arrays.copyOf(aspects, aspects.length);;
	}

	public boolean isInfusionSunlightRequired() {
		return infusionSunlightRequired;
	}

	public void setInfusionSunlightRequired(boolean infusionSunlightRequired) {
		this.infusionSunlightRequired = infusionSunlightRequired;
	}

	public boolean isInfusionAirAboveRequired() {
		return infusionAirAboveRequired;
	}

	public void setInfusionAirAboveRequired(boolean infusionAirAboveRequired) {
		this.infusionAirAboveRequired = infusionAirAboveRequired;
	}

	/**
	 * Check for match on item/block with less aspects (infusion source,
	 * degradation target)
	 * 
	 * @param item
	 * @param meta
	 * @param count
	 * @return
	 */
	public boolean matchesOnLesseer(Item item, int meta) {
		return lesser.matches(item, meta);
	}

	/**
	 * Check for match on item/block with less aspects (infusion source,
	 * degradation target)
	 * 
	 * @param block
	 * @param meta
	 * @param count
	 * @return
	 */
	public boolean matchesOnLesseer(Block block, int meta) {
		return lesser.matches(block, meta);
	}

	/**
	 * Check for match on item/block with less aspects (infusion target,
	 * degradation source)
	 * 
	 * @param item
	 * @param meta
	 * @param count
	 * @return
	 */
	public boolean matchesOnGreater(Item item, int meta) {
		return greater.matches(item, meta);
	}

	/**
	 * Check for match on item/block with less aspects (infusion target,
	 * degradation source)
	 * 
	 * @param block
	 * @param meta
	 * @param count
	 * @return
	 */
	public boolean matchesOnGreater(Block block, int meta) {
		return greater.matches(block, meta);
	}

	// TODO: Decide on this method.(Need different render routines for no-item-blocks)
	public ItemStack getDispayItemStack() {
		// TODO Auto-generated method stub
		return null;
	}

	// TODO: Need decision on these three methods once the rest is intact.

	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [" + getDescription() + "]";
	}
}
