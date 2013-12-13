package founderio.chaoscrystal.worldgen;

import java.util.ArrayList;
import java.util.Random;

import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.Constants;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;

public class GenCrystalPillars implements IWorldGenerator {

	public static final ArrayList<String> allowedBiomes = new ArrayList<String>();
	
	static{
		allowedBiomes.add(Constants.ID_BIOME_CRYSTAL);
	}
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		int count =1;// 5 + random.nextInt(10);
		
		for(int i = 0; i < count; i++) {
			int x = random.nextInt(16);
			int z = random.nextInt(16);
			int xAbsolute = chunkX * 16 + x;
			int zAbsolute = chunkZ * 16 + z;
			if(allowedBiomes.contains(world.getBiomeGenForCoords(xAbsolute, zAbsolute).biomeName)) {
				int yBase = world.getTopSolidOrLiquidBlock(xAbsolute, zAbsolute);
				int height = 5 + random.nextInt(15);
				int width = 3 + random.nextInt(8);
				if(yBase + height > world.getActualHeight() - 10) {
					height = world.getActualHeight() - 10 - yBase;
					if(height <= 0)
						height = 1;
				}
				for(int y = 0; y < height; y++) {
					for(int zscroll = - width/2; zscroll < width/2; zscroll++) {
						for(int xscroll = - width/2; xscroll < width/2; xscroll++) {
							double ws = (width*0.5f)*(width*0.5f);
							double cos = 1.0-Math.sin((y/(float)height) * Math.PI)+0.2-random.nextDouble() * 0.4;
							ws = ws*0.5 + (float)(ws*0.5*cos);
							if(ws < 2)
								ws = 2;
							if(zscroll*zscroll + xscroll*xscroll < ws) {
								world.setBlock(xAbsolute + xscroll, y + yBase, zAbsolute + zscroll, ChaosCrystalMain.blockBase.blockID, 0, 3);
							}
						}
					}
				}
			}
		}
	}

}
