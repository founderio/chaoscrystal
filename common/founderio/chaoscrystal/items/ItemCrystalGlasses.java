package founderio.chaoscrystal.items;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.chaoscrystal.Constants;

public class ItemCrystalGlasses extends ItemArmor {

	public ItemCrystalGlasses() {
		super(ArmorMaterial.DIAMOND, 0, 0);
		this.setHasSubtypes(false);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String getIconString() {
		return Constants.MOD_ID + ":crystalglasses";
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot,
			String type) {
		return Constants.MOD_ID + ":textures/models/armor/crystalglasses.png";
	}
}
