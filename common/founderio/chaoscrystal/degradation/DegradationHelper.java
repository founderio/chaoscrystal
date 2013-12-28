package founderio.chaoscrystal.degradation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.CommonProxy;
import founderio.chaoscrystal.entities.EntityChaosCrystal;

public class DegradationHelper {
	public static boolean isAspectStoreEmpty(NBTTagCompound aspectStore) {
		if(aspectStore == null) {
			return true;
		}
		boolean hasAspects = false;
		for(String aspect : Aspects.ASPECTS) {
			int asp = aspectStore.getInteger(aspect);
			if(asp > 0) {
				hasAspects = true;
				break;
			}
		}
		return !hasAspects;
	}
	
	public static void releaseAspect(EntityChaosCrystal entity, World world, int posX, int posY, int posZ, List<String> filter, double range) {
		//System.out.println(entity.entityId + " " + range);
		int hit = 0;
		int tries = 0;
		do {
			tries++;
	    	float offX = ChaosCrystalMain.rand.nextInt((int)(range*2))-(float)range + 0.5f;
	    	float offY = ChaosCrystalMain.rand.nextInt((int)(range*2))-(float)range + 0.5f;
	    	float offZ = ChaosCrystalMain.rand.nextInt((int)(range*2))-(float)range + 0.5f;
	    	if(Math.sqrt(offX*offX + offY*offY + offZ*offZ) < range) {
	    		int absX = (int)(posX + offX);
	    		int absY = (int)(posY + offY);
	    		int absZ = (int)(posZ + offZ);
		    	int id = world.getBlockId(absX, absY, absZ);
		    	
		    	if(id != 0) {// We can't extract air...
		    		
		    		int meta = world.getBlockMetadata(absX, absY, absZ);
		        	
		        	List<Degradation> degradationInverses = ChaosCrystalMain.degradationStore.getDegradationInverses(id, meta);
		        	
		        	if(degradationInverses != null && !degradationInverses.isEmpty()) {
		        		Degradation degradation = degradationInverses.get(ChaosCrystalMain.rand.nextInt(degradationInverses.size()));
		        		
		        		
		        		
		        		if(!filter.isEmpty() && !filter.containsAll(Arrays.asList(degradation.aspects))) {
		        			continue;
		        		}
		        		
		        		if(canSupportAspects(degradation.aspects, degradation.amounts, entity)) {
		        			hit++;
		        			
		        			for (int i = 0; i < degradation.aspects.length; i++) {
		                		int aspectAmount = entity.getAspect(degradation.aspects[i]);
		                		aspectAmount -= degradation.amounts[i];
		                		entity.setAspect(degradation.aspects[i], aspectAmount);
		    				}
		            		world.setBlock(absX, absY, absZ, degradation.source.itemID, degradation.source.getItemDamage(), 1 + 2);
		            		
		            		CommonProxy.spawnParticleEffects(world.provider.dimensionId, 0,
			        				posX+offX, posY+offY, posZ+offZ,
			        				-offX, -offY, -offZ);
		            		
		        		}
		        		
		        	} else {
		        		if(ChaosCrystalMain.cfgDebugOutput) {
		        			System.out.println(Block.blocksList[id].getLocalizedName() + " - " + id + "/" + meta);
		        		}
		        		//TODO: Can't do anything with those yet... Explode? ignore?
		//        		world.setBlock(posX + offX, posY + offY, posZ + offZ, 0, 0, 1 + 2);
		//        		world.createExplosion(entity, posX + offX, posY + offY, posZ + offZ, 1, false);
		        	}
		    	}
	    	}
		} while(hit < ChaosCrystalMain.cfgHitsPerTick && tries < ChaosCrystalMain.cfgMaxTriesPerTick);
		
		if(hit < ChaosCrystalMain.cfgHitsPerTick) {
			List<EntityItem> items = new ArrayList<EntityItem>();
    		
			for(Object obj : world.loadedEntityList) {
    			if(obj instanceof EntityItem) {
    				double el = ((EntityItem) obj).boundingBox.getAverageEdgeLength();
    				double distX = ((EntityItem) obj).posX - posX;
    				double distY = ((EntityItem) obj).posY - posY + el;
    				double distZ = ((EntityItem) obj).posZ - posZ;
    				double tmp_dist = distX*distX + distY*distY + distZ*distZ;
    				if(Math.sqrt(tmp_dist) < range) {
    					items.add((EntityItem)obj);
    				}
    			}
    		}
    		
    		if(items.size() != 0) {
    		
	    		EntityItem it = items.get(ChaosCrystalMain.rand.nextInt(items.size()));
	    		ItemStack is = it.getEntityItem();
	    		if(is != null) {
	    			List<Degradation> degradationInverses = ChaosCrystalMain.degradationStore.getDegradationInverses(is.itemID, is.getItemDamage());
	    			if(degradationInverses != null && !degradationInverses.isEmpty()) {
	    				Degradation degradation = degradationInverses.get(ChaosCrystalMain.rand.nextInt(degradationInverses.size()));
		        		
	    				if(!filter.isEmpty() && !filter.containsAll(Arrays.asList(degradation.aspects))) {
		        			//continue;
		        		} else {
		    	    		ItemStack degradationStack = new ItemStack(degradation.source.itemID, 0, degradation.source.getItemDamage());
		        		
			        		while(canSupportAspects(degradation.aspects, degradation.amounts, entity) && is.stackSize > 0 && hit < ChaosCrystalMain.cfgHitsPerTick) {
			        			hit++;
				        		//world.setBlock(posX + offX, posY + offY, posZ + offZ, degradation.degraded.itemID, degradation.degraded.getItemDamage(), 1 + 2);
			        			is.stackSize--;
			        			degradationStack.stackSize++;
			        			
				        		for (int i = 0; i < degradation.aspects.length; i++) {
				            		int aspectAmount = entity.getAspect(degradation.aspects[i]);
				            		aspectAmount -= degradation.amounts[i];
				            		entity.setAspect(degradation.aspects[i], aspectAmount);
								}
				        		
			        		}
			        		
			        		CommonProxy.spawnParticleEffects(entity, it, 0);
			        		CommonProxy.spawnParticleEffects(it, 2);
			        		
			        		
			        		if(degradationStack.stackSize > 0) {
			        			EntityItem item = new EntityItem(world, it.posX, it.posY, it.posZ, degradationStack);
			        			item.motionX = it.motionX;
			        			item.motionY = it.motionY;
			        			item.motionZ = it.motionZ;
			        			item.prevPosX = it.prevPosX;
			        			item.prevPosY = it.prevPosY;
			        			item.prevPosZ = it.prevPosZ;
			        			item.yOffset = it.yOffset;
			        			item.hoverStart = it.hoverStart;
			        			item.age = it.age;
			        			item.lifespan = it.lifespan;
			        			item.delayBeforeCanPickup = it.delayBeforeCanPickup;
			        			
			        			world.spawnEntityInWorld(item);
			        		}
			        		
			        		if(is.stackSize == 0) {
			        			it.setDead();
			        		} else {
			        			it.setEntityItemStack(is);
			        		}
		        		}
		        		
		        	} else {
		        		if(ChaosCrystalMain.cfgDebugOutput) {
		        			System.out.println(is.getDisplayName() + " - " + is.itemID + "/" + is.getItemDamage());
		        		}
		        		if(!ChaosCrystalMain.cfgNonDestructive) {
		        			it.setDead();
		        		}
		        	}
	    		}
    		}
		}
		
		if(hit > 0) {
			entity.playSound("mob.enderdragon.hit", 0.1f, 0.1f);
		}
	}
	
	public static void suckAspect(EntityChaosCrystal entity, World world, int posX, int posY, int posZ, List<String> filter, double range) {
		
		int hit = 0;
		int tries = 0;
		do {
			tries++;
			float offX = ChaosCrystalMain.rand.nextInt((int)(range*2))-(float)range + 0.5f;
	    	float offY = ChaosCrystalMain.rand.nextInt((int)(range*2))-(float)range + 0.5f;
	    	float offZ = ChaosCrystalMain.rand.nextInt((int)(range*2))-(float)range + 0.5f;
    	
	    	if(Math.sqrt(offX*offX + offY*offY + offZ*offZ) < range) {
	    		int absX = (int)(posX + offX);
	    		int absY = (int)(posY + offY);
	    		int absZ = (int)(posZ + offZ);
		    	
		    	int id = world.getBlockId(absX, absY, absZ);
		    	
		    	if(id != 0) {// We can't extract air...
		    		
		    		int meta = world.getBlockMetadata(absX, absY, absZ);
		        	
		        	Degradation degradation = ChaosCrystalMain.degradationStore.getDegradation(id, meta);
		        	if(degradation != null) {
		        		if(degradation.degraded.length == 0 || degradation.degraded[0].itemID == 0 && ChaosCrystalMain.cfgNonDestructive) {
		        			continue;
		        		}
		        		
		        		if(!filter.isEmpty() && !filter.containsAll(Arrays.asList(degradation.aspects))) {
		        			continue;
		        		}
		        		
		        		if(canFitAspects(degradation.aspects, degradation.amounts, entity)) {
		        			hit++;
			        		world.setBlock(absX, absY, absZ, degradation.degraded[0].itemID, degradation.degraded[0].getItemDamage(), 1 + 2);
			        		//TODO: respect count
			        		
			        		for (int i = 0; i < degradation.aspects.length; i++) {
			            		int aspectAmount = entity.getAspect(degradation.aspects[i]);
			            		aspectAmount += degradation.amounts[i];
			            		entity.setAspect(degradation.aspects[i], aspectAmount);
							}
			        		
			        		CommonProxy.spawnParticleEffects(world.provider.dimensionId, 0,
			        				posX, posY, posZ,
			        				offX, offY, offZ);
			        		CommonProxy.spawnParticleEffects(world.provider.dimensionId, 2,
			        				posX + offX, posY + offY, posZ + offZ);
		        		}
		        		
		        	} else {
		        		if(ChaosCrystalMain.cfgDebugOutput) {
		        			System.out.println(Block.blocksList[id].getLocalizedName() + " - " + id + "/" + meta);
		        		}
		        		if(!ChaosCrystalMain.cfgNonDestructive) {
			        		world.setBlock(absX, absY, absZ, 0, 0, 1 + 2);
			        		world.createExplosion(entity, absX, absY, absZ, 1, false);
		        		}
		        	}
		    	}
	    	}
		} while(hit < ChaosCrystalMain.cfgHitsPerTick && tries < ChaosCrystalMain.cfgMaxTriesPerTick);
		
		if(hit < ChaosCrystalMain.cfgHitsPerTick) {
			List<EntityItem> items = new ArrayList<EntityItem>();
    		
    		for(Object obj : world.loadedEntityList) {
    			if(obj instanceof EntityItem) {
    				double el = ((EntityItem) obj).boundingBox.getAverageEdgeLength();
    				double distX = ((EntityItem) obj).posX - posX;
    				double distY = ((EntityItem) obj).posY - posY + el;
    				double distZ = ((EntityItem) obj).posZ - posZ;
    				double tmp_dist = distX*distX + distY*distY + distZ*distZ;
    				if(Math.sqrt(tmp_dist) < range) {
    					items.add((EntityItem)obj);
    				}
    			}
    		}
    		
    		if(items.size() != 0) {
    		
	    		EntityItem it = items.get(ChaosCrystalMain.rand.nextInt(items.size()));
	    		ItemStack is = it.getEntityItem();
	    		if(is != null) {
		    		Degradation degradation = ChaosCrystalMain.degradationStore.getDegradation(is.itemID, is.getItemDamage());
		    		if(degradation != null) {
		        		if(degradation.degraded.length == 0 || degradation.degraded[0].itemID == 0 && ChaosCrystalMain.cfgNonDestructive) {
		        			//continue;
		        		} else if(!filter.isEmpty() && !filter.containsAll(Arrays.asList(degradation.aspects))) {
		        			//continue;
		        		} else {
		    	    		ItemStack degradationStack = new ItemStack(degradation.degraded[0].itemID, 0, degradation.degraded[0].getItemDamage());
		        		
			        		while(canFitAspects(degradation.aspects, degradation.amounts, entity) && is.stackSize > 0 && hit < ChaosCrystalMain.cfgHitsPerTick) {
			        			hit++;
				        		//world.setBlock(posX + offX, posY + offY, posZ + offZ, degradation.degraded.itemID, degradation.degraded.getItemDamage(), 1 + 2);
			        			is.stackSize--;
			        			degradationStack.stackSize++;
			        			
				        		for (int i = 0; i < degradation.aspects.length; i++) {
				            		int aspectAmount = entity.getAspect(degradation.aspects[i]);
				            		aspectAmount += degradation.amounts[i];
				            		entity.setAspect(degradation.aspects[i], aspectAmount);
								}
				        		
			        		}
			        		
			        		CommonProxy.spawnParticleEffects(it, entity, 0);
			        		CommonProxy.spawnParticleEffects(it, 2);
			        		
			        		
			        		if(degradationStack.stackSize > 0 && degradationStack.itemID != 0) {
			        			EntityItem item = new EntityItem(world, it.posX, it.posY, it.posZ, degradationStack);
			        			item.motionX = it.motionX;
			        			item.motionY = it.motionY;
			        			item.motionZ = it.motionZ;
			        			item.prevPosX = it.prevPosX;
			        			item.prevPosY = it.prevPosY;
			        			item.prevPosZ = it.prevPosZ;
			        			item.yOffset = it.yOffset;
			        			item.hoverStart = it.hoverStart;
			        			item.age = it.age;
			        			item.lifespan = it.lifespan;
			        			item.delayBeforeCanPickup = it.delayBeforeCanPickup;
			        			
			        			world.spawnEntityInWorld(item);
			        		}
			        		
			        		if(is.stackSize == 0) {
			        			it.setDead();
			        		} else {
			        			it.setEntityItemStack(is);
			        		}
		        		}
		        		
		        	} else {
		        		if(ChaosCrystalMain.cfgDebugOutput) {
		        			System.out.println(is.getDisplayName() + " - " + is.itemID + "/" + is.getItemDamage());
		        		}
		        		if(!ChaosCrystalMain.cfgNonDestructive) {
		        			it.setDead();
		        		}
		        	}
	    		}
    		}
		}
		
		if(hit > 0) {
    		entity.playSound("mob.enderdragon.growl", 0.1f, 0.1f);
		}
	}
	
	public static boolean canFitAspects(String[] aspects, int[] amounts, EntityChaosCrystal crystal) {
		for(int a = 0; a < aspects.length; a++) {
			if(crystal.getAspect(aspects[a]) + amounts[a] > ChaosCrystalMain.cfgMaxAspectStorage) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean canSupportAspects(String[] aspects, int[] amounts, EntityChaosCrystal crystal) {
		for(int a = 0; a < aspects.length; a++) {
			if(crystal.getAspect(aspects[a]) < amounts[a]) {
				return false;
			}
		}
		return true;
	}
}
