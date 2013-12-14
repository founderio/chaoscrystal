package founderio.chaoscrystal.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockApparatus extends Block {

	public BlockApparatus(int par1, Material par2Material) {
		super(par1, par2Material);
	}

	
	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}
	
	public net.minecraft.tileentity.TileEntity createTileEntity(net.minecraft.world.World world, int metadata) {
		return new TileEntityApparatus();
	};
	
	@Override
	public int getRenderType() {
		return -1;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
}
