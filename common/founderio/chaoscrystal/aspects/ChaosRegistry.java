package founderio.chaoscrystal.aspects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.aspects.modules.ModuleVanillaWorldgen;
import founderio.util.ItemUtil;

public class ChaosRegistry {

	private List<Node> nodes;
	private List<AspectModule> modules;
	private HashMap<Item, Repair> repairs;
	
	private List<Node> creations;
	private boolean creationsDirty = true;

	public ChaosRegistry() {
		repairs = new HashMap<Item, Repair>();
		nodes = new ArrayList<Node>();
		modules = new ArrayList<AspectModule>();
		creations = Collections.unmodifiableList(new ArrayList<Node>());

		registerAspectModule(new ModuleVanillaWorldgen());
	}
	
	public void registerAspectModule(AspectModule module) {
		modules.add(module);
		module.registerNodes(this);
	}
	
	public void addNode(Node node) {
		nodes.add(node);
		creationsDirty = true;
	}

	public List<Node> getCreations() {
		if(creationsDirty) {
			List<Node> creations = new ArrayList<Node>();
			for (Node node : nodes) {
				Node[] parents = node.getParents();
				if (parents.length == 1 && parents[0] == ModuleVanillaWorldgen.AIR) {
					creations.add(node);
				}
			}
			this.creations = Collections.unmodifiableList(creations);
			creationsDirty = false;
		}
		
		return new ArrayList<Node>(creations);
	}

	public Repair getRepair(Item item) {
		return repairs.get(item);
	}

	public void registerRepair(Item item, String[] aspects, int[] amounts) {
		repairs.put(item, new Repair(item, aspects, amounts));
	}

	public List<Node> getInfusionsFrom(ItemStack is) {
		List<Node> infusions = new ArrayList<Node>();
		if(is == null) {
			return infusions;
		}
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
		if(is == null) {
			return extractions;
		}
		for (Node node : nodes) {
			if (node.matchesItemStack(is)) {
				extractions.add(node);
			}
		}
		return extractions;
	}

	@SuppressWarnings("rawtypes")
	public Node autoRegisterDegradation(ItemStack is) {
		if(is == null) {
			return null;
		}
		@SuppressWarnings("unchecked")
		List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
		List<IRecipe> matching = new ArrayList<IRecipe>();
		for (IRecipe r : recipes) {
			ItemStack output = r.getRecipeOutput();
			if (ItemUtil.itemsMatch(is, output)) {
				matching.add(r);
			}
		}
		
		if (!matching.isEmpty()) {
			IRecipe r = matching.get(0);
			if (r instanceof ShapedRecipes) {
				return autoRegisterWithItemStacks(r.getRecipeOutput(), ((ShapedRecipes) r).recipeItems, 0);
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
				return autoRegisterWithItemStacks(r.getRecipeOutput(), recipeItems, 0);
			} else {
				if (ChaosCrystalMain.cfgDebugOutput) {
					System.out.println("Registering Item " + is
							+ " failed. No supported crafting recipes.");
				}
				return null;
			}
		}

		Map<?, ?> smelting = FurnaceRecipes.smelting().getSmeltingList();
		//Key: ID, Value: Result
		
		for (Entry<?, ?> kvp : smelting.entrySet()) {
			ItemStack output = (ItemStack) kvp.getValue();
			ItemStack input = (ItemStack) kvp.getKey();
			if (ItemUtil.itemsMatch(is, output)) {
				return autoRegisterWithItemStacks(output, new ItemStack[] { input }, 1);
			}
		}
		
		
		if (ChaosCrystalMain.cfgDebugOutput) {
			System.out.println("Registering Item " + is
					+ " failed. No (supported) recipes.");
		}
		return null;
	}

	private Node autoRegisterWithItemStacks(ItemStack is,
			ItemStack[] recipeItems, int mode) {
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
				Node autoReg = autoRegisterDegradation(crafting);
				if(autoReg == null) {
					if(ChaosCrystalMain.cfgDebugOutput) {
	        			System.out.println("Registering Item " + is + " failed. Could not find aspects for subsequent Item " + crafting + ".");
					}
					return null;
				}
				nodes.add(autoReg);
				addNode(autoReg);
			}
			
			for(int a = 0; a < Aspects.ASPECTS.length; a++) {
				aspectArray[a] += nodes.get(0).getAspects()[a] / crafting.stackSize;
			}
			boolean added = false;
			for(ItemStack dis : degraded) {
				if(ItemUtil.itemsMatch(dis, crafting)) {
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
		switch(mode) {
		case 0:
			return new NodeCrafting(is.copy(), degraded.toArray(new ItemStack[degraded.size()]), parentNodes.toArray(new Node[parentNodes.size()]), aspectArray);
		case 1:
			return new NodeSmelting(is.copy(), degraded.get(0), parentNodes.get(0), aspectArray);
		default:
			// Should never happen...
			return null;
		}
		
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
