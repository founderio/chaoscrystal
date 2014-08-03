package founderio.chaoscrystal.blockbase;

import net.minecraft.item.ItemStack;
import founderio.chaoscrystal.machinery.IModule;

public interface IModuleHost {
	public int getSizeModules();
	public IModule getModule(int index);
	public ItemStack getModuleItemStack(int index);
}
