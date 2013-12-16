package founderio.chaoscrystal.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.degradation.Repair;
import founderio.chaoscrystal.entities.EntityChaosCrystal;
import founderio.chaoscrystal.entities.EntityFocusFilter;
import founderio.chaoscrystal.rendering.OverlayAspectSelector;

public class TileEntitySentry extends TileEntityApparatus {

	public TileEntitySentry() {
		super(4);
	}

	@Override
	public boolean processAspects(EntityChaosCrystal crystal) {
		ItemStack is = getStackInSlot(0);
		
		if(is == null || is.itemID == 0) {
			return false;
		} else {
			int maxDmg = is.getMaxDamage();
			int curDmg = is.getItemDamage();

			if(curDmg == 0) {
				return false;
			}
			
			Repair rep = ChaosCrystalMain.degradationStore.getRepair(is.itemID);
			
			if(rep == null) {
				return false;
			}
			boolean didRepair = false;
			
			for(int step = 0; step < stepsPerTick && curDmg > 0; step++) {
				boolean capable = true;
				for(int a = 0; a < rep.aspects.length; a++) {
					if(crystal.getAspect(rep.aspects[a]) < rep.amounts[a]) {
						capable = false;
					}
				}
				if(!capable) {
					break;
				}
				didRepair = true;
				
				for(int a = 0; a < rep.aspects.length; a++) {
					crystal.setAspect(rep.aspects[a], crystal.getAspect(rep.aspects[a]) - rep.amounts[a]);
				}
				
				curDmg--;
				is.setItemDamage(curDmg);
			}
			
			if(didRepair) {
				updateState();
			}
			
			return didRepair;
			
		}
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player) {
		ItemStack currentEquip = player.getCurrentEquippedItem();
		
		if(currentEquip != null &&
				isItemValidForSlot(0, currentEquip)) {
			for(int i = 0; i < 4; i++) {
				ItemStack is = this.getStackInSlot(i);
				if(is == null || is.itemID == 0) {
					setInventorySlotContents(i, currentEquip.copy());
					player.inventory.mainInventory[player.inventory.currentItem] = null;
					break;
				}
				if(is.isItemEqual(currentEquip)) {
					if(is.stackSize + currentEquip.stackSize <= getInventoryStackLimit()) {
						is.stackSize += currentEquip.stackSize;
						player.inventory.mainInventory[player.inventory.currentItem] = null;
						break;
					} else {
						currentEquip.stackSize -= getInventoryStackLimit() - is.stackSize;
						is.stackSize = getInventoryStackLimit();
						if(currentEquip.stackSize == 0) {
							player.inventory.mainInventory[player.inventory.currentItem] = null;
							break;
						}
					}
				}
			}
		} else {
			for(int i = 0; i < 4; i++) {
				ItemStack is = this.getStackInSlot(i);
				if(is != null && is.itemID != 0 && is.stackSize > 0) {
					if(player.inventory.addItemStackToInventory(is)) {
						this.setInventorySlotContents(i, null);
						break;
					}
				}
			}
		}
		
		onInventoryChanged();
		
		return true;
	}
	
	private Random rand = new Random();
	public static final int sentryRange = 32;
	public boolean isActive = true;
	
	@Override
	public void updateEntity() {
		isActive = !worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
		
		this.animation++;
		if(animation >= 20) {
			animation = 0;
			
			if(!isActive) {
				return;
			}
			
			int arrowSlot = -1;
		
			for(int i = 0; i < getSizeInventory(); i++) {
				ItemStack is = getStackInSlot(i);
				if(is != null && is.itemID == Item.arrow.itemID && is.stackSize > 0) {
					arrowSlot = i;
					break;
				}
			}
			if(arrowSlot == -1) {
				return;
			}
			
			double dist = Double.MAX_VALUE;
			EntityLivingBase target = null;
			
			for(Object obj : this.worldObj.loadedEntityList) {
    			if(obj instanceof EntityLivingBase) {
    				if(obj instanceof EntityPlayer) {
    					if(((EntityPlayer)obj).username.equals(getOwner())) {
    						continue;
    					}
    				}
    				
    				EntityLivingBase eCheck = (EntityLivingBase) obj;
    				double distX = eCheck.posX - ((float)xCoord + 0.5f);
    				double distY = eCheck.posY + eCheck.getEyeHeight() - ((float)yCoord + 1.5f);
    				double distZ = eCheck.posZ - ((float)zCoord + 0.5f);
    				double tmp_dist = Math.sqrt(distX*distX + distY*distY + distZ*distZ);

    				
    				
    				
    				if(tmp_dist < dist && tmp_dist < sentryRange && eCheck.isEntityAlive()) {
    					
    					Vec3 vec3 = Vec3.createVectorHelper(((float)xCoord + 0.5f), (float)yCoord + 1.5f, (float)zCoord + 0.5f);
        				Vec3 vec32 = Vec3.createVectorHelper(eCheck.posX, eCheck.posY + eCheck.getEyeHeight(), eCheck.posZ);
        				MovingObjectPosition mop = this.worldObj.clip(vec3, vec32);
        				if(mop == null ||  mop.hitVec == null) {
        					dist = tmp_dist;
 	    					target = eCheck;
        				} else {
        					List list = worldObj.getEntitiesWithinAABB(EntityLivingBase.class,
            						AxisAlignedBB.getBoundingBox(
            								mop.hitVec.xCoord - .5f, mop.hitVec.yCoord - .5f, mop.hitVec.zCoord - .5f,
            								mop.hitVec.xCoord + .5f, mop.hitVec.yCoord + .5f, mop.hitVec.zCoord + .5f)
            								);
        					 if(!list.isEmpty()) {
        	    					
     	    					dist = tmp_dist;
     	    					target = eCheck;
                             }
        				}
        				
                       
    				}
    			}
    		}
			if(target == null) {
				return;
			}
			
            float f = (float)20 / 20.0F;
            f = (f * f + f * 2.0F) / 3.0F;

            if ((double)f < 0.1D)
            {
                return;
            }

            if (f > 1.0F)
            {
                f = 1.0F;
            }

            EntityArrow entityarrow = new EntityArrow(worldObj, xCoord + 0.5f, yCoord + 2f, zCoord + 0.5f);
            entityarrow.setThrowableHeading(
            		target.posX - ((float)xCoord + 0.5f),
            		target.posY + target.getEyeHeight() - ((float)yCoord + 1.5f),
            		target.posZ - ((float)zCoord + 0.5f),
            		5, 0);
            entityarrow.canBePickedUp = 1;

            decrStackSize(arrowSlot, 1);

            worldObj.playSound(xCoord + 10.5d, yCoord + 2d, zCoord + 10.5d, "random.bow", 2.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 1.2F) + f * 0.5F, true);


            if (!worldObj.isRemote)
            {
            	worldObj.spawnEntityInWorld(entityarrow);
            }
		}
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		//TODO: block top side!
		return new int[] {0, 1, 2, 3};
	}
	
	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if(itemstack == null) {
			return false;
		}
		return itemstack.itemID == Item.arrow.itemID;
	}
	
	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		ItemStack is = getStackInSlot(i);
		if(is != null && is.itemID != 0) {
			return false;
		} else {
			return j == 1 && isItemValidForSlot(i, itemstack);
		}
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return itemstack.getItemDamage() == 0;
	}
	
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}
}
