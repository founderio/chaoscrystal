package founderio.chaoscrystal.blocks;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Items;
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

		animation++;
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

			for (Object obj : worldObj.loadedEntityList) {
				if (obj instanceof EntityLivingBase) {
					if (obj instanceof EntityPlayer) {
						//TODO: Switch to UUID
						if (((EntityPlayer) obj).getDisplayName().equals(getOwner())) {
							continue;
						}
					}

					EntityLivingBase eCheck = (EntityLivingBase) obj;
					double distX = eCheck.posX - (xCoord + 0.5f);
					double distY = eCheck.posY + eCheck.getEyeHeight()
							- (yCoord + 1.5f);
					double distZ = eCheck.posZ - (zCoord + 0.5f);
					double tmp_dist = Math.sqrt(distX * distX + distY * distY
							+ distZ * distZ);

					if (tmp_dist < dist && tmp_dist < sentryRange
							&& eCheck.isEntityAlive()) {

						Vec3 vec3 = Vec3.createVectorHelper(
								xCoord + 0.5f, yCoord + 2f,
								zCoord + 0.5f);
						Vec3 vec32 = Vec3.createVectorHelper(eCheck.posX,
								eCheck.posY + eCheck.getEyeHeight(),
								eCheck.posZ);
						//TODO: Fix Sentry trying to shoot through player
						MovingObjectPosition mop = worldObj.func_147447_a(vec3, vec32, false, false, true);
						if (mop == null) {
							//hm.. ignore?
						} else if(mop.hitVec == null && mop.entityHit == null) {

							boolean valid = isValidTarget(eCheck);

							if(valid) {
								dist = tmp_dist;
								target = eCheck;
							}
						} else if(mop.entityHit != null) {
							if(isValidTarget(mop.entityHit)) {
								dist = tmp_dist;
								target = (EntityLivingBase)mop.entityHit;
							}
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
								//TODO: Safe Mode-Module? -> don't shoot at 2-3 blocks around non-target entities
								EntityLivingBase ent = list.get(0);

								boolean valid = isValidTarget(ent);

								if(valid) {
									dist = tmp_dist;
									target = ent;
								}

							}
						}

					}
				}
			}
			if (target == null) {
				return;
			}

			float f = 20 / 20.0F;
			f = (f * f + f * 2.0F) / 3.0F;

			if (f < 0.1D) {
				return;
			}

			if (f > 1.0F) {
				f = 1.0F;
			}

			if(arrowItem.getItem() == Items.arrow) {
				if (!worldObj.isRemote) {
					EntityArrow entityarrow = new EntityArrow(worldObj, xCoord + 0.5f,
							yCoord + 2f, zCoord + 0.5f);
					entityarrow.setThrowableHeading(
							target.posX - (xCoord + 0.5f),
							target.posY + target.getEyeHeight() * 0.5f - (yCoord + 2f),
							target.posZ - (zCoord + 0.5f),
							8, 0);
					entityarrow.canBePickedUp = 1;

					worldObj.spawnEntityInWorld(entityarrow);
				}
			} else if(arrowItem.getItem() == Items.snowball) {
				if (!worldObj.isRemote) {
					EntitySnowball entitysnowball = new EntityPlayerAwareSnowball(worldObj, xCoord + 0.5f,
							yCoord + 2f, zCoord + 0.5f);
					entitysnowball.setThrowableHeading(
							target.posX - (xCoord + 0.5f),
							target.posY + target.getEyeHeight() - (yCoord + 2f),
							target.posZ - (zCoord + 0.5f),
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

	public boolean isValidTarget(Entity ent) {
		HostilityLevel hostilityLevel = HostilityLevel.Docile;

		if(!(ent instanceof EntityLivingBase)) {
			return false;
		}

		if(ent instanceof EntityMob) {
			hostilityLevel = HostilityLevel.Hostile;
		}

		for(IModule module : modules) {
			if(module instanceof IModuleTarget) {
				if(!((IModuleTarget)module).isTargetValid(ent, hostilityLevel)) {
					return false;
				}
			}
		}
		return true;
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
		return itemstack.getItem() == Items.arrow || itemstack.getItem() == Items.snowball;
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
