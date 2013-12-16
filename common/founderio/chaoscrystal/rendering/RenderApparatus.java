package founderio.chaoscrystal.rendering;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.techne.TechneModel;

import org.lwjgl.opengl.GL11;

import founderio.chaoscrystal.Constants;
import founderio.chaoscrystal.blocks.BlockApparatus;
import founderio.chaoscrystal.blocks.TileEntityApparatus;
import founderio.chaoscrystal.blocks.TileEntityCreator;
import founderio.chaoscrystal.blocks.TileEntityReconstructor;

public class RenderApparatus extends TileEntitySpecialRenderer implements IItemRenderer {
	public final TechneModel modelReconstructor;
	public final ResourceLocation resourceReconstructor;
	public final TechneModel modelCreator;
	public final ResourceLocation resourceCreator;
	private RenderItem ri;
	private EntityItem ei;
	
	public RenderApparatus() {
		String reconstructor = "/assets/" + Constants.MOD_ID + "/models/reconstructor.tcn";
		modelReconstructor = new TechneModel(reconstructor, RenderApparatus.class.getResource(reconstructor));
		resourceReconstructor = new ResourceLocation(Constants.MOD_ID + ":textures/models/reconstructor.png");
		String creator = "/assets/" + Constants.MOD_ID + "/models/creator.tcn";
		modelCreator = new TechneModel(creator, RenderApparatus.class.getResource(creator));
		resourceCreator = new ResourceLocation(Constants.MOD_ID + ":textures/models/creator.png");
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
		
		
		if(tileentity instanceof TileEntityReconstructor) {
			renderModelAt(modelReconstructor, resourceReconstructor, d0, d1, d2);
			
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
		} else if(tileentity instanceof TileEntityCreator) {
			renderModelAt(modelCreator, resourceCreator, d0, d1, d2);
			
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
		} else {
			
		}
		
		
		
		
	}
	
	public void renderModelAt(TechneModel model, ResourceLocation texture, double d0, double d1, double d2) {
		GL11.glPushMatrix();
		
		rot++;
		if(rot >= 360) {
			rot = 0;
		}
		
		
		GL11.glTranslatef((float)d0 + 0.5f, (float)d1 + 0.04f, (float)d2 + 0.5f);
		
		GL11.glScalef(0.0625f, 0.0625f, 0.0625f);//1/16th scale, as techne tends to be big..
		GL11.glRotatef(180f, 1.0f, 0, 0);
		
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		
		if(model == modelReconstructor) {
			modelReconstructor.renderPart("Base");
			modelReconstructor.renderPart("Socket");

			
			GL11.glRotatef(rot, 0, 1, 0);

			modelReconstructor.renderPart("Arm1");
			modelReconstructor.renderPart("Arm2");
			modelReconstructor.renderPart("Arm3");
			modelReconstructor.renderPart("Arm4");
		} else {
			modelCreator.renderAll();
		}
		

		GL11.glPopMatrix();
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return Block.blocksList[item.itemID] instanceof BlockApparatus;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		TechneModel model;
		ResourceLocation resource;
		
		BlockApparatus block = (BlockApparatus)Block.blocksList[item.itemID];
		
		switch(block.metaListIndex) {
		case 0:
			model = modelReconstructor;
			resource = resourceReconstructor;
			break;
		case 1:
			model = modelCreator;
			resource = resourceCreator;
			break;
			default: return;
		}
		
		switch(type) {
		case ENTITY: renderModelAt(model, resource, -0.5, -0.5, -0.5); break;
		case EQUIPPED:
		case EQUIPPED_FIRST_PERSON:
			renderModelAt(model, resource, 0, 0, 0); break;
		case INVENTORY: renderModelAt(model, resource, 0.125, 0, 0.125); break;
		case FIRST_PERSON_MAP: renderModelAt(model, resource, -0.5, -0.5, -0.5); break;
		default: renderModelAt(model, resource, -0.5, -0.5, -0.5); break;
		}
	}
}