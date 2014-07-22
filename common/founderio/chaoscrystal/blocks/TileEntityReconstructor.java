package founderio.chaoscrystal.blocks;

import net.minecraft.item.ItemStack;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.aspects.Repair;
import founderio.chaoscrystal.entities.EntityChaosCrystal;

public class TileEntityReconstructor extends TileEntityApparatus {

	public TileEntityReconstructor() {
		super(1, 0);
	}

	@Override
	public boolean processAspects(EntityChaosCrystal crystal) {
		ItemStack is = getStackInSlot(0);

		if (is == null) {
			return false;
		} else {
			int curDmg = is.getItemDamage();

			if (curDmg == 0) {
				return false;
			}

			Repair rep = ChaosCrystalMain.chaosRegistry.getRepair(is.getItem());

			if (rep == null) {
				return false;
			}
			boolean didRepair = false;

			for (int step = 0; step < stepsPerTick && curDmg > 0; step++) {
				boolean capable = crystal.canProvideAspects(rep.aspects);;
				if (!capable) {
					break;
				}
				didRepair = true;
				crystal.subtractAspects(rep.aspects);

				curDmg--;
				is.setItemDamage(curDmg);
			}

			if (didRepair) {
				updateState();
			}

			return didRepair;

		}
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if (itemstack == null) {
			return false;
		}
		return itemstack.isItemStackDamageable() && !itemstack.isStackable()
				&& itemstack.isItemDamaged();
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return itemstack.getItemDamage() == 0;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return new int[] { 0 };
	}

}
