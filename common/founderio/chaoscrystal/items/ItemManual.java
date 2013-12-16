package founderio.chaoscrystal.items;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemMap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.chaoscrystal.Constants;

public class ItemManual extends ItemMap {

	public ItemManual(int par1) {
		super(par1);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon(Constants.MOD_ID + ":manual");
	}

}
