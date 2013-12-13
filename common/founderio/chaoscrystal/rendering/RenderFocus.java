package founderio.chaoscrystal.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.entities.EntityFocus;

public class RenderFocus extends Render {

    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

	
	@Override
	public void doRender(Entity entity, double d0, double d1, double d2,
			float f, float f1) {

		EntityFocus ef = (EntityFocus)entity;
		
		Tessellator tessellator = Tessellator.instance;

        ItemStack itemstack = ef.buildItemStack();
       
        Icon par2Icon = ChaosCrystalMain.itemFocus.getIcon(itemstack, 0);
        
        /*
         * From here: Based on RenderItem.renderDroppedItem
         */
        
        float f4 = ((Icon)par2Icon).getMinU();
        float f5 = ((Icon)par2Icon).getMaxU();
        float f6 = ((Icon)par2Icon).getMinV();
        float f7 = ((Icon)par2Icon).getMaxV();
        float f8 = 1.0F;
        float f9 = 0.5F;
        float f10 = 0.25F;
        float f11;

        if (this.renderManager.options.fancyGraphics)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef((float)d0, (float)d1, (float)d2);

            GL11.glRotatef(ef.rotationYaw * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(ef.rotationPitch * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);

            float f12 = 0.0625F;
            f11 = 0.021875F;

            GL11.glTranslatef(-f9, -f10, -((f12 + f11)));
            

                GL11.glTranslatef(0f, 0f, f12 + f11);

                if (itemstack.getItemSpriteNumber() == 0)
                {
                    this.bindTexture(TextureMap.locationBlocksTexture);
                }
                else
                {
                    this.bindTexture(TextureMap.locationItemsTexture);
                }

                ItemRenderer.renderItemIn2D(tessellator, f5, f6, f4, f7, ((Icon)par2Icon).getIconWidth(), ((Icon)par2Icon).getIconHeight(), f12);

                if (itemstack.hasEffect(0))
                {
                    GL11.glDepthFunc(GL11.GL_EQUAL);
                    GL11.glDisable(GL11.GL_LIGHTING);
                    this.renderManager.renderEngine.bindTexture(RES_ITEM_GLINT);
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                    float f13 = 0.76F;
                    GL11.glColor4f(0.5F * f13, 0.25F * f13, 0.8F * f13, 1.0F);
                    GL11.glMatrixMode(GL11.GL_TEXTURE);
                    GL11.glPushMatrix();
                    float f14 = 0.125F;
                    GL11.glScalef(f14, f14, f14);
                    float f15 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
                    GL11.glTranslatef(f15, 0.0F, 0.0F);
                    GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                    ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, f12);
                    GL11.glPopMatrix();
                    GL11.glPushMatrix();
                    GL11.glScalef(f14, f14, f14);
                    f15 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
                    GL11.glTranslatef(-f15, 0.0F, 0.0F);
                    GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                    ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, f12);
                    GL11.glPopMatrix();
                    GL11.glMatrixMode(GL11.GL_MODELVIEW);
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glEnable(GL11.GL_LIGHTING);
                    GL11.glDepthFunc(GL11.GL_LEQUAL);
                }

            GL11.glPopMatrix();
        }
        else
        {
            GL11.glPushMatrix();
            GL11.glTranslatef((float)d0, (float)d1, (float)d2);

            GL11.glRotatef(ef.rotationYaw * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(ef.rotationPitch * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);

            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 1.0F, 0.0F);
            tessellator.addVertexWithUV((double)(0.0F - f9), (double)(0.0F - f10), 0.0D, (double)f4, (double)f7);
            tessellator.addVertexWithUV((double)(f8 - f9), (double)(0.0F - f10), 0.0D, (double)f5, (double)f7);
            tessellator.addVertexWithUV((double)(f8 - f9), (double)(1.0F - f10), 0.0D, (double)f5, (double)f6);
            tessellator.addVertexWithUV((double)(0.0F - f9), (double)(1.0F - f10), 0.0D, (double)f4, (double)f6);
            tessellator.draw();
            GL11.glPopMatrix();
        }
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return null;
	}
}
