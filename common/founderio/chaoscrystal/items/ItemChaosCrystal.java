package founderio.chaoscrystal.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.Constants;
import founderio.chaoscrystal.aspects.Aspect;
import founderio.chaoscrystal.entities.EntityChaosCrystal;

public class ItemChaosCrystal extends Item {

	public ItemChaosCrystal() {
		super();
		setMaxStackSize(1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack par1ItemStack, int pass) {
		return pass == 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String getIconString() {
		return Constants.MOD_ID + ":chaoscrystal";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		itemIcon = par1IconRegister.registerIcon(Constants.MOD_ID
				+ ":chaoscrystal");
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer) {
		if (par2World.isRemote) {
			return new ItemStack(Blocks.air, 0, 0);
		}

		EntityChaosCrystal entity = new EntityChaosCrystal(par2World,
				par3EntityPlayer.posX, par3EntityPlayer.posY + 2,
				par3EntityPlayer.posZ, 0f, 0f);
		if (par1ItemStack.getTagCompound() != null) {
			NBTTagCompound aspectStore = par1ItemStack.getTagCompound()
					.getCompoundTag("aspectStore");
			if (aspectStore != null) {
				for (Aspect aspect : Aspect.values()) {
					entity.setAspect(aspect, aspectStore.getInteger(aspect.stringRep));
				}
			}
		}
		entity.setSuckMode(par3EntityPlayer.isSneaking());

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
		if (tag == null) {
			par3List.add("This ChaosCrystal is new and empty.");
			return;
		}
		NBTTagCompound aspectStore = tag.getCompoundTag("aspectStore");
		if (aspectStore == null) {
			par3List.add("This ChaosCrystal is used and empty.");
			return;
		}
		boolean hasAspects = false;
		for (Aspect aspect : Aspect.values()) {
			int asp = aspectStore.getInteger(aspect.stringRep);
			if (asp > 0) {
				hasAspects = true;

				par3List.add(String.format(
						"%s: %d",
						StatCollector.translateToLocal(Constants.MOD_ID
								+ ".aspect." + aspect), asp));
			}
		}
		if (!hasAspects) {
			par3List.add("This ChaosCrystal has no aspects stored.");
		}

	}

}
