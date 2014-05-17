package founderio.chaoscrystal.rendering;

import javax.swing.Icon;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.entities.EntityFocusBorder;
import founderio.chaoscrystal.entities.EntityFocusFilter;
import founderio.chaoscrystal.entities.EntityFocusFilterTarget;
import founderio.chaoscrystal.entities.EntityFocusTransfer;

public class RenderFocus extends Render {

	private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation(
			"textures/misc/enchanted_item_glint.png");

	@Override
	public void doRender(Entity entity, double d0, double d1, double d2,
			float f, float f1) {

		Tessellator tessellator = Tessellator.instance;
		ItemStack itemstack;

		if (entity instanceof EntityFocusTransfer) {
			EntityFocusTransfer ef = (EntityFocusTransfer) entity;
			itemstack = ef.buildItemStack();
		} else if (entity instanceof EntityFocusBorder) {
			EntityFocusBorder ef = (EntityFocusBorder) entity;
			itemstack = ef.buildItemStack();
		} else if (entity instanceof EntityFocusFilter) {
			EntityFocusFilter ef = (EntityFocusFilter) entity;
			itemstack = ef.buildItemStack();
		} else if (entity instanceof EntityFocusFilterTarget) {
			EntityFocusFilterTarget ef = (EntityFocusFilterTarget) entity;
			itemstack = ef.buildItemStack();
		} else {
			return;
		}
		IIcon par2Icon = ChaosCrystalMain.itemFocus.getIconFromDamage(itemstack
				.getItemDamage());

		/*
		 * From here: Based on RenderItem.renderDroppedItem
		 */

		float f4 = ((IIcon) par2Icon).getMinU();
		float f5 = ((IIcon) par2Icon).getMaxU();
		float f6 = ((IIcon) par2Icon).getMinV();
		float f7 = ((IIcon) par2Icon).getMaxV();
		float f9 = 0.5F;
		float f10 = 0.25F;
		float f11;

		GL11.glPushMatrix();
		GL11.glTranslatef((float) d0, (float) d1 + 0.6f, (float) d2);

		GL11.glRotatef(entity.rotationYaw, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(180.0F - entity.rotationPitch, 1.0F, 0.0F, 0.0F);

		float f12 = 0.0625F;
		f11 = 0.021875F;

		GL11.glTranslatef(-f9, -f10, -((f12 + f11)));

		GL11.glTranslatef(0f, 0f, f12 + f11);

		if (itemstack.getItemSpriteNumber() == 0) {
			this.bindTexture(TextureMap.locationBlocksTexture);
		} else {
			this.bindTexture(TextureMap.locationItemsTexture);
		}

		ItemRenderer.renderItemIn2D(tessellator, f5, f6, f4, f7,
				((Icon) par2Icon).getIconWidth(),
				((Icon) par2Icon).getIconHeight(), f12);

		if (itemstack.hasEffect(0)) {
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
			float f15 = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
			GL11.glTranslatef(f15, 0.0F, 0.0F);
			GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
			ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F,
					255, 255, f12);
			GL11.glPopMatrix();
			GL11.glPushMatrix();
			GL11.glScalef(f14, f14, f14);
			f15 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
			GL11.glTranslatef(-f15, 0.0F, 0.0F);
			GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
			ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F,
					255, 255, f12);
			GL11.glPopMatrix();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glDepthFunc(GL11.GL_LEQUAL);
		}

		GL11.glPopMatrix();

	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return null;
	}
}
