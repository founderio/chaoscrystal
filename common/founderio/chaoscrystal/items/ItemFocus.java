package founderio.chaoscrystal.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.Constants;
import founderio.chaoscrystal.entities.EntityFocus;

public class ItemFocus extends Item {

	public ItemFocus(int par1) {
		super(par1);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon(Constants.MOD_ID + ":focus_transfer");
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer) {
		if(par2World.isRemote) {
			return new ItemStack(0, 0, 0);
		}
		
		EntityFocus entity = new EntityFocus(par2World, par3EntityPlayer.posX, par3EntityPlayer.posY + 1.9f, par3EntityPlayer.posZ, par3EntityPlayer.cameraYaw, par3EntityPlayer.cameraPitch);
		//TODO: set type depending on meta
		
		par2World.spawnEntityInWorld(entity);
		//entity.playSpawnSound();
		
		return new ItemStack(ChaosCrystalMain.itemFocus, 0);
	}
	
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		switch(par1ItemStack.getItemDamage()) {
		case 0:
			par3List.add("Focus: Transfer");
			break;
		case 1:
			par3List.add("Focus: Border");
			break;
		case 2:
			par3List.add("Focus: Filter");
			break;
		default:
			par3List.add("Focus: Unknown");
			break;
		}
		
		
	}
	
}
