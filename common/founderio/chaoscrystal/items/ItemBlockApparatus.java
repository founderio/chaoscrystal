package founderio.chaoscrystal.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class ItemBlockApparatus extends ItemBlock {

	public ItemBlockApparatus(Block theBlock) {
		super(theBlock);
		this.setMaxStackSize(16);
		this.setMaxDamage(0);
	}
}
