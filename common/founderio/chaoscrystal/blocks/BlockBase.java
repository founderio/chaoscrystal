package founderio.chaoscrystal.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.chaoscrystal.Constants;

public class BlockBase extends Block {

	public static final String[] metaList = new String[] {
		Constants.ID_BLOCK_BASE_CRYSTALLINE,
		Constants.ID_BLOCK_BASE_CRYSTAL
	};
	public Icon[] iconList;
	
	public BlockBase(int par1, Material par2Material) {
		super(par1, par2Material);
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
		iconList[0] = par1IconRegister.registerIcon(Constants.MOD_ID + ":" + "crystalline");
		iconList[1] = par1IconRegister.registerIcon(Constants.MOD_ID + ":" + "crystal");
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
		return MathHelper.clamp_int(par1, 0, iconList.length - 1);
	}
}
