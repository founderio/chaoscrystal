package founderio.chaoscrystal.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.entities.EntityChaosCrystal;

public class TileEntityTicker extends TileEntityApparatus {

	public TileEntityTicker() {
		super(0, 0);
	}

	@Override
	public boolean processAspects(EntityChaosCrystal crystal) {

		//TODO: Charge Up!
		return false;
	}


	private int acc = 0;
	private int interval = 2;
	public boolean isActive = true;

	@Override
	public void updateEntity() {
		isActive = !worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord,
				zCoord);

		acc++;
		
		if(acc >= interval) {
			acc = 0;
			
			Block blockAbove = worldObj.getBlock(xCoord, yCoord + 1, zCoord);
			if(blockAbove != null) {
				blockAbove.updateTick(worldObj, xCoord, yCoord + 1, zCoord, ChaosCrystalMain.rand);
			}
		}
		
		
	}

	
	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return new int[] { };
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return false;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return false;
	}
}
