package founderio.chaoscrystal.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.chaoscrystal.Constants;

public class BlockLifeless extends Block {

	public BlockLifeless() {
		super(Material.air);
		this.setHardness(0);
		this.setResistance(0);
		this.setStepSound(null);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String getTextureName() {
		return Constants.MOD_ID + ":lifeless";
	}

}
