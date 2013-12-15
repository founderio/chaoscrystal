package founderio.chaoscrystal.degradation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class DegradationStore {
	
	private HashMap<Integer, Degradation[]> degradations;
	private HashMap<Integer, Degradation[]> degradationsInverse;
	private HashMap<Integer, Repair> repairs;
	
	public DegradationStore() {
		degradations = new HashMap<Integer, Degradation[]>();
		degradationsInverse = new HashMap<Integer, Degradation[]>();
		repairs = new HashMap<Integer, Repair>();
	}

	public List<Degradation> getCreations() {
		List<Degradation> creations = new ArrayList<Degradation>();
		for(Degradation[] geds : degradations.values()) {
			for (int i = 0; i < geds.length; i++) {
				if(geds[i].degraded.itemID == 0) {
					creations.add(geds[i]);
				}
			}
		}
		return creations;
	}
	
	public Repair getRepair(int id) {
		return repairs.get(id);
	}
	
	public void registerRepair(int itemId, String[] aspects, int[] amounts) {
		repairs.put(itemId, new Repair(itemId, aspects, amounts));
	}
	
	public Degradation getDegradation(int id, int meta) {
		Degradation[] geds = degradations.get(id);
		if(geds == null) {
			return null;
		}
		
		for (int i = 0; i < geds.length; i++) {
			if(geds[i].source.getItemDamage() == meta) {
				return geds[i];
			}
		}
		
		return null;
	}
	
	public List<Degradation> getDegradationInverses(int id, int meta) {
		List<Degradation> inverses = new ArrayList<Degradation>();
		
		Degradation[] geds = degradationsInverse.get(id);
		if(geds == null) {
			return null;
		}
		
		for (int i = 0; i < geds.length; i++) {
			if(geds[i].degraded.getItemDamage() == meta) {
				inverses .add(geds[i]);
			}
		}
		
		return inverses;
	}
	
	public void registerDegradation(ItemStack source, String[] aspects, int[] amounts,
			ItemStack degraded) {
		Degradation object = new Degradation(source, aspects, amounts, degraded);
		
		Degradation[] geds = degradations.get(source.itemID);
		if(geds == null) {
			geds = new Degradation[1];
		} else {
			Degradation[] geds2 = new Degradation[geds.length+1];
			System.arraycopy(geds, 0, geds2, 0, geds.length);
			geds = geds2;
		}
		geds[geds.length - 1] = object;
		degradations.put(source.itemID, geds);
		
		//Inverse
		geds = degradationsInverse.get(degraded.itemID);
		if(geds == null) {
			geds = new Degradation[1];
		} else {
			Degradation[] geds2 = new Degradation[geds.length+1];
			System.arraycopy(geds, 0, geds2, 0, geds.length);
			geds = geds2;
		}
		geds[geds.length - 1] = object;
		degradationsInverse.put(degraded.itemID, geds);
	}

	public void debugOutput() {
		System.out.println("Degradation List:");
		for(Integer key : degradations.keySet()) {
			Degradation[] degradationList = degradations.get(key);
			System.out.println(Block.blocksList[key].getLocalizedName() + " Transforms to:");
			for (int i = 0; i < degradationList.length; i++) {
				if(degradationList[i].degraded.itemID == 0) {
					System.out.println("Air");
				} else {
					System.out.println(Block.blocksList[degradationList[i].degraded.itemID].getLocalizedName() + " - " + degradationList[i].degraded.getItemDamage());
				}
			}
		}
		
		System.out.println("Inverse Degradation List:");
		for(Integer key : degradationsInverse.keySet()) {
			Degradation[] degradationList = degradationsInverse.get(key);
			if(key == 0) {
				continue;
			}
			System.out.println(Block.blocksList[key].getLocalizedName() + " Transforms to:");
			for (int i = 0; i < degradationList.length; i++) {
				if(degradationList[i].source.itemID == 0) {
					System.out.println("Air");
				} else {
					System.out.println(Block.blocksList[degradationList[i].source.itemID].getLocalizedName() + " - " + degradationList[i].source.getItemDamage());
				}
			}
		}
	}
}
