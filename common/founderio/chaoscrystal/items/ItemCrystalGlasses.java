package founderio.chaoscrystal.items;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.chaoscrystal.Constants;

public class ItemCrystalGlasses extends ItemArmor {

	public ItemCrystalGlasses(int par1) {
		super(par1, EnumArmorMaterial.DIAMOND, 0, 0);
		this.setHasSubtypes(false);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon(Constants.MOD_ID
				+ ":crystalglasses");
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot,
			String type) {
		return Constants.MOD_ID + ":textures/models/armor/crystalglasses.png";
	}
}
