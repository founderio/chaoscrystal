package founderio.chaoscrystal.machinery.modules;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import founderio.chaoscrystal.aspects.HostilityLevel;
import founderio.chaoscrystal.aspects.Targets;
import founderio.chaoscrystal.machinery.IModule;
import founderio.chaoscrystal.machinery.IModuleTarget;

public class ModuleTargetFilter implements IModule, IModuleTarget {

	public String targets;

	@Override
	public boolean isTargetValid(Entity target, HostilityLevel hostilityLevel) {
		if(Targets.TARGET_ALL.equals(targets)) {
			return true;
		} else if(Targets.TARGET_ENTITY.equals(targets)) {
			return true;
		} else if(Targets.TARGET_ENTITY_CREATURES.equals(targets)) {
			return target instanceof EntityLiving;
		} else if(Targets.TARGET_ENTITY_ANIMALS.equals(targets)) {
			return target instanceof EntityAnimal;
		} else if(Targets.TARGET_ENTITY_MOBS.equals(targets)) {
			return target instanceof EntityMob;
		} else if(Targets.TARGET_ENTITY_AMBIENT.equals(targets)) {
			return target instanceof EntityAmbientCreature;
		} else if(Targets.TARGET_ENTITY_PLAYERS.equals(targets)) {
			return target instanceof EntityPlayer;
		} else if(Targets.TARGET_GUARD_MODE.equals(targets)) {
			return hostilityLevel == HostilityLevel.DocileKnownAgressive;
		} else if(Targets.TARGET_GUARD_MODE_L2.equals(targets)) {
			return hostilityLevel == HostilityLevel.HostileKnownAgressive;
		} else if(Targets.TARGET_ITEMS.equals(targets)) {
			return target instanceof EntityItem;
		}
		return false;
	}
}
