package founderio.chaoscrystal.degradation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;

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
				if(geds[i].degraded.length == 1) {
					if(geds[i].degraded[0].getItemDamage() == 0) {
						creations.add(geds[i]);
					}
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
	
	public void autoRegisterDegradation(ItemStack is) {
		List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
		List<IRecipe> matching = new ArrayList<IRecipe>();
		for(IRecipe r: recipes) {
			ItemStack output = r.getRecipeOutput();
			if(output != null && output.itemID == is.itemID && (is.getItemDamage() == 32767 || output.getItemDamage() == is.getItemDamage())) {
				matching.add(r);
			}
		}
		if(matching.isEmpty()) {
			System.out.println("Registering Item " + is + " failed. No crafting recipes.");
			return;
		}
		if(matching.size() > 1) {
			System.out.println("Registering Item " + is + " failed. Multiple crafting recipes.");
			return;
		}
		IRecipe r = matching.get(0);
		if(r instanceof ShapedRecipes) {
			autoRegisterWithItemStacks(is, ((ShapedRecipes) r).recipeItems);
		} else if(r instanceof ShapedOreRecipe) {
			Object[] input = ((ShapedOreRecipe) r).getInput();
			
			ItemStack[] recipeItems = new ItemStack[input.length];
			for(int i = 0; i < input.length; i++) {
				if(input[i] instanceof ItemStack) {
					recipeItems[i] = (ItemStack)input[i];
				} else if(input[i] instanceof ArrayList) {
					recipeItems[i] = (ItemStack)((ArrayList)input[i]).get(0);
				}
			}
			autoRegisterWithItemStacks(is, recipeItems);
		} else {
			System.out.println("Registering Item " + is + " failed. No supported crafting recipes.");
		}
		//TODO: Add smelting recipes
	}
	
	private void autoRegisterWithItemStacks(ItemStack is, ItemStack[] recipeItems) {
		int[] amounts = new int[Aspects.ASPECTS.length];
		List<ItemStack> degraded = new ArrayList<ItemStack>();
		for(ItemStack crafting : recipeItems) {
			if(crafting == null || crafting.stackSize <= 0) {
				System.out.println("Registering Item " + is + " failed. Crafting recipe for subsequent Item " + crafting + " has zero stack size.");
				return;
			}
			Degradation deg = getDegradation(crafting);
			if(deg == null) {
				autoRegisterDegradation(crafting);
				deg = getDegradation(crafting);
				if(deg == null) {
					System.out.println("Registering Item " + is + " failed. Could not find aspects for subsequent Item " + crafting + ".");
					return;
				}
			}
			
			for(int a = 0; a < deg.aspects.length; a++) {
				String aspect = deg.aspects[a];
				amounts[Aspects.getAspectDisplayId(aspect)] += deg.amounts[a] / crafting.stackSize;
			}
			boolean added = false;
			for(ItemStack dis : degraded) {
				if(dis.isItemEqual(crafting)) {
					dis.stackSize += crafting.stackSize;
					added = true;
					break;
				}
			}
			if(!added) {
				degraded.add(crafting.copy());
			}
		}
		amounts[Aspects.getAspectDisplayId(Aspects.ASPECT_CRAFTING)] += 5;
		registerDegradation(is, Aspects.ASPECTS.clone(), amounts, degraded.toArray(new ItemStack[degraded.size()]));
	}
	
	public Degradation getDegradation(ItemStack is) {
		return getDegradation(is.itemID, is.getItemDamage());
	}
	
	public Degradation getDegradation(int id, int meta) {
		Degradation[] geds = degradations.get(id);
		if(geds == null) {
			return null;
		}
		
		for (int i = 0; i < geds.length; i++) {
			if(geds[i].source.getItemDamage() == 32767 || meta == 32767 || geds[i].source.getItemDamage() == meta) {
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
			if(geds[i].degraded.length == 1) {
				if(geds[i].degraded[0].getItemDamage() == meta) {
					inverses.add(geds[i]);
				}
			}
			
		}
		
		return inverses;
	}
	
	
	public void registerDegradation(ItemStack source, String[] aspects, int[] amounts,
			ItemStack... degraded) {
		Degradation object = new Degradation(source.copy(), aspects.clone(), amounts.clone(), degraded.clone());
		
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
		for(int i = 0; i < degraded.length; i++) {
			geds = degradationsInverse.get(degraded[i].itemID);
			if(geds == null) {
				geds = new Degradation[1];
			} else {
				Degradation[] geds2 = new Degradation[geds.length+1];
				System.arraycopy(geds, 0, geds2, 0, geds.length);
				geds = geds2;
			}
			geds[geds.length - 1] = object;
			degradationsInverse.put(degraded[i].itemID, geds);
		}
		
	}

	public void debugOutput() {
		System.out.println("Degradation List:");
		for(Integer key : degradations.keySet()) {
			Degradation[] degradationList = degradations.get(key);
			System.out.println(Block.blocksList[key].getLocalizedName() + " (" + key + ") Transforms to:");
			for (int i = 0; i < degradationList.length; i++) {
				for(int d = 0; d < degradationList[i].degraded.length; d++) {
					if(degradationList[i].degraded[d].itemID == 0) {
						System.out.println("Air");
					} else {
						System.out.println(Block.blocksList[degradationList[i].degraded[d].itemID].getLocalizedName() + " - " + degradationList[i].degraded[d].getItemDamage());
					}
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
