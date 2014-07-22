package founderio.chaoscrystal.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.chaoscrystal.Constants;

public class ItemShard extends Item {

	public ItemShard() {
		super();
	}

	public static final String[] metaList = new String[] {
		Constants.ID_ITEM_SHARD_CRYSTALLINE_ENERGY,
		Constants.ID_ITEM_SHARD_CRYSTALLINE_CHAOS,
		Constants.ID_ITEM_SHARD_CRYSTALLINE_LIGHT,
		Constants.ID_ITEM_SHARD_CRYSTAL_CLEAR };

	private IIcon[] iconList;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs creativeTab, List list) {
		for (int i = 0; i < metaList.length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String getIconString() {
		return Constants.MOD_ID + ":lifeless_shard";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int damage) {
		return iconList[MathHelper.clamp_int(damage, 0, metaList.length - 1)];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister ir) {
		iconList = new IIcon[metaList.length];
		iconList[0] = ir.registerIcon(Constants.MOD_ID + ":shard_crystalline_energy");
		iconList[1] = ir.registerIcon(Constants.MOD_ID + ":shard_crystalline_chaos");
		iconList[2] = ir.registerIcon(Constants.MOD_ID + ":shard_crystalline_light");
		iconList[3] = ir.registerIcon(Constants.MOD_ID + ":shard_crystal_clear");
	}

	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		int idx = MathHelper.clamp_int(itemStack.getItemDamage(), 0, metaList.length - 1);
		return "item." + metaList[idx];
	}

	@Override
	public int getMetadata(int meta) {
		return MathHelper.clamp_int(meta, 0, metaList.length - 1);
	}

}
