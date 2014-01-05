package founderio.chaoscrystal.machinery;

import net.minecraft.item.ItemStack;

public interface IItemModule {
	IModule getModuleFromItemStack(ItemStack is);

	ItemStack getItemStackFromModule(IModule module);
}
