package founderio.chaoscrystal;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityAuraFX;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import founderio.chaoscrystal.blocks.TileEntityApparatus;
import founderio.chaoscrystal.entities.DegradationParticles;
import founderio.chaoscrystal.entities.EntityChaosCrystal;
import founderio.chaoscrystal.entities.EntityFocusBorder;
import founderio.chaoscrystal.entities.EntityFocusFilter;
import founderio.chaoscrystal.entities.EntityFocusFilterTarget;
import founderio.chaoscrystal.entities.EntityFocusTransfer;
import founderio.chaoscrystal.rendering.OverlayAspectSelector;
import founderio.chaoscrystal.rendering.RenderApparatus;
import founderio.chaoscrystal.rendering.RenderChaosCrystal;
import founderio.chaoscrystal.rendering.RenderFocus;
import founderio.chaoscrystal.rendering.RenderItemManual;

public class ClientProxy extends CommonProxy {

	public static RenderApparatus render;

	@Override
	public void registerRenderStuff() {
		RenderFocus rf = new RenderFocus();
		RenderingRegistry.registerEntityRenderingHandler(EntityChaosCrystal.class, new RenderChaosCrystal());
		RenderingRegistry.registerEntityRenderingHandler(EntityFocusTransfer.class, rf);
		RenderingRegistry.registerEntityRenderingHandler(EntityFocusBorder.class, rf);
		RenderingRegistry.registerEntityRenderingHandler(EntityFocusFilter.class, rf);
		RenderingRegistry.registerEntityRenderingHandler(EntityFocusFilterTarget.class, rf);

		render = new RenderApparatus();

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityApparatus.class, render);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ChaosCrystalMain.blockReconstructor), render);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ChaosCrystalMain.blockCreator), render);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ChaosCrystalMain.blockSentry), render);
		MinecraftForgeClient.registerItemRenderer(ChaosCrystalMain.itemManual, new RenderItemManual());


		MinecraftForge.EVENT_BUS.register(new OverlayAspectSelector());
		FMLCommonHandler.instance().bus().register(render);

	}

	@Override
	public void spawnParticleEntity(World world, int type, double posX,
			double posY, double posZ, double offX, double offY, double offZ,
			float variation) {
		if(world != null) {
			float varHalf = variation/2;
			if(type == 2) {
				for(int i = 0; i < 5 + ChaosCrystalMain.rand.nextInt(20); i++) {
					Minecraft.getMinecraft().effectRenderer.addEffect(
							new EntityAuraFX(
									world,
									posX + ChaosCrystalMain.rand.nextDouble()*variation-varHalf,
									posY + ChaosCrystalMain.rand.nextDouble()*variation-varHalf,
									posZ + ChaosCrystalMain.rand.nextDouble()*variation-varHalf, 1, 1, 1));
				}
			} else {
				for(int i = 0; i < 5; i++) {
					Minecraft.getMinecraft().effectRenderer.addEffect(
							new DegradationParticles(
									world,
									posX + ChaosCrystalMain.rand.nextDouble()*variation-varHalf,
									posY + ChaosCrystalMain.rand.nextDouble()*variation-varHalf,
									posZ + ChaosCrystalMain.rand.nextDouble()*variation-varHalf,
									offX + ChaosCrystalMain.rand.nextDouble()*variation-varHalf,
									offY + ChaosCrystalMain.rand.nextDouble()*variation-varHalf,
									offZ + ChaosCrystalMain.rand.nextDouble()*variation-varHalf,
									type));
				}
			}
		}
	}
}
