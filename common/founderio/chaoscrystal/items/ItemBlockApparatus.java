package founderio.chaoscrystal.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import founderio.chaoscrystal.blocks.BlockApparatus;

public class ItemBlockApparatus extends ItemBlockWithMetadata {

	public ItemBlockApparatus(int par1, Block theBlock) {
		super(par1, theBlock);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		int idx = MathHelper.clamp_int(par1ItemStack.itemID, 0, BlockApparatus.metaList.length - 1);
		return "tile." + BlockApparatus.metaList[idx];
	}
}
