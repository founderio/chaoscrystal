package founderio.util;

import net.minecraft.item.ItemStack;

public final class ItemUtil {
	private ItemUtil() {
		// Util Class
	}
	

	
	public static boolean itemsMatch(ItemStack reference, ItemStack compare) {
		return compare != null && compare.itemID == reference.itemID && (reference.getItemDamage() == 32767 || reference.getItemDamage() == reference.getItemDamage());
	}
}
