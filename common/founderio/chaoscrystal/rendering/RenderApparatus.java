package founderio.chaoscrystal.rendering;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.techne.TechneModel;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import founderio.chaoscrystal.Constants;
import founderio.chaoscrystal.blocks.BlockApparatus;
import founderio.chaoscrystal.blocks.TileEntityApparatus;
import founderio.chaoscrystal.blocks.TileEntityCreator;
import founderio.chaoscrystal.blocks.TileEntityReconstructor;
import founderio.chaoscrystal.blocks.TileEntitySentry;

public class RenderApparatus extends TileEntitySpecialRenderer implements
		IItemRenderer {
	public final TechneModel modelReconstructor;
	public final ResourceLocation resourceReconstructor;
	public final TechneModel modelCreator;
	public final ResourceLocation resourceCreator;
	public final ResourceLocation resourceCreatorOff;
	public final TechneModel modelSentry;
	public final ResourceLocation resourceSentry;
	public final ResourceLocation resourceSentryOff;
	private RenderItem ri;
	private EntityItem ei;

	private float rot = 0;
	
	public RenderApparatus() {
		modelReconstructor = new TechneModel(new ResourceLocation(Constants.MOD_ID + ":models/reconstructor.tcn"));
		resourceReconstructor = new ResourceLocation(Constants.MOD_ID + ":textures/models/reconstructor.png");
		modelCreator = new TechneModel(new ResourceLocation(Constants.MOD_ID + ":models/creator.tcn"));
		resourceCreator = new ResourceLocation(Constants.MOD_ID + ":textures/models/creator.png");
		resourceCreatorOff = new ResourceLocation(Constants.MOD_ID + ":textures/models/creator_off.png");
		modelSentry = new TechneModel(new ResourceLocation(Constants.MOD_ID + ":models/sentry.tcn"));
		resourceSentry = new ResourceLocation(Constants.MOD_ID + ":textures/models/sentry.png");
		resourceSentryOff = new ResourceLocation(Constants.MOD_ID + ":textures/models/sentry_off.png");
		ri = new RenderItem() {
			@Override
			public boolean shouldBob() {
				return false;
			}
		};
		ei = new EntityItem(null, 0, 0, 0, new ItemStack(Items.diamond_pickaxe));
		ri.setRenderManager(RenderManager.instance);
	}

	@SubscribeEvent
	public void tick(ClientTickEvent event) {
		if(event.phase == Phase.END) {
			if (Minecraft.getMinecraft().theWorld != null) {
				rot = (Minecraft.getMinecraft().theWorld.getWorldTime() % 360f) * 4f;
			}
		}
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d0, double d1,
			double d2, float f) {

		if (tileentity instanceof TileEntityReconstructor) {
			renderModelAt(modelReconstructor, resourceReconstructor, d0, d1, d2);

			ItemStack is = ((TileEntityApparatus) tileentity).getStackInSlot(0);
			if (is == null) {
				ei.setEntityItemStack(new ItemStack(Blocks.air, 0, 0));
			} else {
				ei.setEntityItemStack(is);

				GL11.glPushMatrix();
				GL11.glTranslatef((float) d0 + 0.5f, (float) d1 + 0.15f,
						(float) d2 + 0.5f);
				GL11.glRotatef(rot, 0, 1, 0);

				ei.setPosition(d0, d1 + 1, d2);

				RenderItem.renderInFrame = true;
				ri.doRender(ei, 0, .5f, 0, 0, 0);

				GL11.glPopMatrix();
			}
		} else if (tileentity instanceof TileEntityCreator) {
			renderModelAt(modelCreator,
					((TileEntityCreator) tileentity).isActive ? resourceCreator
							: resourceCreatorOff, d0, d1, d2);

			ItemStack is = ((TileEntityApparatus) tileentity).getStackInSlot(0);
			if (is == null) {
				ei.setEntityItemStack(new ItemStack(Blocks.air, 0, 0));
			} else {
				ei.setEntityItemStack(is);

				GL11.glPushMatrix();
				GL11.glTranslatef((float) d0 + 0.5f, (float) d1 + 0.15f,
						(float) d2 + 0.5f);
				GL11.glRotatef(rot, 0, 1, 0);

				ei.setPosition(d0, d1 + 1, d2);

				RenderItem.renderInFrame = true;
				ri.doRender(ei, 0, .5f, 0, 0, 0);

				GL11.glPopMatrix();
			}
		} else if (tileentity instanceof TileEntitySentry) {
			renderModelAt(modelSentry,
					((TileEntitySentry) tileentity).isActive ? resourceSentry
							: resourceSentryOff, d0, d1, d2);

			GL11.glPushMatrix();
			//
			// GL11.glTranslatef(0, (float)Math.sin(Math.toRadians(rot))*0.1f,
			// 0);
			for (int i = 0; i < 4; i++) {
				ItemStack is = ((TileEntityApparatus) tileentity)
						.getStackInSlot(i);
				if (is == null) {
					ei.setEntityItemStack(new ItemStack(Blocks.air, 0, 0));
				} else {
					ei.setEntityItemStack(is);
					float offX = (i == 0 || i == 1) ? 0.325f : 0.625f;
					float offZ = (i == 0 || i == 2) ? 0.325f : 0.625f;
					GL11.glPushMatrix();
					GL11.glTranslatef((float) d0 + offX, (float) d1 + 0.15f,
							(float) d2 + offZ);
					GL11.glRotatef(rot, 0, 1, 0);
					GL11.glScalef(1 / 3f, 1 / 3f, 1 / 3f);

					ei.setPosition(d0, d1 + 1, d2);

					RenderItem.renderInFrame = true;
					ri.doRender(ei, 0, .5f, 0, 0, 0);

					GL11.glPopMatrix();
				}
			}
			GL11.glPopMatrix();

		} else {

		}
		if(tileentity instanceof TileEntityApparatus) {
			TileEntityApparatus tea = ((TileEntityApparatus)tileentity);
			for(int i = 0; i < tea.getSizeModules(); i++) {
				ItemStack is = tea.getModuleItemStack(i);
				if(is != null) {
					ei.setEntityItemStack(is);
					float offX = 0.15f;
					float offZ = 0.125f + i * 0.0625f;
					GL11.glPushMatrix();
					GL11.glTranslatef((float) d0 + offX, (float) d1 - 0.175f,
							(float) d2 + offZ);
					//GL11.glRotatef(rot, 0, 1, 0);
					GL11.glScalef(1 / 2f, 1 / 2f, 1 / 2f);

					ei.setPosition(d0, d1 + 1, d2);

					RenderItem.renderInFrame = true;
					ri.doRender(ei, 0, .5f, 0, 0, 0);

					GL11.glPopMatrix();
				}
			}
		}

	}

	public void renderModelAt(TechneModel model, ResourceLocation texture,
			double d0, double d1, double d2) {
		GL11.glPushMatrix();

		// rot = (Minecraft.getMinecraft().theWorld.getWorldTime()) % 360f;

		GL11.glTranslatef((float) d0 + 0.5f, (float) d1 + 0.04f,
				(float) d2 + 0.5f);

		GL11.glScalef(0.0625f, 0.0625f, 0.0625f);// 1/16th scale, as techne
													// tends to be big..
		GL11.glRotatef(180f, 1.0f, 0, 0);

		Minecraft.getMinecraft().renderEngine.bindTexture(texture);

		if (model == modelReconstructor) {
			modelReconstructor.renderPart("Base");
			modelReconstructor.renderPart("Socket");

			GL11.glRotatef(rot, 0, 1, 0);

			modelReconstructor.renderPart("Arm1");
			modelReconstructor.renderPart("Arm2");
			modelReconstructor.renderPart("Arm3");
			modelReconstructor.renderPart("Arm4");
		} else if (model == modelCreator) {
			modelCreator.renderPart("Base");
			modelCreator.renderPart("Crystal");

			GL11.glPushMatrix();
			GL11.glTranslatef(0,
					-6f + (float) Math.sin(Math.toRadians(rot - 90) * 2f) * 5f,
					0);

			modelCreator.renderPart("Ring1");
			modelCreator.renderPart("Ring2");
			modelCreator.renderPart("Ring3");
			modelCreator.renderPart("Ring4");

			GL11.glPopMatrix();
		} else if (model == modelSentry) {

			modelSentry.renderPart("Base");

			modelSentry.renderPart("Crystal");
			GL11.glPushMatrix();
			GL11.glTranslatef(0,
					(float) Math.sin(Math.toRadians(rot - 90) * 1f), 0);

			modelSentry.renderPart("Staff1");
			modelSentry.renderPart("Staff2");
			modelSentry.renderPart("Staff3");
			modelSentry.renderPart("Staff4");

			modelSentry.renderPart("Corner1");
			modelSentry.renderPart("Corner2");
			modelSentry.renderPart("Corner3");
			modelSentry.renderPart("Corner4");

			GL11.glPopMatrix();

			GL11.glPushMatrix();
			GL11.glTranslatef(0, (float) Math.sin(Math.toRadians(rot) * 1f), 0);

			GL11.glRotatef(rot, 0, 1, 0);

			modelSentry.renderPart("Crystal2");
			GL11.glPopMatrix();
		} else {
			model.renderAll();
		}

		GL11.glPopMatrix();
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return Block.getBlockFromItem(item.getItem()) instanceof BlockApparatus;
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

		BlockApparatus block = (BlockApparatus) Block.getBlockFromItem(item.getItem());

		switch (block.metaListIndex) {
		case 0:
			model = modelReconstructor;
			resource = resourceReconstructor;
			break;
		case 1:
			model = modelCreator;
			resource = resourceCreator;
			break;
		case 2:
			model = modelSentry;
			resource = resourceSentry;
			break;
		default:
			return;
		}

		switch (type) {
		case EQUIPPED:
		case EQUIPPED_FIRST_PERSON:
			renderModelAt(model, resource, 0, 0, 0);
			break;
		case INVENTORY:
			renderModelAt(model, resource, 0.125, 0, 0.125);
			break;
		case ENTITY:
		case FIRST_PERSON_MAP:
		default:
			renderModelAt(model, resource, -0.5, -0.5, -0.5);
			break;
		}
	}

}
