package founderio.chaoscrystal.items;

import founderio.chaoscrystal.Constants;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class ItemBlockBase extends ItemBlock {

	public ItemBlockBase(int par1) {
		super(par1);
		this.setHasSubtypes(true);
	}
	
	String[] nameList = new String[]{
		Constants.ID_BLOCK_BASE_CRYSTALLINE,
		Constants.ID_BLOCK_BASE_CRYSTAL
	};
	
	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		int idx = MathHelper.clamp_int(par1ItemStack.itemID, 0, nameList.length - 1);
		return nameList[idx];
	}
	
	@Override
	public int getMetadata(int par1) {
		return par1;
	}

}
