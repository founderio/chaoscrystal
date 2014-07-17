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
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.Constants;

public class BlockSproutingCrystal extends Block {

	public static final String[] metaList = new String[] {
			Constants.ID_BLOCK_CRYSTALLINE_ENERGY_SPROUT,
			Constants.ID_BLOCK_CRYSTAL_SPROUT,
			Constants.ID_BLOCK_CRYSTALLINE_LIGHT_SPROUT, };
	public IIcon[] iconList;

	public BlockSproutingCrystal() {
		super(Material.glass);
		this.setHardness(4);
		this.setLightLevel(0.2f);
		this.setResistance(1.5f);
		this.setStepSound(Block.soundTypeGlass);
		this.setHarvestLevel("pickaxe", 1);
		this.setTickRandomly(true);
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
				+ "crystal_sprout");
		iconList[2] = par1IconRegister.registerIcon(Constants.MOD_ID + ":"
				+ "crystalline_light_sprout");
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
	protected boolean canSilkHarvest() {
		return true;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {

		int meta = world.getBlockMetadata(x, y, z);
		
		int maxHeight = 50;
		int maxTrunkRadius = 5;
		
		int currentGrowthHeight = 0;
		for(int dy = 0; dy < maxHeight; dy ++) {
			Block bl = world.getBlock(x, y + dy, z);
			if(bl == ChaosCrystalMain.blockBase || bl == ChaosCrystalMain.blockSproutingCrystal) {
				currentGrowthHeight = dy;
				if(eatBlock(world, x, y + dy, z, meta)) {
					return;
				}
				if(eatBlock(world, x, y - dy, z, meta)) {
					return;
				}
				if(eatBlock(world, x, y, z + dy, meta)) {
					return;                    
				}                              
				if(eatBlock(world, x, y, z - dy, meta)) {
					return;
				}
				if(eatBlock(world, x + dy, y, z, meta)) {
					return;              
				}                        
				if(eatBlock(world, x - dy, y, z, meta)) {
					return;
				}
			} else {
				//TODO: just save and grow if rest of crystal is intact (afterwards)
				break;
			}
		}
		float trunkwidthCurMax = currentGrowthHeight/(float)maxHeight;
		trunkwidthCurMax *= maxTrunkRadius;
		
		for(int dy = 0; dy < currentGrowthHeight; dy ++) {
			float trunkWidthCurLevel = 1f - (float)dy/currentGrowthHeight/2;
			trunkWidthCurLevel *= trunkwidthCurMax;
			for(int dx = 1; dx < trunkWidthCurLevel; dx++) {
				for(int dz = 1; dz < trunkWidthCurLevel; dz++) {
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
					//TODO: N, W, S, E
				}
			}
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
			if(worldMeta > 2) {
				world.setBlock(x, y, z, ChaosCrystalMain.blockBase, worldMeta - 3, 1 + 2);
				return true;
			} else {
				return false;
			}
			//TODO: if meta is different than sprout, mix some of these blocks in the rest somehow?
		}
				
		//TODO: check whether to eat that block or grow around it & create item/aspect storage
		
		world.setBlock(x, y, z, ChaosCrystalMain.blockBase, meta, 1 + 2);
		return false;//true
	}
	
	@Override
	public void onBlockClicked(World world, int x,
			int y, int z, EntityPlayer player) {
		// Just DEV...
//		updateTick(world, x, y, z, ChaosCrystalMain.rand);
	}
}
