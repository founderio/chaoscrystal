package founderio.chaoscrystal.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

import org.apache.commons.lang3.ArrayUtils;

import founderio.chaoscrystal.blockbase.IOwnable;
import founderio.chaoscrystal.blockbase.IUseable;
import founderio.chaoscrystal.entities.EntityChaosCrystal;
import founderio.chaoscrystal.machinery.IItemModule;
import founderio.chaoscrystal.machinery.IModule;

public abstract class TileEntityApparatus extends TileEntity implements
IInventory, ISidedInventory, IOwnable, IUseable {

	public final int stepsPerTick = 5;
	public short animation;

	protected final ItemStack[] inventory;
	protected final ItemStack[] moduleItems;
	protected final IModule[] modules;
	private String owner = "";

	public TileEntityApparatus(int inventorySize, int moduleCount) {
		inventory = new ItemStack[inventorySize];
		moduleItems = new ItemStack[moduleCount];
		modules = new IModule[moduleCount];
	}

	public abstract boolean processAspects(EntityChaosCrystal crystal);

	/*
	 * BEGIN: IModuleHost
	 */
	
	public int getSizeModules() {
		return modules.length;
	}

	public IModule getModule(int index) {
		return modules[index];
	}

	public ItemStack getModuleItemStack(int index) {
		return moduleItems[index];
	}

	/*
	 * BEGIN: IOwnable
	 */
	
	@Override
	public void setOwner(String username) {
		owner = username;
		if (owner == null) {
			owner = "";
		}
		updateState();
	}

	@Override
	public String getOwner() {
		return owner;
	}
	
	/*
	 * BEGIN: IInventory
	 */
	

	@Override
	public int getSizeInventory() {
		return inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		if (i < 0 || i >= inventory.length) {
			return null;
		} else {
			return inventory[i];
		}
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		ItemStack inInv = getStackInSlot(i);
		if (inInv != null) {
			if (inInv.stackSize <= j) {
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
		if (inInv != null) {
			setInventorySlotContents(i, null);
		}
		return inInv;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		if (i < 0 || i >= inventory.length) {
			return;
		} else {
			inventory[i] = itemstack;
			if (itemstack != null
					&& itemstack.stackSize > getInventoryStackLimit()) {
				itemstack.stackSize = getInventoryStackLimit();
			}
		}
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		if (itemstack == null) {
			return false;
		} else {
			boolean isValidSlot = ArrayUtils.contains(getAccessibleSlotsFromSide(j), i);
			return isValidSlot && isItemValidForSlot(i, itemstack);
		}
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public void markDirty() {
		super.markDirty();
		updateState();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public String getInventoryName() {
		return "inv";
	}

	@Override
	public void openInventory() {
		// Nothing to do...
	}

	@Override
	public void closeInventory() {
		// Nothing to do...
	}

	/*
	 * BEGIN: Regular Implementation
	 */

	public final void updateState() {
		if (worldObj.isRemote) {
			return;
		}
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public final Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		writePropertiesToNBT(nbt);

		S35PacketUpdateTileEntity packet = new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);

		return packet;
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		NBTTagCompound nbt = pkt.func_148857_g();

		readPropertiesFromNBT(nbt);
	}

	@Override
	public final void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		writePropertiesToNBT(nbt);
	}

	protected void writePropertiesToNBT(NBTTagCompound nbt) {
		NBTTagList items = new NBTTagList();
		for (int i = 0; i < inventory.length; i++) {
			ItemStack is = inventory[i];
			if (is == null) {
				is = new ItemStack(Blocks.air, 0, 0);
			}
			NBTTagCompound stackTag = new NBTTagCompound();
			is.writeToNBT(stackTag);
			items.appendTag(stackTag);
		}
		nbt.setTag("inventory", items);
		NBTTagList moduleItemsNBT = new NBTTagList();
		for (int i = 0; i < moduleItems.length; i++) {
			ItemStack is = moduleItems[i];
			if (is == null) {
				is = new ItemStack(Blocks.air, 0, 0);
			}
			NBTTagCompound stackTag = new NBTTagCompound();
			is.writeToNBT(stackTag);
			moduleItemsNBT.appendTag(stackTag);
		}
		nbt.setTag("modules", moduleItemsNBT);
		nbt.setShort("animation", animation);
		nbt.setString("owner", owner);
	}

	@Override
	public final void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		readPropertiesFromNBT(nbt);
	}

	protected void readPropertiesFromNBT(NBTTagCompound nbt) {
		NBTTagList items = nbt.getTagList("inventory", NBT.TAG_COMPOUND);
		if (items != null) {
			for (int i = 0; i < items.tagCount(); i++) {
				NBTTagCompound stackTag = items.getCompoundTagAt(i);
				setInventorySlotContents(i, ItemStack.loadItemStackFromNBT(stackTag));
			}
		}
		NBTTagList moduleItemsNBT = nbt.getTagList("modules", NBT.TAG_COMPOUND);
		if (moduleItemsNBT != null) {
			for (int i = 0; i < moduleItemsNBT.tagCount(); i++) {
				NBTTagCompound stackTag = moduleItemsNBT.getCompoundTagAt(i);
				ItemStack mi = ItemStack.loadItemStackFromNBT(stackTag);
				if(mi != null && mi.getItem() instanceof IItemModule) {
					moduleItems[i] = mi;
					modules[i] = ((IItemModule)mi.getItem()).getModuleFromItemStack(mi);
				}
			}
		}
		animation = nbt.getShort("animation");
		owner = nbt.getString("owner");
	}


	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int side, float hitx, float hity, float hitz) {
		ItemStack currentEquip = player.getHeldItem();

		boolean itemValid = false;

		if (currentEquip != null) {
			for (int i = 0; i < inventory.length; i++) {
				if(isItemValidForSlot(i, currentEquip)) {
					itemValid = true;
					ItemStack is = getStackInSlot(i);
					if (is == null || is.getItem() == null) {
						if (currentEquip.stackSize <= getInventoryStackLimit()) {
							setInventorySlotContents(i, currentEquip.copy());
							player.inventory.mainInventory[player.inventory.currentItem] = null;
							break;
						} else {
							currentEquip.stackSize -= getInventoryStackLimit();

							ItemStack copy = currentEquip.copy();
							copy.stackSize = getInventoryStackLimit();
							setInventorySlotContents(i, copy);
							if (currentEquip.stackSize == 0) {
								player.inventory.mainInventory[player.inventory.currentItem] = null;
								break;
							}
						}
					} else if (is.isItemEqual(currentEquip)) {
						if (is.stackSize + currentEquip.stackSize <= getInventoryStackLimit()) {
							is.stackSize += currentEquip.stackSize;
							player.inventory.mainInventory[player.inventory.currentItem] = null;
							break;
						} else {
							currentEquip.stackSize -= getInventoryStackLimit()
									- is.stackSize;
							is.stackSize = getInventoryStackLimit();
							if (currentEquip.stackSize == 0) {
								player.inventory.mainInventory[player.inventory.currentItem] = null;
								break;
							}
						}
					}
				}
			}

			if(currentEquip.getItem() instanceof IItemModule) {
				IItemModule iim = (IItemModule)currentEquip.getItem();
				for(int i = 0; i < modules.length; i++) {
					if(modules[i] == null) {
						modules[i] = iim.getModuleFromItemStack(currentEquip);
						if(modules[i] != null) {
							moduleItems[i] = iim.getItemStackFromModule(modules[i]);
							currentEquip.stackSize -= 1;
							if (currentEquip.stackSize == 0) {
								player.inventory.mainInventory[player.inventory.currentItem] = null;
							}
						}
						break;
					}
				}
			}

		}

		if(!itemValid) {
			for (int i = 0; i < 4; i++) {
				ItemStack is = getStackInSlot(i);
				if (is != null && is.stackSize > 0) {
					if (player.inventory.addItemStackToInventory(is)) {
						setInventorySlotContents(i, null);
						break;
					}
				}
			}
		}

		markDirty();
		return true;
	}

}
