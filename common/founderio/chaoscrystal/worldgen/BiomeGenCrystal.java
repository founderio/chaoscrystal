package founderio.chaoscrystal.worldgen;

import net.minecraft.block.Block;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.SpawnListEntry;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.Constants;

public class BiomeGenCrystal extends BiomeGenBase {

	
	
	@SuppressWarnings("unchecked")
	public BiomeGenCrystal(int par1) {
		super(par1);
		this.spawnableCreatureList.clear();
        this.spawnableMonsterList.clear();
        this.spawnableCaveCreatureList.clear();
        this.spawnableWaterCreatureList.clear();
		this.spawnableMonsterList.add(new SpawnListEntry(EntityCreeper.class, 10, 1, 6));
        this.spawnableMonsterList.add(new SpawnListEntry(EntitySlime.class, 10, 1, 6));
        this.topBlock = (byte)ChaosCrystalMain.blockBase.blockID;
        this.fillerBlock = (byte)Block.dirt.blockID;
        this.setDisableRain();
        this.temperature = 0.3f;
        this.theBiomeDecorator.treesPerChunk = -999;
        this.theBiomeDecorator.deadBushPerChunk = 0;
        this.theBiomeDecorator.reedsPerChunk = 0;
        this.theBiomeDecorator.cactiPerChunk = 0;
        this.biomeName = Constants.NAME_BIOME_CRYSTAL;
	}
	
	@Override
	public int getWaterColorMultiplier() {
		return 0x003333;
	}

}
