package founderio.chaoscrystal.entities;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.Config;
import founderio.chaoscrystal.aspects.Targets;
import founderio.util.GeometryHelper;

public class EntityFocusFilterTarget extends EntityFocus {

	public EntityFocusFilterTarget(World world) {
		super(world);
	}

	public EntityFocusFilterTarget(World world, double x, double y, double z,
			float yaw, float pitch) {
		super(world, x, y, z, yaw, pitch);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(30, Targets.TARGET_ALL);
	}

	public String getTarget() {
		return dataWatcher.getWatchableObjectString(30);
	}

	public void setTarget(String target) {
		dataWatcher.updateObject(30, target);
	}

	@Override
	public ItemStack buildItemStack() {
		ItemStack is = new ItemStack(ChaosCrystalMain.itemFocus, 1, 3);
		return is;
	}

	@Override
	protected void logicUpdate() {
		if (age > Config.focusTickInterval) {
			age = 0;
		}
		List<EntityChaosCrystal> crystals = new ArrayList<EntityChaosCrystal>();

		for (Object obj : worldObj.loadedEntityList) {
			if (obj instanceof EntityChaosCrystal) {
				double tmp_dist = GeometryHelper.entityDistance(
						(EntityChaosCrystal) obj, this);
				if (tmp_dist < Config.focusRange) {
					crystals.add((EntityChaosCrystal) obj);
				}
			}
		}

		if (!crystals.isEmpty()) {
			EntityChaosCrystal crystal1 = crystals.get(rand.nextInt(crystals
					.size()));
			lookX = (float) crystal1.posX;
			lookY = (float) (crystal1.boundingBox.minY + crystal1.boundingBox.maxY) / 2.0F;
			lookZ = (float) crystal1.posZ;
			updateLook();
		} else if (age == 0) {
			lookX = (float) posX + (rand.nextFloat() - 0.5f) * 10;
			lookY = (float) posY;
			lookZ = (float) posZ + (rand.nextFloat() - 0.5f) * 10;
			updateLook();
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		String target = nbttagcompound.getString("target");
		if (!Targets.isTarget(target)) {
			target = Targets.TARGETS[0];
		}
		setTarget(target);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setString("target", getTarget());
	}

}
