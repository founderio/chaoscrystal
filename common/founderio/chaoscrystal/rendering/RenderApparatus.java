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
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.obj.WavefrontObject;
import net.minecraftforge.client.model.techne.TechneModel;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import founderio.chaoscrystal.Constants;
import founderio.chaoscrystal.blocks.BlockApparatus;
import founderio.chaoscrystal.blocks.TileEntityApparatus;
import founderio.chaoscrystal.blocks.TileEntityInfuser;
import founderio.chaoscrystal.blocks.TileEntityReconstructor;
import founderio.chaoscrystal.blocks.TileEntitySentry;
import founderio.chaoscrystal.blocks.TileEntityShard;
import founderio.chaoscrystal.blocks.TileEntityTicker;

public class RenderApparatus extends TileEntitySpecialRenderer implements IItemRenderer {
	public final TechneModel modelReconstructor;
	public final ResourceLocation texReconstructor;
	public final TechneModel modelCreator;
	public final ResourceLocation texCreator;
	public final ResourceLocation texCreatorOff;
	public final TechneModel modelSentry;
	public final ResourceLocation texSentry;
	public final ResourceLocation texSentryOff;
	
	public final WavefrontObject modelShard;
	public final ResourceLocation[] texShard;
	
	private RenderItem ri;
	private EntityItem ei;

	private float rot = 0;

	public RenderApparatus() {
		modelReconstructor = new TechneModel(new ResourceLocation(Constants.MOD_ID + ":models/reconstructor.tcn"));
		texReconstructor = new ResourceLocation(Constants.MOD_ID + ":textures/models/reconstructor.png");
		modelCreator = new TechneModel(new ResourceLocation(Constants.MOD_ID + ":models/creator.tcn"));
		texCreator = new ResourceLocation(Constants.MOD_ID + ":textures/models/creator.png");
		texCreatorOff = new ResourceLocation(Constants.MOD_ID + ":textures/models/creator_off.png");
		modelSentry = new TechneModel(new ResourceLocation(Constants.MOD_ID + ":models/sentry.tcn"));
		texSentry = new ResourceLocation(Constants.MOD_ID + ":textures/models/sentry.png");
		texSentryOff = new ResourceLocation(Constants.MOD_ID + ":textures/models/sentry_off.png");
		
		modelShard = new WavefrontObject(new ResourceLocation(Constants.MOD_ID + ":models/shard.obj"));
		texShard = new ResourceLocation[4];
		texShard[0] = new ResourceLocation(Constants.MOD_ID + ":textures/blocks/crystalline_energy.png");
		texShard[1] = new ResourceLocation(Constants.MOD_ID + ":textures/blocks/crystalline_chaos.png");
		texShard[2] = new ResourceLocation(Constants.MOD_ID + ":textures/blocks/crystalline_light.png");
		texShard[3] = new ResourceLocation(Constants.MOD_ID + ":textures/blocks/crystal_clear.png");
		
		ri = new RenderItem() {
			@Override
			public boolean shouldBob() {
				return false;
			}
		};
		ei = new EntityItem(null, 0, 0, 0, new ItemStack(Items.diamond_pickaxe));
		ri.setRenderManager(RenderManager.instance);
	}

	/**
	 * Handler for game ticks (rotating & floating model elements)
	 * @param event
	 */
	@SubscribeEvent
	public void tick(ClientTickEvent event) {
		if(event.phase == Phase.END) {
			if (Minecraft.getMinecraft().theWorld != null) {
				rot = Minecraft.getMinecraft().theWorld.getWorldTime() % 360f * 4f;
			}
		}
	}

	/*
	 * BEGIN TileEntitySpecialRenderer
	 */
	
	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {

		if (tileentity instanceof TileEntityReconstructor) {
			renderModelAt(modelReconstructor, texReconstructor, x, y, z);

			ItemStack is = ((TileEntityApparatus) tileentity).getStackInSlot(0);
			if (is == null) {
				ei.setEntityItemStack(new ItemStack(Blocks.air, 0, 0));
			} else {
				ei.setEntityItemStack(is);

				GL11.glPushMatrix();
				GL11.glTranslatef((float) x + 0.5f, (float) y + 0.15f,
						(float) z + 0.5f);
				GL11.glRotatef(rot, 0, 1, 0);

				ei.setPosition(x, y + 1, z);

				RenderItem.renderInFrame = true;
				ri.doRender(ei, 0, .5f, 0, 0, 0);

				GL11.glPopMatrix();
			}
		} else if (tileentity instanceof TileEntityInfuser) {
			renderModelAt(modelCreator,
					((TileEntityInfuser) tileentity).isActive ? texCreator
							: texCreatorOff, x, y, z);

			ItemStack is = ((TileEntityApparatus) tileentity).getStackInSlot(0);
			if (is == null) {
				ei.setEntityItemStack(new ItemStack(Blocks.air, 0, 0));
			} else {
				ei.setEntityItemStack(is);

				GL11.glPushMatrix();
				GL11.glTranslatef((float) x + 0.5f, (float) y + 0.15f,
						(float) z + 0.5f);
				GL11.glRotatef(rot, 0, 1, 0);

				ei.setPosition(x, y + 1, z);

				RenderItem.renderInFrame = true;
				ri.doRender(ei, 0, .5f, 0, 0, 0);

				GL11.glPopMatrix();
			}
		} else if (tileentity instanceof TileEntitySentry) {
			renderModelAt(modelSentry,
					((TileEntitySentry) tileentity).isActive ? texSentry
							: texSentryOff, x, y, z);

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
					float offX = i == 0 || i == 1 ? 0.325f : 0.625f;
					float offZ = i == 0 || i == 2 ? 0.325f : 0.625f;
					GL11.glPushMatrix();
					GL11.glTranslatef((float) x + offX, (float) y + 0.15f,
							(float) z + offZ);
					GL11.glRotatef(rot, 0, 1, 0);
					GL11.glScalef(1 / 3f, 1 / 3f, 1 / 3f);

					ei.setPosition(x, y + 1, z);

					RenderItem.renderInFrame = true;
					ri.doRender(ei, 0, .5f, 0, 0, 0);

					GL11.glPopMatrix();
				}
			}
			GL11.glPopMatrix();

		} else if (tileentity instanceof TileEntityShard) {
			int meta = MathHelper.clamp_int(tileentity.blockMetadata, 0, texShard.length * 2 - 1);
			if(meta >= texShard.length) {
				meta -= texShard.length;
			}
			renderModelAt(modelShard, texShard[meta], x, y, z);

		} else if (tileentity instanceof TileEntityTicker) {
			renderModelAt(modelShard, texShard[0], x, y, z);

		} else {

		}
		if(tileentity instanceof TileEntityApparatus) {
			TileEntityApparatus tea = (TileEntityApparatus)tileentity;
			for(int i = 0; i < tea.getSizeModules(); i++) {
				ItemStack is = tea.getModuleItemStack(i);
				if(is != null) {
					ei.setEntityItemStack(is);
					float offX = 0.15f;
					float offZ = 0.125f + i * 0.0625f;
					GL11.glPushMatrix();
					GL11.glTranslatef((float) x + offX, (float) y - 0.175f,
							(float) z + offZ);
					//GL11.glRotatef(rot, 0, 1, 0);
					GL11.glScalef(1 / 2f, 1 / 2f, 1 / 2f);

					ei.setPosition(x, y + 1, z);

					RenderItem.renderInFrame = true;
					ri.doRender(ei, 0, .5f, 0, 0, 0);

					GL11.glPopMatrix();
				}
			}
		}

	}

	/*
	 * BEGIN IItemRenderer
	 */
	
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
		IModelCustom model;
		ResourceLocation tex;

		BlockApparatus block = (BlockApparatus) Block.getBlockFromItem(item.getItem());

		switch (block.metaListIndex) {
		case 0:
			model = modelReconstructor;
			tex = texReconstructor;
			break;
		case 1:
			model = modelCreator;
			tex = texCreator;
			break;
		case 2:
			model = modelSentry;
			tex = texSentry;
			break;
		case 3:
			model = modelShard;
			tex = texShard[0];
			break;
		case 4:
			model = modelShard;
			int meta = MathHelper.clamp_int(item.getItemDamage(), 0, texShard.length - 1);
			tex = texShard[meta];
		default:
			return;
		}

		switch (type) {
		case EQUIPPED:
		case EQUIPPED_FIRST_PERSON:
			renderModelAt(model, tex, 0, 0, 0);
			break;
		case INVENTORY:
			renderModelAt(model, tex, 0.125, 0, 0.125);
			break;
		case ENTITY:
		case FIRST_PERSON_MAP:
		default:
			renderModelAt(model, tex, -0.5, -0.5, -0.5);
			break;
		}
	}

	/*
	 * BEGIN Helper Methods
	 */

	public void renderModelAt(IModelCustom model, ResourceLocation texture,
			double d0, double d1, double d2) {
		GL11.glPushMatrix();

		GL11.glTranslatef((float) d0 + 0.5f, (float) d1 + 0.04f,
				(float) d2 + 0.5f);
		
		// Only need to adjust techne models. OBJs are fine
		if(model instanceof TechneModel) {
			// 1/16th scale, as techne tends to be big..
			GL11.glScalef(0.0625f, 0.0625f, 0.0625f);
			// Flip upside down
			GL11.glRotatef(180f, 1.0f, 0, 0);
		}

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		if(texture != null) {
			Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		}

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
//		} else if(model instanceof WavefrontObject) {
//			((WavefrontObject)model).tessellateAll(Tessellator.instance);
		} else {
			model.renderAll();
		}

		GL11.glPopMatrix();
	}

}
