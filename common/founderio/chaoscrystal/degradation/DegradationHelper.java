package founderio.chaoscrystal.degradation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.CommonProxy;
import founderio.chaoscrystal.aspects.Node;
import founderio.chaoscrystal.aspects.modules.ModuleVanillaWorldgen;
import founderio.chaoscrystal.entities.EntityChaosCrystal;
import founderio.util.ItemUtil;
import founderio.util.ListUtil;

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
	
	public static void processReplacement(World world, int posX, int posY, int posZ, Node[] replacement) {
		
		if(replacement.length == 1 && replacement[0].getDispayItemStack().getItem() instanceof ItemBlock) {
			world.setBlock(posX, posY, posZ,
					replacement[0].getDispayItemStack().itemID, replacement[0].getDispayItemStack().getItemDamage(),
					1 + 2);
			
		} else {
			spawnMultiplesOfNodes(replacement, 1, world, posX, posY, posZ);
		}
	}

	public static void spawnMultiplesOfNodes(Node[] nodes, int count, World world, EntityItem reference) {
		for(int i = 0; i < nodes.length; i++) {
			Node p = nodes[i];
			int maxStackSize = p.getDispayItemStack().getMaxStackSize();
			for(int ists = 0; ists < Math.floor(count / maxStackSize); ists++) {
				ItemStack spawnStack = p.getDispayItemStack().copy();
				spawnStack.stackSize = maxStackSize;
				ItemUtil.spawnItemStack(spawnStack, world, reference);
			}
			ItemStack spawnStack = p.getDispayItemStack().copy();
			spawnStack.stackSize = count % maxStackSize;
		}
	}
	
	public static void spawnMultiplesOfNodes(Node[] nodes, int count, World world, int posX, int posY, int posZ) {
		for(int i = 0; i < nodes.length; i++) {
			Node p = nodes[i];
			int maxStackSize = p.getDispayItemStack().getMaxStackSize();
			for(int ists = 0; ists < Math.floor(count / maxStackSize); ists++) {
				ItemStack spawnStack = p.getDispayItemStack().copy();
				spawnStack.stackSize = maxStackSize;
				ItemUtil.spawnItemStack(spawnStack, world, posX + 0.5, posY + 0.5, posZ + 0.5);
			}
			ItemStack spawnStack = p.getDispayItemStack().copy();
			spawnStack.stackSize = count % maxStackSize;
		}
	}
	
	public static void crystalTick(EntityChaosCrystal entity,
			World world, int posX, int posY, int posZ, List<String> filter,
			double range, boolean extract) {
		
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
		    		List<Node> nodes;
		    		if(extract) {
		    			nodes = ChaosCrystalMain.degradationStore.getExtractionsFrom(new ItemStack(id, 1, meta));
		    		} else {
		    			nodes = ChaosCrystalMain.degradationStore.getInfusionsFrom(new ItemStack(id, 1, meta));
		    		}
		        	
		        	Node degradation = ListUtil.getRandomFromList(nodes, ChaosCrystalMain.rand);
		        	
		        	if(degradation != null) {
		        		Node[] parents;
		        		if(extract) {
		        			parents = degradation.getParents();
		        		} else {
		        			parents = new Node[] { degradation };
		        		}
		        		if(parents.length == 0 || parents[0] == ModuleVanillaWorldgen.AIR && ChaosCrystalMain.cfgNonDestructive) {
		        			continue;
		        		}
		        		int[] aspects = degradation.getAspectDifference();
		        		
		        		if(!filter.isEmpty() && !filter.containsAll(Arrays.asList(Aspects.getAspectNames(aspects)))) {
		        			continue;
		        		}
		        		
		        		
		        		if(extract) {
		        			if(entity.canAcceptAspects(aspects)) {
		        				hit++;

			        			entity.addAspects(aspects);
			        			processReplacement(world, absX, absY, absZ, parents);
			        			
				        		
				        		CommonProxy.spawnParticleEffects(world.provider.dimensionId, 0,
				        				posX, posY, posZ,
				        				offX, offY, offZ);
				        		CommonProxy.spawnParticleEffects(world.provider.dimensionId, 2,
				        				posX + offX, posY + offY, posZ + offZ);
		        			}
		        		} else {
		        			if(entity.canProvideAspects(aspects)) {
		        				hit++;
			        			
		        				entity.subtractAspects(aspects);
		        				processReplacement(world, absX, absY, absZ, parents);
			        			
			            		
			            		CommonProxy.spawnParticleEffects(world.provider.dimensionId, 0,
				        				posX+offX, posY+offY, posZ+offZ,
				        				-offX, -offY, -offZ);
		        			}
		        		}
		        		
		        	} else {
		        		if(ChaosCrystalMain.cfgDebugOutput) {
		        			System.out.println(Block.blocksList[id].getLocalizedName() + " - " + id + "/" + meta);
		        		}
		        		if(extract && !ChaosCrystalMain.cfgNonDestructive) {
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
	    			List<Node> nodes;
		    		if(extract) {
		    			nodes = ChaosCrystalMain.degradationStore.getExtractionsFrom(is);
		    		} else {
		    			nodes = ChaosCrystalMain.degradationStore.getInfusionsFrom(is);
		    		}
		    		Node degradation = ListUtil.getRandomFromList(nodes, ChaosCrystalMain.rand);
		        	
		        	if(degradation != null) {
		        		Node[] parents;
		        		if(extract) {
		        			parents = degradation.getParents();
		        		} else {
		        			parents = new Node[] { degradation };
		        		}
		        		if(parents.length == 0 || parents[0] == ModuleVanillaWorldgen.AIR && ChaosCrystalMain.cfgNonDestructive) {
		        			//continue;
		        		} else {
		        			
		        		int[] aspects = degradation.getAspectDifference();
		        		
			        		if(!filter.isEmpty() && !filter.containsAll(Arrays.asList(Aspects.getAspectNames(aspects)))) {
			        			//continue;
			        		} else {
			        			int count = 0;
			    	    		
			    	    		if(extract) {
				        			for(int st = 0; st < is.stackSize; st++) {
				        				if(!entity.canAcceptAspects(aspects)) {
				        					break;
				        				} else {
				        					count++;
		
						        			entity.addAspects(aspects);
				        				}
				        			}
				        		} else {
				        			for(int st = 0; st < is.stackSize; st++) {
				        				if(!entity.canProvideAspects(aspects)) {
				        					break;
				        				} else {
				        					count++;
		
						        			entity.subtractAspects(aspects);
				        				}
				        			}
				        		}
			    	    		if(count > 0) {
			    	    			is.stackSize -= count;
			    	    			
			    	    			
			    	    			spawnMultiplesOfNodes(parents, count, world, it);
			    	    			
			    	    			if(is.stackSize == 0) {
					        			it.setDead();
					        		} else {
					        			it.setEntityItemStack(is);
					        		}
		
					        		CommonProxy.spawnParticleEffects(it, entity, 0);
					        		CommonProxy.spawnParticleEffects(it, 2);
			    	    		}
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
	
	public static boolean canAcceptAspects(String[] aspects, int[] amounts, IAspectStore aspectStore) {
		for(int a = 0; a < aspects.length; a++) {
			if(aspectStore.getAspect(aspects[a]) + amounts[a] > ChaosCrystalMain.cfgCrystalAspectStorage) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean canProvideAspects(String[] aspects, int[] amounts, IAspectStore aspectStore) {
		for(int a = 0; a < aspects.length; a++) {
			if(aspectStore.getAspect(aspects[a]) < amounts[a]) {
				return false;
			}
		}
		return true;
	}
}
