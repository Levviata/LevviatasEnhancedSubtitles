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
	public static Property propBackgroundRed;
	public static Property propBackgroundGreen;
	public static Property propBackgroundBlue;
	public static Property propBackgroundAlpha;
	public static int backgroundRed;
	public static int backgroundGreen;
	public static int backgroundBlue;
	public static int backgroundAlpha;
	public static final String CATEGORY_NAME_GENERAL = "category_general";
	public static final String CATEGORY_NAME_POSITION = "category_position";
	public static final String CATEGORY_NAME_BACKGROUND = "category_background";
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
		propOverlayPosition = config.get(
				CATEGORY_NAME_POSITION,
				"overlayPosition", OVERLAY_POSITION_DEFAULT_VALUE,
				"The position for the subtitle overlay.\nAcceptable values: BOTTOM_RIGHT, " +
		"BOTTOM_CENTER, BOTTOM_LEFT, CENTER_LEFT, TOP_LEFT, TOP_CENTER, TOP_RIGHT, CENTER_RIGHT");
				propOverlayPosition.setLanguageKey("gui.config.overlayPosition");
		propOverlayPosition.setValidValues(POSITION_CHOICES);

		final int BACKGROUND_RED_MIN_VALUE = 0;
		final int BACKGROUND_RED_MAX_VALUE = 255;
		final int BACKGROUND_RED_DEFAULT_VALUE = 0;
		propBackgroundRed = config.get(
				CATEGORY_NAME_BACKGROUND,
				"backgroundRed", BACKGROUND_RED_DEFAULT_VALUE,
				"The RGB red value (in decimals) for the subtitle's background.",
				BACKGROUND_RED_MIN_VALUE,
				BACKGROUND_RED_MAX_VALUE);
		propBackgroundRed.setLanguageKey("gui.config.backgroundRed");

		final int BACKGROUND_GREEN_MIN_VALUE = 0;
		final int BACKGROUND_GREEN_MAX_VALUE = 255;
		final int BACKGROUND_GREEN_DEFAULT_VALUE = 0;
		propBackgroundGreen = config.get(
				CATEGORY_NAME_BACKGROUND,
				"backgroundGreen", BACKGROUND_GREEN_DEFAULT_VALUE,
				"The RGB green value (in decimals) for the subtitle's background.",
				BACKGROUND_GREEN_MIN_VALUE,
				BACKGROUND_GREEN_MAX_VALUE);
		propBackgroundGreen.setLanguageKey("gui.config.backgroundGreen");

		final int BACKGROUND_BLUE_MIN_VALUE = 0;
		final int BACKGROUND_BLUE_MAX_VALUE = 255;
		final int BACKGROUND_BLUE_DEFAULT_VALUE = 0;
		propBackgroundBlue = config.get(
				CATEGORY_NAME_BACKGROUND,
				"backgroundBlue", BACKGROUND_BLUE_DEFAULT_VALUE,
				"The RGB blue value (in decimals) for the subtitle's background.",
				BACKGROUND_BLUE_MIN_VALUE,
				BACKGROUND_BLUE_MAX_VALUE);
		propBackgroundBlue.setLanguageKey("gui.config.backgroundBlue");

		final int BACKGROUND_ALPHA_MIN_VALUE = 0;
		final int BACKGROUND_ALPHA_MAX_VALUE = 255;
		final int BACKGROUND_ALPHA_DEFAULT_VALUE = 255;
		propBackgroundAlpha = config.get(
				CATEGORY_NAME_BACKGROUND,
				"backgroundAlpha", BACKGROUND_ALPHA_DEFAULT_VALUE,
				"The RGB alpha value (in decimals) for the subtitle's background.",
				BACKGROUND_ALPHA_MIN_VALUE,
				BACKGROUND_ALPHA_MAX_VALUE);
		propBackgroundAlpha.setLanguageKey("gui.config.backgroundAlpha");

		List<String> propOrderPosition = new ArrayList<String>();
		propOrderPosition.add(propOverlayPosition.getName());
		config.setCategoryPropertyOrder(CATEGORY_NAME_POSITION, propOrderPosition);

		List<String> propOrderBackground = new ArrayList<String>();
		propOrderBackground.add(propBackgroundRed.getName());
		propOrderBackground.add(propBackgroundGreen.getName());
		propOrderBackground.add(propBackgroundBlue.getName());
		propOrderBackground.add(propBackgroundAlpha.getName());
		config.setCategoryPropertyOrder(CATEGORY_NAME_BACKGROUND, propOrderBackground);

		if (readFieldsFromConfig)
		{
			backgroundRed = propBackgroundRed.getInt(BACKGROUND_RED_DEFAULT_VALUE);
			if (backgroundRed > BACKGROUND_RED_MAX_VALUE || backgroundRed < BACKGROUND_RED_MIN_VALUE) {
				backgroundRed = BACKGROUND_RED_DEFAULT_VALUE;
			}
			backgroundGreen = propBackgroundGreen.getInt(BACKGROUND_GREEN_DEFAULT_VALUE);
			if (backgroundGreen > BACKGROUND_GREEN_MAX_VALUE || backgroundGreen < BACKGROUND_GREEN_MIN_VALUE) {
				backgroundGreen = BACKGROUND_GREEN_DEFAULT_VALUE;
			}
			backgroundBlue = propBackgroundBlue.getInt(BACKGROUND_BLUE_DEFAULT_VALUE);
			if (backgroundBlue > BACKGROUND_BLUE_MAX_VALUE || backgroundBlue < BACKGROUND_BLUE_MIN_VALUE) {
				backgroundBlue = BACKGROUND_BLUE_DEFAULT_VALUE;
			}
			backgroundAlpha = propBackgroundAlpha.getInt(BACKGROUND_ALPHA_DEFAULT_VALUE);
			if (backgroundAlpha > BACKGROUND_ALPHA_MAX_VALUE || backgroundAlpha < BACKGROUND_ALPHA_MIN_VALUE) {
				backgroundAlpha = BACKGROUND_ALPHA_DEFAULT_VALUE;
			}

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
		propBackgroundRed.set(backgroundRed);
		propBackgroundGreen.set(backgroundGreen);
		propBackgroundBlue.set(backgroundBlue);
		propBackgroundAlpha.set(backgroundAlpha);

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
				if (event.getConfigID().equals(CATEGORY_NAME_BACKGROUND) || event.getConfigID().equals(CATEGORY_NAME_POSITION)/*|| event.getConfigID().equals(CATEGORY_NAME_OTHER)*/)
				{
					syncFromGUI();
				}
			}
		}
	}
}
