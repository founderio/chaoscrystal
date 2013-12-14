package founderio.chaoscrystal.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.techne.TechneModel;

import org.lwjgl.opengl.GL11;

import founderio.chaoscrystal.Constants;
import founderio.chaoscrystal.blocks.TileEntityApparatus;

public class TileEntityRenderer extends TileEntitySpecialRenderer {
	public final TechneModel modelReenactor;
	public final ResourceLocation resourceReenactor;
	private RenderItem ri;
	private EntityItem ei;
	
	public TileEntityRenderer() {
		String reenactor = "/assets/" + Constants.MOD_ID + "/models/reenactor.tcn";
		modelReenactor = new TechneModel(reenactor, TileEntityRenderer.class.getResource(reenactor));
		resourceReenactor = new ResourceLocation(Constants.MOD_ID + ":textures/models/reenactor.png");
		ri = new RenderItem() {
			@Override
			public boolean shouldBob() {
				return false;
			}
		};
		ei = new EntityItem(null, 0, 0, 0, new ItemStack(Item.pickaxeDiamond));
		ri.setRenderManager(RenderManager.instance);
	}
	
	private int rot = 0;
	
	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d0, double d1,
			double d2, float f) {
		GL11.glPushMatrix();
		
		rot++;
		if(rot >= 360) {
			rot = 0;
		}
		
		
		GL11.glTranslatef((float)d0 + 0.5f, (float)d1 + 0.04f, (float)d2 + 0.5f);
		
		GL11.glScalef(0.0625f, 0.0625f, 0.0625f);//1/16th scale, as techne tends to be big..
		GL11.glRotatef(180f, 1.0f, 0, 0);
		
		Minecraft.getMinecraft().renderEngine.bindTexture(resourceReenactor);
		
		modelReenactor.renderPart("Base");
		modelReenactor.renderPart("Socket");

		
		GL11.glRotatef(rot, 0, 1, 0);

		modelReenactor.renderPart("Arm1");
		modelReenactor.renderPart("Arm2");
		modelReenactor.renderPart("Arm3");
		modelReenactor.renderPart("Arm4");
		GL11.glPopMatrix();
		
		
		ItemStack is = ((TileEntityApparatus)tileentity).getStackInSlot(0);
		if(is == null) {
			ei.setEntityItemStack(new ItemStack(0,0,0));
		} else {
			ei.setEntityItemStack(is);
			
			GL11.glPushMatrix();
			GL11.glTranslatef((float)d0 + 0.5f, (float)d1 + 0.15f, (float)d2 + 0.5f);
			GL11.glRotatef(rot, 0, 1, 0);
			
			ei.setPosition(d0, d1+1, d2);

			RenderItem.renderInFrame = true;
			ri.doRenderItem(ei, 0, .5f, 0, 0, 0);
			
			GL11.glPopMatrix();
		}
		
		
	}
}
