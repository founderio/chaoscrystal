package founderio.chaoscrystal.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.Constants;

public class BlockBase extends Block {

	public static final String[] metaList = new String[] {
			Constants.ID_BLOCK_CRYSTALLINE_ENERGY,
			Constants.ID_BLOCK_CRYSTAL,
			Constants.ID_BLOCK_CRYSTALLINE_LIGHT,
			Constants.ID_BLOCK_CRYSTALLINE_ENERGY_CRACKED,
			Constants.ID_BLOCK_CRYSTAL_CRACKED,
			Constants.ID_BLOCK_CRYSTALLINE_LIGHT_CRACKED, };
	public IIcon[] iconList;

	public BlockBase() {
		super(Material.glass);
		this.setHardness(4);
		this.setLightLevel(0.2f);
		this.setResistance(1.5f);
		this.setStepSound(Block.soundTypeGlass);
		this.setHarvestLevel("pickaxe", 1);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs creativeTabs,
			List list) {
		for (int i = 0; i < metaList.length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		iconList = new IIcon[metaList.length];
		iconList[0] = par1IconRegister.registerIcon(Constants.MOD_ID + ":crystalline_energy");
		iconList[1] = par1IconRegister.registerIcon(Constants.MOD_ID + ":crystal");
		iconList[2] = par1IconRegister.registerIcon(Constants.MOD_ID + ":crystalline_light");
		iconList[3] = par1IconRegister.registerIcon(Constants.MOD_ID + ":crystalline_energy_cracked");
		iconList[4] = par1IconRegister.registerIcon(Constants.MOD_ID + ":crystal_cracked");
		iconList[5] = par1IconRegister.registerIcon(Constants.MOD_ID + ":crystalline_light_cracked");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int par1, int par2) {
		int idx = MathHelper.clamp_int(par2, 0, iconList.length - 1);
		return iconList[idx];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int damageDropped(int par1) {
		return MathHelper.clamp_int(par1, 0, metaList.length - 1);
	}
	
	@Override
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int meta) {
		checkBlockCracksAround(world, x, y, z);
	}
	
	@Override
	public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion) {
		checkBlockCracksAround(world, x, y, z);
	}
	
	public void checkBlockCrack(World world, int x, int y, int z) {
		if(world.getBlock(x, y, z) == ChaosCrystalMain.blockBase) {
			int meta = world.getBlockMetadata(x, y, z);
			if(meta < 2) {
				// Crack
				world.setBlock(x, y, z, ChaosCrystalMain.blockBase, meta + 3, 1 + 2);
			} else if(meta < 4) {
				// Break
				world.setBlockToAir(x, y, z);
				ChaosCrystalMain.blockBase.dropBlockAsItem(world, x, y, z, meta, 0);
			}
		}
	}
	
	public void checkBlockCracksAround(World world, int x, int y, int z) {
		checkBlockCrack(world, x + 1, y, z);
		checkBlockCrack(world, x, y + 1, z);
		checkBlockCrack(world, x, y, z + 1);
		checkBlockCrack(world, x - 1, y, z);
		checkBlockCrack(world, x, y - 1, z);
		checkBlockCrack(world, x, y, z - 1);
	}

}
