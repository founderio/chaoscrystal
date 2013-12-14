package founderio.chaoscrystal.degradation;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.network.PacketDispatcher;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.Constants;

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
	
	public static void releaseAspect(Entity entity, World world, NBTTagCompound aspectStore, int posX, int posY, int posZ, List<String> filter, double range) {
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
		            		int aspectAmount = aspectStore.getInteger(degradation.aspects[i]);
		            		if(aspectAmount < degradation.amounts[i]) {
		            			capable = false;
		            			break;
		            		}
						}
		        		
		        		if(capable) {
		        			//System.out.println("Capable! " + Arrays.asList(degradation.aspects));
		        			hit++;
		        			
		        			for (int i = 0; i < degradation.aspects.length; i++) {
		                		int aspectAmount = aspectStore.getInteger(degradation.aspects[i]);
		                		aspectAmount -= degradation.amounts[i];
		                		aspectStore.setInteger(degradation.aspects[i], aspectAmount);
		    				}
		            		world.setBlock(posX + offX, posY + offY, posZ + offZ, degradation.source.itemID, degradation.source.getItemDamage(), 1 + 2);
		            		
		            		try {
		                		ByteArrayOutputStream bos = new ByteArrayOutputStream(Integer.SIZE * 7);
		                		DataOutputStream dos = new DataOutputStream(bos);

			            		dos.writeInt(0);
		                		dos.writeInt(posX + offX);
		                		dos.writeInt(posY + offY);
		                		dos.writeInt(posZ + offZ);
		    					dos.writeInt(-offX);
		    					dos.writeInt(-offY);
		                		dos.writeInt(-offZ);
		                		dos.writeInt(world.provider.dimensionId);
		                		
		                		Packet250CustomPayload degradationPacket = new Packet250CustomPayload();
		                		degradationPacket.channel = Constants.CHANNEL_NAME_PARTICLES;
		                		degradationPacket.data = bos.toByteArray();
		                		degradationPacket.length = bos.size();
		
		                		dos.close();
		                		
		                		PacketDispatcher.sendPacketToAllAround(posX, posY, posZ, 128, world.provider.dimensionId, degradationPacket);
		    				} catch (IOException e) {
		    					// TODO Auto-generated catch block
		    					e.printStackTrace();
		    				}
		        		} else {
		        			//System.out.println("Not Capable! " + Arrays.asList(degradation.aspects));
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
	
	public static void suckAspect(Entity entity, World world, NBTTagCompound aspectStore, int posX, int posY, int posZ, List<String> filter, double range) {
		
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
		        		if(!filter.isEmpty() && !filter.containsAll(Arrays.asList(degradation.aspects))) {
		        			//System.out.println(Arrays.asList(degradation.aspects) + "Mismatching filter: " + filter);
		        			continue;
		        		}
		        		//System.out.println("Matching filter! Aspects: " + Arrays.asList(degradation.aspects) + Arrays.asList(degradation.amounts));
		        		
		        		hit++;
		        		world.setBlock(posX + offX, posY + offY, posZ + offZ, degradation.degraded.itemID, degradation.degraded.getItemDamage(), 1 + 2);
		        		
		        		for (int i = 0; i < degradation.aspects.length; i++) {
		            		int aspectAmount = aspectStore.getInteger(degradation.aspects[i]);
		            		aspectAmount += degradation.amounts[i];
		            		aspectStore.setInteger(degradation.aspects[i], aspectAmount);
						}
		        		
		        		try {
		            		ByteArrayOutputStream bos = new ByteArrayOutputStream(Integer.SIZE * 8);
		            		DataOutputStream dos = new DataOutputStream(bos);
		            		
		            		dos.writeInt(0);
							dos.writeInt(posX);
							dos.writeInt(posY);
		            		dos.writeInt(posZ);
		            		dos.writeInt(offX);
		            		dos.writeInt(offY);
		            		dos.writeInt(offZ);
		            		dos.writeInt(world.provider.dimensionId);
		            		
		            		Packet250CustomPayload degradationPacket = new Packet250CustomPayload();
		            		degradationPacket.channel = Constants.CHANNEL_NAME_PARTICLES;
		            		degradationPacket.data = bos.toByteArray();
		            		degradationPacket.length = bos.size();
		
		            		dos.close();
		            		
		            		PacketDispatcher.sendPacketToAllAround(posX, posY, posZ, 128, world.provider.dimensionId, degradationPacket);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		        	} else {
		        		System.out.println(Block.blocksList[id].getLocalizedName() + " - " + id + "/" + meta);
		        		world.setBlock(posX + offX, posY + offY, posZ + offZ, 0, 0, 1 + 2);
		        		world.createExplosion(entity, posX + offX, posY + offY, posZ + offZ, 1, false);
		        	}
		    	}
	    	}
		} while(hit < hitsPerDegrade && tries < maxTries);
		if(hit > 0) {
    		entity.playSound("mob.enderdragon.growl", 0.1f, 0.1f);
		}
	}
}
