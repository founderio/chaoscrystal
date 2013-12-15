package founderio.chaoscrystal.degradation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.ChaosCrystalNetworkHandler;
import founderio.chaoscrystal.entities.EntityChaosCrystal;
import founderio.chaoscrystal.entities.EntityFocusFilter;

public class DegradationHelper {
	private static final Random rand = new Random();
	
	public static void loadConfig(Configuration config) {
		degradeRange = config.get("Settings", "degradeRange", degradeRange).getInt();
		degradeRange = config.get("Settings", "maxTries", maxTries).getInt();
		degradeRange = config.get("Settings", "hitsPerDegrade", hitsPerDegrade).getInt();
	}
	
	public static int degradeRange = 10;
	public static int maxTries = 80;
	public static int hitsPerDegrade = 1;
	
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
	    	int offX = (int)(rand.nextInt((int)(range*2))-range);
	    	int offY = (int)(rand.nextInt((int)(range*2))-range);
	    	int offZ = (int)(rand.nextInt((int)(range*2))-range);
	    	if(offX*offX + offY*offY + offZ*offZ < range*range*range) {
		    	int id = world.getBlockId(posX + offX, posY + offY, posZ + offZ);
		    	
		    	if(id != 0) {// We can't extract air...
		    		
		    		int meta = world.getBlockMetadata(posX + offX, posY + offY, posZ + offZ);
		        	
		        	List<Degradation> degradationInverses = ChaosCrystalMain.degradationStore.getDegradationInverses(id, meta);
		        	
		        	if(degradationInverses != null && !degradationInverses.isEmpty()) {
		        		Degradation degradation = degradationInverses.get(rand.nextInt(degradationInverses.size()));
		        		
		        		
		        		boolean capable = true;
		        		
		        		if(!filter.isEmpty() && !filter.containsAll(Arrays.asList(degradation.aspects))) {
		        			continue;
		        		}
		        		
		        		
		        		for (int i = 0; i < degradation.aspects.length; i++) {
		            		int aspectAmount = entity.getAspect(degradation.aspects[i]);
		            		if(aspectAmount < degradation.amounts[i]) {
		            			capable = false;
		            			break;
		            		}
						}
		        		
		        		if(capable) {
		        			hit++;
		        			
		        			for (int i = 0; i < degradation.aspects.length; i++) {
		                		int aspectAmount = entity.getAspect(degradation.aspects[i]);
		                		aspectAmount -= degradation.amounts[i];
		                		entity.setAspect(degradation.aspects[i], aspectAmount);
		    				}
		            		world.setBlock(posX + offX, posY + offY, posZ + offZ, degradation.source.itemID, degradation.source.getItemDamage(), 1 + 2);
		            		
		            		ChaosCrystalNetworkHandler.spawnParticleEffects(world.provider.dimensionId, 0,
			        				posX+offX, posY+offY, posZ+offZ,
			        				-offX, -offY, -offZ);
		            		
		        		}
		        		
		        	} else {
		        		//System.out.println(Block.blocksList[id].getLocalizedName() + " - " + id + "/" + meta);
		        		//TODO: Can't do anything with those yet... Explode? ignore?
		//        		world.setBlock(posX + offX, posY + offY, posZ + offZ, 0, 0, 1 + 2);
		//        		world.createExplosion(entity, posX + offX, posY + offY, posZ + offZ, 1, false);
		        	}
		    	}
	    	}
		} while(hit < hitsPerDegrade && tries < maxTries);
		if(hit > 0) {
			entity.playSound("mob.enderdragon.hit", 0.1f, 0.1f);
		}
	}
	
	public static void suckAspect(EntityChaosCrystal entity, World world, int posX, int posY, int posZ, List<String> filter, double range) {
		
		int hit = 0;
		int tries = 0;
		do {
			tries++;
			int offX = (int)(rand.nextInt((int)(range*2))-range);
	    	int offY = (int)(rand.nextInt((int)(range*2))-range);
	    	int offZ = (int)(rand.nextInt((int)(range*2))-range);
    	
	    	if(offX*offX + offY*offY + offZ*offZ < range*range*range) {
		    	
		    	int id = world.getBlockId(posX + offX, posY + offY, posZ + offZ);
		    	
		    	if(id != 0) {// We can't extract air...
		    		
		    		int meta = world.getBlockMetadata(posX + offX, posY + offY, posZ + offZ);
		        	
		        	Degradation degradation = ChaosCrystalMain.degradationStore.getDegradation(id, meta);
		        	if(degradation != null) {
		        		if(degradation.degraded.itemID == 0 && ChaosCrystalMain.cfg_nonDestructive) {
		        			continue;
		        		}
		        		
		        		if(!filter.isEmpty() && !filter.containsAll(Arrays.asList(degradation.aspects))) {
		        			continue;
		        		}
		        		
		        		if(canFitAspects(degradation.aspects, degradation.amounts, entity)) {
		        			hit++;
			        		world.setBlock(posX + offX, posY + offY, posZ + offZ, degradation.degraded.itemID, degradation.degraded.getItemDamage(), 1 + 2);
			        		
			        		for (int i = 0; i < degradation.aspects.length; i++) {
			            		int aspectAmount = entity.getAspect(degradation.aspects[i]);
			            		aspectAmount += degradation.amounts[i];
			            		entity.setAspect(degradation.aspects[i], aspectAmount);
							}
			        		
			        		ChaosCrystalNetworkHandler.spawnParticleEffects(world.provider.dimensionId, 0,
			        				posX, posY, posZ,
			        				offX, offY, offZ);
			        		ChaosCrystalNetworkHandler.spawnParticleEffects(world.provider.dimensionId, 2,
			        				posX + offX, posY + offY, posZ + offZ);
		        		}
		        		
		        	} else {
		        		System.out.println(Block.blocksList[id].getLocalizedName() + " - " + id + "/" + meta);
		        		if(!ChaosCrystalMain.cfg_nonDestructive) {
			        		world.setBlock(posX + offX, posY + offY, posZ + offZ, 0, 0, 1 + 2);
			        		world.createExplosion(entity, posX + offX, posY + offY, posZ + offZ, 1, false);
		        		}
		        	}
		    	}
	    	}
		} while(hit < hitsPerDegrade && tries < maxTries);
		
		if(hit == 0) {
			List<EntityItem> items = new ArrayList<EntityItem>();
    		
    		for(Object obj : world.loadedEntityList) {
    			if(obj instanceof EntityItem) {
    				double distX = ((EntityItem) obj).posX - posX;
    				double distY = ((EntityItem) obj).posY - posY;
    				double distZ = ((EntityItem) obj).posZ - posZ;
    				double tmp_dist = distX*distX + distY*distY + distZ*distZ;
    				if(tmp_dist < range*range*range) {
    					items.add((EntityItem)obj);
    				}
    			}
    		}
    		
    		if(items.size() != 0) {
    		
	    		EntityItem it = items.get(rand.nextInt(items.size()));
	    		ItemStack is = it.getEntityItem();
	    		if(is != null) {
		    		Degradation degradation = ChaosCrystalMain.degradationStore.getDegradation(is.itemID, is.getItemDamage());
		    		if(degradation != null) {
		        		if(degradation.degraded.itemID == 0 && ChaosCrystalMain.cfg_nonDestructive) {
		        			//continue;
		        		} else if(!filter.isEmpty() && !filter.containsAll(Arrays.asList(degradation.aspects))) {
		        			//continue;
		        		} else {
		    	    		ItemStack degradationStack = new ItemStack(degradation.degraded.itemID, 0, degradation.degraded.getItemDamage());
		        		
			        		while(canFitAspects(degradation.aspects, degradation.amounts, entity) && is.stackSize > 0) {
			        			hit++;
				        		//world.setBlock(posX + offX, posY + offY, posZ + offZ, degradation.degraded.itemID, degradation.degraded.getItemDamage(), 1 + 2);
			        			is.stackSize--;
			        			degradationStack.stackSize++;
			        			
				        		for (int i = 0; i < degradation.aspects.length; i++) {
				            		int aspectAmount = entity.getAspect(degradation.aspects[i]);
				            		aspectAmount += degradation.amounts[i];
				            		entity.setAspect(degradation.aspects[i], aspectAmount);
								}
				        		ChaosCrystalNetworkHandler.spawnParticleEffects(it, entity, 0);
				        		ChaosCrystalNetworkHandler.spawnParticleEffects(it, 2);
				        		
			        		}
			        		
			        		if(degradationStack.stackSize > 0) {
			        			EntityItem item = new EntityItem(world, it.posX, it.posY, it.posZ, degradationStack);
			        			item.motionX = it.motionX;
			        			item.motionY = it.motionY;
			        			item.motionZ = it.motionZ;
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
		        		System.out.println(is.getDisplayName() + " - " + is.itemID + "/" + is.getItemDamage());
		        		if(!ChaosCrystalMain.cfg_nonDestructive) {
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
			if(crystal.getAspect(aspects[a]) + amounts[a] > ChaosCrystalMain.cfg_maxAspectStorage) {
				return false;
			}
		}
		return true;
	}
}
