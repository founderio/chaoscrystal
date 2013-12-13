package founderio.chaoscrystal.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.chaoscrystal.Constants;
import founderio.chaoscrystal.entities.EntityChaosCrystal;

public class ItemChaosCrystal extends Item {

	public ItemChaosCrystal(int par1) {
		super(par1);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon(Constants.MOD_ID + ":chaoscrystal");
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer) {
		if(par2World.isRemote) {
			return new ItemStack(0, 0, 0);
		}
		
		par2World.spawnEntityInWorld(new EntityChaosCrystal(par2World, par3EntityPlayer.posX, par3EntityPlayer.posY + 1.9f, par3EntityPlayer.posZ));
		
		return new ItemStack(0, 0, 0);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		NBTTagCompound tag = par1ItemStack.getTagCompound();
		if(tag == null) {
			par3List.add("This ChaosCrystal is new and empty.");
			return;
		}
		NBTTagCompound aspectStore = tag.getCompoundTag("aspectStore");
		if(aspectStore == null) {
			par3List.add("This ChaosCrystal is used and empty.");
			return;
		}
		par3List.add(String.format("Water: %d", aspectStore.getInteger("water")));
	}
	
}
