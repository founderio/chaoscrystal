package founderio.chaoscrystal.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.degradation.Repair;
import founderio.chaoscrystal.entities.EntityChaosCrystal;

public class TileEntityReenactor extends TileEntityApparatus {

	@Override
	public boolean processAspects(EntityChaosCrystal crystal) {
		ItemStack is = getStackInSlot(0);
		
		if(is == null || is.itemID == 0) {
			return false;
		} else {
			int maxDmg = is.getMaxDamage();
			int curDmg = is.getItemDamage();

			if(curDmg == 0) {
				return false;
			}
			
			Repair rep = ChaosCrystalMain.degradationStore.getRepair(is.itemID);
			
			if(rep == null) {
				return false;
			}
			boolean didRepair = false;
			
			for(int step = 0; step < stepsPerTick && curDmg > 0; step++) {
				boolean capable = true;
				for(int a = 0; a < rep.aspects.length; a++) {
					if(crystal.getAspect(rep.aspects[a]) < rep.amounts[a]) {
						capable = false;
					}
				}
				if(!capable) {
					break;
				}
				didRepair = true;
				
				for(int a = 0; a < rep.aspects.length; a++) {
					crystal.setAspect(rep.aspects[a], crystal.getAspect(rep.aspects[a]) - rep.amounts[a]);
				}
				
				curDmg--;
				is.setItemDamage(curDmg);
			}
			
			if(didRepair) {
				updateState();
			}
			
			return didRepair;
			
		}
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
		return itemstack.isItemStackDamageable() && !itemstack.isStackable() && itemstack.isItemDamaged();
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
		return itemstack.getItemDamage() == 0;
	}
	
}
