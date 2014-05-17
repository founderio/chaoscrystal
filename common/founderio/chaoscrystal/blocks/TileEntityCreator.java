package founderio.chaoscrystal.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.item.ItemStack;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.CommonProxy;
import founderio.chaoscrystal.aspects.Node;
import founderio.chaoscrystal.entities.EntityChaosCrystal;

public class TileEntityCreator extends TileEntityApparatus {

	Random rand = new Random();
	public boolean isActive = true;

	public TileEntityCreator() {
		super(1, 4);
	}

	@Override
	public boolean processAspects(EntityChaosCrystal crystal) {

		if (!isActive) {
			return false;
		}

		ItemStack is = getStackInSlot(0);

		if (is == null) {

			List<Node> degs = ChaosCrystalMain.degradationStore.getCreations();

			for (int i = degs.size() - 1; i >= 0; i--) {
				Node creation = degs.get(i);
				if (!crystal.canProvideAspects(creation.getAspects())) {
					degs.remove(i);
				}
			}
			if (degs.size() == 0) {
				return false;
			}

			Node creation = degs.get(rand.nextInt(degs.size()));

			crystal.subtractAspects(creation.getAspects());

			CommonProxy.spawnParticleEffects(worldObj.provider.dimensionId, 2,
					(float) xCoord + 0.5f, (float) yCoord + 0.5f,
					(float) zCoord + 0.5f, 0, 0, 0, 0.7f);
			is = creation.getDispayItemStack().copy();
			setInventorySlotContents(0, is);

			updateState();

			return true;
		} else {
			return false;
		}
	}

	@Override
	public void updateEntity() {
		isActive = !worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord,
				zCoord);
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if (itemstack == null) {
			return false;
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return true;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return new int[] { 0 };
	}

}
