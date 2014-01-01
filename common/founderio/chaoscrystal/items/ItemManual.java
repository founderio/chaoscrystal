package founderio.chaoscrystal.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.Constants;

public class ItemManual extends ItemMap {

	public ItemManual(int par1) {
		super(par1);
		this.setMaxStackSize(1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister ir) {
		this.itemIcon = ir.registerIcon(Constants.MOD_ID + ":manual");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer player,
			List info, boolean additionalInfoEnabled) {
		ItemStack helmet;
		if (player == null) {
			helmet = null;
		} else {
			helmet = player.inventory.armorInventory[3];
		}
		if (helmet == null
				|| helmet.itemID != ChaosCrystalMain.itemCrystalGlasses.itemID) {
			info.add("You see a lot of written text");
			info.add("on this stone tablet but you");
			info.add("can't decipher it. Maybe some");
			info.add("crystal glasses might help?");
		} else {
			info.add("Hold shift and use the mouse wheel to turn pages.");
		}
	}
}
