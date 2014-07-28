package founderio.chaoscrystal;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import founderio.chaoscrystal.network.CCPParticle;

public class CommonProxy {

	public void registerRenderStuff() {
		// Implemented in ClientProxy
	}

	public static void spawnParticleEffects(int dimension, int effect,
			double sourceX, double sourceY, double sourceZ) {
		spawnParticleEffects(dimension, effect,
				sourceX + 0.5f, sourceY + 0.7f, sourceZ + 0.5f,
				0, 0, 0, 1.0f);
	}

	public static void spawnParticleEffects(Entity at, int effect) {
		spawnParticleEffects(at.worldObj.provider.dimensionId, effect,
				at.posX, at.posY, at.posZ,
				0, 0, 0, 0.5f);
	}

	public static void spawnParticleEffects(Entity from, TileEntity to, int effect) {
		spawnParticleEffects(from.worldObj.provider.dimensionId, effect,
				from.posX, from.posY, from.posZ,
				to.xCoord, to.yCoord, to.zCoord,
				1);
	}

	public static void spawnParticleEffects(Entity from, Entity to, int effect) {
		spawnParticleEffects(from.worldObj.provider.dimensionId, effect,
				from.posX, from.posY, from.posZ,
				to.posX, to.posY, to.posZ,
				1);
	}

	public static void spawnParticleEffects(int dimension, int effect,
			double sourceX, double sourceY, double sourceZ,
			double targetX, double targetY, double targetZ) {
		spawnParticleEffects(dimension, effect, sourceX, sourceY, sourceZ,
				targetX, targetY, targetZ, 1);
	}

	public static void spawnParticleEffects(int dimension, int effect,
			double sourceX, double sourceY, double sourceZ,
			double offX, double offY, double offZ,
			float variation) {
		ChaosCrystalMain.network.sendToAllAround(new CCPParticle(effect, sourceX, sourceY, sourceZ, offX, offY, offZ, variation),
				new TargetPoint(dimension, sourceX, sourceY, sourceZ, 32));

	}

	public void spawnParticleEntity(World world, int type,
			double posX, double posY, double posZ,
			double offX, double offY, double offZ,
			float variation) {
		// Implemented in ClientProxy
	}
}
