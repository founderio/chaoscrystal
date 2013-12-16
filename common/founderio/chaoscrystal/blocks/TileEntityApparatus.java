package founderio.chaoscrystal.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.PacketDispatcher;
import founderio.chaoscrystal.entities.EntityChaosCrystal;


public abstract class TileEntityApparatus extends TileEntity implements IInventory, ISidedInventory {

	public final int stepsPerTick = 5;
	public short animation;
	
	private ItemStack[] inventory;
	private String owner = "";
	
	public TileEntityApparatus(int inventorySize) {
		inventory = new ItemStack[inventorySize];
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
		return "inv";//No GUI, so no name...
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
		super.onInventoryChanged();
		updateState();
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
	public int[] getAccessibleSlotsFromSide(int var1) {
		//TODO: block top side!
		return new int[] {0};
	}

	
	
	public abstract boolean processAspects(EntityChaosCrystal crystal);
	public abstract boolean onBlockActivated(EntityPlayer player);
	
	public void updateState() {
		if(worldObj.isRemote) {
			return;
		}
		PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord, 128, worldObj.provider.dimensionId, getDescriptionPacket());
	}
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		writePropertiesToNBT(nbt);
		
		Packet132TileEntityData packet = new Packet132TileEntityData(xCoord, yCoord, zCoord, 0, nbt);
		
		return packet;
	}
	
	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		NBTTagCompound nbt = pkt.data;
		
		readPropertiesFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound par1nbtTagCompound) {
		super.writeToNBT(par1nbtTagCompound);
		
		writePropertiesToNBT(par1nbtTagCompound);
	}
	
	protected void writePropertiesToNBT(NBTTagCompound par1nbtTagCompound) {
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
		par1nbtTagCompound.setShort("animation", animation);
		par1nbtTagCompound.setString("owner", owner);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound par1nbtTagCompound) {
		super.readFromNBT(par1nbtTagCompound);
		
		readPropertiesFromNBT(par1nbtTagCompound);
	}
	
	protected void readPropertiesFromNBT(NBTTagCompound par1nbtTagCompound) {
		NBTTagList items = par1nbtTagCompound.getTagList("inventory");
		if(items != null) {
			for(int i = 0; i < items.tagCount(); i++) {
				NBTTagCompound stackTag = (NBTTagCompound) items.tagAt(i);
				setInventorySlotContents(i, ItemStack.loadItemStackFromNBT(stackTag));
			}
		}
		animation = par1nbtTagCompound.getShort("animation");
		owner = par1nbtTagCompound.getString("owner");
	}

	public void setOwner(String username) {
		this.owner = username;
		if(this.owner == null) {
			this.owner = "";
		}
		updateState();
	}
	
	public String getOwner() {
		return this.owner;
	}
}
