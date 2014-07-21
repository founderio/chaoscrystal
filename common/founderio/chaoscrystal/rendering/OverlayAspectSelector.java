package founderio.chaoscrystal.rendering;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.Config;
import founderio.chaoscrystal.Constants;
import founderio.chaoscrystal.IModeChangingItem;
import founderio.chaoscrystal.aspects.Aspect;
import founderio.chaoscrystal.aspects.IAspectStore;
import founderio.chaoscrystal.aspects.Node;
import founderio.chaoscrystal.aspects.NodePoint;
import founderio.chaoscrystal.blocks.TileEntityApparatus;
import founderio.chaoscrystal.entities.EntityChaosCrystal;
import founderio.chaoscrystal.entities.EntityFocusBorder;
import founderio.chaoscrystal.entities.EntityFocusFilter;
import founderio.chaoscrystal.entities.EntityFocusFilterTarget;
import founderio.chaoscrystal.entities.EntityFocusTransfer;
import founderio.chaoscrystal.network.CCPModeItemChanged;
import founderio.util.GeometryHelper;

public class OverlayAspectSelector extends Gui {

	private RenderItem ri;

	public OverlayAspectSelector() {
		ri = new RenderItem();
		ri.setRenderManager(RenderManager.instance);
	}

	@SubscribeEvent
	public void onMouseWheel(MouseEvent event) {


		if(event.dwheel == 0) {
			return;
		}

		if(!Minecraft.getMinecraft().thePlayer.isSneaking()) {
			return;
		}

		ItemStack currentItemStack = Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem();
		if(currentItemStack == null) {
			return;
		}
		Item item = currentItemStack.getItem();
		
		if(item instanceof IModeChangingItem) {
			IModeChangingItem mci = ((IModeChangingItem) item);
			event.setCanceled(true);
			
			if(mci.getModeCount(currentItemStack) > 0) {
				int modeIndex = mci.getSelectedModeForItemStack(currentItemStack);
				
				
				if(event.dwheel > 0) {
					modeIndex++;
				} else if(event.dwheel < 0) {
					modeIndex--;
				}
				
				if(modeIndex >= mci.getModeCount(currentItemStack)) {
					modeIndex = mci.getModeCount(currentItemStack) - 1;
				}
				
				if(modeIndex < 0) {
					modeIndex = 0;
				}
				
				mci.setSelectedModeForItemStack(currentItemStack, modeIndex);
				

				ChaosCrystalMain.packetPipeline.sendToServer(new CCPModeItemChanged(modeIndex));
			}
			
		} else if(currentItemStack.getItem() == ChaosCrystalMain.itemManual) {
			event.setCanceled(true);
			if(event.dwheel > 0) {
				RenderItemManual.page--;
			} else {
				RenderItemManual.page++;
			}
		}

	}

	public void renderItem(ItemStack is, int x, int y) {
		if(is == null || is.getItem() == null) {
			return;
		}
		RenderHelper.enableGUIStandardItemLighting();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)240 / 1.0F, (float)240 / 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_LIGHTING);
		ri.renderItemAndEffectIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().renderEngine, is, x, y);
		ri.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().renderEngine, is, x, y);
	}

	/*
	 * Copy & modify from Minecraft.getMinecraft().entityRenderer.getMouseOver()
	 */
	public static MovingObjectPosition getMouseOver(float par1)
	{
		Entity pointedEntity = null;
		Minecraft mc = Minecraft.getMinecraft();

		if (mc.renderViewEntity != null && mc.theWorld != null)
		{

			double blockReachDistance = (double)mc.playerController.getBlockReachDistance();
			MovingObjectPosition mop = rayTrace(mc.renderViewEntity, blockReachDistance, par1);
			double blockHitDistance = blockReachDistance;
			Vec3 vecPos = mc.renderViewEntity.getPosition(par1);

			if (mc.playerController.extendedReach())
			{
				blockReachDistance = 6.0D;
			}
			else if (blockReachDistance > 3.0D)
			{
				blockReachDistance = 3.0D;
			}
			blockHitDistance = blockReachDistance;

			if (mop != null)
			{
				blockHitDistance = mop.hitVec.distanceTo(vecPos);
			}

			Vec3 vecLook = mc.renderViewEntity.getLook(par1);
			Vec3 vecMaxReach = vecPos.addVector(vecLook.xCoord * blockReachDistance, vecLook.yCoord * blockReachDistance, vecLook.zCoord * blockReachDistance);
			pointedEntity = null;
			float searchRadius = 1.0F;
			@SuppressWarnings("rawtypes")
			List list = mc.theWorld.getEntitiesWithinAABBExcludingEntity(
					mc.renderViewEntity,
					mc.renderViewEntity.boundingBox.
					addCoord(vecLook.xCoord * blockReachDistance, vecLook.yCoord * blockReachDistance, vecLook.zCoord * blockReachDistance)
					.expand((double)searchRadius, (double)searchRadius, (double)searchRadius));
			double entityHitDistance = blockHitDistance;

			for (int i = 0; i < list.size(); ++i)
			{
				Entity entity = (Entity)list.get(i);

				double collistionBorderSize = entity.getCollisionBorderSize();
				AxisAlignedBB axisalignedbb = entity.boundingBox.expand(collistionBorderSize, collistionBorderSize, collistionBorderSize);
				MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vecPos, vecMaxReach);

				if (axisalignedbb.isVecInside(vecPos))
				{
					if (entityHitDistance >= 0.0D)
					{
						pointedEntity = entity;
						entityHitDistance = 0.0D;
					}
				}
				else if (movingobjectposition != null)
				{
					double distance = vecPos.distanceTo(movingobjectposition.hitVec);

					if (distance < entityHitDistance || entityHitDistance == 0.0D)
					{
						if (entity == mc.renderViewEntity.ridingEntity && !entity.canRiderInteract())
						{
							if (entityHitDistance == 0.0D)
							{
								pointedEntity = entity;
							}
						}
						else
						{
							pointedEntity = entity;
							entityHitDistance = distance;
						}
					}
				}
			}

			if (pointedEntity != null && (entityHitDistance < blockHitDistance || mop == null))
			{
				mop = new MovingObjectPosition(pointedEntity);
			}
			return mop;
		}
		return null;
	}

	private void renderAspectList(int xPos, int yPos, IAspectStore store) {
		int offset = 0;
		int colOffset = 0;
		final int colWidth = 64;

		for(int i = 0; i < Aspect.values().length; i++) {
			Aspect aspect = Aspect.values()[i];
			int asp = store.getAspect(aspect);

			Minecraft.getMinecraft().renderEngine.bindTexture(Aspect.RESOURCE_LOCATIONS[aspect.ordinal()]);
			this.drawTexturedModalRectScaled(xPos + 5 + colOffset, yPos + offset + 5, 0, 0, 10, 10, 256, 256);

			Minecraft.getMinecraft().fontRenderer.drawString(
					Integer.toString(asp),
					xPos + 16 + colOffset, yPos + 2 + offset + 5,
					16777215);


			if(offset >= 30) {
				offset = 0;
				colOffset += colWidth;
			} else {
				offset += 10;
			}
		}
	}
	private void renderAspectList(int xPos, int yPos, int[] aspectArray) {

		int offset = 0;
		int colOffset = 0;
		final int colWidth = 64;


		for(int i = 0; i < aspectArray.length; i++) {
			Aspect aspect = Aspect.values()[i];
			int asp = aspectArray[i];

			if(asp > 0) {
				Minecraft.getMinecraft().renderEngine.bindTexture(Aspect.RESOURCE_LOCATIONS[aspect.ordinal()]);
				this.drawTexturedModalRectScaled(xPos + 5 + colOffset, yPos + offset + 5,
						0, 0,
						10, 10,
						256, 256);

				Minecraft.getMinecraft().fontRenderer.drawString(
						Integer.toString(asp),
						xPos + 16 + colOffset, yPos + 2 + offset + 5,
						16777215);

				if(offset >= 30) {
					offset = 0;
					colOffset += colWidth;
				} else {
					offset += 10;
				}
			}

		}
	}

	@SubscribeEvent
	public void onRenderHud(RenderGameOverlayEvent event) {

		if(event.type != ElementType.CROSSHAIRS) {
			return;
		}

		ItemStack helmet = Minecraft.getMinecraft().thePlayer.inventory.armorInventory[3];

		ItemStack currentItemStack = Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem();

		boolean specialSkip = (currentItemStack != null && currentItemStack.getItem() instanceof ItemMap) || Config.cfgSneakToShowAspects && !Minecraft.getMinecraft().thePlayer.isSneaking();

		int centerW = event.resolution.getScaledWidth()/2;
		int centerH = event.resolution.getScaledHeight()/2;

		GL11.glPushMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		if(helmet != null && helmet.getItem() == ChaosCrystalMain.itemCrystalGlasses && !specialSkip) {
			MovingObjectPosition mop = getMouseOver(0);

			if(mop != null) {
				Entity lookingAt = mop.entityHit;

				if(lookingAt != null) {

					if(lookingAt instanceof EntityChaosCrystal) {
						EntityChaosCrystal e = (EntityChaosCrystal)lookingAt;




						Minecraft.getMinecraft().renderEngine.bindTexture(
								new ResourceLocation(Constants.MOD_ID + ":" + "textures/items/chaoscrystal.png"));
						this.drawTexturedModalRectScaled(centerW - 16 - 5, centerH + 5, 0, 0, 16, 16, 256, 256);

						if(e.isInSuckMode()) {
							Minecraft.getMinecraft().renderEngine.bindTexture(
									new ResourceLocation(Constants.MOD_ID + ":" + "textures/hud/mode_suck.png"));
						} else {
							Minecraft.getMinecraft().renderEngine.bindTexture(
									new ResourceLocation(Constants.MOD_ID + ":" + "textures/hud/mode_expel.png"));
						}
						this.drawTexturedModalRectScaled(centerW - 16 - 5, centerH - 16 - 5, 0, 0, 16, 16, 256, 256);

						renderAspectList(centerW, centerH, e);


					} else if(lookingAt instanceof EntityFocusFilter) {


						Minecraft.getMinecraft().renderEngine.bindTexture(
								new ResourceLocation(Constants.MOD_ID + ":" + "textures/items/focus_filter.png"));
						this.drawTexturedModalRectScaled(centerW - 16 - 5, centerH + 5, 0, 0, 16, 16, 256, 256);


						Aspect aspect = ((EntityFocusFilter)lookingAt).getAspect();

						Minecraft.getMinecraft().renderEngine.bindTexture(Aspect.RESOURCE_LOCATIONS[aspect.ordinal()]);
						this.drawTexturedModalRectScaled(centerW + 5, centerH + 5, 0, 0, 16, 16, 256, 256);


					} else if(lookingAt instanceof EntityFocusFilterTarget) {


						Minecraft.getMinecraft().renderEngine.bindTexture(
								new ResourceLocation(Constants.MOD_ID + ":" + "textures/items/focus_filter_type.png"));
						this.drawTexturedModalRectScaled(centerW - 16 - 5, centerH + 5, 0, 0, 16, 16, 256, 256);


						String target = ((EntityFocusFilterTarget)lookingAt).getTarget();

						Minecraft.getMinecraft().renderEngine.bindTexture(
								new ResourceLocation(Constants.MOD_ID + ":" + "textures/hud/target_" + target + ".png"));
						this.drawTexturedModalRectScaled(centerW + 5, centerH + 5, 0, 0, 16, 16, 256, 256);


					} else if(lookingAt instanceof EntityFocusBorder) {


						Minecraft.getMinecraft().renderEngine.bindTexture(
								new ResourceLocation(Constants.MOD_ID + ":" + "textures/items/focus_border.png"));
						this.drawTexturedModalRectScaled(centerW - 16 - 5, centerH + 5, 0, 0, 16, 16, 256, 256);


					} else if(lookingAt instanceof EntityFocusTransfer) {


						Minecraft.getMinecraft().renderEngine.bindTexture(
								new ResourceLocation(Constants.MOD_ID + ":" + "textures/items/focus_transfer.png"));
						this.drawTexturedModalRectScaled(centerW - 16 - 5, centerH + 5, 0, 0, 16, 16, 256, 256);


					} else if(lookingAt instanceof EntityItem) {

						EntityItem ei = ((EntityItem)lookingAt);
						ItemStack is = ei.getEntityItem();

						List<Node> degradations = ChaosCrystalMain.chaosRegistry.getExtractionsFrom(is);
						if(degradations.size() == 0) {
							//TODO: Render Questionmark
						} else if(!ChaosCrystalMain.chaosRegistry.isIgnoreItem(is.getItem(), true)) {
							Node node = degradations.get(0);
							
							//TODO: get full aspect list
							renderAspectList(centerW, centerH, node.getAspects());

							NodePoint np = node.getLesser();
							
							ItemStack display = np.createItemStack();
							
							if(display != null) {
								renderItem(display, centerW + 5, centerH - 16 - 5);
							}
							//TODO: render block somehow if item is null
							
//							ItemStack[] parents = node.getDegradedFrom(node.getDispayItemStack());
//
//							for(int s = 0; s < parents.length; s++) {
//								if(parents[s].getItem() != null) {
//									renderItem(parents[s], centerW + 5 + s*16, centerH - 16 - 5);
//								}
//							}

						}

						renderItem(is, centerW - 16 - 5, centerH - 16 - 5);
						
						if(Config.cfgDebugGlasses) {
							String[] lines = new String[8];
							if(is.getItem() != null) {
								lines[0] = is.getDisplayName() + "(" + is.getUnlocalizedName() + ":" + is.getItemDamage() + ")";
								lines[1] = "Internal ID: " + Item.getIdFromItem(is.getItem());
							} else {
								lines[0] = "";
								lines[1] = "";
							}
							lines[2] = "Lifespan: " + ei.lifespan;
							lines[3] = "Age: " + ei.age;
							lines[4] = "Ticks existed: " + ei.ticksExisted;
							lines[5] = "Time Until Portal: " + ei.timeUntilPortal;
							lines[6] = "Delay before can pickup: " + ei.delayBeforeCanPickup;
							lines[7] = "Hover Start: " + ei.hoverStart;
							
							for(int l = 0; l < lines.length; l++) {
								Minecraft.getMinecraft().fontRenderer.drawString(
										lines[l], centerW + 10, centerH - 33 - l * 10,
										16777215);
							}
							
						}
					}
				}

				if(mop.typeOfHit == MovingObjectType.BLOCK) {
					World w = Minecraft.getMinecraft().thePlayer.worldObj;
					Block block = w.getBlock( mop.blockX, mop.blockY, mop.blockZ);

					if(!block.isAir(w, mop.blockX, mop.blockY, mop.blockZ)) {// We can't extract air...

						int meta = w.getBlockMetadata(
								mop.blockX,
								mop.blockY,
								mop.blockZ);

						List<Node> degradations = ChaosCrystalMain.chaosRegistry.getExtractionsFrom(new ItemStack(block, 1, meta));
						if(degradations.size() == 0) {
							//TODO: Render Questionmark
						} else if(!ChaosCrystalMain.chaosRegistry.isIgnoreBlock(block, true)) {
							Node node = degradations.get(0);

							//TODO: get full aspect list
							renderAspectList(centerW, centerH, node.getAspects());

							NodePoint np = node.getLesser();
							
							ItemStack display = np.createItemStack();
							
							if(display != null) {
								renderItem(display, centerW + 5, centerH - 16 - 5);
							}
							//TODO: render block somehow if item is null
							
//							ItemStack[] parents = node.getDegradedFrom(node.getDispayItemStack());
//
//							for(int s = 0; s < parents.length; s++) {
//								if(parents[s].getItem() != null) {
//									renderItem(parents[s], centerW + 5 + s*16, centerH - 16 - 5);
//								}
//							}

						}

						TileEntity te = w.getTileEntity(mop.blockX, mop.blockY, mop.blockZ);
						if(te instanceof TileEntityApparatus) {
							TileEntityApparatus apparatus = (TileEntityApparatus)te;
							for(int i = 0; i < apparatus.getSizeInventory(); i++) {
								ItemStack its = ((TileEntityApparatus) te).getStackInSlot(i);
								if(its != null && its.getItem() != null) {
									renderItem(its, centerW - 16 - 5 - 16*i, centerH + 5);
								}
							}
							if(apparatus.getOwner().length() > 0) {
								Minecraft.getMinecraft().fontRenderer.drawString(
										"Owner: " + apparatus.getOwner(),
										centerW + 10, centerH - 3,
										16777215);
							}
							
						}

						ItemStack is = new ItemStack(block, 1, meta);
						
						renderItem(is, centerW - 16 - 5, centerH - 16 - 5);
						
						if(Config.cfgDebugGlasses) {
							String[] lines = new String[3];
							lines[0] = block.getLocalizedName() + "(" + block.getUnlocalizedName() + ":" + meta + ")";
							if(is.getItem() != null) {
								lines[1] = is.getDisplayName() + "(" + is.getUnlocalizedName() + ":" + is.getItemDamageForDisplay() + ")";
							} else {
								lines[1] = "";
							}
							lines[2] = "Internal ID: " + Block.getIdFromBlock(block);
							
							for(int l = 0; l < lines.length; l++) {
								Minecraft.getMinecraft().fontRenderer.drawString(
										lines[l], centerW + 10, centerH - 33 - l * 10,
										16777215);
							}
							
						}

					}
				}
			}
		}

		if(currentItemStack != null) {
			
			Item item = currentItemStack.getItem();
			
			if(item instanceof IModeChangingItem) {
				IModeChangingItem mci = ((IModeChangingItem) item);


				int modeCount = mci.getModeCount(currentItemStack);
				if(modeCount > 0) {
					int modeIndex = mci.getSelectedModeForItemStack(currentItemStack);
					

					int bottom = event.resolution.getScaledHeight() - 80;


					Minecraft.getMinecraft().renderEngine.bindTexture(
							mci.getIconForMode(currentItemStack, modeIndex));
					this.drawTexturedModalRectScaled(centerW - 8, bottom, 0, 0, 16, 16, 256, 256);

					GL11.glColor4f(0.4F, 0.4F, 0.4F, 0.4F);

					if(modeIndex > 0) {
						Minecraft.getMinecraft().renderEngine.bindTexture(
								mci.getIconForMode(currentItemStack, modeIndex - 1));
						this.drawTexturedModalRectScaled(centerW - 8 - 14 - 2, bottom + 2, 0, 0, 14, 14, 256, 256);

					}

					if(modeIndex < modeCount - 1) {
						Minecraft.getMinecraft().renderEngine.bindTexture(
								mci.getIconForMode(currentItemStack, modeIndex + 1));
						this.drawTexturedModalRectScaled(centerW - 8 + 14 + 2, bottom + 2, 0, 0, 14, 14, 256, 256);

					}

					GL11.glColor4f(0.2F, 0.2F, 0.2F, 0.2F);

					if(modeIndex > 1) {
						Minecraft.getMinecraft().renderEngine.bindTexture(
								mci.getIconForMode(currentItemStack, modeIndex - 2));
						this.drawTexturedModalRectScaled(centerW - 8 - 26 - 4, bottom + 6, 0, 0, 10, 10, 256, 256);

					}

					if(modeIndex < modeCount - 2) {
						Minecraft.getMinecraft().renderEngine.bindTexture(
								mci.getIconForMode(currentItemStack, modeIndex + 2));
						this.drawTexturedModalRectScaled(centerW - 8 + 26 + 4, bottom + 6, 0, 0, 10, 10, 256, 256);

					}
				}
			}
		}

		GL11.glPopMatrix();
		Minecraft.getMinecraft().renderEngine.bindTexture(Gui.icons);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);

	}

	/**
	 * Draws a textured rectangle at the stored z-value. Args: x, y, u, v, width, height, textureWidth, textureHeight
	 * This uses a separate draw size, not the texture size.
	 */
	public void drawTexturedModalRectScaled(int x, int y, int u, int v, int width, int height, int texWidth, int texHeight)
	{
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV((double)(x + 0), (double)(y + height), (double)this.zLevel,
				(double)((float)(u + 0) * f), (double)((float)(v + texHeight) * f1));

		tessellator.addVertexWithUV((double)(x + width), (double)(y + height), (double)this.zLevel,
				(double)((float)(u + texWidth) * f), (double)((float)(v + texHeight) * f1));

		tessellator.addVertexWithUV((double)(x + width), (double)(y + 0), (double)this.zLevel,
				(double)((float)(u + texWidth) * f), (double)((float)(v + 0) * f1));

		tessellator.addVertexWithUV((double)(x + 0), (double)(y + 0), (double)this.zLevel,
				(double)((float)(u + 0) * f), (double)((float)(v + 0) * f1));
		tessellator.draw();
	}
	
    public static MovingObjectPosition rayTrace(EntityLivingBase player, double par1, float par3)
    {
        Vec3 vec3 = player.getPosition(par3);
        Vec3 vec31 = player.getLook(par3);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * par1, vec31.yCoord * par1, vec31.zCoord * par1);
        return raytraceDo(player.worldObj, vec3, vec32, !GeometryHelper.isEntityInBlock(player, true), false, true);
    }
	
    public static MovingObjectPosition raytraceDo(World world, Vec3 start, Vec3 end, boolean collisionCheck, boolean p_147447_4_, boolean p_147447_5_)
    {
        if (!Double.isNaN(start.xCoord) && !Double.isNaN(start.yCoord) && !Double.isNaN(start.zCoord))
        {
            if (!Double.isNaN(end.xCoord) && !Double.isNaN(end.yCoord) && !Double.isNaN(end.zCoord))
            {
                int i = MathHelper.floor_double(end.xCoord);
                int j = MathHelper.floor_double(end.yCoord);
                int k = MathHelper.floor_double(end.zCoord);
                int l = MathHelper.floor_double(start.xCoord);
                int i1 = MathHelper.floor_double(start.yCoord);
                int j1 = MathHelper.floor_double(start.zCoord);
                Block block = world.getBlock(l, i1, j1);
                int k1 = world.getBlockMetadata(l, i1, j1);

                if ((!p_147447_4_ || block.getCollisionBoundingBoxFromPool(world, l, i1, j1) != null) && block.canCollideCheck(k1, collisionCheck) && !world.isAirBlock(l, i1, j1))
                {
                    MovingObjectPosition movingobjectposition = block.collisionRayTrace(world, l, i1, j1, start, end);

                    if (movingobjectposition != null)
                    {
                        return movingobjectposition;
                    }
                }

                MovingObjectPosition movingobjectposition2 = null;
                k1 = 200;

                while (k1-- >= 0)
                {
                    if (Double.isNaN(start.xCoord) || Double.isNaN(start.yCoord) || Double.isNaN(start.zCoord))
                    {
                        return null;
                    }

                    if (l == i && i1 == j && j1 == k)
                    {
                        return p_147447_5_ ? movingobjectposition2 : null;
                    }

                    boolean flag6 = true;
                    boolean flag3 = true;
                    boolean flag4 = true;
                    double d0 = 999.0D;
                    double d1 = 999.0D;
                    double d2 = 999.0D;

                    if (i > l)
                    {
                        d0 = (double)l + 1.0D;
                    }
                    else if (i < l)
                    {
                        d0 = (double)l + 0.0D;
                    }
                    else
                    {
                        flag6 = false;
                    }

                    if (j > i1)
                    {
                        d1 = (double)i1 + 1.0D;
                    }
                    else if (j < i1)
                    {
                        d1 = (double)i1 + 0.0D;
                    }
                    else
                    {
                        flag3 = false;
                    }

                    if (k > j1)
                    {
                        d2 = (double)j1 + 1.0D;
                    }
                    else if (k < j1)
                    {
                        d2 = (double)j1 + 0.0D;
                    }
                    else
                    {
                        flag4 = false;
                    }

                    double d3 = 999.0D;
                    double d4 = 999.0D;
                    double d5 = 999.0D;
                    double d6 = end.xCoord - start.xCoord;
                    double d7 = end.yCoord - start.yCoord;
                    double d8 = end.zCoord - start.zCoord;

                    if (flag6)
                    {
                        d3 = (d0 - start.xCoord) / d6;
                    }

                    if (flag3)
                    {
                        d4 = (d1 - start.yCoord) / d7;
                    }

                    if (flag4)
                    {
                        d5 = (d2 - start.zCoord) / d8;
                    }

//                   boolean flag5 = false;
                    byte b0;

                    if (d3 < d4 && d3 < d5)
                    {
                        if (i > l)
                        {
                            b0 = 4;
                        }
                        else
                        {
                            b0 = 5;
                        }

                        start.xCoord = d0;
                        start.yCoord += d7 * d3;
                        start.zCoord += d8 * d3;
                    }
                    else if (d4 < d5)
                    {
                        if (j > i1)
                        {
                            b0 = 0;
                        }
                        else
                        {
                            b0 = 1;
                        }

                        start.xCoord += d6 * d4;
                        start.yCoord = d1;
                        start.zCoord += d8 * d4;
                    }
                    else
                    {
                        if (k > j1)
                        {
                            b0 = 2;
                        }
                        else
                        {
                            b0 = 3;
                        }

                        start.xCoord += d6 * d5;
                        start.yCoord += d7 * d5;
                        start.zCoord = d2;
                    }

                    Vec3 vec32 = Vec3.createVectorHelper(start.xCoord, start.yCoord, start.zCoord);
                    l = (int)(vec32.xCoord = (double)MathHelper.floor_double(start.xCoord));

                    if (b0 == 5)
                    {
                        --l;
                        ++vec32.xCoord;
                    }

                    i1 = (int)(vec32.yCoord = (double)MathHelper.floor_double(start.yCoord));

                    if (b0 == 1)
                    {
                        --i1;
                        ++vec32.yCoord;
                    }

                    j1 = (int)(vec32.zCoord = (double)MathHelper.floor_double(start.zCoord));

                    if (b0 == 3)
                    {
                        --j1;
                        ++vec32.zCoord;
                    }

                    Block block1 = world.getBlock(l, i1, j1);
                    int l1 = world.getBlockMetadata(l, i1, j1);

                    if ((!p_147447_4_ || block1.getCollisionBoundingBoxFromPool(world, l, i1, j1) != null) && !block1.isAir(world, l, i1, j1))
                    {
                        if (block1.canCollideCheck(l1, collisionCheck))
                        {
                            MovingObjectPosition movingobjectposition1 = block1.collisionRayTrace(world, l, i1, j1, start, end);

                            if (movingobjectposition1 != null)
                            {
                                return movingobjectposition1;
                            }
                        }
                        else
                        {
                            movingobjectposition2 = new MovingObjectPosition(l, i1, j1, b0, start, false);
                        }
                    }
                }

                return p_147447_5_ ? movingobjectposition2 : null;
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }
}
