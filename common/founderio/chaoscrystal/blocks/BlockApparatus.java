package founderio.chaoscrystal.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockApparatus extends BlockContainer {

	public BlockApparatus(int par1, Material par2Material) {
		super(par1, par2Material);
	}

	
	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}
	
	public TileEntity createTileEntity(net.minecraft.world.World world, int metadata) {
		return new TileEntityApparatus();
	}
	
	@Override
	public int getRenderType() {
		return -1;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9) {
		TileEntityApparatus te = ((TileEntityApparatus)par1World.getBlockTileEntity(par2, par3, par4));
		ItemStack is = te.getStackInSlot(0);
		if(is == null || is.stackSize == 0) {
			if(par5EntityPlayer.getCurrentEquippedItem() != null && te.isItemValidForSlot(0, par5EntityPlayer.getCurrentEquippedItem())) {
				te.setInventorySlotContents(0, par5EntityPlayer.getCurrentEquippedItem());
				par5EntityPlayer.inventory.mainInventory[par5EntityPlayer.inventory.currentItem] = null;
			}
		} else {
			if(par5EntityPlayer.inventory.addItemStackToInventory(is)) {
				te.setInventorySlotContents(0, null);
			}
		}
		
		te.updateState();
		
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return null;
	}
	
	
}
