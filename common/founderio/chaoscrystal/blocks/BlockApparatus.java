package founderio.chaoscrystal.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import founderio.chaoscrystal.Constants;

public class BlockApparatus extends BlockContainer {

	public static final String[] metaList = new String[] {
		Constants.ID_BLOCK_APPARATUS_RECONSTRUCTOR,
		Constants.ID_BLOCK_APPARATUS_CREATOR
	};
	
	public final int metaListIndex;
	
	public BlockApparatus(int par1, Material par2Material, int meta) {
		super(par1, par2Material);
		this.metaListIndex = meta;
	}

	
	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}
	
	public TileEntity createTileEntity(World world, int metadata) {
		switch(metaListIndex) {
		case 0:
			return new TileEntityReconstructor();
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
	
	
}
