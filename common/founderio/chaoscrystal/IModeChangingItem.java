package founderio.chaoscrystal;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public interface IModeChangingItem {
	int getSelectedModeForItemStack(ItemStack is);
	void setSelectedModeForItemStack(ItemStack is, int mode);
	int getModeCount(ItemStack is);
	ResourceLocation getIconForMode(ItemStack is, int mode);
}
