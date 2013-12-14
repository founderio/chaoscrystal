package founderio.chaoscrystal.items;

import founderio.chaoscrystal.Constants;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class ItemCrystalGlasses extends ItemArmor {

	public ItemCrystalGlasses(int par1) {
		super(par1, EnumArmorMaterial.DIAMOND, 0, 0);
		this.setHasSubtypes(false);
		this.setCreativeTab(CreativeTabs.tabTools);
	}

	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon(Constants.MOD_ID + ":crystalglasses");
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot,
			String type) {
		return Constants.MOD_ID + ":textures/models/armor/crystalglasses.png";
	}
}
