package founderio.chaoscrystal.aspects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.aspects.modules.ModuleVanillaWorldgen;

public class ChaosRegistry {

	private List<Node> nodes;
	private List<AspectModule> modules;
	private HashMap<Item, Repair> repairs;

	public ChaosRegistry() {
		repairs = new HashMap<Item, Repair>();
		nodes = new ArrayList<Node>();
		modules = new ArrayList<AspectModule>();
	}
	
	public boolean isIgnoreItem(Item item, boolean extract) {
		if(item == null) {
			return true;
		}
		if(extract && item == ChaosCrystalMain.itemLifelessShard) {
			return true;
		}
		if(item instanceof ItemBlock) {
			Block block = Block.getBlockFromItem(item);
			return isIgnoreBlock(block, extract);
		}
		
		return false;
	}
	
	public boolean isIgnoreBlock(Block block, boolean extract) {
		if(block == null) {
			return true;
		}
		if(block == Blocks.air) {
			return true;
		}
		if(extract && block == ChaosCrystalMain.blockLifeless) {
			return true;
		}
		return false;
	}
	
	public void registerAspectModule(AspectModule module) {
		if(module == null) {
			//TODO: log!
			return;
		}
		modules.add(module);
		nodes.addAll(module.getNodes());
	}
	
	public void addNode(Node node) {
		nodes.add(node);
	}
	
	public int getDegradationNodeCount() {
		return nodes.size();
	}
	
	public Node getDegradationNode(int index) {
		return nodes.get(index);
	}

	public Repair getRepair(Item item) {
		return repairs.get(item);
	}

	public void registerRepair(Item item, int[] aspects) {
		repairs.put(item, new Repair(item, aspects));
	}

	public List<Node> getInfusionsFrom(ItemStack is) {
		List<Node> infusions = new ArrayList<Node>();
		if(is == null) {
			return infusions;
		}
		for (Node node : nodes) {
			if (node == null) {
				return null;
			}
			if (node.matchesOnLesseer(is.getItem(), is.getItemDamage())) {
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
			if (node == null) {
				return null;
			}
			if (node.matchesOnGreater(is.getItem(), is.getItemDamage())) {
				extractions.add(node);
			}
		}
		return extractions;
	}

	public void debugOutput() {
		//TODO: add that output again...
		
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
