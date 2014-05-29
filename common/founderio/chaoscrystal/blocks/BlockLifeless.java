package founderio.chaoscrystal.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.chaoscrystal.Constants;

public class BlockLifeless extends Block {

	public BlockLifeless() {
		super(Material.coral);
		this.setHardness(0);
		this.setResistance(0);
		this.setStepSound(new SoundType("", 0, 0));
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String getTextureName() {
		return Constants.MOD_ID + ":lifeless";
	}

	@Override
	public boolean isNormalCube() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_,
			int p_149668_2_, int p_149668_3_, int p_149668_4_) {
		return null;
	}
	
}
