package founderio.chaoscrystal.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.Constants;

public class RenderItemManual implements IItemRenderer {
	private RenderItem ri;
		
	public RenderItemManual() {
		ri = new RenderItem();
		ri.setRenderManager(RenderManager.instance);
	}
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return type == ItemRenderType.FIRST_PERSON_MAP;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public static int page = 1;

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		//switch(type) {
		//case FIRST_PERSON_MAP:
			
			//this.bufferedImage.updateDynamicTexture();
	        byte b1 = 0;
	        byte b2 = 0;
	        int w = 135;
	        Tessellator tessellator = Tessellator.instance;
	        float f = 0.0F;
	        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Constants.MOD_ID + ":textures/hud/manual_background.png"));
	        GL11.glEnable(GL11.GL_BLEND);
	        GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
	        GL11.glDisable(GL11.GL_ALPHA_TEST);
	        tessellator.startDrawingQuads();
	        tessellator.addVertexWithUV((double)((float)(b1 + -20) + f), (double)((float)(b2 + 148) - f), -0.009999999776482582D, 0.0D, 1.0D);
	        tessellator.addVertexWithUV((double)((float)(b1 + 148) - f), (double)((float)(b2 + 148) - f), -0.009999999776482582D, 1.0D, 1.0D);
	        tessellator.addVertexWithUV((double)((float)(b1 + 148) - f), (double)((float)(b2 + -20) + f), -0.009999999776482582D, 1.0D, 0.0D);
	        tessellator.addVertexWithUV((double)((float)(b1 + -20) + f), (double)((float)(b2 + -20) + f), -0.009999999776482582D, 0.0D, 0.0D);
	        tessellator.draw();
	       
	        ItemStack helmet = Minecraft.getMinecraft().thePlayer.inventory.armorInventory[3];
			
			if(helmet == null || helmet.itemID != ChaosCrystalMain.itemCrystalGlasses.itemID) {
				Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Constants.MOD_ID + ":textures/hud/manual_stubtext.png"));
			    
		        tessellator.startDrawingQuads();
		        tessellator.addVertexWithUV((double)((float)(b1 + -20) + f), (double)((float)(b2 + 148) - f), -0.009999999776482582D, 0.0D, 1.0D);
		        tessellator.addVertexWithUV((double)((float)(b1 + 148) - f), (double)((float)(b2 + 148) - f), -0.009999999776482582D, 1.0D, 1.0D);
		        tessellator.addVertexWithUV((double)((float)(b1 + 148) - f), (double)((float)(b2 + -20) + f), -0.009999999776482582D, 1.0D, 0.0D);
		        tessellator.addVertexWithUV((double)((float)(b1 + -20) + f), (double)((float)(b2 + -20) + f), -0.009999999776482582D, 0.0D, 0.0D);
		        tessellator.draw();
			} else {
				b1 -= 3;
		        GL11.glDisable(GL11.GL_DEPTH_TEST);
		        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
		        int strWidth;
		        if(page <= 0) {
		        	page = 2;
		        }
		        switch(page) {
		        default:
		        	page = 1;
		        case 1:
		        	 strWidth= fr.getStringWidth("Chaos Crystals");
				        fr.drawStringWithShadow("Chaos Crystals", b1 + w/2 - strWidth/2, b2, 0xFFFFFF);
				        fr.drawSplitString("The mysterious chaos crystals are one of the greatest mysteries.\n\n" +
				        		"Since the old times wizards try to understand the way they work.\n\n" +
				        		"On this tablet you can read about some of their findings.",
				        		b1, b2 + fr.FONT_HEIGHT * 2, w, 0xFFFFFF);
				        break;
		        case 2:
			        GL11.glEnable(GL11.GL_ALPHA_TEST);
		        	ri.renderItemAndEffectIntoGUI(fr, Minecraft.getMinecraft().renderEngine, new ItemStack(ChaosCrystalMain.itemChaosCrystal), b1, b2);
			        GL11.glDisable(GL11.GL_ALPHA_TEST);
		        	
		        }
		       
		        strWidth = fr.getStringWidth("Page " + page + "/2");
		        fr.drawString("Page " + page + "/2", b1 + w - strWidth, b2 + w - fr.FONT_HEIGHT, 0xFFFFFF);
		        //fr.drawStringWithShadow("The mysterious chaos crystals are one of the greatest mysteries.", b1, b2 + fr.FONT_HEIGHT * 2, 0xFFFFFF);
			}
	       
	        
	        GL11.glEnable(GL11.GL_DEPTH_TEST);
	        GL11.glEnable(GL11.GL_ALPHA_TEST);
	        GL11.glDisable(GL11.GL_BLEND);
	        
	        
//	        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("textures/map/map_icons.png"););
//	        int k1 = 0;
//
//	        for (Iterator iterator = par3MapData.playersVisibleOnMap.values().iterator(); iterator.hasNext(); ++k1)
//	        {
//	            MapCoord mapcoord = (MapCoord)iterator.next();
//	            GL11.glPushMatrix();
//	            GL11.glTranslatef((float)b1 + (float)mapcoord.centerX / 2.0F + 64.0F, (float)b2 + (float)mapcoord.centerZ / 2.0F + 64.0F, -0.02F);
//	            GL11.glRotatef((float)(mapcoord.iconRotation * 360) / 16.0F, 0.0F, 0.0F, 1.0F);
//	            GL11.glScalef(4.0F, 4.0F, 3.0F);
//	            GL11.glTranslatef(-0.125F, 0.125F, 0.0F);
//	            float f1 = (float)(mapcoord.iconSize % 4 + 0) / 4.0F;
//	            float f2 = (float)(mapcoord.iconSize / 4 + 0) / 4.0F;
//	            float f3 = (float)(mapcoord.iconSize % 4 + 1) / 4.0F;
//	            float f4 = (float)(mapcoord.iconSize / 4 + 1) / 4.0F;
//	            tessellator.startDrawingQuads();
//	            tessellator.addVertexWithUV(-1.0D, 1.0D, (double)((float)k1 * 0.001F), (double)f1, (double)f2);
//	            tessellator.addVertexWithUV(1.0D, 1.0D, (double)((float)k1 * 0.001F), (double)f3, (double)f2);
//	            tessellator.addVertexWithUV(1.0D, -1.0D, (double)((float)k1 * 0.001F), (double)f3, (double)f4);
//	            tessellator.addVertexWithUV(-1.0D, -1.0D, (double)((float)k1 * 0.001F), (double)f1, (double)f4);
//	            tessellator.draw();
//	            GL11.glPopMatrix();
//	        }

	        GL11.glPushMatrix();
	        GL11.glTranslatef(0.0F, 0.0F, -0.04F);
	        GL11.glScalef(1.0F, 1.0F, 1.0F);
	        GL11.glPopMatrix();
//	        GL11.glPopMatrix();
		//}
	}

}
