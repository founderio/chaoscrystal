package founderio.chaoscrystal.rendering;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
import founderio.chaoscrystal.blocks.TileEntityApparatus;
import founderio.chaoscrystal.degradation.Aspects;
import founderio.chaoscrystal.degradation.Degradation;
import founderio.chaoscrystal.entities.EntityChaosCrystal;
import founderio.chaoscrystal.entities.EntityFocusBorder;
import founderio.chaoscrystal.entities.EntityFocusFilter;
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

		
		ItemStack currentItem = Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem();
		if(currentItem == null) {
			return;
		}
		
		if(currentItem.itemID == ChaosCrystalMain.itemFocus.itemID && currentItem.getItemDamage() == 2) {
			event.setCanceled(true);
			int aspectIndex;
			NBTTagCompound tags = currentItem.getTagCompound();
			if(tags != null) {
				String selectedAspect = tags.getString("aspect");
				aspectIndex = Aspects.getAspectDisplayId(selectedAspect);
				if(aspectIndex == -1) {
					aspectIndex = 0;
				}
			} else {
				tags = new NBTTagCompound();
				aspectIndex = 0;
			}
			if(event.dwheel > 0 &&  aspectIndex < Aspects.ASPECTS.length - 1) {
				aspectIndex++;
	        }
			if(event.dwheel < 0 &&  aspectIndex > 0) {
				aspectIndex--;
	        }
			tags.setString("aspect", Aspects.ASPECTS[aspectIndex]);
			currentItem.setTagCompound(tags);
			
			try {
	    		ByteArrayOutputStream bos = new ByteArrayOutputStream(30);
	    		DataOutputStream dos = new DataOutputStream(bos);

	    		dos.writeInt(2);
	    		dos.writeInt(Minecraft.getMinecraft().thePlayer.dimension);
	    		dos.writeUTF(Minecraft.getMinecraft().thePlayer.username);
	    		dos.writeUTF(Aspects.ASPECTS[aspectIndex]);
	    		
	    		Packet250CustomPayload degradationPacket = new Packet250CustomPayload();
	    		degradationPacket.channel = Constants.CHANNEL_NAME_OTHER_VISUAL;
	    		degradationPacket.data = bos.toByteArray();
	    		degradationPacket.length = bos.size();

	    		dos.close();
	    		
	    		PacketDispatcher.sendPacketToServer(degradationPacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if(currentItem.itemID == ChaosCrystalMain.itemManual.itemID) {
			event.setCanceled(true);
			if(event.dwheel > 0) {
				RenderItemManual.page++;
			} else {
				RenderItemManual.page--;
			}
		}
		
	}
	
	/*
	 * Copy & modify from Minecraft.getMinecraft().entityRenderer.getMouseOver()
	 */
	public MovingObjectPosition getMouseOver(float par1)
    {
		Entity pointedEntity = null;
		Minecraft mc = Minecraft.getMinecraft();
		
        if (mc.renderViewEntity != null)
        {
            if (mc.theWorld != null)
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
                List list = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.renderViewEntity, mc.renderViewEntity.boundingBox.addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand((double)f1, (double)f1, (double)f1));
                double d2 = d1;

                for (int i = 0; i < list.size(); ++i)
                {
                    Entity entity = (Entity)list.get(i);

//                    if (entity.canBeCollidedWith())
//                    {
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
//                    }
                }

                if (pointedEntity != null && (d2 < d1 || mop == null))
                {
                    mop = new MovingObjectPosition(pointedEntity);
                }
                return mop;
            }
        }
        return null;
    }
	
	
	@ForgeSubscribe(priority = EventPriority.NORMAL)
	public void onRenderHud(RenderGameOverlayEvent event) {
		
		if(event.type != ElementType.CROSSHAIRS) {
			return;
		}
		
		ItemStack helmet = Minecraft.getMinecraft().thePlayer.inventory.armorInventory[3];
		
		ItemStack currentItem = Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem();
		
		boolean specialSkip = (currentItem != null && currentItem.getItem() instanceof ItemMap) || ChaosCrystalMain.cfg_sneakToShowAspects && !Minecraft.getMinecraft().thePlayer.isSneaking();
		
		
		if(helmet != null && helmet.itemID == ChaosCrystalMain.itemCrystalGlasses.itemID && !specialSkip) {
			MovingObjectPosition mop = getMouseOver(0);
			
			if(mop != null) {
				Entity lookingAt = mop.entityHit;
				
				if(lookingAt != null) {
					if(lookingAt instanceof EntityChaosCrystal) {
						EntityChaosCrystal e = (EntityChaosCrystal)lookingAt;
						
						int centerW = event.resolution.getScaledWidth()/2;
						int centerH = event.resolution.getScaledHeight()/2;
						
						int offset = 0;
						int colOffset = 0;
						final int colWidth = 64;
						
						GL11.glPushMatrix();
				        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				        GL11.glEnable(GL11.GL_BLEND);
				        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				        
				        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Constants.MOD_ID + ":" + "textures/items/chaoscrystal.png"));
						this.drawTexturedModalRectScaled(centerW - 15, centerH, 0, 0, 10, 10, 256, 256);
						
						for(String aspect : Aspects.ASPECTS) {
							int asp = e.getAspect(aspect);

							Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Constants.MOD_ID + ":" + "textures/hud/aspect_" + aspect + ".png"));
							this.drawTexturedModalRectScaled(centerW + 5 + colOffset, centerH + offset, 0, 0, 10, 10, 256, 256);

							Minecraft.getMinecraft().fontRenderer.drawString(Integer.toString(asp), centerW + 16 + colOffset, centerH + 2 + offset, 16777215);
							
							
							if(offset >= 30) {
								offset = 0;
								colOffset += colWidth;
							} else {
								offset += 10;
							}
						}
						

						Minecraft.getMinecraft().renderEngine.bindTexture(Gui.icons);
				
				        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
						GL11.glDisable(GL11.GL_BLEND);
						GL11.glPopMatrix();
					} else if(lookingAt instanceof EntityFocusFilter) {
						int centerW = event.resolution.getScaledWidth()/2;
						int centerH = event.resolution.getScaledHeight()/2;
						
						GL11.glPushMatrix();
				        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				        GL11.glEnable(GL11.GL_BLEND);
				        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				        
				        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Constants.MOD_ID + ":" + "textures/items/focus_filter.png"));
						this.drawTexturedModalRectScaled(centerW - 15, centerH, 0, 0, 10, 10, 256, 256);
						
				        
				        String aspect = ((EntityFocusFilter)lookingAt).getAspect();
				        System.out.println("Rendering with " + aspect + " " + lookingAt.worldObj.isRemote);
				        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Constants.MOD_ID + ":" + "textures/hud/aspect_" + aspect + ".png"));
						this.drawTexturedModalRectScaled(centerW + 5, centerH, 0, 0, 10, 10, 256, 256);
						
				        Minecraft.getMinecraft().renderEngine.bindTexture(Gui.icons);
						
				        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
						GL11.glDisable(GL11.GL_BLEND);
						GL11.glPopMatrix();
					} else if(lookingAt instanceof EntityFocusBorder) {
						int centerW = event.resolution.getScaledWidth()/2;
						int centerH = event.resolution.getScaledHeight()/2;
						
						GL11.glPushMatrix();
				        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				        GL11.glEnable(GL11.GL_BLEND);
				        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				        
				        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Constants.MOD_ID + ":" + "textures/items/focus_border.png"));
						this.drawTexturedModalRectScaled(centerW - 15, centerH, 0, 0, 10, 10, 256, 256);
					
				        Minecraft.getMinecraft().renderEngine.bindTexture(Gui.icons);
						
				        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
						GL11.glDisable(GL11.GL_BLEND);
						GL11.glPopMatrix();
					} else if(lookingAt instanceof EntityFocusTransfer) {
						int centerW = event.resolution.getScaledWidth()/2;
						int centerH = event.resolution.getScaledHeight()/2;
						
						GL11.glPushMatrix();
				        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				        GL11.glEnable(GL11.GL_BLEND);
				        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				        
				        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Constants.MOD_ID + ":" + "textures/items/focus_transfer.png"));
						this.drawTexturedModalRectScaled(centerW - 15, centerH, 0, 0, 10, 10, 256, 256);

				        Minecraft.getMinecraft().renderEngine.bindTexture(Gui.icons);
				        
				        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
						GL11.glDisable(GL11.GL_BLEND);
						GL11.glPopMatrix();
					} else if(lookingAt instanceof EntityItem) {
						int centerW = event.resolution.getScaledWidth()/2;
						int centerH = event.resolution.getScaledHeight()/2;
						
						GL11.glPushMatrix();
				        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				        GL11.glEnable(GL11.GL_BLEND);
				        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				        
				        ItemStack is = ((EntityItem)lookingAt).getEntityItem();
				        
				        Degradation degradation = ChaosCrystalMain.degradationStore.getDegradation(is.itemID, is.getItemDamage());
			        	if(degradation == null) {
			        		
			        	} else {
							int offset = 0;
							int colOffset = 0;
							final int colWidth = 64;
							
							GL11.glPushMatrix();
					        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					        GL11.glEnable(GL11.GL_BLEND);
					        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					        
							for(int i = 0; i < degradation.aspects.length; i++) {
								String aspect = degradation.aspects[i];
								int asp = degradation.amounts[i];

								Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Constants.MOD_ID + ":" + "textures/hud/aspect_" + aspect + ".png"));
								this.drawTexturedModalRectScaled(centerW + 5 + colOffset, centerH + offset, 0, 0, 10, 10, 256, 256);

								Minecraft.getMinecraft().fontRenderer.drawString(Integer.toString(asp), centerW + 16 + colOffset, centerH + 2 + offset, 16777215);
								
								
								if(offset >= 30) {
									offset = 0;
									colOffset += colWidth;
								} else {
									offset += 10;
								}
							}
							
							GL11.glPopMatrix();
			        	}
			        	
				        ri.renderItemAndEffectIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().renderEngine, is, centerW - 16 - 5, centerH);
				        
				        ri.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().renderEngine, is, centerW - 16 - 5, centerH);
				        
				        Minecraft.getMinecraft().renderEngine.bindTexture(Gui.icons);
				        
				        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				        GL11.glDisable(GL11.GL_LIGHTING);
						GL11.glDisable(GL11.GL_BLEND);
						GL11.glPopMatrix();
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
			        	
			    		int centerW = event.resolution.getScaledWidth()/2;
						int centerH = event.resolution.getScaledHeight()/2;
			    		boolean doRenderMiniBlock = false;
			        	Degradation degradation = ChaosCrystalMain.degradationStore.getDegradation(id, meta);
			        	if(degradation == null) {
			        		
			        	} else {
			        		doRenderMiniBlock = true;
							int offset = 0;
							int colOffset = 0;
							final int colWidth = 64;
							
							GL11.glPushMatrix();
					        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					        GL11.glEnable(GL11.GL_BLEND);
					        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					        
							for(int i = 0; i < degradation.aspects.length; i++) {
								String aspect = degradation.aspects[i];
								int asp = degradation.amounts[i];

								Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Constants.MOD_ID + ":" + "textures/hud/aspect_" + aspect + ".png"));
								this.drawTexturedModalRectScaled(centerW + 5 + colOffset, centerH + offset, 0, 0, 10, 10, 256, 256);

								Minecraft.getMinecraft().fontRenderer.drawString(Integer.toString(asp), centerW + 16 + colOffset, centerH + 2 + offset, 16777215);
								
								
								if(offset >= 30) {
									offset = 0;
									colOffset += colWidth;
								} else {
									offset += 10;
								}
							}
							
							GL11.glPopMatrix();
			        	}
			        	
			        	TileEntity te = w.getBlockTileEntity(mop.blockX,
								mop.blockY,
								mop.blockZ);
			        	if(te != null) {
			        		if(te instanceof TileEntityApparatus) {
			        			//doRenderMiniBlock = true;
			        			ItemStack its = ((TileEntityApparatus) te).getStackInSlot(0);
			        			if(its != null && its.itemID != 0) {
			        				ri.renderItemAndEffectIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().renderEngine,
				        					its, centerW - 16 - 5, centerH);
			        				ri.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().renderEngine,
			        						its, centerW - 16 - 5, centerH);
			        			}
			        			
			        		}
			        	}
			        	
			        	if(doRenderMiniBlock) {
			        		ri.renderItemAndEffectIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().renderEngine, new ItemStack(id, 1, meta), centerW - 16 - 5, centerH);
				        	
			        		ri.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().renderEngine, new ItemStack(id, 1, meta), centerW - 16 - 5, centerH);
			        	}
			        	
			        	Minecraft.getMinecraft().renderEngine.bindTexture(Gui.icons);
			        	GL11.glDisable(GL11.GL_BLEND);
			        	GL11.glDisable(GL11.GL_LIGHTING);
			        	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			    	}
				}
			}
		}
		
		
		if(currentItem != null && currentItem.itemID == ChaosCrystalMain.itemFocus.itemID && currentItem.getItemDamage() == 2) {
			
			String selectedAspect;
			int aspectIndex;
			NBTTagCompound tags = currentItem.getTagCompound();
			if(tags != null) {
				selectedAspect = tags.getString("aspect");
				aspectIndex = Aspects.getAspectDisplayId(selectedAspect);
				if(aspectIndex == -1) {
					selectedAspect = Aspects.ASPECTS[0];
					aspectIndex = 0;
				}
			} else {
				selectedAspect = Aspects.ASPECTS[0];
				aspectIndex = 0;
			}
			
			int center = event.resolution.getScaledWidth()/2;
			int bottom = event.resolution.getScaledHeight() - 80;
			
			
			GL11.glPushMatrix();
	        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	        GL11.glEnable(GL11.GL_BLEND);
	        GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
	        
	        
	        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Constants.MOD_ID + ":" + "textures/hud/aspect_" + selectedAspect + ".png"));
	        this.drawTexturedModalRectScaled(center - 8, bottom, 0, 0, 16, 16, 256, 256);
			
	        GL11.glColor4f(0.4F, 0.4F, 0.4F, 0.4F);
	        
	        if(aspectIndex > 0) {
	        	Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Constants.MOD_ID + ":" + "textures/hud/aspect_" + Aspects.ASPECTS[aspectIndex - 1] + ".png"));
	            this.drawTexturedModalRectScaled(center - 8 - 14 - 2, bottom + 2, 0, 0, 14, 14, 256, 256);
	    		
	        }
	        
	        if(aspectIndex < Aspects.ASPECTS.length - 1) {
	        	Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Constants.MOD_ID + ":" + "textures/hud/aspect_" + Aspects.ASPECTS[aspectIndex + 1] + ".png"));
	            this.drawTexturedModalRectScaled(center - 8 + 14 + 2, bottom + 2, 0, 0, 14, 14, 256, 256);
	    		
	        }
	        
	        GL11.glColor4f(0.2F, 0.2F, 0.2F, 0.2F);
	        
	        if(aspectIndex > 1) {
	        	Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Constants.MOD_ID + ":" + "textures/hud/aspect_" + Aspects.ASPECTS[aspectIndex - 2] + ".png"));
	            this.drawTexturedModalRectScaled(center - 8 - 26 - 4, bottom + 6, 0, 0, 10, 10, 256, 256);
	    		
	        }
	        
	        if(aspectIndex < Aspects.ASPECTS.length - 2) {
	        	Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Constants.MOD_ID + ":" + "textures/hud/aspect_" + Aspects.ASPECTS[aspectIndex + 2] + ".png"));
	            this.drawTexturedModalRectScaled(center - 8 + 26 + 4, bottom + 6, 0, 0, 10, 10, 256, 256);
	    		
	        }
	        
			Minecraft.getMinecraft().renderEngine.bindTexture(Gui.icons);
	
	        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glPopMatrix();
		}
		
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
