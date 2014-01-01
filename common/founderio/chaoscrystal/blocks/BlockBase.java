package founderio.chaoscrystal.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.chaoscrystal.Constants;

public class BlockBase extends Block {

	public static final String[] metaList = new String[] {
			Constants.ID_BLOCK_BASE_CRYSTALLINE,
			Constants.ID_BLOCK_BASE_CRYSTAL,
			Constants.ID_BLOCK_BASE_CRYSTALLINE_LIGHT, };
	public Icon[] iconList;

	public BlockBase(int id) {
		super(id, Material.glass);
		this.setHardness(4);
		this.setLightValue(0.2f);
		this.setResistance(1.5f);
		this.setStepSound(Block.soundGlassFootstep);
		MinecraftForge.setBlockHarvestLevel(this, "pickaxe", 1);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs,
			List par3List) {
		for (int i = 0; i < metaList.length; i++) {
			par3List.add(new ItemStack(par1, 1, i));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		iconList = new Icon[metaList.length];
		iconList[0] = par1IconRegister.registerIcon(Constants.MOD_ID + ":"
				+ "crystalline");
		iconList[1] = par1IconRegister.registerIcon(Constants.MOD_ID + ":"
				+ "crystal");
		iconList[2] = par1IconRegister.registerIcon(Constants.MOD_ID + ":"
				+ "crystalline_light");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int par1, int par2) {
		int idx = MathHelper.clamp_int(par2, 0, iconList.length - 1);
		return iconList[idx];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int damageDropped(int par1) {
		return MathHelper.clamp_int(par1, 0, metaList.length - 1);
	}

}
