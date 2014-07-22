package founderio.chaoscrystal.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.Config;
import founderio.chaoscrystal.Constants;

public class BlockSproutingCrystal extends Block {

	public static final String[] metaList = new String[] {
		Constants.ID_BLOCK_CRYSTALLINE_ENERGY_SPROUT,
		Constants.ID_BLOCK_CRYSTALLINE_CHAOS_SPROUT,
		Constants.ID_BLOCK_CRYSTALLINE_LIGHT_SPROUT,
		Constants.ID_BLOCK_CRYSTAL_CLEAR_SPROUT, };
	public IIcon[] iconList;

	public BlockSproutingCrystal() {
		super(Material.glass);
		setHardness(4);
		setLightLevel(0.2f);
		setResistance(1.5f);
		setStepSound(Block.soundTypeGlass);
		this.setHarvestLevel("pickaxe", 1);
		setTickRandomly(true);
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
		iconList[0] = par1IconRegister.registerIcon(Constants.MOD_ID + ":"
				+ "crystalline_energy_sprout");
		iconList[1] = par1IconRegister.registerIcon(Constants.MOD_ID + ":"
				+ "crystalline_chaos_sprout");
		iconList[2] = par1IconRegister.registerIcon(Constants.MOD_ID + ":"
				+ "crystalline_light_sprout");
		iconList[3] = par1IconRegister.registerIcon(Constants.MOD_ID + ":"
				+ "crystal_clear_sprout");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int par1, int par2) {
		int idx = MathHelper.clamp_int(par2, 0, metaList.length - 1);
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

	/**
	 * Returns true if the given side of this block type should be rendered (if it's solid or not), if the adjacent
	 * block is at the given coordinates. Args: blockAccess, x, y, z, side
	 */
	@Override
	public boolean isBlockSolid(IBlockAccess blockAccess, int x, int y, int z, int side)
	{
		Block adjacent = blockAccess.getBlock(x, y, z);
		if(adjacent == this || adjacent == ChaosCrystalMain.blockBase) {
			ForgeDirection dir = ForgeDirection.getOrientation(side);
			int adjacentMeta = blockAccess.getBlockMetadata(x, y, z);
			int thisMeta = blockAccess.getBlockMetadata(x - dir.offsetX, y - dir.offsetY, z - dir.offsetZ);
			return thisMeta != adjacentMeta;
		} else {
			return true;
		}
	}

	/**
	 * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
	 * coordinates.  Args: blockAccess, x, y, z, side
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int side)
	{
		Block adjacent = blockAccess.getBlock(x, y, z);
		if(adjacent == this || adjacent == ChaosCrystalMain.blockBase) {
			ForgeDirection dir = ForgeDirection.getOrientation(side);
			int adjacentMeta = blockAccess.getBlockMetadata(x, y, z);
			int thisMeta = blockAccess.getBlockMetadata(x - dir.offsetX, y - dir.offsetY, z - dir.offsetZ);
			return thisMeta != adjacentMeta;
		} else {
			return true;
		}
	}

	@Override
	public int damageDropped(int par1) {
		return MathHelper.clamp_int(par1, 0, metaList.length - 1);
	}

	@Override
	public Item getItemDropped(int meta, Random rand, int fortune) {
		// Drop regular crystal block
		return Item.getItemFromBlock(ChaosCrystalMain.blockBase);
	}

	@Override
	public int getDamageValue(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return MathHelper.clamp_int(meta, 0, metaList.length - 1);
	}

	@Override
	protected boolean canSilkHarvest() {
		return true;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {

		int meta = world.getBlockMetadata(x, y, z);

		int currentGrowthHeight = 0;

		/*
		 * Growth will stop at any time if a block was "grown" unless insta-growth is enabled.
		 */
		boolean allowGrowth = false;

		for(int dy = 0; dy < Config.spikeMaxHeight; dy ++) {
			Block bl1 = world.getBlock(x, y + dy, z);
			Block bl2 = world.getBlock(x, y - dy, z);
			Block bl3 = world.getBlock(x, y, z + dy);
			Block bl4 = world.getBlock(x, y, z - dy);
			Block bl5 = world.getBlock(x + dy, y, z);
			Block bl6 = world.getBlock(x - dy, y, z);
			allowGrowth = isAcceptedBlockOrAir(bl1, world, x, y + dy, z)
					&& isAcceptedBlockOrAir(bl2, world, x, y - dy, z)
					&& isAcceptedBlockOrAir(bl3, world, x, y, z + dy)
					&& isAcceptedBlockOrAir(bl4, world, x, y, z - dy)
					&& isAcceptedBlockOrAir(bl5, world, x + dy, y, z)
					&& isAcceptedBlockOrAir(bl6, world, x - dy, y, z);
			// Stop crystal growing in length if there is a block in the way somewhere
			// But still grow in width as far as possible (encased blocks can happen)
			if(allowGrowth) {
				if(Config.enableInstaGrowth || isCrystalBlock(bl1) && isCrystalBlock(bl2) && isCrystalBlock(bl3) && isCrystalBlock(bl4) && isCrystalBlock(bl5) && isCrystalBlock(bl6)) {
					// Check for current growth height (more guessed than checked)
					// This decides how big the crystal will grow width-wise
					currentGrowthHeight = dy;
					// Repair all cracked pieces on the way
					if (eatBlock(world, x, y + dy, z, meta)) {
						return;
					}
					if (eatBlock(world, x, y - dy, z, meta)) {
						return;
					}
					if (eatBlock(world, x, y, z + dy, meta)) {
						return;
					}
					if (eatBlock(world, x, y, z - dy, meta)) {
						return;
					}
					if (eatBlock(world, x + dy, y, z, meta)) {
						return;
					}
					if (eatBlock(world, x - dy, y, z, meta)) {
						return;
					}
				} else {
					// Current growth height reached.
					break;
				}
			} else {
				break;
			}
		}
		float spikeRadiusMax = currentGrowthHeight/(float)Config.spikeMaxHeight;
		spikeRadiusMax *= Config.spikeMaxRadius;

		// Grow the spikes
		for(int dy = 0; dy < currentGrowthHeight; dy ++) {
			// Spike radius at current level (will be thickest at the center)
			float spikeRadiusCurLevel = 1f - (float)dy/currentGrowthHeight/2;
			spikeRadiusCurLevel *= spikeRadiusMax;

			for(int dx = 1; dx < spikeRadiusCurLevel; dx++) {
				for(int dz = 1; dz < spikeRadiusCurLevel; dz++) {
					// Up
					if(eatBlock(world, x + dx, y + dy, z + dz, meta)) {
						return;
					}
					if(eatBlock(world, x - dx, y + dy, z - dz, meta)) {
						return;
					}
					if(eatBlock(world, x + dx, y + dy, z - dz, meta)) {
						return;
					}
					if(eatBlock(world, x - dx, y + dy, z + dz, meta)) {
						return;
					}
					//Down
					if(eatBlock(world, x + dx, y - dy, z + dz, meta)) {
						return;
					}
					if(eatBlock(world, x - dx, y - dy, z - dz, meta)) {
						return;
					}
					if(eatBlock(world, x + dx, y - dy, z - dz, meta)) {
						return;
					}
					if(eatBlock(world, x - dx, y - dy, z + dz, meta)) {
						return;
					}
					//+x
					if(eatBlock(world, x + dy, y + dx, z + dz, meta)) {
						return;
					}
					if(eatBlock(world, x + dy, y - dx, z - dz, meta)) {
						return;
					}
					if(eatBlock(world, x + dy, y + dx, z - dz, meta)) {
						return;
					}
					if(eatBlock(world, x + dy, y - dx, z + dz, meta)) {
						return;
					}
					//-x
					if(eatBlock(world, x - dy, y + dx, z + dz, meta)) {
						return;
					}
					if(eatBlock(world, x - dy, y - dx, z - dz, meta)) {
						return;
					}
					if(eatBlock(world, x - dy, y + dx, z - dz, meta)) {
						return;
					}
					if(eatBlock(world, x - dy, y - dx, z + dz, meta)) {
						return;
					}
					//+z
					if(eatBlock(world, x + dx, y + dz, z + dy, meta)) {
						return;
					}
					if(eatBlock(world, x - dx, y - dz, z + dy, meta)) {
						return;
					}
					if(eatBlock(world, x + dx, y - dz, z + dy, meta)) {
						return;
					}
					if(eatBlock(world, x - dx, y + dz, z + dy, meta)) {
						return;
					}
					//-z
					if(eatBlock(world, x + dx, y + dz, z - dy, meta)) {
						return;
					}
					if(eatBlock(world, x - dx, y - dz, z - dy, meta)) {
						return;
					}
					if(eatBlock(world, x + dx, y - dz, z - dy, meta)) {
						return;
					}
					if(eatBlock(world, x - dx, y + dz, z - dy, meta)) {
						return;
					}
				}
			}
		}

		if(!allowGrowth) {
			return;
		}

		// Finally grow the tips longer if everything else is intact.
		if(eatBlock(world, x, y + currentGrowthHeight + 1, z, meta)) {
			return;
		}
		if(eatBlock(world, x, y + currentGrowthHeight - 1, z, meta)) {
			return;
		}
		if(eatBlock(world, x + currentGrowthHeight + 1, y, z, meta)) {
			return;
		}
		if(eatBlock(world, x + currentGrowthHeight - 1, y, z, meta)) {
			return;
		}
		if(eatBlock(world, x, y, z + currentGrowthHeight + 1, meta)) {
			return;
		}
		if(eatBlock(world, x, y, z + currentGrowthHeight - 1, meta)) {
			return;
		}

		//TODO: Create secondary & tertiary sprouts (limit those, so they don't take over the world)
	}

	private boolean eatBlock(World world, int x, int y, int z, int meta) {
		Block currentWorldBlock = world.getBlock(x, y, z);
		if(currentWorldBlock == Blocks.bedrock) {
			return false;
		}
		if(currentWorldBlock == ChaosCrystalMain.blockSproutingCrystal) {
			return false;
		}
		if(currentWorldBlock == ChaosCrystalMain.blockBase) {
			int worldMeta = world.getBlockMetadata(x, y, z);
			if(ChaosCrystalMain.blockBase.isCrackedVersion(worldMeta)) {
				int uncracked = ChaosCrystalMain.blockBase.getUncrackedVersion(worldMeta);
				world.setBlock(x, y, z, ChaosCrystalMain.blockBase, uncracked, 1 + 2);
				return !Config.enableInstaGrowth;
			} else {
				return false;
			}
			//TODO: if meta is different than sprout, mix some of these blocks in the rest somehow?
		}
		//TODO: check for blocks that limit growth (red slowed by blue) or speed up growth (red sped up by yellow) or nullify each other (yellow <> blue)

		if(isAcceptedBlockOrAir(currentWorldBlock, world, x, y, z)) {
			world.setBlock(x, y, z, ChaosCrystalMain.blockBase, meta, 1 + 2);
			return !Config.enableInstaGrowth;
		} else {
			return false;
		}

	}

	public static boolean isAcceptedBlock(Block block) {
		return block == Blocks.stone || block == Blocks.dirt || block == Blocks.gravel || block == Blocks.grass || block == Blocks.sandstone || block == Blocks.netherrack || block == Blocks.water;
	}

	public static boolean isCrystalBlock(Block block) {
		return block == ChaosCrystalMain.blockSproutingCrystal || block == ChaosCrystalMain.blockBase;
	}

	public static boolean isAcceptedBlockOrAir(Block block, World world, int x, int y, int z) {
		return block.isAir(world, x, z, z) || isAcceptedBlock(block) || isCrystalBlock(block);
	}


	@Override
	public void onBlockClicked(World world, int x,
			int y, int z, EntityPlayer player) {
		// Just DEV...
		if(Config.enableClickTick) {
			updateTick(world, x, y, z, ChaosCrystalMain.rand);
		}
	}
}
