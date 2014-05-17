package founderio.chaoscrystal.worldgen;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.Constants;

public class GenCrystalFloats implements IWorldGenerator {

	public static final ArrayList<String> allowedBiomes = new ArrayList<String>();

	static {
		allowedBiomes.add(Constants.NAME_BIOME_CRYSTAL);
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		int count = 1 + random.nextInt(3);

		for (int i = 0; i < count; i++) {
			int meta = random.nextInt(2);
			int x = random.nextInt(16);
			int z = random.nextInt(16);
			int xAbsolute = chunkX * 16 + x;
			int zAbsolute = chunkZ * 16 + z;
			if (allowedBiomes.contains(world.getBiomeGenForCoords(xAbsolute,
					zAbsolute).biomeName)) {
				int yBase = world
						.getTopSolidOrLiquidBlock(xAbsolute, zAbsolute);
				if (yBase < world.getActualHeight() - 80) {
					yBase += random.nextInt(world.getActualHeight() - yBase
							- 50);
					int width = 3 + random.nextInt(8);

					for (int y = -width / 2; y < width / 2; y++) {
						for (int zscroll = -width / 2; zscroll < width / 2; zscroll++) {
							for (int xscroll = -width / 2; xscroll < width / 2; xscroll++) {

								if (zscroll * zscroll + xscroll * xscroll + y
										* y < width) {
									world.setBlock(xAbsolute + xscroll, y
											+ yBase, zAbsolute + zscroll,
											ChaosCrystalMain.blockBase,
											meta, 3);
								}
							}
						}
					}
				}

			}
		}

		count = 1 + random.nextInt(3);

		for (int i = 0; i < count; i++) {
			int meta = 2;
			int x = random.nextInt(16);
			int z = random.nextInt(16);
			int xAbsolute = chunkX * 16 + x;
			int zAbsolute = chunkZ * 16 + z;
			if (allowedBiomes.contains(world.getBiomeGenForCoords(xAbsolute,
					zAbsolute).biomeName)) {
				int yBase = 10;
				yBase += random.nextInt(world.getActualHeight() - yBase - 50);

				world.setBlock(xAbsolute, yBase, zAbsolute,
						ChaosCrystalMain.blockBase, meta, 3);
			}
		}
	}

}
