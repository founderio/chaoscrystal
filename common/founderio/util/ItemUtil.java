package founderio.util;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public final class ItemUtil {
	private ItemUtil() {
		// Util Class
	}

	public static boolean itemsMatch(ItemStack reference, ItemStack compare) {
		return compare != null
				&& compare.itemID == reference.itemID
				&& (reference.getItemDamage() == 32767 || reference
						.getItemDamage() == compare.getItemDamage());
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

}
