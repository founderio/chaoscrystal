package founderio.chaoscrystal.items;

import java.util.List;

import net.minecraft.client.Minecraft;
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
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon(Constants.MOD_ID
				+ ":manual");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		ItemStack helmet = Minecraft.getMinecraft().thePlayer.inventory.armorInventory[3];
		if (helmet == null
				|| helmet.itemID != ChaosCrystalMain.itemCrystalGlasses.itemID) {
			par3List.add("You see a lot of written text");
			par3List.add("on this stone tablet but you");
			par3List.add("can't decipher it. Maybe some");
			par3List.add("crystal glasses might help?");
		}
	}
}
