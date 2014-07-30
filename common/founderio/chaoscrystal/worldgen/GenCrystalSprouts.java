package founderio.chaoscrystal.worldgen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.Config;
import founderio.chaoscrystal.blocks.BlockBase;

public class GenCrystalSprouts implements IWorldGenerator {
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {

		if(!Config.generateCrystalSprouts) {
			return;
		}

		if(Config.generateCrystalSproutsOverworldOnly && world.provider.dimensionId != 0) {
			return;
		}

		boolean generate = random.nextInt(200) > 198;

		if (!generate) {
			return;
		}




		int x = random.nextInt(16);
		int z = random.nextInt(16);
		int xAbsolute = chunkX * 16 + x;
		int zAbsolute = chunkZ * 16 + z;

		BiomeGenBase biome = world.getBiomeGenForCoords(xAbsolute, zAbsolute);
		int meta = 3; // Default to glass

		int[] chances = new int[] {10, 10, 10, 10};

		if(biome.temperature > 0.6f) {
			chances[0] += 5;//energy
			chances[2] += 5;//light
		}

		if(biome.temperature > 0.8f) {
			chances[0] += 5;//energy
			chances[2] += 8;//light
		}

		if(biome.getSpawningChance() > 0.3f) {
			chances[1] += 5;//chaos
		}

		if(biome.isHighHumidity()) {
			chances[2] -= 5;//light
		}

		if(world.provider.isHellWorld) {
			chances[1] += 20;//chaos
		}
		if(world.provider.hasNoSky) {
			chances[2] = 0;//light
		}

		int max = 0;

		for(int i = 0; i < chances.length; i++) {
			if(chances[i] < 0) {
				chances[i] = 0;
			}
			max += chances[i];
		}
		if(max > 0) {
			int choice = random.nextInt(max + 1);
			int acc = 0;
			for(int i = 0; i < chances.length; i++) {
				acc += chances[i];
				if(acc >= choice) {
					meta = i;
					break;
				}
			}
		}

		int ySpawnMax = 50;
		int ySpawnMin = 20;

		if(meta == 2) {
			ySpawnMax = 60;
			ySpawnMin = 45;
		}

		// Convert from regular to sprout
		meta += 8;
		
		int yMax = Math.min(ySpawnMax, world.getTopSolidOrLiquidBlock(xAbsolute, zAbsolute));
		int yAbsolute = ySpawnMin + random.nextInt(yMax - ySpawnMin);

		Block bl = world.getBlock(xAbsolute, yAbsolute, zAbsolute);

		if(BlockBase.isAcceptedBlock(bl)) {
			// Set the sprout block
			world.setBlock(xAbsolute, yAbsolute, zAbsolute,
					ChaosCrystalMain.blockBase, meta, 1 + 3);
			// Pregenerate some of the crystal
			for(int i = 0; i < 5; i++) {
				ChaosCrystalMain.blockBase.updateTick(world, xAbsolute, yAbsolute, zAbsolute, random);
			}
		}
	}
}
