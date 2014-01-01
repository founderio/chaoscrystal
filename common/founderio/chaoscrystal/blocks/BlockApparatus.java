package founderio.chaoscrystal.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.chaoscrystal.Constants;

public class BlockApparatus extends BlockContainer {

	public static final String[] metaList = new String[] {
		Constants.ID_BLOCK_APPARATUS_RECONSTRUCTOR,
		Constants.ID_BLOCK_APPARATUS_CREATOR,
		Constants.ID_BLOCK_APPARATUS_SENTRY
	};
	
	public final int metaListIndex;
	
	public BlockApparatus(int par1, int meta) {
		super(par1, Material.rock);
		this.metaListIndex = meta;
		this.setHardness(2);
		this.setLightValue(0.2f);
		this.setResistance(6f);
		this.setStepSound(Block.soundStoneFootstep);
		MinecraftForge.setBlockHarvestLevel(this, "pickaxe", 0);
	}

	
	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}
	
	public TileEntity createTileEntity(World world, int metadata) {
		switch(metaListIndex) {
		case 0:
			return new TileEntityReconstructor();
		case 1:
			return new TileEntityCreator();
		case 2:
			return new TileEntitySentry();
		default: return null;
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
		if(par5EntityLivingBase instanceof EntityPlayer) {
			TileEntityApparatus te = ((TileEntityApparatus)par1World.getBlockTileEntity(par2, par3, par4));
			
			te.setOwner(((EntityPlayer)par5EntityLivingBase).username);
		}
		
		
	}
	
	@Override
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9) {
		TileEntityApparatus te = ((TileEntityApparatus)par1World.getBlockTileEntity(par2, par3, par4));
		
		return te.onBlockActivated(par5EntityPlayer);
	}
	
	private static Random random = new Random();
	
	@Override
	public void breakBlock(World par1World, int par2, int par3, int par4,
			int par5, int par6) {
		if(par1World.isRemote) {
			return;
		}
		TileEntityApparatus te = (TileEntityApparatus)par1World.getBlockTileEntity(par2, par3, par4);

		if(te != null) {
			for (int j1 = 0; j1 < te.getSizeInventory(); ++j1)
			{
				ItemStack itemstack = te.getStackInSlot(j1);

				if (itemstack != null)
				{
					float f = random.nextFloat() * 0.8F + 0.1F;
					float f1 = random.nextFloat() * 0.8F + 0.1F;
					EntityItem entityitem;

					for (float f2 = random.nextFloat() * 0.8F + 0.1F; itemstack.stackSize > 0; par1World.spawnEntityInWorld(entityitem))
					{
						int k1 = random.nextInt(21) + 10;

						if (k1 > itemstack.stackSize)
						{
							k1 = itemstack.stackSize;
						}

						itemstack.stackSize -= k1;
						entityitem = new EntityItem(par1World, (double)((float)par2 + f), (double)((float)par3 + f1), (double)((float)par4 + f2), new ItemStack(itemstack.itemID, k1, itemstack.getItemDamage()));
						float f3 = 0.05F;
						entityitem.motionX = (double)((float)random.nextGaussian() * f3);
						entityitem.motionY = (double)((float)random.nextGaussian() * f3 + 0.2F);
						entityitem.motionZ = (double)((float)random.nextGaussian() * f3);

						if (itemstack.hasTagCompound())
						{
							entityitem.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
						}
					}
				}
			}
		}

		super.breakBlock(par1World, par2, par3, par4, par5, par6);
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return null;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.blockIcon = par1IconRegister.registerIcon(Constants.MOD_ID + ":apparatus");
	}
	
}
