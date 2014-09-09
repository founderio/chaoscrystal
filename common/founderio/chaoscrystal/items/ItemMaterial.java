package founderio.chaoscrystal.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.chaoscrystal.Constants;

public class ItemMaterial extends Item {

	private IIcon[] iconList;

	public ItemMaterial() {
		super();
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int meta) {

		if (meta < 0 || meta >= iconList.length) {
			meta = 0;
		}

		return iconList[meta];
	}

	public String getUnlocalizedName(ItemStack itemStack) {
		int i = itemStack.getItemDamage();

		if (i < 0 || i >= Constants.METALIST_MATERIAL.length) {
			i = 0;
		}

		return super.getUnlocalizedName() + "." + Constants.METALIST_MATERIAL[i];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister ir) {
		iconList = new IIcon[Constants.METALIST_MATERIAL.length];
		for (int i = 0; i < Constants.METALIST_MATERIAL.length; i++) {
			iconList[i] = ir.registerIcon(Constants.MOD_ID + ":material." + Constants.METALIST_MATERIAL[i]);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs creativeTab, List list) {
		for (int i = 0; i < Constants.METALIST_MATERIAL.length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}
}
