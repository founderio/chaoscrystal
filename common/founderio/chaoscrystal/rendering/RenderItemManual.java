package founderio.chaoscrystal.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.ClientProxy;
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
	public static final int maxPage = 11;

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
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
		tessellator.addVertexWithUV(b1 + -20 + f, b2 + 148 - f, -0.009999999776482582D, 0.0D, 1.0D);
		tessellator.addVertexWithUV(b1 + 148 - f, b2 + 148 - f, -0.009999999776482582D, 1.0D, 1.0D);
		tessellator.addVertexWithUV(b1 + 148 - f, b2 + -20 + f, -0.009999999776482582D, 1.0D, 0.0D);
		tessellator.addVertexWithUV(b1 + -20 + f, b2 + -20 + f, -0.009999999776482582D, 0.0D, 0.0D);
		tessellator.draw();

		ItemStack helmet = Minecraft.getMinecraft().thePlayer.inventory.armorInventory[3];

		if(helmet == null || helmet.getItem() != ChaosCrystalMain.itemCrystalGlasses) {
			Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Constants.MOD_ID + ":textures/hud/manual_stubtext.png"));

			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(b1 + -20 + f, b2 + 148 - f, -0.009999999776482582D, 0.0D, 1.0D);
			tessellator.addVertexWithUV(b1 + 148 - f, b2 + 148 - f, -0.009999999776482582D, 1.0D, 1.0D);
			tessellator.addVertexWithUV(b1 + 148 - f, b2 + -20 + f, -0.009999999776482582D, 1.0D, 0.0D);
			tessellator.addVertexWithUV(b1 + -20 + f, b2 + -20 + f, -0.009999999776482582D, 0.0D, 0.0D);
			tessellator.draw();
		} else {
			b1 -= 3;
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
			int strWidth;
			if(page <= 0) {
				page = maxPage;
			}
			if(page > maxPage) {
				page = 1;
			}
			switch(page) {
			default:
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
				fr.drawSplitString("This is a chaos crystal.\n\n" +
						"It can store aspects and transform blocks and items by adding or extracting such aspects.\n\n" +
						"Right click to place it in the world. Hit it to pick it back up.",
						b1 + 16, b2, w - 16, 0xFFFFFF);
				break;
			case 3:
				GL11.glEnable(GL11.GL_ALPHA_TEST);
				ri.renderItemAndEffectIntoGUI(fr, Minecraft.getMinecraft().renderEngine, new ItemStack(ChaosCrystalMain.itemChaosCrystal), b1, b2);
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				fr.drawSplitString("This is a chaos crystal.\n\n" +
						"When placed in the world it will randomly start extracting aspects from nearby blocks.\n\n" +
						"If you hold shift while placing the crystal it will add aspects to nearby blocks instead of extracting them.",
						b1 + 16, b2, w - 16, 0xFFFFFF);
				break;
			case 4:
				GL11.glEnable(GL11.GL_ALPHA_TEST);
				Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Constants.MOD_ID + ":textures/hud/craftinggrid.png"));

				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(b1 + 9 + f, b2 + 59 - f, -0.009999999776482582D, 0.0D, 1.0D);
				tessellator.addVertexWithUV(b1 + 59 - f, b2 + 59 - f, -0.009999999776482582D, 1.0D, 1.0D);
				tessellator.addVertexWithUV(b1 + 59 - f, b2 + 9 + f, -0.009999999776482582D, 1.0D, 0.0D);
				tessellator.addVertexWithUV(b1 + 9 + f, b2 + 9 + f, -0.009999999776482582D, 0.0D, 0.0D);
				tessellator.draw();

				Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Constants.MOD_ID + ":textures/hud/craftingarrow.png"));

				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(b1 + 60 + f, b2 + 46 - f, -0.009999999776482582D, 0.0D, 1.0D);
				tessellator.addVertexWithUV(b1 + 80 - f, b2 + 46 - f, -0.009999999776482582D, 1.0D, 1.0D);
				tessellator.addVertexWithUV(b1 + 80 - f, b2 + 26 + f, -0.009999999776482582D, 1.0D, 0.0D);
				tessellator.addVertexWithUV(b1 + 60 + f, b2 + 26 + f, -0.009999999776482582D, 0.0D, 0.0D);
				tessellator.draw();

				Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Constants.MOD_ID + ":textures/hud/craftingoutput.png"));

				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(b1 + 88 + f, b2 + 46 - f, -0.009999999776482582D, 0.0D, 1.0D);
				tessellator.addVertexWithUV(b1 + 108 - f, b2 + 46 - f, -0.009999999776482582D, 1.0D, 1.0D);
				tessellator.addVertexWithUV(b1 + 108 - f, b2 + 26 + f, -0.009999999776482582D, 1.0D, 0.0D);
				tessellator.addVertexWithUV(b1 + 88 + f, b2 + 26 + f, -0.009999999776482582D, 0.0D, 0.0D);
				tessellator.draw();
				//GL11.glEnable(GL11.GL_DEPTH_TEST);
				// GL11.glPushMatrix();

				//GL11.glScalef(10,  10,  10);
				//GL11.glRotatef(-90f, 0, 1f, 0f);
				//GL11.glRotatef(45f, 0, 1f, 1f);

				// ClientProxy.render.renderModelAt(ClientProxy.render.modelSentry, ClientProxy.render.resourceSentry, b1+2.2f, b1+3, 0.4f);

				Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Constants.MOD_ID + ":textures/hud/block_red.png"));

				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(b1 + 10 + f, b2 + 26 - f, -0.009999999776482582D, 0.0D, 1.0D);
				tessellator.addVertexWithUV(b1 + 26 - f, b2 + 26 - f, -0.009999999776482582D, 1.0D, 1.0D);
				tessellator.addVertexWithUV(b1 + 26 - f, b2 + 10 + f, -0.009999999776482582D, 1.0D, 0.0D);
				tessellator.addVertexWithUV(b1 + 10 + f, b2 + 10 + f, -0.009999999776482582D, 0.0D, 0.0D);
				tessellator.draw();

				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(b1 + 10 + f, b2 + 26+16 - f, -0.009999999776482582D, 0.0D, 1.0D);
				tessellator.addVertexWithUV(b1 + 26 - f, b2 + 26+16 - f, -0.009999999776482582D, 1.0D, 1.0D);
				tessellator.addVertexWithUV(b1 + 26 - f, b2 + 10+16 + f, -0.009999999776482582D, 1.0D, 0.0D);
				tessellator.addVertexWithUV(b1 + 10 + f, b2 + 10+16 + f, -0.009999999776482582D, 0.0D, 0.0D);
				tessellator.draw();

				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(b1 + 10 + f, b2 + 26+16+16 - f, -0.009999999776482582D, 0.0D, 1.0D);
				tessellator.addVertexWithUV(b1 + 26 - f, b2 + 26+16+16 - f, -0.009999999776482582D, 1.0D, 1.0D);
				tessellator.addVertexWithUV(b1 + 26 - f, b2 + 10+16+16 + f, -0.009999999776482582D, 1.0D, 0.0D);
				tessellator.addVertexWithUV(b1 + 10 + f, b2 + 10+16+16 + f, -0.009999999776482582D, 0.0D, 0.0D);
				tessellator.draw();

				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(b1 + 10+16+16 + f, b2 + 26 - f, -0.009999999776482582D, 0.0D, 1.0D);
				tessellator.addVertexWithUV(b1 + 26+16+16 - f, b2 + 26 - f, -0.009999999776482582D, 1.0D, 1.0D);
				tessellator.addVertexWithUV(b1 + 26+16+16 - f, b2 + 10 + f, -0.009999999776482582D, 1.0D, 0.0D);
				tessellator.addVertexWithUV(b1 + 10+16+16 + f, b2 + 10 + f, -0.009999999776482582D, 0.0D, 0.0D);
				tessellator.draw();

				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(b1 + 10+16+16 + f, b2 + 26+16 - f, -0.009999999776482582D, 0.0D, 1.0D);
				tessellator.addVertexWithUV(b1 + 26+16+16 - f, b2 + 26+16 - f, -0.009999999776482582D, 1.0D, 1.0D);
				tessellator.addVertexWithUV(b1 + 26+16+16 - f, b2 + 10+16 + f, -0.009999999776482582D, 1.0D, 0.0D);
				tessellator.addVertexWithUV(b1 + 10+16+16 + f, b2 + 10+16 + f, -0.009999999776482582D, 0.0D, 0.0D);
				tessellator.draw();

				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(b1 + 10+16+16 + f, b2 + 26+16+16 - f, -0.009999999776482582D, 0.0D, 1.0D);
				tessellator.addVertexWithUV(b1 + 26+16+16 - f, b2 + 26+16+16 - f, -0.009999999776482582D, 1.0D, 1.0D);
				tessellator.addVertexWithUV(b1 + 26+16+16 - f, b2 + 10+16+16 + f, -0.009999999776482582D, 1.0D, 0.0D);
				tessellator.addVertexWithUV(b1 + 10+16+16 + f, b2 + 10+16+16 + f, -0.009999999776482582D, 0.0D, 0.0D);
				tessellator.draw();

				// GL11.glPopMatrix();
				// GL11.glDisable(GL11.GL_DEPTH_TEST);
				// GL11.glDisable(GL11.GL_LIGHTING);
				// GL11.glColor4f(1, 1, 1, 1);



				ri.renderItemAndEffectIntoGUI(fr, Minecraft.getMinecraft().renderEngine,
						new ItemStack(ChaosCrystalMain.itemChaosCrystal),
						b1+90, b2+28);

				ri.renderItemAndEffectIntoGUI(fr, Minecraft.getMinecraft().renderEngine,
						new ItemStack(Items.diamond, 1),
						b1+26, b2+10);

				ri.renderItemAndEffectIntoGUI(fr, Minecraft.getMinecraft().renderEngine,
						new ItemStack(Items.ender_pearl, 1),
						b1+26, b2+26);

				ri.renderItemAndEffectIntoGUI(fr, Minecraft.getMinecraft().renderEngine,
						new ItemStack(Items.diamond, 1),
						b1+26, b2+42);

				GL11.glDisable(GL11.GL_ALPHA_TEST);
				break;
			case 5:
				GL11.glEnable(GL11.GL_ALPHA_TEST);
				ri.renderItemAndEffectIntoGUI(fr, Minecraft.getMinecraft().renderEngine, new ItemStack(ChaosCrystalMain.itemFocus, 1, 0), b1, b2);
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				fr.drawSplitString("This is a transfer focus.\n\n" +
						"Place it in the world to balance the aspect levels in two or more chaos crystals and transfer aspects from chaos crystals to any magic apparatus capable of using aspects.",
						b1 + 16, b2, w - 16, 0xFFFFFF);
				break;
			case 6:
				GL11.glEnable(GL11.GL_ALPHA_TEST);
				ri.renderItemAndEffectIntoGUI(fr, Minecraft.getMinecraft().renderEngine, new ItemStack(ChaosCrystalMain.itemFocus, 1, 1), b1, b2);
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				fr.drawSplitString("This is a border focus.\n\n" +
						"Place it in the world to further limit the range of nearby chaos crystals. Any chaos crystal will chose the largest cube possible without stepping over any border.\n" +
						"The border focus will also face the chaos crystal closest to it if it can find any.",
						b1 + 16, b2, w - 16, 0xFFFFFF);
				break;
			case 7:
				GL11.glEnable(GL11.GL_ALPHA_TEST);
				ri.renderItemAndEffectIntoGUI(fr, Minecraft.getMinecraft().renderEngine, new ItemStack(ChaosCrystalMain.itemFocus, 1, 2), b1, b2);
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				fr.drawSplitString("This is a filter focus.\n\n" +
						"Place it close to a chaos crystal to filter the aspects it can extract or add. You can add multiple filter foci to allow more than one aspect type.\n" +
						"You can change the aspect for the filter with shift + mouse wheel when holding it in your hand.",
						b1 + 16, b2, w - 16, 0xFFFFFF);
				break;
			case 8:
				GL11.glEnable(GL11.GL_ALPHA_TEST);
				ri.renderItemAndEffectIntoGUI(fr, Minecraft.getMinecraft().renderEngine, new ItemStack(ChaosCrystalMain.itemCrystalGlasses, 1, 0), b1, b2);
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				fr.drawSplitString("These are the crystal glasses.\n\n" +
						"When wearing them they allow you to see aspects in the world, the content of chaos machines and information about items sitting in the world.",
						b1 + 16, b2, w - 16, 0xFFFFFF);
				break;
			case 9:
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glPushMatrix();

				GL11.glScalef(10,  10,  10);
				GL11.glRotatef(-90f, 1f, 0, 0);
				GL11.glRotatef(45f, 0, 1f, 0);

				ClientProxy.render.renderModelAt(ClientProxy.render.modelReconstructor, ClientProxy.render.resourceReconstructor, b1+2.2f, b1+3, 0.4f);

				GL11.glPopMatrix();
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glColor4f(1, 1, 1, 1);
				fr.drawSplitString("This is the reconstructor.\n\n" +
						"Place it near an active transfer focus. When you put a damaged tool or weapon in the apparatus by right clicking it the reconstructor will slowly repair it using the aspects transferred from nearby chaos crystals.",
						b1 + 16, b2, w - 16, 0xFFFFFF);
				break;
			case 10:
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glPushMatrix();

				GL11.glScalef(10,  10,  10);
				GL11.glRotatef(-90f, 1f, 0, 0);
				GL11.glRotatef(45f, 0, 1f, 0);

				ClientProxy.render.renderModelAt(ClientProxy.render.modelCreator, ClientProxy.render.resourceCreator, b1+2.2f, b1+3, 0.4f);

				GL11.glPopMatrix();
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glColor4f(1, 1, 1, 1);
				fr.drawSplitString("This is the creator.\n\n" +
						"Place it near an active transfer focus. It will use aspects from the crystal network to create blocks and items.",
						b1 + 16, b2, w - 16, 0xFFFFFF);
				break;
			case 11:
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glPushMatrix();

				GL11.glScalef(10,  10,  10);
				GL11.glRotatef(-90f, 1f, 0, 0);
				GL11.glRotatef(45f, 0, 1f, 0);

				ClientProxy.render.renderModelAt(ClientProxy.render.modelSentry, ClientProxy.render.resourceSentry, b1+2.2f, b1+3, 0.4f);

				GL11.glPopMatrix();
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glColor4f(1, 1, 1, 1);
				fr.drawSplitString("This is the sentry.\n\n" +
						"It will shoot any living beings aside from the one that placed it.\n" +
						"Currently it does only need arrows. You can put them in the sentry by right clicking them or piping them in.\n\n" +
						"It does quite a good job on bats!",
						b1 + 16, b2, w - 16, 0xFFFFFF);
				break;
			}

			strWidth = fr.getStringWidth("Page " + page + "/" + maxPage);
			fr.drawString("Page " + page + "/" + maxPage, b1 + w - strWidth, b2 + w - fr.FONT_HEIGHT, 0xFFFFFF);

		}


		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_BLEND);



		GL11.glPushMatrix();
		GL11.glTranslatef(0.0F, 0.0F, -0.04F);
		GL11.glScalef(1.0F, 1.0F, 1.0F);
		GL11.glPopMatrix();
	}

}
