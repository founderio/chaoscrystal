package founderio.chaoscrystal.blocks;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.aspects.HostilityLevel;
import founderio.chaoscrystal.entities.EntityChaosCrystal;
import founderio.chaoscrystal.entities.EntityPlayerAwareSnowball;
import founderio.chaoscrystal.machinery.IModule;
import founderio.chaoscrystal.machinery.IModuleTarget;

public class TileEntitySentry extends TileEntityApparatus {

	public TileEntitySentry() {
		super(4, 4);
	}

	@Override
	public boolean processAspects(EntityChaosCrystal crystal) {

		//TODO: Charge Up!
		return false;
	}


	public static final int sentryRange = 32;
	public boolean isActive = true;

	@Override
	public void updateEntity() {
		isActive = !worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord,
				zCoord);

		this.animation++;
		if (animation >= 20) {
			animation = 0;

			if (!isActive) {
				return;
			}

			int arrowSlot = -1;
			ItemStack arrowItem = null;

			for (int i = 0; i < getSizeInventory(); i++) {
				ItemStack is = getStackInSlot(i);
				if (isItemValidForSlot(i, is) && is.stackSize > 0) {
					arrowSlot = i;
					arrowItem = is;
					break;
				}
			}
			if (arrowSlot == -1) {
				return;
			}

			double dist = Double.MAX_VALUE;
			EntityLivingBase target = null;

			for (Object obj : this.worldObj.loadedEntityList) {
				if (obj instanceof EntityLivingBase) {
					if (obj instanceof EntityPlayer) {
						if (((EntityPlayer) obj).username.equals(getOwner())) {
							continue;
						}
					}

					EntityLivingBase eCheck = (EntityLivingBase) obj;
					double distX = eCheck.posX - ((float) xCoord + 0.5f);
					double distY = eCheck.posY + eCheck.getEyeHeight()
							- ((float) yCoord + 1.5f);
					double distZ = eCheck.posZ - ((float) zCoord + 0.5f);
					double tmp_dist = Math.sqrt(distX * distX + distY * distY
							+ distZ * distZ);

					if (tmp_dist < dist && tmp_dist < sentryRange
							&& eCheck.isEntityAlive()) {

						Vec3 vec3 = Vec3.createVectorHelper(
								((float) xCoord + 0.5f), (float) yCoord + 1.5f,
								(float) zCoord + 0.5f);
						Vec3 vec32 = Vec3.createVectorHelper(eCheck.posX,
								eCheck.posY + eCheck.getEyeHeight(),
								eCheck.posZ);
						MovingObjectPosition mop = this.worldObj.clip(vec3,
								vec32);
						if (mop == null || mop.hitVec == null) {
							dist = tmp_dist;
							target = eCheck;
						} else {
							
							@SuppressWarnings("unchecked")
							List<EntityLivingBase> list = worldObj.getEntitiesWithinAABB(
									EntityLivingBase.class, AxisAlignedBB
									.getBoundingBox(
											mop.hitVec.xCoord - .5f,
											mop.hitVec.yCoord - .5f,
											mop.hitVec.zCoord - .5f,
											mop.hitVec.xCoord + .5f,
											mop.hitVec.yCoord + .5f,
											mop.hitVec.zCoord + .5f));
							if (!list.isEmpty()) {
								//TODO: Safe Mode-Module?
								EntityLivingBase ent = list.get(0);
								
								HostilityLevel hostilityLevel = HostilityLevel.Docile;
								
								if(ent instanceof EntityMob) {
									hostilityLevel = HostilityLevel.Hostile;
								}
								
								boolean valid = true;
								
								for(IModule module : modules) {
									if(module instanceof IModuleTarget) {
										if(!((IModuleTarget)module).isTargetValid(target, hostilityLevel));
									}
								}
								
								if(valid) {
									dist = tmp_dist;
									target = eCheck;
								}
								
							}
						}

					}
				}
			}
			if (target == null) {
				return;
			}

			float f = (float) 20 / 20.0F;
			f = (f * f + f * 2.0F) / 3.0F;

			if ((double) f < 0.1D) {
				return;
			}

			if (f > 1.0F) {
				f = 1.0F;
			}


			if(arrowItem.itemID == Item.arrow.itemID) {
				if (!worldObj.isRemote) {
					EntityArrow entityarrow = new EntityArrow(worldObj, xCoord + 0.5f,
							yCoord + 2f, zCoord + 0.5f);
					entityarrow.setThrowableHeading(
							target.posX - ((float) xCoord + 0.5f),
							target.posY + target.getEyeHeight() * 0.5f - ((float) yCoord + 2f),
							target.posZ - ((float) zCoord + 0.5f),
							5, 0);
					entityarrow.canBePickedUp = 1;

					worldObj.spawnEntityInWorld(entityarrow);
				}
			} else if(arrowItem.itemID == Item.snowball.itemID) {
				if (!worldObj.isRemote) {
					EntitySnowball entitysnowball = new EntityPlayerAwareSnowball(worldObj, xCoord + 0.5f,
							yCoord + 2f, zCoord + 0.5f);
					entitysnowball.setThrowableHeading(
							target.posX - ((float) xCoord + 0.5f),
							target.posY + target.getEyeHeight() - ((float) yCoord + 2f),
							target.posZ - ((float) zCoord + 0.5f),
							5f, 0);

					worldObj.spawnEntityInWorld(entitysnowball);
				}
			}


			worldObj.playSound(xCoord + 10.5d, yCoord + 2d, zCoord + 10.5d,
					"random.bow", 2.0F, 1.0F
					/ (ChaosCrystalMain.rand.nextFloat() * 0.4F + 1.2F) + f * 0.5F,
					true);


			decrStackSize(arrowSlot, 1);
		}
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return new int[] { 0, 1, 2, 3 };
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if (itemstack == null) {
			return false;
		}
		return itemstack.itemID == Item.arrow.itemID || itemstack.itemID == Item.snowball.itemID;
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
