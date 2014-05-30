package founderio.chaoscrystal.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.chaoscrystal.Constants;
import founderio.chaoscrystal.IModeChangingItem;
import founderio.chaoscrystal.aspects.Aspects;
import founderio.chaoscrystal.aspects.Targets;
import founderio.chaoscrystal.entities.EntityFocusBorder;
import founderio.chaoscrystal.entities.EntityFocusFilter;
import founderio.chaoscrystal.entities.EntityFocusFilterTarget;
import founderio.chaoscrystal.entities.EntityFocusTransfer;
import founderio.chaoscrystal.machinery.IItemModule;
import founderio.chaoscrystal.machinery.IModule;
import founderio.chaoscrystal.machinery.modules.ModuleTargetFilter;

public class ItemFocus extends Item implements IModeChangingItem, IItemModule {

	public ItemFocus() {
		super();
		this.setHasSubtypes(true);
		this.setMaxStackSize(16);
	}

	@SideOnly(Side.CLIENT)
	IIcon[] iconList;
	
	@Override
	public int getSelectedModeForItemStack(ItemStack is) {
		NBTTagCompound tags = is.getTagCompound();
		int mode;
		if(tags == null) {
			tags = new NBTTagCompound();
		}
		switch(is.getItemDamage()) {
		case 2:
			String selectedAspect = tags.getString("aspect");
			mode = Aspects.getAspectIndex(selectedAspect);
			if(mode == -1) {
				mode = 0;
			}
			return mode;
		case 3:
			String selectedTarget = tags.getString("target");
			mode = Targets.getTargetIndex(selectedTarget);
			if(mode == -1) {
				mode = 0;
			}
			return mode;
		default:
			return 0;
		}
	}

	@Override
	public void setSelectedModeForItemStack(ItemStack is, int mode) {
		NBTTagCompound tags = is.getTagCompound();
		if (tags == null) {
			tags = new NBTTagCompound();
		}
		switch (is.getItemDamage()) {
		case 2:
			tags.setString("aspect", Aspects.ASPECTS[mode]);
			is.setTagCompound(tags);
			break;
		case 3:
			tags.setString("target", Targets.TARGETS[mode]);
			is.setTagCompound(tags);
			break;
		default:
			return;
		}
	}

	@Override
	public int getModeCount(ItemStack is) {
		switch (is.getItemDamage()) {
		case 2:
			return Aspects.ASPECTS.length;
		case 3:
			return Targets.TARGETS.length;
		default:
			return 0;
		}
	}

	@Override
	public ResourceLocation getIconForMode(ItemStack is, int mode) {
		switch (is.getItemDamage()) {
		case 2:
			return Aspects.RESOURCE_LOCATIONS[mode];
		case 3:
			return Targets.RESOURCE_LOCATIONS[mode];
		default:
			return null;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister ri) {
		iconList = new IIcon[4];
		iconList[0] = ri.registerIcon(Constants.MOD_ID + ":focus_transfer");
		iconList[1] = ri.registerIcon(Constants.MOD_ID + ":focus_border");
		iconList[2] = ri.registerIcon(Constants.MOD_ID + ":focus_filter");
		iconList[3] = ri.registerIcon(Constants.MOD_ID + ":focus_filter_type");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int meta) {
		return iconList[MathHelper.clamp_int(meta, 0, 4)];
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world,
			EntityPlayer player) {
		if (!world.isRemote) {
			if (itemStack.getItemDamage() == 0) {
				EntityFocusTransfer entity = new EntityFocusTransfer(world,
						player.posX, player.posY + 3,
						player.posZ,
						180f - player.rotationYaw,
						player.rotationPitch);
				world.spawnEntityInWorld(entity);
			} else if (itemStack.getItemDamage() == 1) {
				EntityFocusBorder entity = new EntityFocusBorder(world,
						player.posX, player.posY + 3,
						player.posZ,
						180f - player.rotationYaw,
						player.rotationPitch);
				world.spawnEntityInWorld(entity);
			} else if (itemStack.getItemDamage() == 2) {
				EntityFocusFilter entity = new EntityFocusFilter(world,
						player.posX, player.posY + 3,
						player.posZ,
						180f - player.rotationYaw,
						player.rotationPitch);
				NBTTagCompound tags = itemStack.getTagCompound();
				if (tags != null) {
					String aspect = tags.getString("aspect");
					if (!Aspects.isAspect(aspect)) {
						aspect = Aspects.ASPECTS[0];
					}
					entity.setAspect(aspect);
				} else {
					entity.setAspect(Aspects.ASPECTS[0]);
				}
				// System.out.println("Spawning Entity with aspect: " +
				// entity.aspect);
				world.spawnEntityInWorld(entity);
			} else if (itemStack.getItemDamage() == 3) {
				EntityFocusFilterTarget entity = new EntityFocusFilterTarget(world,
						player.posX, player.posY + 3,
						player.posZ,
						180f - player.rotationYaw,
						player.rotationPitch);
				NBTTagCompound tags = itemStack.getTagCompound();
				if (tags != null) {
					String target = tags.getString("target");
					if (!Targets.isTarget(target)) {
						target = Targets.TARGETS[0];
					}
					entity.setTarget(target);
				} else {
					entity.setTarget(Targets.TARGETS[0]);
				}
				// System.out.println("Spawning Entity with aspect: " +
				// entity.aspect);
				world.spawnEntityInWorld(entity);
			}
			// entity.playSpawnSound();
		}
		ItemStack retVal = itemStack.copy();
		retVal.stackSize--;
		return retVal;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs creativeTab,
			List list) {
		for (int meta = 0; meta < 4; meta++) {
			list.add(new ItemStack(item, 1, meta));
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		NBTTagCompound tags = par1ItemStack.getTagCompound();
		switch (par1ItemStack.getItemDamage()) {
		case 0:
			par3List.add("Transfer");
			break;
		case 1:
			par3List.add("Border");
			break;
		case 2:
			par3List.add("Aspect Filter");
			String selectedAspect;
			
			if (tags != null) {
				selectedAspect = tags.getString("aspect");
				if (!Aspects.isAspect(selectedAspect)) {
					selectedAspect = Aspects.ASPECTS[0];
				}
			} else {
				selectedAspect = Aspects.ASPECTS[0];
			}
			par3List.add("Aspect: "
					+ StatCollector.translateToLocal(Constants.MOD_ID
							+ ".aspect." + selectedAspect));
			break;
		case 3:
			par3List.add("Target Filter");
			String selectedTarget;
			if (tags != null) {
				selectedTarget = tags.getString("target");
				if (!Targets.isTarget(selectedTarget)) {
					selectedTarget = Targets.TARGETS[0];
				}
			} else {
				selectedTarget = Targets.TARGETS[0];
			}
			par3List.add("Target: "
					+ StatCollector.translateToLocal(Constants.MOD_ID
							+ ".target." + selectedTarget));
			break;
		default:
			par3List.add("Unknown");
			break;
		}
	}

	@Override
	public IModule getModuleFromItemStack(ItemStack is) {
		if(is == null || is.getItemDamage() != 3) {
			return null;
		} else {
			ModuleTargetFilter mtf = new ModuleTargetFilter();
			if(is.getTagCompound() != null) {
				mtf.targets = is.getTagCompound().getString("target");
			}
			if (!Targets.isTarget(mtf.targets)) {
				mtf.targets = Targets.TARGETS[0];
			}
			return mtf;
		}
	}

	@Override
	public ItemStack getItemStackFromModule(IModule module) {
		if(module instanceof ModuleTargetFilter) {
			ItemStack is = new ItemStack(this, 1, 3);
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("target", ((ModuleTargetFilter) module).targets);
			is.setTagCompound(nbt);
			return is;
		} else {
			return null;
		}
	}

	

}
