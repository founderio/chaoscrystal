package founderio.chaoscrystal.degradation;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.registry.LanguageRegistry;
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
	public static int maxTries = 20;
	public static int hitsPerDegrade = 10;
	
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
	
	public static void releaseAspect(Entity entity, World world, NBTTagCompound aspectStore, int posX, int posY, int posZ) {
		int hit = 0;
		int tries = 0;
		do {
    	int offX = rand.nextInt(degradeRange*2)-degradeRange;
    	int offY = rand.nextInt(degradeRange*2)-degradeRange;
    	int offZ = rand.nextInt(degradeRange*2)-degradeRange;
    	if(offX*offX + offY*offY + offZ*offZ < degradeRange*degradeRange) {
	    	int id = world.getBlockId(posX + offX, posY + offY, posZ + offZ);
	    	
	    	if(id != 0) {// We can't extract air...
	    		
	    		int meta = world.getBlockMetadata(posX + offX, posY + offY, posZ + offZ);
	        	
	        	List<Degradation> degradationInverses = ChaosCrystalMain.degradationStore.getDegradationInverses(id, meta);
	        	
	        	if(degradationInverses != null && !degradationInverses.isEmpty()) {
	        		Degradation degradation = degradationInverses.get(rand.nextInt(degradationInverses.size()));
	        		
	        		
	        		boolean capable = true;
	        		
	        		for (int i = 0; i < degradation.aspects.length; i++) {
	            		int aspectAmount = aspectStore.getInteger(degradation.aspects[i]);
	            		if(aspectAmount < degradation.amounts[i]) {
	            			capable = false;
	            			break;
	            		}
					}
	        		
	        		if(capable) {
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
	
	                		dos.writeInt(posX + offX);
	                		dos.writeInt(posY + offY);
	                		dos.writeInt(posZ + offZ);
	    					dos.writeInt(-offX);
	    					dos.writeInt(-offY);
	                		dos.writeInt(-offZ);
	                		dos.writeInt(world.provider.dimensionId);
	                		
	                		Packet250CustomPayload degradationPacket = new Packet250CustomPayload();
	                		degradationPacket.channel = Constants.CHANNEL_NAME;
	                		degradationPacket.data = bos.toByteArray();
	                		degradationPacket.length = bos.size();
	
	                		dos.close();
	                		
	                		PacketDispatcher.sendPacketToAllAround(posX, posY, posZ, 128, world.provider.dimensionId, degradationPacket);
	    				} catch (IOException e) {
	    					// TODO Auto-generated catch block
	    					e.printStackTrace();
	    				}
	        		} else {
	        		}
	        		
	        	} else {
	        		//System.out.println(Block.blocksList[id].getLocalizedName() + " - " + id + "/" + meta);
	        		//TODO: Can't do anything with those yet... Explode? ignore?
	//        		world.setBlock(posX + offX, posY + offY, posZ + offZ, 0, 0, 1 + 2);
	//        		world.createExplosion(entity, posX + offX, posY + offY, posZ + offZ, 1, false);
	        	}
	    	}
    	}
		tries++;
		} while(hit < hitsPerDegrade && tries < maxTries);
		if(hit > 0) {
			entity.playSound("mob.enderdragon.hit", 0.1f, 0.1f);
		}
	}
	
	public static void suckAspect(Entity entity, World world, NBTTagCompound aspectStore, int posX, int posY, int posZ) {
		int hit = 0;
		int tries = 0;
		do {
    	int offX = rand.nextInt(degradeRange*2)-degradeRange;
    	int offY = rand.nextInt(degradeRange*2)-degradeRange;
    	int offZ = rand.nextInt(degradeRange*2)-degradeRange;
    	
    	if(offX*offX + offY*offY + offZ*offZ < degradeRange*degradeRange) {
	    	
	    	int id = world.getBlockId(posX + offX, posY + offY, posZ + offZ);
	    	
	    	if(id != 0) {// We can't extract air...
	    		hit++;
	    		int meta = world.getBlockMetadata(posX + offX, posY + offY, posZ + offZ);
	        	
	        	Degradation degradation = ChaosCrystalMain.degradationStore.getDegradation(id, meta);
	        	if(degradation != null) {
	        		world.setBlock(posX + offX, posY + offY, posZ + offZ, degradation.degraded.itemID, degradation.degraded.getItemDamage(), 1 + 2);
	        		
	        		for (int i = 0; i < degradation.aspects.length; i++) {
	            		int aspectAmount = aspectStore.getInteger(degradation.aspects[i]);
	            		aspectAmount += degradation.amounts[i];
	            		aspectStore.setInteger(degradation.aspects[i], aspectAmount);
					}
	        		
	        		try {
	            		ByteArrayOutputStream bos = new ByteArrayOutputStream(Integer.SIZE * 7);
	            		DataOutputStream dos = new DataOutputStream(bos);
	            		
						dos.writeInt(posX);
						dos.writeInt(posY);
	            		dos.writeInt(posZ);
	            		dos.writeInt(offX);
	            		dos.writeInt(offY);
	            		dos.writeInt(offZ);
	            		dos.writeInt(world.provider.dimensionId);
	            		
	            		Packet250CustomPayload degradationPacket = new Packet250CustomPayload();
	            		degradationPacket.channel = Constants.CHANNEL_NAME;
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
		tries++;
		} while(hit < hitsPerDegrade && tries < maxTries);
		if(hit > 0) {
    		entity.playSound("mob.enderdragon.growl", 0.1f, 0.1f);
		}
	}
}
