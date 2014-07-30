package founderio.chaoscrystal.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.Config;
import founderio.chaoscrystal.Constants;

public class BlockBase extends Block {

	private IIcon[] iconList;

	public BlockBase() {
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
		for (int i = 0; i < Constants.METALIST_BLOCK_BASE.length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	/**
	 * Returns true if the given side of this block type should be rendered (if it's solid or not), if the adjacent
	 * block is at the given coordinates. Args: blockAccess, x, y, z, side
	 */
	@Override
	public boolean isBlockSolid(IBlockAccess blockAccess, int x, int y, int z, int side)
	{
		Block adjacent = blockAccess.getBlock(x, y, z);
		if(adjacent == this) {
			// Only display cracked versions or different color versions
			ForgeDirection dir = ForgeDirection.getOrientation(side);
			int adjacentMeta = blockAccess.getBlockMetadata(x, y, z);
			int thisMeta = blockAccess.getBlockMetadata(x - dir.offsetX, y - dir.offsetY, z - dir.offsetZ);
			// Convert sprouts to regular, so only cracked or different color will be different
			return getNonSproutVersion(thisMeta) != getNonSproutVersion(adjacentMeta);
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
		return isBlockSolid(blockAccess, x, y, z, side);
	}

	@Override
	public Item getItemDropped(int meta, Random rand, int fortune) {
		if(isCrackedVersion(meta)) {
			return ChaosCrystalMain.itemShard;
		} else {
			return Item.getItemFromBlock(this);
		}
	}

	@Override
	public int getDamageValue(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return MathHelper.clamp_int(meta, 0, Constants.METALIST_BLOCK_BASE.length - 1);
	}

	@Override
	public int damageDropped(int meta) {
		int idx = MathHelper.clamp_int(meta, 0, Constants.METALIST_BLOCK_BASE.length - 1);
		if(isCrackedVersion(idx)) {
			// Returns shard meta
			return getUncrackedVersion(idx);
		} else {
			//Returns Block Meta, but sprouts will be converted to regular ones (only silk touch)
			return getNonSproutVersion(idx);
		}
	}

	@Override
	public int quantityDropped(int meta, int fortune, Random random) {
		if(isCrackedVersion(meta)) {
			// Shards
			return 4 + (int)(fortune / 1.5);
		} else {
			// Blocks
			return 1;
		}
	}
	
	@Override
	protected boolean canSilkHarvest() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {
		iconList = new IIcon[Constants.METALIST_BLOCK_BASE.length];
		iconList[0] = ir.registerIcon(Constants.MOD_ID + ":crystalline_energy");
		iconList[1] = ir.registerIcon(Constants.MOD_ID + ":crystalline_chaos");
		iconList[2] = ir.registerIcon(Constants.MOD_ID + ":crystalline_light");
		iconList[3] = ir.registerIcon(Constants.MOD_ID + ":crystal_clear");

		iconList[4] = ir.registerIcon(Constants.MOD_ID + ":crystalline_energy_cracked");
		iconList[5] = ir.registerIcon(Constants.MOD_ID + ":crystalline_chaos_cracked");
		iconList[6] = ir.registerIcon(Constants.MOD_ID + ":crystalline_light_cracked");
		iconList[7] = ir.registerIcon(Constants.MOD_ID + ":crystal_clear_cracked");
		
		iconList[8] = ir.registerIcon(Constants.MOD_ID + ":crystalline_energy_sprout");
		iconList[9] = ir.registerIcon(Constants.MOD_ID + ":crystalline_chaos_sprout");
		iconList[10] = ir.registerIcon(Constants.MOD_ID + ":crystalline_light_sprout");
		iconList[11] = ir.registerIcon(Constants.MOD_ID + ":crystal_clear_sprout");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		int idx = MathHelper.clamp_int(meta, 0, Constants.METALIST_BLOCK_BASE.length - 1);
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
	public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion) {
		if(!Config.crackCrystalsOnExplosion){
			return;
		}
		checkBlockCracksAround(world, x, y, z, 0.9f, 0);
	}
	
	public boolean isRegularVersion(int meta) {
		int idx = MathHelper.clamp_int(meta, 0, Constants.METALIST_BLOCK_BASE.length - 1);
		return idx < 4;
	}
	
	public boolean isCrackedVersion(int meta) {
		int idx = MathHelper.clamp_int(meta, 0, Constants.METALIST_BLOCK_BASE.length - 1);
		return idx > 3 && idx < 8;
	}
	
	public boolean isSproutVersion(int meta) {
		int idx = MathHelper.clamp_int(meta, 0, Constants.METALIST_BLOCK_BASE.length - 1);
		return idx > 7;
	}

	/**
	 * Cracked > Regular
	 * @param meta
	 * @return
	 */
	public int getUncrackedVersion(int meta) {
		int idx = MathHelper.clamp_int(meta, 0, Constants.METALIST_BLOCK_BASE.length - 1);
		if(isCrackedVersion(idx)) {
			return idx - 4;
		} else {
			return idx;
		}
	}

	/**
	 * Regular > Cracked
	 * @param meta
	 * @return
	 */
	public int getCrackedVersion(int meta) {
		int idx = MathHelper.clamp_int(meta, 0, Constants.METALIST_BLOCK_BASE.length - 1);
		if(isCrackedVersion(idx)) {
			return idx;
		} else {
			return idx + 4;
		}
	}

	/**
	 * Sprout > Regular
	 * @param meta
	 * @return
	 */
	public int getNonSproutVersion(int meta) {
		int idx = MathHelper.clamp_int(meta, 0, Constants.METALIST_BLOCK_BASE.length - 1);
		if(isSproutVersion(idx)) {
			return idx - 8;
		} else {
			return idx;
		}
	}

	/**
	 * Cracked > Sprout
	 * Regular > Sprout
	 * @param meta
	 * @return
	 */
	public int getSproutVersion(int meta) {
		int idx = MathHelper.clamp_int(meta, 0, Constants.METALIST_BLOCK_BASE.length - 1);
		if(isCrackedVersion(idx)) {
			return idx + 4;
		} else if(isSproutVersion(idx)) {
			return idx;
		} else {
			return idx + 8;
		}
	}

	/**
	 * Cracked > Regular
	 * Sprout > Regular
	 * @param meta
	 * @return
	 */
	public int getRegularVersion(int meta) {
		int idx = MathHelper.clamp_int(meta, 0, Constants.METALIST_BLOCK_BASE.length - 1);
		if(isCrackedVersion(idx)) {
			return idx - 4;
		} else if(isSproutVersion(idx)) {
			return idx - 8;
		} else {
			return idx;
		}
	}

	@Override
	public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player) {
		if(!Config.crackCrystalsOnHarvest) {
			return;
		}
		// Don't break stuff if in creative mode
		if(player.capabilities.isCreativeMode) {
			return;
		}
		// Less likely to break stuff if the player uses silk touch
		boolean hasSilkTouch = Config.crackCrystalsLessLikelyWithSilkTouch && EnchantmentHelper.getSilkTouchModifier(player);
		int fortune = EnchantmentHelper.getFortuneModifier(player);
		checkBlockCracksAround(world, x, y, z, hasSilkTouch ? 0.45f : 0.90f, fortune);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x,
			int y, int z, Entity entity) {
		double motion = Vec3.createVectorHelper(entity.motionX, entity.motionY, entity.motionZ).lengthVector();
		handleCollision(world, x, y, z, entity, motion);
	}

	@Override
	public void onFallenUpon(World world, int x,
			int y, int z, Entity entity,
			float force) {
		handleCollision(world, x, y, z, entity, force);
	}

	/**
	 * Handles collisions (regular and fall) with an entity. This will crack the
	 * collided crystal blocks and respects configuration settings, feather
	 * falling, creative mode etc.
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param entity
	 *            Collided entity for enchantment & creative mode check. Can be
	 *            null.
	 * @param force
	 *            Impact force, >2.5 for light crack and >5 for bigger crack.
	 */
	public void handleCollision(World world, int x,
			int y, int z, Entity entity,
			double force) {
		if(world.isRemote) {
			return;
		}
		if(!Config.crackCrystalsOnCollition) {
			return;
		}
		// Don't Destroy stuff in Creative Mode
		if(entity instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)entity;
			if(ep.capabilities.isCreativeMode) {
				return;
			}
		}
		if(Config.crackCrystalsLessLikelyWithFeatherFalling && entity instanceof EntityLivingBase) {
			EntityLivingBase ep = (EntityLivingBase)entity;
			int featherFalling = EnchantmentHelper.getEnchantmentLevel(Enchantment.featherFalling.effectId, ep.getEquipmentInSlot(1));
			if(ChaosCrystalMain.rand.nextInt(10) < featherFalling) {
				return;
			}
		}


		// Bigger smash or smaller depending on force
		if(force > 5) {
			boolean result1 = checkBlockCrack(world, x, y, z, 1, 0);
			boolean result2 = checkBlockCracksAround(world, x, y, z, 1, 0);
			if(result1 || result2) {
				world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(this) + (world.getBlockMetadata(x, y, z) << 12));
			}
		} else if(force > 2.5f) {
			if(checkBlockCrack(world, x, y, z, 1, 0)) {
				world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(this) + (world.getBlockMetadata(x, y, z) << 12));
			}
		}
	}

	/**
	 * Checks the block at the specified coordinates and cracks it if possible.
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param chance
	 * @param fortune
	 * @return true if the block was cracked.
	 */
	public boolean checkBlockCrack(World world, int x, int y, int z, float chance, int fortune) {
		if(world.getBlock(x, y, z) == ChaosCrystalMain.blockBase) {
			if(ChaosCrystalMain.rand.nextFloat() > chance) {
				return false;
			}
			int meta = MathHelper.clamp_int(world.getBlockMetadata(x, y, z), 0, Constants.METALIST_BLOCK_BASE.length - 1);
			if(isCrackedVersion(meta)) {
				// Break
				world.setBlockToAir(x, y, z);
				this.dropBlockAsItem(world, x, y, z, meta, fortune);
				return true;
			} else if(isSproutVersion(meta)) {
				// Do Nothing? TODO: Decide.
				return false;
			} else {
				// Crack
				int cracked = getCrackedVersion(meta);
				world.setBlock(x, y, z, ChaosCrystalMain.blockBase, cracked, 1 + 2);
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks in a 1 block radius around a block if any blocks can be cracked
	 * and cracks those. Does NOT crack the block at the specified coordinates,
	 * just the adjacent blocks.
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param chance
	 * @param fortune
	 * @return true if any blocks were cracked.
	 */
	public boolean checkBlockCracksAround(World world, int x, int y, int z, float chance, int fortune) {
		boolean result = false;
		result |= checkBlockCrack(world, x + 1, y, z, chance, fortune);
		result |= checkBlockCrack(world, x, y + 1, z, chance, fortune);
		result |= checkBlockCrack(world, x, y, z + 1, chance, fortune);
		result |= checkBlockCrack(world, x - 1, y, z, chance, fortune);
		result |= checkBlockCrack(world, x, y - 1, z, chance, fortune);
		result |= checkBlockCrack(world, x, y, z - 1, chance, fortune);
		return result;
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {

		int meta = MathHelper.clamp_int(world.getBlockMetadata(x, y, z), 0, Constants.METALIST_BLOCK_BASE.length - 1);
		
		// No ticks for non-sprout crystals
		if(!isSproutVersion(meta)) {
			return;
		}
		meta = getRegularVersion(meta);
		
		int currentGrowthHeight = 0;

		/*
		 * Growth will stop at any time if a block was "grown" unless insta-growth is enabled.
		 */
		boolean allowGrowth = false;
		
		for(int dy = 0; dy < Config.spikeMaxHeight; dy ++) {
			//TODO: Loop-Version of these? For readability.
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

	/**
	 * Eats a world block or reparis a broken crystal.
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param meta
	 * @return true if a block was eaten or instaGrowth is enabled.
	 */
	private boolean eatBlock(World world, int x, int y, int z, int meta) {
		Block currentWorldBlock = world.getBlock(x, y, z);
		if(currentWorldBlock == Blocks.bedrock) {
			return false;
		}
		if(currentWorldBlock == ChaosCrystalMain.blockBase) {
			int worldMeta = world.getBlockMetadata(x, y, z);
			if(ChaosCrystalMain.blockBase.isCrackedVersion(worldMeta)) {
				int uncracked = ChaosCrystalMain.blockBase.getUncrackedVersion(worldMeta);
				world.setBlock(x, y, z, ChaosCrystalMain.blockBase, uncracked, 1 + 2);
				return !Config.enableInstaGrowth;
			} else {
				// Regular or Sprout -> Nothing to do.
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

	/**
	 * Determines if a block type can be "eaten" by crystals.
	 * 
	 * @param block
	 * @return true if so, false if not.
	 */
	public static boolean isAcceptedBlock(Block block) {
		return block == Blocks.stone || block == Blocks.dirt
				|| block == Blocks.gravel || block == Blocks.grass
				|| block == Blocks.sandstone || block == Blocks.netherrack
				|| block == Blocks.water;
	}

	/**
	 * Determines if a block type is seen as crystal (Therefore will be regarded
	 * as part of the crystal when growing crystals)
	 * 
	 * @param block
	 * @return true if so, false if not.
	 */
	public static boolean isCrystalBlock(Block block) {
		return block == ChaosCrystalMain.blockBase;
	}

	/**
	 * Determines if a block can be "eaten" by crystals.
	 * 
	 * @param block
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return true if the block isAir, canBeReplacedByLeaves or it is an
	 *         acceptable block that can be "eaten" by crystals or is regarded
	 *         as a crystal.
	 */
	public static boolean isAcceptedBlockOrAir(Block block, World world, int x, int y, int z) {
		return block.isAir(world, x, z, z) || block.canBeReplacedByLeaves(world, x, y, z) || isCrystalBlock(block) || isAcceptedBlock(block);
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
