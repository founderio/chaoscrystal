package founderio.chaoscrystal.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import founderio.chaoscrystal.blocks.BlockSproutingCrystal;

public class ItemBlockSproutingCrystal extends ItemBlockWithMetadata {

	public ItemBlockSproutingCrystal(Block theBlock) {
		super(theBlock, theBlock);
	}

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		int idx = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0,
				BlockSproutingCrystal.metaList.length - 1);
		return "tile." + BlockSproutingCrystal.metaList[idx];
	}

	@Override
	public int getMetadata(int par1) {
		return par1;
	}
}
