package founderio.chaoscrystal.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.CommonProxy;
import founderio.chaoscrystal.aspects.Node;
import founderio.chaoscrystal.entities.EntityChaosCrystal;

public class TileEntityInfuser extends TileEntityApparatus {

	Random rand = new Random();
	public boolean isActive = true;
	public boolean didInfuse = false;

	public TileEntityInfuser() {
		super(1, 4);
	}

	@Override
	public boolean processAspects(EntityChaosCrystal crystal) {

		if (!isActive) {
			return false;
		}

		ItemStack is = getStackInSlot(0);

		if (is != null) {
			
			List<Node> degs = ChaosCrystalMain.degradationStore.getInfusionsFrom(is);

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
			
			// TODO: respect stack size...
			is = creation.getDispayItemStack().copy();
			setInventorySlotContents(0, is);
			didInfuse = true;
			
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
		if(!ChaosCrystalMain.degradationStore.getInfusionsFrom(itemstack).isEmpty()) {
			return true;
		}
		return false;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		super.setInventorySlotContents(i, itemstack);
		didInfuse = false;
	}
	
	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return didInfuse;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return new int[] { 0 };
	}
	
	@Override
	protected void readPropertiesFromNBT(NBTTagCompound par1nbtTagCompound) {
		super.readPropertiesFromNBT(par1nbtTagCompound);
		par1nbtTagCompound.setBoolean("didInfuse", didInfuse);
	}

	
	@Override
	protected void writePropertiesToNBT(NBTTagCompound par1nbtTagCompound) {
		super.writePropertiesToNBT(par1nbtTagCompound);
		didInfuse = par1nbtTagCompound.getBoolean("didInfuse");
	}
}
