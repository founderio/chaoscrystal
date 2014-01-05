package founderio.chaoscrystal.rendering;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.network.PacketDispatcher;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.Constants;
import founderio.chaoscrystal.IModeChangingItem;
import founderio.chaoscrystal.aspects.Aspects;
import founderio.chaoscrystal.aspects.IAspectStore;
import founderio.chaoscrystal.aspects.Node;
import founderio.chaoscrystal.blocks.TileEntityApparatus;
import founderio.chaoscrystal.entities.EntityChaosCrystal;
import founderio.chaoscrystal.entities.EntityFocusBorder;
import founderio.chaoscrystal.entities.EntityFocusFilter;
import founderio.chaoscrystal.entities.EntityFocusFilterTarget;
import founderio.chaoscrystal.entities.EntityFocusTransfer;

public class OverlayAspectSelector extends Gui {

	private RenderItem ri;

	public OverlayAspectSelector() {
		ri = new RenderItem();
		ri.setRenderManager(RenderManager.instance);
	}

	@ForgeSubscribe(priority = EventPriority.NORMAL)
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
				

				try {
					ByteArrayOutputStream bos = new ByteArrayOutputStream(30);
					DataOutputStream dos = new DataOutputStream(bos);

					dos.writeInt(2);
					dos.writeInt(Minecraft.getMinecraft().thePlayer.dimension);
					dos.writeUTF(Minecraft.getMinecraft().thePlayer.username);
					dos.writeInt(modeIndex);

					Packet250CustomPayload degradationPacket = new Packet250CustomPayload();
					degradationPacket.channel = Constants.CHANNEL_NAME_OTHER_VISUAL;
					degradationPacket.data = bos.toByteArray();
					degradationPacket.length = bos.size();

					dos.close();

					PacketDispatcher.sendPacketToServer(degradationPacket);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		} else if(currentItemStack.itemID == ChaosCrystalMain.itemManual.itemID) {
			event.setCanceled(true);
			if(event.dwheel > 0) {
				RenderItemManual.page--;
			} else {
				RenderItemManual.page++;
			}
		}

	}

	public void renderItem(ItemStack is, int x, int y) {
		RenderHelper.enableGUIStandardItemLighting();
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
			//mc.pointedEntityLiving = null;
			double d0 = (double)mc.playerController.getBlockReachDistance();
			MovingObjectPosition mop = mc.renderViewEntity.rayTrace(d0, par1);
			double d1 = d0;
			Vec3 vec3 = mc.renderViewEntity.getPosition(par1);

			if (mc.playerController.extendedReach())
			{
				d0 = 6.0D;
				d1 = 6.0D;
			}
			else
			{
				if (d0 > 3.0D)
				{
					d1 = 3.0D;
				}

				d0 = d1;
			}

			if (mop != null)
			{
				d1 = mop.hitVec.distanceTo(vec3);
			}

			Vec3 vec31 = mc.renderViewEntity.getLook(par1);
			Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
			pointedEntity = null;
			float f1 = 1.0F;
			@SuppressWarnings("rawtypes")
			List list = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.renderViewEntity, mc.renderViewEntity.boundingBox.addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand((double)f1, (double)f1, (double)f1));
			double d2 = d1;

			for (int i = 0; i < list.size(); ++i)
			{
				Entity entity = (Entity)list.get(i);

				float f2 = entity.getCollisionBorderSize();
				AxisAlignedBB axisalignedbb = entity.boundingBox.expand((double)f2, (double)f2, (double)f2);
				MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

				if (axisalignedbb.isVecInside(vec3))
				{
					if (0.0D < d2 || d2 == 0.0D)
					{
						pointedEntity = entity;
						d2 = 0.0D;
					}
				}
				else if (movingobjectposition != null)
				{
					double d3 = vec3.distanceTo(movingobjectposition.hitVec);

					if (d3 < d2 || d2 == 0.0D)
					{
						if (entity == mc.renderViewEntity.ridingEntity && !entity.canRiderInteract())
						{
							if (d2 == 0.0D)
							{
								pointedEntity = entity;
							}
						}
						else
						{
							pointedEntity = entity;
							d2 = d3;
						}
					}
				}
			}

			if (pointedEntity != null && (d2 < d1 || mop == null))
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

		for(String aspect : Aspects.ASPECTS) {
			int asp = store.getAspect(aspect);

			Minecraft.getMinecraft().renderEngine.bindTexture(
					new ResourceLocation(Constants.MOD_ID + ":" + "textures/hud/aspect_" + aspect + ".png"));
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
			String aspect = Aspects.ASPECTS[i];
			int asp = aspectArray[i];

			if(asp > 0) {
				Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Constants.MOD_ID + ":" + "textures/hud/aspect_" + aspect + ".png"));
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

	@ForgeSubscribe(priority = EventPriority.NORMAL)
	public void onRenderHud(RenderGameOverlayEvent event) {

		if(event.type != ElementType.CROSSHAIRS) {
			return;
		}

		ItemStack helmet = Minecraft.getMinecraft().thePlayer.inventory.armorInventory[3];

		ItemStack currentItemStack = Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem();

		boolean specialSkip = (currentItemStack != null && currentItemStack.getItem() instanceof ItemMap) || ChaosCrystalMain.cfgSneakToShowAspects && !Minecraft.getMinecraft().thePlayer.isSneaking();

		int centerW = event.resolution.getScaledWidth()/2;
		int centerH = event.resolution.getScaledHeight()/2;

		GL11.glPushMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		if(helmet != null && helmet.itemID == ChaosCrystalMain.itemCrystalGlasses.itemID && !specialSkip) {
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


						String aspect = ((EntityFocusFilter)lookingAt).getAspect();

						Minecraft.getMinecraft().renderEngine.bindTexture(
								new ResourceLocation(Constants.MOD_ID + ":" + "textures/hud/aspect_" + aspect + ".png"));
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


						ItemStack is = ((EntityItem)lookingAt).getEntityItem();

						List<Node> degradations = ChaosCrystalMain.degradationStore.getExtractionsFrom(is);
						if(degradations.size() == 0) {

						} else {
							Node node = degradations.get(0);

							renderAspectList(centerW, centerH, node.getAspects());

							ItemStack[] parents = node.getDegradedFrom(node.getDispayItemStack());

							for(int s = 0; s < parents.length; s++) {
								if(parents[s].itemID != 0) {
									renderItem(parents[s], centerW + 5 + s*16, centerH - 16 - 5);
								}
							}

						}

						renderItem(is, centerW - 16 - 5, centerH - 16 - 5);
					}
				}

				if(mop.typeOfHit == EnumMovingObjectType.TILE) {
					World w = Minecraft.getMinecraft().thePlayer.worldObj;
					int id = w.getBlockId(
							mop.blockX,
							mop.blockY,
							mop.blockZ);

					if(id != 0) {// We can't extract air...

						int meta = w.getBlockMetadata(
								mop.blockX,
								mop.blockY,
								mop.blockZ);

						boolean doRenderMiniBlock = false;
						List<Node> degradations = ChaosCrystalMain.degradationStore.getExtractionsFrom(new ItemStack(id, 1, meta));
						if(degradations.size() != 0) {
							Node node = degradations.get(0);
							doRenderMiniBlock = true;

							renderAspectList(centerW, centerH, node.getAspects());

							ItemStack[] parents = node.getDegradedFrom(node.getDispayItemStack());

							for(int s = 0; s < parents.length; s++) {
								if(parents[s].itemID != 0) {
									renderItem(parents[s], centerW + 5 + s*16, centerH - 16 - 5);
								}
							}

						}

						TileEntity te = w.getBlockTileEntity(mop.blockX,
								mop.blockY,
								mop.blockZ);
						if(te instanceof TileEntityApparatus) {
							TileEntityApparatus apparatus = (TileEntityApparatus)te;
							doRenderMiniBlock = true;
							for(int i = 0; i < apparatus.getSizeInventory(); i++) {
								ItemStack its = ((TileEntityApparatus) te).getStackInSlot(i);
								if(its != null && its.itemID != 0) {
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

						if(doRenderMiniBlock) {
							renderItem(new ItemStack(id, 1, meta), centerW - 16 - 5, centerH - 16 - 5);
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
}
