package founderio.chaoscrystal.aspects;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameData;
import founderio.util.ItemUtil;


public class NodePoint {
	private String uniqueName = "";
	private NodePointType type = NodePointType.ITEM;
	private int meta = ItemUtil.WILDCARD_META;

	private transient boolean initialized = false;
	private transient Item item;
	private transient Block block;

	private void initialize() {
		if(initialized) {
			return;
		}
		initialized = true;
		if(type == NodePointType.BLOCK) {
			block = GameData.getBlockRegistry().getRaw(uniqueName);
			if(block != null) {
				item = Item.getItemFromBlock(block);
			}
		} else if(type==NodePointType.ITEM) {
			item = GameData.getItemRegistry().getRaw(uniqueName);
			if(item != null && item instanceof ItemBlock) {
				block = Block.getBlockFromItem(item);
			}
		} else {
			throw new RuntimeException("Umm.. WHAT? Unexpected Enum value!");
		}
	}

	public String getUniqueName() {
		return uniqueName;
	}

	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}

	public NodePointType getType() {
		return type;
	}

	public void setType(NodePointType type) {
		this.type = type;
	}

	public int getMeta() {
		return meta;
	}

	public void setMeta(int meta) {
		this.meta = meta;
	}

	public boolean matches(Item item, int meta) {
		initialize();
		return item == this.item && ItemUtil.metaMatch(meta, this.meta);
	}

	public boolean matches(Block block, int meta) {
		initialize();
		return block == this.block && ItemUtil.metaMatch(meta, this.meta);
	}

	public ItemStack createItemStack() {
		initialize();
		if(item == null) {
			return null;
		}
		return new ItemStack(item, 1, meta);
	}

	public Block getBlock() {
		initialize();
		return block;
	}

	public Item getItem() {
		initialize();
		return item;
	}
}