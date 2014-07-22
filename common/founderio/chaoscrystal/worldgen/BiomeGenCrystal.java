package founderio.chaoscrystal.worldgen;

import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.Constants;

public class BiomeGenCrystal extends BiomeGenBase {

	@SuppressWarnings("unchecked")
	public BiomeGenCrystal(int par1) {
		super(par1, false);
		spawnableCreatureList.clear();
		spawnableMonsterList.clear();
		spawnableCaveCreatureList.clear();
		spawnableWaterCreatureList.clear();
		spawnableMonsterList.add(new SpawnListEntry(EntityCreeper.class,
				10, 1, 6));
		spawnableMonsterList.add(new SpawnListEntry(EntitySlime.class, 10,
				1, 6));
		topBlock = ChaosCrystalMain.blockBase;
		fillerBlock = Blocks.dirt;
		setDisableRain();
		temperature = 0.3f;
		theBiomeDecorator.treesPerChunk = -999;
		theBiomeDecorator.deadBushPerChunk = 0;
		theBiomeDecorator.reedsPerChunk = 0;
		theBiomeDecorator.cactiPerChunk = 0;
		biomeName = Constants.NAME_BIOME_CRYSTAL;
	}

	@Override
	public int getWaterColorMultiplier() {
		return 0x003333;
	}

}
