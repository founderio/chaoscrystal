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

	private IIcon[] iconList;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs creativeTab, List list) {
		for (int i = 0; i < Constants.METALIST_ITEM_SHARD.length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String getIconString() {
		return Constants.MOD_ID + ":shard_crystalline_energy";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int damage) {
		if(iconList == null) {
			return null;
		}
		return iconList[getMetadata(damage)];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister ir) {
		iconList = new IIcon[Constants.METALIST_ITEM_SHARD.length];
		iconList[0] = ir.registerIcon(Constants.MOD_ID + ":shard_crystalline_energy");
		iconList[1] = ir.registerIcon(Constants.MOD_ID + ":shard_crystalline_chaos");
		iconList[2] = ir.registerIcon(Constants.MOD_ID + ":shard_crystalline_light");
		iconList[3] = ir.registerIcon(Constants.MOD_ID + ":shard_crystal_clear");
	}

	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		int idx = getMetadata(itemStack.getItemDamage());
		return super.getUnlocalizedName(itemStack) + "." + Constants.METALIST_ITEM_SHARD[idx];
	}

	@Override
	public int getMetadata(int meta) {
		return MathHelper.clamp_int(meta, 0, Constants.METALIST_ITEM_SHARD.length - 1);
	}

}
