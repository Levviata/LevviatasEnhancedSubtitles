package levviatasenhancedsubtitles.config;

import levviatasenhancedsubtitles.LES;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
public class LESConfiguration {
	public static String overlayPosition;
	public static Property propOverlayPosition;
	public static final String CATEGORY_NAME_GENERAL = "category_general";
	//public static final String CATEGORY_NAME_OTHER = "category_other";
	public static void preInit()
	{

		File configFile = new File(Loader.instance().getConfigDir(), "LevviatasEnhancedSubtitles.cfg");

		config = new Configuration(configFile);

		syncFromFile();
	}
	public static void clientPreInit() {
		MinecraftForge.EVENT_BUS.register(new ConfigEventHandler());
	}
	public static Configuration getConfig() {
		return config;
	}
	public static void syncFromFile() {
		syncConfig(true, true);
	}
	public static void syncFromGUI() {
		syncConfig(false, true);
	}
	public static void syncFromFields() {
		syncConfig(false, false);
	}

	private static void syncConfig(boolean loadConfigFromFile, boolean readFieldsFromConfig)
	{
		if (loadConfigFromFile) {
			config.load();
		}

		//final OverlayPosition OVERLAY_POSITION_DEFAULT_VALUE = OverlayPosition.valueOf(BOTTOM_RIGHT.name());
		/*final String[] POSITION_CHOICES = {
				BOTTOM_RIGHT,
				"BOTTOM_CENTER",
				"BOTTOM_LEFT",
				"CENTER_LEFT",
				"TOP_LEFT",
				"TOP_CENTER",
				"TOP_RIGHT",
				"CENTER_RIGHT"
				};*/
		final String OVERLAY_POSITION_DEFAULT_VALUE = "BOTTOM_RIGHT";
		final String[] POSITION_CHOICES = {
				"BOTTOM_RIGHT",
				"BOTTOM_LEFT",
				"BOTTOM_CENTER",
				"CENTER_LEFT",
				"TOP_LEFT",
				"TOP_CENTER",
				"TOP_RIGHT",
				"CENTER_RIGHT"
		};
		propOverlayPosition = config.get(CATEGORY_NAME_GENERAL,
				"overlayPosition", OVERLAY_POSITION_DEFAULT_VALUE,
				"The position for the subtitle overlay.\nAcceptable values: BOTTOM_RIGHT, " +
		"BOTTOM_CENTER, BOTTOM_LEFT, CENTER_LEFT, TOP_LEFT, TOP_CENTER, TOP_RIGHT, CENTER_RIGHT");
				propOverlayPosition.setLanguageKey("gui.config.overlayPosition");
		propOverlayPosition.setValidValues(POSITION_CHOICES);

		List<String> propOrderGeneral = new ArrayList<String>();
		propOrderGeneral.add(propOverlayPosition.getName());
		config.setCategoryPropertyOrder(CATEGORY_NAME_GENERAL, propOrderGeneral);

		//List<String> propOrderOther = new ArrayList<String>();

		//config.setCategoryPropertyOrder(CATEGORY_NAME_OTHER, propOrderOther);


		if (readFieldsFromConfig)
		{
			// If overlayPosition can't get any config it just simply defaults to "BOTTOM_RIGHT"
			overlayPosition = propOverlayPosition.getString();
			boolean overlayMatched = false;
			for (String entry : POSITION_CHOICES) {
				if (entry.equals(overlayPosition)) {
					overlayMatched = true;
					break;
				}
			}
			if (!overlayMatched) {
				overlayPosition = OVERLAY_POSITION_DEFAULT_VALUE;
			}
		}

		propOverlayPosition.set(overlayPosition);

		if (config.hasChanged()) {
			config.save();
		}
	}

	private static Configuration config = null;

	public static class ConfigEventHandler
	{
		@SubscribeEvent(priority = EventPriority.NORMAL)
		public void onEvent(ConfigChangedEvent.OnConfigChangedEvent event)
		{
			if (LES.MODID.equals(event.getModID()) && !event.isWorldRunning())
			{
				if (event.getConfigID().equals(CATEGORY_NAME_GENERAL) /*|| event.getConfigID().equals(CATEGORY_NAME_OTHER)*/)
				{
					syncFromGUI();
				}
			}
		}
	}
}
