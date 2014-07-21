package founderio.chaoscrystal.entities;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.Config;
import founderio.chaoscrystal.aspects.Aspect;
import founderio.util.GeometryHelper;

public class EntityFocusFilter extends EntityFocus {

	public EntityFocusFilter(World world) {
		super(world);
	}

	public EntityFocusFilter(World world, double x, double y, double z,
			float yaw, float pitch) {
		super(world, x, y, z, yaw, pitch);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		setAspect(Aspect.values()[0]);
	}

	public Aspect getAspect() {
		return Aspect.findByStringRep(this.dataWatcher.getWatchableObjectString(30));
	}

	public void setAspect(Aspect aspect) {
		this.dataWatcher.updateObject(30, aspect.stringRep);
	}

	@Override
	public ItemStack buildItemStack() {
		ItemStack is = new ItemStack(ChaosCrystalMain.itemFocus, 1, 2);
		return is;
	}

	@Override
	protected void logicUpdate() {
		if (age > Config.cfgFocusTickInterval) {
			age = 0;
		}
		List<EntityChaosCrystal> crystals = new ArrayList<EntityChaosCrystal>();

		for (Object obj : this.worldObj.loadedEntityList) {
			if (obj instanceof EntityChaosCrystal) {
				double tmp_dist = GeometryHelper.entityDistance(
						(EntityChaosCrystal) obj, this);
				if (tmp_dist < Config.cfgFocusRange) {
					crystals.add((EntityChaosCrystal) obj);
				}
			}
		}

		if (!crystals.isEmpty()) {
			EntityChaosCrystal crystal1 = crystals.get(rand.nextInt(crystals
					.size()));
			lookX = (float) crystal1.posX;
			lookY = (float) (crystal1.boundingBox.minY + crystal1.boundingBox.maxY) / 2.0F;
			lookZ = (float) crystal1.posZ;
			updateLook();
		} else if (age == 0) {
			lookX = (float) posX + (this.rand.nextFloat() - 0.5f) * 10;
			lookY = (float) posY;
			lookZ = (float) posZ + (this.rand.nextFloat() - 0.5f) * 10;
			updateLook();
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		String aspect = nbttagcompound.getString("aspect");
		Aspect a = Aspect.findByStringRep(aspect);
		if(a == null) {
			a = Aspect.values()[0];
		}
		setAspect(a);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setString("aspect", getAspect().stringRep);
	}

}
