package founderio.chaoscrystal.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
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
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4,
			EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack) {
		if (par5EntityLivingBase instanceof EntityPlayer) {
			TileEntityApparatus te = ((TileEntityApparatus) par1World
					.getTileEntity(par2, par3, par4));

			//TODO: switch to UUID
			te.setOwner(((EntityPlayer) par5EntityLivingBase).getDisplayName());
		}

	}

	@Override
	public boolean onBlockActivated(World par1World, int par2, int par3,
			int par4, EntityPlayer par5EntityPlayer, int par6, float par7,
			float par8, float par9) {
		TileEntityApparatus te = ((TileEntityApparatus) par1World
				.getTileEntity(par2, par3, par4));

		return te.onBlockActivated(par5EntityPlayer);
	}

	@Override
	public void breakBlock(World par1World, int par2, int par3, int par4,
			Block block, int par6) {
		if (par1World.isRemote) {
			return;
		}
		TileEntityApparatus te = (TileEntityApparatus) par1World
				.getTileEntity(par2, par3, par4);

		if (te != null) {
			for (int index = 0; index < te.getSizeInventory(); index++) {
				ItemStack itemstack = te.getStackInSlot(index);

				if (itemstack != null) {
					ItemUtil.spawnItemStackDropped(itemstack, par1World, par2, par3, par4);
				}
			}
			for (int index = 0; index < te.getSizeModules(); index++) {
				ItemStack itemstack = te.getModuleItemStack(index);

				if (itemstack != null) {
					ItemUtil.spawnItemStackDropped(itemstack, par1World, par2, par3, par4);
				}
			}
		}

		super.breakBlock(par1World, par2, par3, par4, block, par6);
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
