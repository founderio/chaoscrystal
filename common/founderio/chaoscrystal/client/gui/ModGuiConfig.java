package founderio.chaoscrystal.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import founderio.chaoscrystal.Config;
import founderio.chaoscrystal.Constants;

public class ModGuiConfig extends GuiConfig {
	public ModGuiConfig(GuiScreen guiScreen)
	{
		
		super(guiScreen,
				getElements(),
				Constants.MOD_ID,
				false,
				false,
				GuiConfig.getAbridgedConfigPath(Config.config.toString()));

	}
	
	@SuppressWarnings("rawtypes")
	private static List<IConfigElement> getElements() {
		List<IConfigElement> elements = new ArrayList<IConfigElement>();
		ConfigElement<Object> cfgEl = new ConfigElement<Object>(Config.config.getCategory(Configuration.CATEGORY_GENERAL));
		ConfigElement<Object> cfgElWorldgen = new ConfigElement<Object>(Config.config.getCategory("worldgen"));
		ConfigElement<Object> cfgElPhysics = new ConfigElement<Object>(Config.config.getCategory("physics"));
		elements.addAll(cfgEl.getChildElements());
		elements.addAll(cfgElWorldgen.getChildElements());
		elements.addAll(cfgElPhysics.getChildElements());
		return elements;
	}
}
