package founderio.chaoscrystal.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.chaoscrystal.Constants;
import founderio.util.ItemUtil;

public class BlockApparatus extends BlockContainer {

	public static final String[] metaList = new String[] {
			Constants.ID_BLOCK_APPARATUS_RECONSTRUCTOR,
			Constants.ID_BLOCK_APPARATUS_CREATOR,
			Constants.ID_BLOCK_APPARATUS_SENTRY };

	public final int metaListIndex;

	public BlockApparatus(int meta) {
		super(Material.rock);
		this.metaListIndex = meta;
		this.setHardness(2);
		this.setLightLevel(0.3f);
		this.setResistance(6f);
		this.setStepSound(Block.soundTypeStone);
		this.setHarvestLevel("pickaxe", 0);
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		switch (metaListIndex) {
		case 0:
			return new TileEntityReconstructor();
		case 1:
			return new TileEntityCreator();
		case 2:
			return new TileEntitySentry();
		default:
			return null;
		}
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z,
			EntityLivingBase entity, ItemStack itemStack) {
		if (entity instanceof EntityPlayer) {
			TileEntityApparatus te = getTileEntityApparatus(world, x, y, z);

			// TODO: switch to UUID
			te.setOwner(((EntityPlayer) entity).getDisplayName());
		}

	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int side, float hitx, float hity, float hitz) {
		TileEntityApparatus te = getTileEntityApparatus(world, x, y, z);

		return te.onBlockActivated(player);
	}

	public TileEntityApparatus getTileEntityApparatus(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityApparatus) {
			return (TileEntityApparatus) te;
		} else {
			return null;
		}
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		if (world.isRemote) {
			return;
		}
		TileEntityApparatus te = getTileEntityApparatus(world, x, y, z);

		if (te != null) {
			for (int index = 0; index < te.getSizeInventory(); index++) {
				ItemStack itemstack = te.getStackInSlot(index);

				if (itemstack != null) {
					ItemUtil.spawnItemStackDropped(itemstack, world, x, y, z);
				}
			}
			for (int index = 0; index < te.getSizeModules(); index++) {
				ItemStack itemstack = te.getModuleItemStack(index);

				if (itemstack != null) {
					ItemUtil.spawnItemStackDropped(itemstack, world, x, y, z);
				}
			}
		}

		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String getTextureName() {
		return Constants.MOD_ID + ":apparatus";
	}

}
