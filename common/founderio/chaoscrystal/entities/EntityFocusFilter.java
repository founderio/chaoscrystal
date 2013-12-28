package founderio.chaoscrystal.entities;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.degradation.Aspects;

public class EntityFocusFilter extends EntityFocus {

	public EntityFocusFilter(World world) {
		super(world);
	}
	
	public EntityFocusFilter(World world, double x, double y,
			double z, float yaw, float pitch) {
		super(world, x, y, z, yaw, pitch);
	}
	
	
	
	public static final int focusRange = 10;

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(30, Aspects.ASPECTS[0]);
	}
	
	public String getAspect() {
		return this.dataWatcher.getWatchableObjectString(30);
	}
	
	public void setAspect(String aspect) {
		this.dataWatcher.updateObject(30, aspect);
	}
	
	@Override
	public ItemStack buildItemStack() {
		ItemStack is = new ItemStack(ChaosCrystalMain.itemFocus, 1, 2);
		return is;
	}
	
	@Override
	protected void logicUpdate() {
    	if(age > ChaosCrystalMain.cfgFocusTickInterval) {
    		age = 0;
    	}
    	List<EntityChaosCrystal> crystals = new ArrayList<EntityChaosCrystal>();
		 
        for(Object obj : this.worldObj.loadedEntityList) {
			if(obj instanceof EntityChaosCrystal) {
				double distX = ((EntityChaosCrystal) obj).posX - posX;
				double distY = ((EntityChaosCrystal) obj).posY - posY;
				double distZ = ((EntityChaosCrystal) obj).posZ - posZ;
				double tmp_dist = Math.sqrt(distX*distX + distY*distY + distZ*distZ);
				if(tmp_dist < focusRange) {
					crystals.add((EntityChaosCrystal)obj);
				}
			}
		}
        
        if(!crystals.isEmpty()) {
        	EntityChaosCrystal crystal1 = crystals.get(rand.nextInt(crystals.size()));
        	lookX = (float)crystal1.posX;
    		lookY = (float)(crystal1.boundingBox.minY + crystal1.boundingBox.maxY) / 2.0F;
    		lookZ = (float)crystal1.posZ;
        	updateLook();
        } else if(age == 0) {
        	lookX = (float)posX + (this.rand.nextFloat() - 0.5f) * 10;
        	lookY = (float)posY;
        	lookZ = (float)posZ + (this.rand.nextFloat() - 0.5f) * 10;
        	updateLook();
        }
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		String aspect = nbttagcompound.getString("aspect");
		if(!Aspects.isAspect(aspect)) {
			aspect = Aspects.ASPECTS[0];
		}
		setAspect(aspect);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setString("aspect", getAspect());
	}

}
