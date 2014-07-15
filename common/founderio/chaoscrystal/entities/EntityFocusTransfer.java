package founderio.chaoscrystal.entities;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.CommonProxy;
import founderio.chaoscrystal.Config;
import founderio.chaoscrystal.aspects.Aspects;
import founderio.chaoscrystal.blocks.TileEntityApparatus;
import founderio.util.GeometryHelper;

public class EntityFocusTransfer extends EntityFocus {

	public EntityFocusTransfer(World world) {
		super(world);
		this.tickInterval = Config.cfgFocusTickInterval;
	}

	public EntityFocusTransfer(World world, double x, double y,
			double z, float yaw, float pitch) {
		super(world, x, y, z, yaw, pitch);
		this.tickInterval = Config.cfgFocusTickInterval;
	}

	public boolean didTransferToEntity = false;

	@Override
	public ItemStack buildItemStack() {
		ItemStack is = new ItemStack(ChaosCrystalMain.itemFocus, 1, 0);
		return is;
	}

	@Override
	protected void logicUpdate() {
		List<EntityChaosCrystal> ents = new ArrayList<EntityChaosCrystal>();
		for(Object obj : this.worldObj.loadedEntityList) {
			if(obj instanceof EntityChaosCrystal) {
				double tmp_dist = GeometryHelper.entityDistance((Entity)obj, this);
				if(tmp_dist < Config.cfgFocusRange) {
					ents.add((EntityChaosCrystal)obj);
				}
			}
		}

		List<TileEntityApparatus> tEnts = new ArrayList<TileEntityApparatus>();
		for(Object obj : this.worldObj.loadedTileEntityList) {
			if(obj instanceof TileEntityApparatus) {
				double tmp_dist = GeometryHelper.entityDistance((TileEntity)obj, this);
				if(tmp_dist < Config.cfgFocusRange) {
					tEnts.add((TileEntityApparatus)obj);
				}
			}
		}

		if(ents.isEmpty()) {
			lookX = (float)posX + (this.rand.nextFloat() - 0.5f) * 10;
			lookY = (float)posY;
			lookZ = (float)posZ + (this.rand.nextFloat() - 0.5f) * 10;
			updateLook();

		} else if(didTransferToEntity && !tEnts.isEmpty()) {
			TileEntityApparatus te = tEnts.get(rand.nextInt(tEnts.size()));
			EntityChaosCrystal crystal = ents.get(this.rand.nextInt(ents.size()));

			lookX = (float)te.xCoord;
			lookY = (float)(te.yCoord + 0.5f);
			lookZ = (float)te.zCoord;
			updateLook();

			if(te.processAspects(crystal)) {
				CommonProxy.spawnParticleEffects(crystal, this, 1);
				CommonProxy.spawnParticleEffects(this, te, 1);
			}
			didTransferToEntity = false;
		} else {
			if(ents.size() == 1) {
				EntityChaosCrystal crystal1 = (EntityChaosCrystal) ents.get(0);

				lookX = (float)crystal1.posX;
				lookY = (float)(crystal1.boundingBox.minY + crystal1.boundingBox.maxY) / 2.0f;
				lookZ = (float)crystal1.posZ;
				updateLook();
			} else {
				int idx = this.rand.nextInt(ents.size());
				EntityChaosCrystal crystal1 = (EntityChaosCrystal) ents.get(idx);
				ents.remove(idx);

				EntityChaosCrystal crystal2 = (EntityChaosCrystal) ents.get(this.rand.nextInt(ents.size()));

				lookX = (float)crystal1.posX;
				lookY = (float)(crystal1.boundingBox.minY + crystal1.boundingBox.maxY) / 2.0f;
				lookZ = (float)crystal1.posZ;
				updateLook();

				CommonProxy.spawnParticleEffects(this, crystal2, 1);
				CommonProxy.spawnParticleEffects(crystal1, this, 1);



				for(String aspect : Aspects.ASPECTS) {
					int aspects = crystal1.getAspect(aspect) + crystal2.getAspect(aspect);
					int asp1 = aspects/2;
					int asp2 = aspects - asp1;
					crystal1.setAspect(aspect, asp1);
					crystal2.setAspect(aspect, asp2);
				}
			}
			didTransferToEntity = true;
		}
	}
}
