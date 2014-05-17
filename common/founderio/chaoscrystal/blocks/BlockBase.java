package founderio.chaoscrystal.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.chaoscrystal.Constants;

public class BlockBase extends Block {

	public static final String[] metaList = new String[] {
			Constants.ID_BLOCK_BASE_CRYSTALLINE,
			Constants.ID_BLOCK_BASE_CRYSTAL,
			Constants.ID_BLOCK_BASE_CRYSTALLINE_LIGHT, };
	public IIcon[] iconList;

	public BlockBase() {
		super(Material.glass);
		this.setHardness(4);
		this.setLightLevel(0.2f);
		this.setResistance(1.5f);
		this.setStepSound(Block.soundTypeGlass);
		this.setHarvestLevel("pickaxe", 1);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs creativeTabs,
			List list) {
		for (int i = 0; i < metaList.length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		iconList = new IIcon[metaList.length];
		iconList[0] = par1IconRegister.registerIcon(Constants.MOD_ID + ":"
				+ "crystalline");
		iconList[1] = par1IconRegister.registerIcon(Constants.MOD_ID + ":"
				+ "crystal");
		iconList[2] = par1IconRegister.registerIcon(Constants.MOD_ID + ":"
				+ "crystalline_light");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int par1, int par2) {
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
