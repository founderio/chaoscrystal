package founderio.chaoscrystal;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import founderio.chaoscrystal.network.CCPParticle;

public class CommonProxy {

	public void registerRenderStuff() {

	}

	public static void spawnParticleEffects(int dimension, int effect,
			double sourceX, double sourceY, double sourceZ) {
		spawnParticleEffects(dimension, effect,
				sourceX + 0.5f, sourceY + 0.7f, sourceZ + 0.5f,
				0, 0, 0, 1.0f);
	}

	public static void spawnParticleEffects(Entity to, int effect) {
		spawnParticleEffects(to.worldObj.provider.dimensionId, effect,
				(double)to.posX, (double)to.posY, (double)to.posZ,
				0, 0, 0, 0.5f);
	}

	public static void spawnParticleEffects(Entity from, TileEntity to, int effect) {
		spawnParticleEffects(from.worldObj.provider.dimensionId, effect,
				to.xCoord, to.yCoord, to.zCoord,
				(double)(from.posX - to.xCoord), (double)(from.posY - to.yCoord), (double)(from.posZ - to.zCoord));
	}

	public static void spawnParticleEffects(Entity from, Entity to, int effect) {
		spawnParticleEffects(from.worldObj.provider.dimensionId, effect,
				(double)to.posX, (double)to.posY, (double)to.posZ,
				(double)(from.posX - to.posX), (double)(from.posY - to.posY), (double)(from.posZ - to.posZ));
	}

	public static void spawnParticleEffects(int dimension, int effect,
			double sourceX, double sourceY, double sourceZ,
			double offX, double offY, double offZ, float variation) {
		ChaosCrystalMain.packetPipeline.sendToAllAround(new CCPParticle(effect, sourceX, sourceY, sourceZ, offX, offY, offZ, variation),
				new TargetPoint(dimension, sourceX, sourceY, sourceZ, 32));
		
	}
	//TODO: Change to double!!
	public static void spawnParticleEffects(int dimension, int effect,
			double sourceX, double sourceY, double sourceZ,
			double offX, double offY, double offZ) {
		CommonProxy.spawnParticleEffects(dimension, effect, sourceX, sourceY, sourceZ, offX, offY, offZ, 1);
	}
}
