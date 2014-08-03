package founderio.chaoscrystal.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.chaoscrystal.Constants;
import founderio.chaoscrystal.blockbase.BlockWithTileEntity;

public class BlockApparatus extends BlockWithTileEntity {

	public final int metaListIndex;

	public BlockApparatus(int meta) {
		super(Material.rock);
		metaListIndex = meta;
		setHardness(2);
		setLightLevel(0.3f);
		setResistance(6f);
		setStepSound(Block.soundTypeStone);
		this.setHarvestLevel("pickaxe", 0);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		switch (metaListIndex) {
		case 0:
			return new TileEntityReconstructor();
		case 1:
			return new TileEntityInfuser();
		case 2:
			return new TileEntitySentry();
		case 3:
			return new TileEntityTicker();
		default:
			return null;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String getTextureName() {
		return Constants.MOD_ID + ":apparatus";
	}

}
