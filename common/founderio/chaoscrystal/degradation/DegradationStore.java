package founderio.chaoscrystal.degradation;

import java.util.HashMap;

import net.minecraft.item.ItemStack;

public class DegradationStore {
	
	private HashMap<Integer, Degradation[]> degradations;
	
	public DegradationStore() {
		degradations = new HashMap<Integer, Degradation[]>();
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
	
	public void registerDegradation(ItemStack source, String[] aspects, int[] amounts,
			ItemStack degraded) {
		Degradation[] geds = degradations.get(source.itemID);
		if(geds == null) {
			geds = new Degradation[1];
		} else {
			Degradation[] geds2 = new Degradation[geds.length];
			System.arraycopy(geds, 0, geds2, 0, geds.length);
			geds = geds2;
		}
		geds[geds.length - 1] = new Degradation(source, aspects, amounts, degraded);
		degradations.put(source.itemID, geds);
	}
}
