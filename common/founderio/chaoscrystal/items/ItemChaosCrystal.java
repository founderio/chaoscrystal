package founderio.chaoscrystal.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.Constants;
import founderio.chaoscrystal.degradation.Aspects;
import founderio.chaoscrystal.degradation.DegradationHelper;
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
		
		EntityChaosCrystal entity = new EntityChaosCrystal(par2World, (int)par3EntityPlayer.posX, (int)par3EntityPlayer.posY + 2, (int)par3EntityPlayer.posZ);
		if(par1ItemStack.getTagCompound() != null) {
			entity.aspectStore = par1ItemStack.getTagCompound().getCompoundTag("aspectStore");
			if (entity.aspectStore == null) {
				entity.aspectStore = new NBTTagCompound();
			}
			entity.isInSuckMode = DegradationHelper.isAspectStoreEmpty(entity.aspectStore);
		} else {
			entity.isInSuckMode = true;
		}
		
		
		par2World.spawnEntityInWorld(entity);
		entity.playSpawnSound();
		
		return new ItemStack(ChaosCrystalMain.itemChaosCrystal, 0);
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
		boolean hasAspects = false;
		for(String aspect : Aspects.ASPECTS) {
			int asp = aspectStore.getInteger(aspect);
			if(asp > 0) {
				hasAspects = true;
				
				par3List.add(String.format("%s: %d", StatCollector.translateToLocal(Constants.MOD_ID + ".aspect." + aspect), asp));
			}
		}
		if(!hasAspects) {
			par3List.add("This ChaosCrystal has no aspects stored.");
		}
		
	}
	
}
