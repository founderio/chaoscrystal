package founderio.chaoscrystal.degradation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.aspects.AspectModule;
import founderio.chaoscrystal.aspects.Node;
import founderio.chaoscrystal.aspects.NodeCrafting;
import founderio.chaoscrystal.aspects.modules.ModuleVanillaWorldgen;
import founderio.util.ItemUtil;

public class DegradationStore {

	private List<Node> nodes;
	private HashMap<Integer, Repair> repairs;

	public DegradationStore() {
		repairs = new HashMap<Integer, Repair>();
		nodes = new ArrayList<Node>();

		addAspectModule(new ModuleVanillaWorldgen());
	}

	public void addAspectModule(AspectModule module) {
		nodes.addAll(module.nodes);
	}

	public List<Node> getCreations() {
		// TODO: Buffer this!
		List<Node> creations = new ArrayList<Node>();
		for (Node node : nodes) {
			Node[] parents = node.getParents();
			if (parents.length == 1 && parents[0] == ModuleVanillaWorldgen.AIR) {
				creations.add(node);
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

	public List<Node> getInfusionsFrom(ItemStack is) {
		List<Node> infusions = new ArrayList<Node>();
		for (Node node : nodes) {
			Node[] parents = node.getParents();
			if (parents == null) {
				return null;
			}
			if (parents.length == 1 && parents[0] == null) {
				return null;
			}
			if (parents.length == 1 && parents[0].matchesItemStack(is)) {
				infusions.add(node);
			}
		}
		return infusions;
	}

	public List<Node> getExtractionsFrom(ItemStack is) {
		List<Node> extractions = new ArrayList<Node>();
		for (Node node : nodes) {
			if (node.matchesItemStack(is)) {
				extractions.add(node);
			}
		}
		return extractions;
	}

	@SuppressWarnings("rawtypes")
	public void autoRegisterDegradation(ItemStack is) {
		@SuppressWarnings("unchecked")
		List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
		List<IRecipe> matching = new ArrayList<IRecipe>();
		for (IRecipe r : recipes) {
			ItemStack output = r.getRecipeOutput();
			if (ItemUtil.itemsMatch(is, output)) {
				matching.add(r);
			}
		}
		if (matching.isEmpty()) {
			if (ChaosCrystalMain.cfgDebugOutput) {
				System.out.println("Registering Item " + is
						+ " failed. No crafting recipes.");
			}
			return;
		}
		if (matching.size() > 1) {
			if (ChaosCrystalMain.cfgDebugOutput) {
				System.out.println("Registering Item " + is
						+ " failed. Multiple crafting recipes.");
			}
			return;
		}
		IRecipe r = matching.get(0);
		if (r instanceof ShapedRecipes) {
			autoRegisterWithItemStacks(is, ((ShapedRecipes) r).recipeItems);
		} else if (r instanceof ShapedOreRecipe) {
			Object[] input = ((ShapedOreRecipe) r).getInput();

			ItemStack[] recipeItems = new ItemStack[input.length];
			for (int i = 0; i < input.length; i++) {
				if (input[i] instanceof ItemStack) {
					recipeItems[i] = (ItemStack) input[i];
				} else if (input[i] instanceof ArrayList) {
					recipeItems[i] = (ItemStack) ((ArrayList) input[i]).get(0);
				}
			}
			autoRegisterWithItemStacks(is, recipeItems);
		} else {
			if (ChaosCrystalMain.cfgDebugOutput) {
				System.out.println("Registering Item " + is
						+ " failed. No supported crafting recipes.");
			}
		}
		// TODO: Add smelting recipes
	}

	private NodeCrafting autoRegisterWithItemStacks(ItemStack is,
			ItemStack[] recipeItems) {
		int[] aspectArray = new int[Aspects.ASPECTS.length];
		List<ItemStack> degraded = new ArrayList<ItemStack>();
		List<Node> parentNodes = new ArrayList<Node>();
		for(ItemStack crafting : recipeItems) {
			if(crafting == null || crafting.stackSize <= 0) {
				if(ChaosCrystalMain.cfgDebugOutput) {
        			System.out.println("Registering Item " + is + " failed. Crafting recipe for subsequent Item " + crafting + " has zero stack size.");
				}
				return null;
			}
			List<Node> nodes = getExtractionsFrom(crafting);
			if(nodes.isEmpty()) {
				autoRegisterDegradation(crafting);
				nodes = getExtractionsFrom(crafting);
				if(nodes.isEmpty()) {
					if(ChaosCrystalMain.cfgDebugOutput) {
	        			System.out.println("Registering Item " + is + " failed. Could not find aspects for subsequent Item " + crafting + ".");
					}
					return null;
				}
			}
			
			for(int a = 0; a < Aspects.ASPECTS.length; a++) {
				aspectArray[a] += nodes.get(0).getAspects()[a] / crafting.stackSize;
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
				parentNodes.add(nodes.get(0));
			}
		}
		
		return new NodeCrafting(is.copy(), degraded.toArray(new ItemStack[degraded.size()]), parentNodes.toArray(new Node[parentNodes.size()]), aspectArray);
	}

	public void debugOutput() {
//		System.out.println("Degradation List:");
//		for(Integer key : degradations.keySet()) {
//			Degradation[] degradationList = degradations.get(key);
//			System.out.println(Block.blocksList[key].getLocalizedName() + " (" + key + ") Transforms to:");
//			for (int i = 0; i < degradationList.length; i++) {
//				for(int d = 0; d < degradationList[i].degraded.length; d++) {
//					if(degradationList[i].degraded[d].itemID == 0) {
//						System.out.println("Air");
//					} else {
//						System.out.println(Block.blocksList[degradationList[i].degraded[d].itemID].getLocalizedName() + " - " + degradationList[i].degraded[d].getItemDamage());
//					}
//				}
//			}
//		}
//		
//		System.out.println("Inverse Degradation List:");
//		for(Integer key : degradationsInverse.keySet()) {
//			Degradation[] degradationList = degradationsInverse.get(key);
//			if(key == 0) {
//				continue;
//			}
//			System.out.println(Block.blocksList[key].getLocalizedName() + " Transforms to:");
//			for (int i = 0; i < degradationList.length; i++) {
//				if(degradationList[i].source.itemID == 0) {
//					System.out.println("Air");
//				} else {
//					System.out.println(Block.blocksList[degradationList[i].source.itemID].getLocalizedName() + " - " + degradationList[i].source.getItemDamage());
//				}
//			}
//		}
	}
}
