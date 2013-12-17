package founderio.chaoscrystal.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.ChaosCrystalNetworkHandler;
import founderio.chaoscrystal.degradation.Degradation;
import founderio.chaoscrystal.degradation.DegradationHelper;
import founderio.chaoscrystal.entities.EntityChaosCrystal;

public class TileEntityCreator extends TileEntityApparatus {

	Random rand = new Random();
	public boolean isActive = true;
	
	public TileEntityCreator() {
		super(1);
	}
	
	@Override
	public boolean processAspects(EntityChaosCrystal crystal) {
		
		if(!isActive) {
			return false;
		}
		
		ItemStack is = getStackInSlot(0);
		
		if(is == null || is.itemID == 0) {
			
			
			List<Degradation> degs = ChaosCrystalMain.degradationStore.getCreations();
			
			if(degs.size() == 0) {
				return false;
			}
			
			for(int i = degs.size() - 1; i >= 0; i--) {
				Degradation creation = degs.get(i);
				if(!DegradationHelper.canSupportAspects(creation.aspects, creation.amounts, crystal)) {
					degs.remove(i);
				}
			}
			
			Degradation creation = degs.get(rand.nextInt(degs.size()));
			
			for(int a = 0; a < creation.aspects.length; a++) {
				crystal.setAspect(creation.aspects[a],
						crystal.getAspect(creation.aspects[a]) - creation.amounts[a]);
			}
			
			ChaosCrystalNetworkHandler.spawnParticleEffects(worldObj.provider.dimensionId, 2,
					(float)xCoord + 0.5f, (float)yCoord + 0.5f, (float)zCoord + 0.5f,
					0, 0, 0, 0.7f);
			is = creation.source.copy();
			is.stackSize = 1;
			setInventorySlotContents(0, is);
			
			updateState();
			
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void updateEntity() {
		isActive = !worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player) {
		ItemStack is = getStackInSlot(0);
		if(is == null || is.stackSize == 0) {
			if(player.getCurrentEquippedItem() != null &&
					isItemValidForSlot(0, player.getCurrentEquippedItem())) {
				setInventorySlotContents(0, player.getCurrentEquippedItem());
				player.inventory.mainInventory[player.inventory.currentItem] = null;
			}
		} else {
			if(player.inventory.addItemStackToInventory(is)) {
				setInventorySlotContents(0, null);
			}
		}
		
		onInventoryChanged();
		
		return true;
	}
	
	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if(itemstack == null) {
			return false;
		}
		return false;
	}
	
	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		ItemStack is = getStackInSlot(i);
		if(is != null && is.itemID != 0) {
			return false;
		} else {
			return j == 1 && isItemValidForSlot(i, itemstack);
		}
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return true;
	}
	
}
