package founderio.util;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

public final class GeometryHelper {
	
	private GeometryHelper() {
		// Util class
	}
	
	public static double entityDistance(Entity e1, Entity e2) {
		double distX = e1.posX - e2.posX;
		double distY = e1.posY - e2.posY;
		double distZ = e1.posZ - e2.posZ;
		return Math.sqrt(distX*distX + distY*distY + distZ*distZ);
	}
	
	public static double entityDistance(TileEntity e1, Entity e2) {
		double distX = e1.xCoord - e2.posX;
		double distY = e1.yCoord - e2.posY;
		double distZ = e1.zCoord - e2.posZ;
		return Math.sqrt(distX*distX + distY*distY + distZ*distZ);
	}
}
