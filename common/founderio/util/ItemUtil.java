package founderio.util;

import founderio.chaoscrystal.ChaosCrystalMain;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public final class ItemUtil {
	private ItemUtil() {
		// Util Class
	}

	public static boolean itemsMatch(ItemStack reference, ItemStack compare) {
		if(reference == null) {
			return compare == null;
		}
		if(compare == null) {
			return reference == null;
		}
		Item a = compare.getItem();
		Item b = reference.getItem();
		if(a != b) {
			return false;
		}
		if(a == null) {
			return true;
		}
		return reference.getItemDamage() == 32767 || compare.getItemDamage() == 32767 ||
				reference.getItemDamage() == compare.getItemDamage();
	}

	public static void spawnItemStack(ItemStack is, World world,
			EntityItem reference) {
		EntityItem item = new EntityItem(world, reference.posX, reference.posY,
				reference.posZ, is);
		item.motionX = reference.motionX;
		item.motionY = reference.motionY;
		item.motionZ = reference.motionZ;
		item.prevPosX = reference.prevPosX;
		item.prevPosY = reference.prevPosY;
		item.prevPosZ = reference.prevPosZ;
		item.yOffset = reference.yOffset;
		item.hoverStart = reference.hoverStart;
		item.age = reference.age;
		item.lifespan = reference.lifespan;
		item.delayBeforeCanPickup = reference.delayBeforeCanPickup;

		world.spawnEntityInWorld(item);
	}

	public static void spawnItemStack(ItemStack is, World world, double posX,
			double posY, double posZ) {
		EntityItem item = new EntityItem(world, posX, posY, posZ, is);

		world.spawnEntityInWorld(item);
	}
	
	public static void spawnItemStackDropped(ItemStack is, World world, int x, int y, int z) {
		float f = ChaosCrystalMain.rand.nextFloat() * 0.8F + 0.1F;
		float f1 = ChaosCrystalMain.rand.nextFloat() * 0.8F + 0.1F;
		EntityItem entityitem;

		for (float f2 = ChaosCrystalMain.rand.nextFloat() * 0.8F + 0.1F; is.stackSize > 0; world.spawnEntityInWorld(entityitem)) {
			int k1 = ChaosCrystalMain.rand.nextInt(21) + 10;

			if (k1 > is.stackSize) {
				k1 = is.stackSize;
			}

			is.stackSize -= k1;
			entityitem = new EntityItem(world, (double) ((float) x + f),
					(double) ((float) y + f1), (double) ((float) z + f2),
					new ItemStack(is.getItem(), k1, is.getItemDamage()));
			float f3 = 0.05F;
			entityitem.motionX = (double) ((float) ChaosCrystalMain.rand
					.nextGaussian() * f3);
			entityitem.motionY = (double) ((float) ChaosCrystalMain.rand
					.nextGaussian() * f3 + 0.2F);
			entityitem.motionZ = (double) ((float) ChaosCrystalMain.rand
					.nextGaussian() * f3);

			if (is.hasTagCompound()) {
				entityitem.getEntityItem().setTagCompound(
						(NBTTagCompound) is.getTagCompound().copy());
			}
		}
	}

}
