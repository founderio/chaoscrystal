package founderio.chaoscrystal.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.techne.TechneModel;

import org.lwjgl.opengl.GL11;

import founderio.chaoscrystal.Constants;

public class TileEntityRenderer extends TileEntitySpecialRenderer {
	public final TechneModel modelReenactor;
	public final ResourceLocation resourceReenactor;
	
	public TileEntityRenderer() {
		String reenactor = "/assets/" + Constants.MOD_ID + "/models/reenactor.tcn";
		modelReenactor = new TechneModel(reenactor, TileEntityRenderer.class.getResource(reenactor));
		resourceReenactor = new ResourceLocation(Constants.MOD_ID + ":textures/models/reenactor.png");
	}
	
	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d0, double d1,
			double d2, float f) {
		GL11.glPushMatrix();
		
		GL11.glTranslatef((float)d0 + 0.5f, (float)d1, (float)d2 + 0.5f);
		GL11.glScalef(0.0625f, 0.0625f, 0.0625f);//1/16th scale, as techne tends to be big..
		GL11.glRotatef(180f, 1.0f, 0, 0);
		
		Minecraft.getMinecraft().renderEngine.bindTexture(resourceReenactor);
		
		modelReenactor.renderAll();
		
		GL11.glPopMatrix();
	}
}
