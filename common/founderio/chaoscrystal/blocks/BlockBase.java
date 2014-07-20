package founderio.chaoscrystal.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
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
import founderio.chaoscrystal.Constants;

public class BlockBase extends Block {

	public static final String[] metaList = new String[] {
			Constants.ID_BLOCK_CRYSTALLINE_ENERGY,
			Constants.ID_BLOCK_CRYSTALLINE_CHAOS,
			Constants.ID_BLOCK_CRYSTALLINE_LIGHT,
			Constants.ID_BLOCK_CRYSTAL_CLEAR,
			Constants.ID_BLOCK_CRYSTALLINE_ENERGY_CRACKED,
			Constants.ID_BLOCK_CRYSTALLINE_CHAOS_CRACKED,
			Constants.ID_BLOCK_CRYSTALLINE_LIGHT_CRACKED,
			Constants.ID_BLOCK_CRYSTAL_CLEAR_CRACKED, };
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

    /**
     * Returns true if the given side of this block type should be rendered (if it's solid or not), if the adjacent
     * block is at the given coordinates. Args: blockAccess, x, y, z, side
     */
    public boolean isBlockSolid(IBlockAccess blockAccess, int x, int y, int z, int side)
    {
    	Block adjacent = blockAccess.getBlock(x, y, z);
    	if(adjacent == this || adjacent == ChaosCrystalMain.blockSproutingCrystal) {
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
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int side)
    {
    	Block adjacent = blockAccess.getBlock(x, y, z);
    	if(adjacent == this || adjacent == ChaosCrystalMain.blockSproutingCrystal) {
        	ForgeDirection dir = ForgeDirection.getOrientation(side);
    		int adjacentMeta = blockAccess.getBlockMetadata(x, y, z);
    		int thisMeta = blockAccess.getBlockMetadata(x - dir.offsetX, y - dir.offsetY, z - dir.offsetZ);
    		return thisMeta != adjacentMeta;
    	} else {
    		return true;
    	}
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
		return MathHelper.clamp_int(meta, 0, metaList.length - 1);
	}

	@Override
	public int damageDropped(int meta) {
		if(isCrackedVersion(meta)) {
			// Returns shard meta
			return getUncrackedVersion(meta);
		} else {
			//Returns Block Meta
			return MathHelper.clamp_int(meta, 0, metaList.length - 1);
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
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {
		iconList = new IIcon[metaList.length];
		iconList[0] = ir.registerIcon(Constants.MOD_ID + ":crystalline_energy");
		iconList[1] = ir.registerIcon(Constants.MOD_ID + ":crystalline_chaos");
		iconList[2] = ir.registerIcon(Constants.MOD_ID + ":crystalline_light");
		iconList[3] = ir.registerIcon(Constants.MOD_ID + ":crystal_clear");
		
		iconList[4] = ir.registerIcon(Constants.MOD_ID + ":crystalline_energy_cracked");
		iconList[5] = ir.registerIcon(Constants.MOD_ID + ":crystalline_chaos_cracked");
		iconList[6] = ir.registerIcon(Constants.MOD_ID + ":crystalline_light_cracked");
		iconList[7] = ir.registerIcon(Constants.MOD_ID + ":crystal_clear_cracked");
	}
	
	public boolean isCrackedVersion(int meta) {
		int idx = MathHelper.clamp_int(meta, 0, metaList.length - 1);
		return idx > 3;
	}
	
	public int getUncrackedVersion(int meta) {
		int idx = MathHelper.clamp_int(meta, 0, metaList.length - 1);
		if(isCrackedVersion(idx)) {
			return idx - 4;
		} else {
			return idx;
		}
	}
	
	public int getCrackedVersion(int meta) {
		int idx = MathHelper.clamp_int(meta, 0, metaList.length - 1);
		if(isCrackedVersion(idx)) {
			return idx;
		} else {
			return idx + 4;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		int idx = MathHelper.clamp_int(meta, 0, metaList.length - 1);
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
		checkBlockCracksAround(world, x, y, z, 0.9f, 0);
	}
	
	@Override
	public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player) {
		// Don't break stuff if in creative mode
		if(player.capabilities.isCreativeMode) {
			return;
		}
		// Less likely to break stuff if the player uses silk touch
		boolean hasSilkTouch = EnchantmentHelper.getSilkTouchModifier(player);
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
	
	public void handleCollision(World world, int x,
			int y, int z, Entity entity,
			double force) {
		if(world.isRemote) {
			return;
		}
		// Don't Destroy stuff in Creative Mode
		if(entity instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)entity;
			if(ep.capabilities.isCreativeMode) {
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
	
	public boolean checkBlockCrack(World world, int x, int y, int z, float chance, int fortune) {
		if(world.getBlock(x, y, z) == ChaosCrystalMain.blockBase) {
			int meta = world.getBlockMetadata(x, y, z);
			if(ChaosCrystalMain.rand.nextFloat() > chance) {
				return false;
			}
			if(isCrackedVersion(meta)) {
				// Break
				world.setBlockToAir(x, y, z);
	            this.dropBlockAsItem(world, x, y, z, meta, fortune);
			} else {
				// Crack
				int cracked = getCrackedVersion(meta);
				world.setBlock(x, y, z, ChaosCrystalMain.blockBase, cracked, 1 + 2);
			}
			return true;
		}
		return false;
	}
	
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

}
