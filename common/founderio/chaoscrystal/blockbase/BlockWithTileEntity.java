package founderio.chaoscrystal.blockbase;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import founderio.util.ItemUtil;

public abstract class BlockWithTileEntity extends BlockContainer {

	protected BlockWithTileEntity(Material material) {
		super(material);
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z,
			EntityLivingBase entity, ItemStack itemStack) {
		if (entity instanceof EntityPlayer) {
			TileEntity te = world.getTileEntity(x, y, z);
			
			/* 
			 * Setting ownership
			 */
			if(te instanceof IOwnable) {
				
				String username = ((EntityPlayer) entity).getDisplayName();
				// TODO: 1.8 - switch to UUID
				((IOwnable) te).setOwner(username);
			}
		}
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int side, float hitx, float hity, float hitz) {
		TileEntity te = world.getTileEntity(x, y, z);

		if(te instanceof IOwnable) {
			IOwnable ownable = (IOwnable)te;
			//TODO: Locking implementation here
			
			/* 
			 * Setting/removing ownership
			 */
			ItemStack currentEquip = player.getHeldItem();
			if(currentEquip == null && player.isSneaking()) {
				if(ownable.getOwner().equals(player.getDisplayName())) {
					// Release ownership when owned by oneself
					ownable.setOwner("");
					return true;
				} else if(ownable.getOwner().equals("")) {
					// Claim ownership when not owned
					ownable.setOwner(player.getDisplayName());
					return true;
				}
			}
		}
		
		//TODO: Maybe process item inserting/extracting here?
		
		/*
		 * Pass the "used" event
		 */
		if(te instanceof IUseable) {
			IUseable useable = (IUseable) te;
			return useable.onBlockActivated(world, x, y, z, player, side, hitx, hity, hitz);
		}
		return false;
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		if (world.isRemote) {
			return;
		}
		TileEntity te = world.getTileEntity(x, y, z);

		/*
		 * Drop Items
		 */
		if(te instanceof IInventory) {
			IInventory inventory = (IInventory)te;
			for (int index = 0; index < inventory.getSizeInventory(); index++) {
				ItemStack itemstack = inventory.getStackInSlot(index);

				if (itemstack != null && itemstack.getItem() != null) {
					ItemUtil.spawnItemStackDropped(itemstack, world, x, y, z);
				}
			}
		}
		
		/*
		 * Drop Modules
		 */
		if(te instanceof IModuleHost) {
			IModuleHost moduleHost = (IModuleHost)te;
			for (int index = 0; index < moduleHost.getSizeModules(); index++) {
				ItemStack itemstack = moduleHost.getModuleItemStack(index);

				if (itemstack != null && itemstack.getItem() != null) {
					ItemUtil.spawnItemStackDropped(itemstack, world, x, y, z);
				}
			}
		}

		super.breakBlock(world, x, y, z, block, meta);
	}
	
}
