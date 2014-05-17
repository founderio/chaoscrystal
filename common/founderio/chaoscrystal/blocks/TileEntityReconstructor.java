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

			Repair rep = ChaosCrystalMain.degradationStore.getRepair(is.getItem());

			if (rep == null) {
				return false;
			}
			boolean didRepair = false;

			for (int step = 0; step < stepsPerTick && curDmg > 0; step++) {
				boolean capable = true;
				for (int a = 0; a < rep.aspects.length; a++) {
					if (crystal.getAspect(rep.aspects[a]) < rep.amounts[a]) {
						capable = false;
					}
				}
				if (!capable) {
					break;
				}
				didRepair = true;

				for (int a = 0; a < rep.aspects.length; a++) {
					crystal.setAspect(rep.aspects[a],
							crystal.getAspect(rep.aspects[a]) - rep.amounts[a]);
				}

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
