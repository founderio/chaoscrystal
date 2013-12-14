package founderio.chaoscrystal.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;


public class TileEntityApparatus extends TileEntity implements IInventory, ISidedInventory {

	private ItemStack[] inventory;
	
	public TileEntityApparatus() {
		inventory = new ItemStack[1];
	}
	
	@Override
	public int getSizeInventory() {
		return inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		if(i < 0 || i >= inventory.length) {
			return null;
		} else {
			return inventory[i];
		}
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		ItemStack inInv = getStackInSlot(i);
		if(inInv != null) {
			if(inInv.stackSize <= j) {
				setInventorySlotContents(i, null);
			} else {
				inInv = inInv.splitStack(j);
			}
		}
		return inInv;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		ItemStack inInv = getStackInSlot(i);
		if(inInv != null) {
			setInventorySlotContents(i, null);
		}
		return inInv;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		if(i < 0 || i >= inventory.length) {
			return;
		} else {
			inventory[i] = itemstack;
			if(itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
				itemstack.stackSize = getInventoryStackLimit();
			}
		}
	}

	@Override
	public String getInvName() {
		return "inv";//TODO: Localization
	}

	@Override
	public boolean isInvNameLocalized() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public void onInventoryChanged() {
		//
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public void openChest() {
		
	}

	@Override
	public void closeChest() {
		
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if(itemstack == null) {
			return false;
		}
		return itemstack.isItemStackDamageable() && !itemstack.isStackable() && itemstack.isItemDamaged();
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		//TODO: block top side!
		return new int[] {1};
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		return true;//TODO: verify!!
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return true;//TODO: verify!!
	}

	@Override
	public void writeToNBT(NBTTagCompound par1nbtTagCompound) {
		super.writeToNBT(par1nbtTagCompound);
		
		NBTTagList items = new NBTTagList();
		for(int i = 0; i < inventory.length; i++) {
			ItemStack is = inventory[i];
			if(is == null) {
				is = new ItemStack(0,0,0);
			}
			NBTTagCompound stackTag = new NBTTagCompound();
			is.writeToNBT(stackTag);
			items.appendTag(stackTag);
		}
		par1nbtTagCompound.setTag("inventory", items);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound par1nbtTagCompound) {
		super.readFromNBT(par1nbtTagCompound);
		
		NBTTagList items = par1nbtTagCompound.getTagList("inventory");
		if(items != null) {
			for(int i = 0; i < items.tagCount(); i++) {
				NBTTagCompound stackTag = (NBTTagCompound) items.tagAt(i);
				setInventorySlotContents(i, ItemStack.loadItemStackFromNBT(stackTag));
			}
		}
	}
}
