package founderio.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;

public final class GeometryHelper {

	private GeometryHelper() {
		// Util class
	}

	public static double entityDistance(Entity e1, Entity e2) {
		double distX = e1.posX - e2.posX;
		double distY = e1.posY - e2.posY;
		double distZ = e1.posZ - e2.posZ;
		return Math.sqrt(distX * distX + distY * distY + distZ * distZ);
	}

	public static double entityDistance(TileEntity e1, Entity e2) {
		double distX = e1.xCoord - e2.posX;
		double distY = e1.yCoord - e2.posY;
		double distZ = e1.zCoord - e2.posZ;
		return Math.sqrt(distX * distX + distY * distY + distZ * distZ);
	}

	public static boolean isEntityInBlock(Entity ent, boolean fluidsOnly) {
		double d0 = ent.posY + ent.getEyeHeight();
		int i = MathHelper.floor_double(ent.posX);
		int j = MathHelper.floor_float(MathHelper.floor_double(d0));
		int k = MathHelper.floor_double(ent.posZ);
		Block block = ent.worldObj.getBlock(i, j, k);

		double filled = 1.0f; // If it's not a liquid assume it's a solid block
		if (block instanceof IFluidBlock) {
			filled = ((IFluidBlock) block).getFilledPercentage(ent.worldObj, i,
					j, k);
			System.out.println("Fluid");
		} else if(block instanceof BlockLiquid) {
			filled = 1.0f;
		} else if (fluidsOnly) {
			return false;
		}

		if (filled < 0) {
			filled *= -1;
			return d0 > j + (1 - filled);
		} else {
			return d0 < j + filled;
		}
	}

	public static <T extends Entity> List<T> getEntitiesInRange(World world, double posX, double posY, double posZ, double range, Class<T> entityClass) {
		List<T> entities = new ArrayList<T>();

		for (Object obj : world.loadedEntityList) {
			if(!(obj instanceof Entity)) {
				continue;
			}
			Entity ent = (Entity) obj;
			if (entityClass.isAssignableFrom(ent.getClass())) {
				double tmp_dist = ent.getDistance(posX, posY, posZ);
				if (tmp_dist < range) {
					entities.add(entityClass.cast(ent));
				}
			}
		}
		return entities;
	}
}
