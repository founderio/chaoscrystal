package founderio.chaoscrystal.blocks;

import java.util.List;

import founderio.chaoscrystal.Constants;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockApparatus extends BlockContainer {

	public static final String[] metaList = new String[] {
		Constants.ID_BLOCK_APPARATUS_REENACTOR,
		Constants.ID_BLOCK_APPARATUS_CREATOR
	};
	
	public BlockApparatus(int par1, Material par2Material) {
		super(par1, par2Material);
	}

	
	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}
	
	public TileEntity createTileEntity(World world, int metadata) {
		switch(metadata) {
		case 0:
			return new TileEntityReenactor();
		case 1:
			return new TileEntityCreator();
		default: return null;
		}
		
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
		
		return te.onBlockActivated(par5EntityPlayer);
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return null;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs,
			List par3List) {
		par3List.add(new ItemStack(par1, 1, 0));
		par3List.add(new ItemStack(par1, 1, 1));
	}

	@Override
	public int damageDropped(int par1) {
		return MathHelper.clamp_int(par1, 0, metaList.length - 1);
	}
	
	
	
}
