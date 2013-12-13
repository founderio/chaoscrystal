package founderio.chaoscrystal.worldgen;

import founderio.chaoscrystal.ChaosCrystalMain;
import net.minecraft.block.Block;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.SpawnListEntry;

public class BiomeGenCrystal extends BiomeGenBase {

	
	
	public BiomeGenCrystal(int par1) {
		super(par1);
		this.spawnableCreatureList.clear();
		this.spawnableMonsterList.add(new SpawnListEntry(EntityCreeper.class, 10, 1, 6));
        this.spawnableMonsterList.add(new SpawnListEntry(EntitySlime.class, 10, 1, 6));
        this.spawnableCaveCreatureList.clear();
        this.spawnableWaterCreatureList.clear();
        this.topBlock = (byte)ChaosCrystalMain.blockBase.blockID;
        this.fillerBlock = (byte)ChaosCrystalMain.blockBase.blockID;//TODO: custom blocks
        this.theBiomeDecorator.treesPerChunk = -999;
        this.theBiomeDecorator.deadBushPerChunk = 0;
        this.theBiomeDecorator.reedsPerChunk = 0;
        this.theBiomeDecorator.cactiPerChunk = 0;
	}

}
