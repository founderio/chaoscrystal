package founderio.chaoscrystal.items;

import founderio.chaoscrystal.Constants;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;

public class ItemCrystalGlasses extends ItemArmor {

	public ItemCrystalGlasses(int par1) {
		super(par1, EnumArmorMaterial.DIAMOND, 0, 0);
		this.setHasSubtypes(false);
	}

	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon(Constants.MOD_ID + ":crystalglasses");
	}
}
