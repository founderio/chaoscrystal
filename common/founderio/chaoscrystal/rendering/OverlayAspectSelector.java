package founderio.chaoscrystal.rendering;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.DimensionManager;
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
		if(!Minecraft.getMinecraft().thePlayer.isSneaking()) {
			return;
		}
		ItemStack currentItem = Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem();
		if(currentItem == null || currentItem.itemID != ChaosCrystalMain.itemFocus.itemID) {
			return;
		}
		if(currentItem.getItemDamage() != 2) {
			return;
		}
		if(event.dwheel == 0) {
			return;
		}
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
	}
	
	
	@ForgeSubscribe(priority = EventPriority.NORMAL)
	public void onRenderHud(RenderGameOverlayEvent event) {
		
		if(event.type != ElementType.CROSSHAIRS) {
			return;
		}
		
		ItemStack helmet = Minecraft.getMinecraft().thePlayer.inventory.armorInventory[3];
		
		if(helmet != null && helmet.itemID == ChaosCrystalMain.itemCrystalGlasses.itemID) {
			Minecraft.getMinecraft().entityRenderer.getMouseOver(0);
			if(Minecraft.getMinecraft().objectMouseOver != null) {
				Entity lookingAt = Minecraft.getMinecraft().objectMouseOver.entityHit;
				
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
					}
				}
				
				if(Minecraft.getMinecraft().objectMouseOver.typeOfHit == EnumMovingObjectType.TILE) {
					World w = Minecraft.getMinecraft().thePlayer.worldObj;
					int id = w.getBlockId(
							Minecraft.getMinecraft().objectMouseOver.blockX,
							Minecraft.getMinecraft().objectMouseOver.blockY,
							Minecraft.getMinecraft().objectMouseOver.blockZ);
			    	
			    	if(id != 0) {// We can't extract air...
			    		
			    		int meta = w.getBlockMetadata(
			    				Minecraft.getMinecraft().objectMouseOver.blockX,
								Minecraft.getMinecraft().objectMouseOver.blockY,
								Minecraft.getMinecraft().objectMouseOver.blockZ);
			        	
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
					        
//					        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Constants.MOD_ID + ":" + "textures/items/chaoscrystal.png"));
//							this.drawTexturedModalRectScaled(centerW - 15, centerH, 0, 0, 10, 10, 256, 256);
							//TODO: Render Block icon?
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
							

							
					
					        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
							GL11.glDisable(GL11.GL_BLEND);
							GL11.glPopMatrix();
			        	}
			        	
			        	TileEntity te = w.getBlockTileEntity(Minecraft.getMinecraft().objectMouseOver.blockX,
								Minecraft.getMinecraft().objectMouseOver.blockY,
								Minecraft.getMinecraft().objectMouseOver.blockZ);
			        	if(te != null) {
			        		if(te instanceof TileEntityApparatus) {
			        			doRenderMiniBlock = true;
			        			ItemStack its = ((TileEntityApparatus) te).getStackInSlot(0);
			        			if(its != null && its.itemID != 0) {
			        				ri.renderItemIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().renderEngine,
				        					its, centerW - 16 - 5, centerH + 16, true);
			        				ri.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().renderEngine,
			        						its, centerW - 16 - 5, centerH + 16);
			        			}
			        			
			        		}
//			        		else if(te instanceof TileEntityChest) {
//			        			doRenderMiniBlock = true;
//			        			int offset = 0;
//			        			int colOffset = 0;
//			        			int stacks = ((TileEntityChest) te).getSizeInventory();
//			        			
//			        			for(int i = 0; i < stacks; i++) {
//			        				ItemStack its = ((TileEntityChest) te).getStackInSlot(i);
//			        				if(its != null && its.itemID != 0) {
//				        				ri.renderItemIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().renderEngine,
//					        					its, centerW - 16 - 5 - colOffset, centerH + 16 + offset, true);
//				        				ri.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().renderEngine,
//				        						its, centerW - 16 - 5 - colOffset, centerH + 16 + offset);
//				        			
//				        				offset += 16;
//				        				if(offset >= 16*3) {
//											offset = 0;
//											colOffset += 16;
//										} else {
//											offset += 16;
//										}
//			        				}
//			        				
//			        			}
//			        		}
			        	}
			        	
			        	if(doRenderMiniBlock) {
			        		ri.renderItemIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().renderEngine, new ItemStack(id, 1, meta), centerW - 16 - 5, centerH, true);
			        	}
			        	
			        	Minecraft.getMinecraft().renderEngine.bindTexture(Gui.icons);
			        	GL11.glDisable(GL11.GL_BLEND);
			        	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			    	}
				}
			}
		}
		
		ItemStack currentItem = Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem();
		
		
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
			int bottom = event.resolution.getScaledHeight() - 60;
			
			
			GL11.glPushMatrix();
	        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	        GL11.glEnable(GL11.GL_BLEND);
	        GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
	        
	        
	        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Constants.MOD_ID + ":" + "textures/hud/aspect_" + selectedAspect + ".png"));
	        this.drawTexturedModalRectScaled(center - 8, bottom, 0, 0, 16, 16, 256, 256);
			
	        GL11.glColor4f(0.08F, 0.08F, 0.08F, 0.10003F);
	        
	        if(aspectIndex > 0) {
	        	Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Constants.MOD_ID + ":" + "textures/hud/aspect_" + Aspects.ASPECTS[aspectIndex - 1] + ".png"));
	            this.drawTexturedModalRectScaled(center - 8 - 14 - 2, bottom + 2, 0, 0, 14, 14, 256, 256);
	    		
	        }
	        
	        if(aspectIndex < Aspects.ASPECTS.length - 1) {
	        	Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Constants.MOD_ID + ":" + "textures/hud/aspect_" + Aspects.ASPECTS[aspectIndex + 1] + ".png"));
	            this.drawTexturedModalRectScaled(center - 8 + 14 + 2, bottom + 2, 0, 0, 14, 14, 256, 256);
	    		
	        }
	        
	        GL11.glColor4f(0.08F, 0.08F, 0.08F, 0.1000F);
	        
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
