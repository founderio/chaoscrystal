package founderio.chaoscrystal.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import founderio.chaoscrystal.blocks.BlockBase;

public class ItemBlockBase extends ItemBlockWithMetadata {

	public ItemBlockBase(Block theBlock) {
		super(theBlock, theBlock);
	}

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		int idx = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0,
				BlockBase.metaList.length - 1);
		return "tile." + BlockBase.metaList[idx];
	}

	@Override
	public int getMetadata(int par1) {
		return par1;
	}
}
