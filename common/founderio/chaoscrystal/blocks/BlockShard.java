package founderio.chaoscrystal.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.chaoscrystal.Constants;
import founderio.chaoscrystal.blockbase.BlockWithTileEntity;

public class BlockShard extends BlockWithTileEntity {

	public BlockShard() {
		super(Material.circuits);
		setHardness(0);
		setStepSound(soundTypeGlass);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String getTextureName() {
		return Constants.MOD_ID + ":crystal_clear";
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public String getItemIconName() {
		return Constants.MOD_ID + ":crystal_clear";
	}
	
	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		int idx = MathHelper.clamp_int(meta, 0, Constants.METALIST_SHARD.length - 1);
		return idx > 3 ? 15 : 0;
	}

	@Override
	public boolean isNormalCube() {
		return false;
	}
	
	@Override
	public boolean isBlockSolid(IBlockAccess world, int x, int y, int z,
			int meta) {
		return false;
	}
	
	@Override
    public boolean isOpaqueCube()
    {
        return false;
    }
	
	@Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
    {
        return null;
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {
	}
	
	@Override
	public int damageDropped(int meta) {
		int idx = MathHelper.clamp_int(meta, 0, Constants.METALIST_SHARD.length - 1);
		return idx;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return null;
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityShard();
	}

}
