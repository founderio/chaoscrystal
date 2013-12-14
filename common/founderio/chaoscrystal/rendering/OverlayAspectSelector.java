package founderio.chaoscrystal.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;

import org.lwjgl.opengl.GL11;

import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.Constants;
import founderio.chaoscrystal.degradation.Aspects;

public class OverlayAspectSelector extends Gui {
	@ForgeSubscribe(priority = EventPriority.NORMAL)
	public void onRenderExperienceBar(RenderGameOverlayEvent event) {
		
		ItemStack currentItem = Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem();
		if(currentItem.itemID != ChaosCrystalMain.itemFocus.itemID) {
			return;
		}
		if(currentItem.getItemDamage() != 1) {
			return;
		}
		
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
        
        if(aspectIndex < Aspects.ASPECTS.length) {
        	Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Constants.MOD_ID + ":" + "textures/hud/aspect_" + Aspects.ASPECTS[aspectIndex + 1] + ".png"));
            this.drawTexturedModalRectScaled(center - 8 + 14 + 2, bottom + 2, 0, 0, 14, 14, 256, 256);
    		
        }
        
        GL11.glColor4f(0.08F, 0.08F, 0.08F, 0.1000F);
        
        if(aspectIndex > 1) {
        	Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Constants.MOD_ID + ":" + "textures/hud/aspect_" + Aspects.ASPECTS[aspectIndex - 2] + ".png"));
            this.drawTexturedModalRectScaled(center - 8 - 26 - 4, bottom + 6, 0, 0, 10, 10, 256, 256);
    		
        }
        
        if(aspectIndex < Aspects.ASPECTS.length - 1) {
        	Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Constants.MOD_ID + ":" + "textures/hud/aspect_" + Aspects.ASPECTS[aspectIndex + 2] + ".png"));
            this.drawTexturedModalRectScaled(center - 8 + 26 + 4, bottom + 6, 0, 0, 10, 10, 256, 256);
    		
        }
        
        int offset = 0;
        
		for(String aspect : Aspects.ASPECTS) {
			
			
			
	       // GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			offset += 16;
		}
		
		
		Minecraft.getMinecraft().renderEngine.bindTexture(Gui.icons);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
		
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
