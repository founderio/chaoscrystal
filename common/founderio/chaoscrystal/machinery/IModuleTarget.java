package founderio.chaoscrystal.machinery;

import net.minecraft.entity.Entity;
import founderio.chaoscrystal.aspects.HostilityLevel;

public interface IModuleTarget extends IModule {
	public boolean isTargetValid(Entity target, HostilityLevel hostilityLevel);
}
