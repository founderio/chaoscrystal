package founderio.chaoscrystal.entities;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.Config;
import founderio.util.GeometryHelper;

public class EntityFocusBorder extends EntityFocus {

	public EntityFocusBorder(World world) {
		super(world);
	}

	public EntityFocusBorder(World world, double x, double y, double z,
			float yaw, float pitch) {
		super(world, x, y, z, yaw, pitch);
	}

	@Override
	public ItemStack buildItemStack() {
		ItemStack is = new ItemStack(ChaosCrystalMain.itemFocus, 1, 1);
		return is;
	}

	@Override
	protected void logicUpdate() {
		if (age > Config.focusTickInterval) {
			age = 0;
		}
		EntityChaosCrystal crystal1 = null;
		double dist = Double.MAX_VALUE;

		for (Object obj : worldObj.loadedEntityList) {
			if (obj instanceof EntityChaosCrystal) {
				double tmp_dist = GeometryHelper.entityDistance((EntityChaosCrystal) obj, this);
				if (tmp_dist < dist) {
					crystal1 = (EntityChaosCrystal) obj;
					dist = tmp_dist;
				}
			}
		}

		if (crystal1 != null) {
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
}
