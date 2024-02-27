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
	public static Property propFontRed;
	public static Property propFontGreen;
	public static Property propFontBlue;
	public static Property propFontAlpha;
	public static Property propBackgroundBlackOn;
	public static int backgroundRed;
	public static int backgroundGreen;
	public static int backgroundBlue;
	public static int backgroundAlpha;
	public static int fontRed;
	public static int fontGreen;
	public static int fontBlue;
	public static final String CATEGORY_NAME_POSITION = "category_position";
	public static final String CATEGORY_NAME_BACKGROUND = "category_background";
	public static final String CATEGORY_NAME_FONT = "category_font";
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
		propOverlayPosition.setLanguageKey("gui.les_configuration.overlayPosition");
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
		propBackgroundRed.setLanguageKey("gui.les_configuration.backgroundRed");

		final int BACKGROUND_GREEN_MIN_VALUE = 0;
		final int BACKGROUND_GREEN_MAX_VALUE = 255;
		final int BACKGROUND_GREEN_DEFAULT_VALUE = 0;
		propBackgroundGreen = config.get(
				CATEGORY_NAME_BACKGROUND,
				"backgroundGreen", BACKGROUND_GREEN_DEFAULT_VALUE,
				"The RGB green value (in decimals) for the subtitle's background.",
				BACKGROUND_GREEN_MIN_VALUE,
				BACKGROUND_GREEN_MAX_VALUE);
		propBackgroundGreen.setLanguageKey("gui.les_configuration.backgroundGreen");

		final int BACKGROUND_BLUE_MIN_VALUE = 0;
		final int BACKGROUND_BLUE_MAX_VALUE = 255;
		final int BACKGROUND_BLUE_DEFAULT_VALUE = 0;
		propBackgroundBlue = config.get(
				CATEGORY_NAME_BACKGROUND,
				"backgroundBlue", BACKGROUND_BLUE_DEFAULT_VALUE,
				"The RGB blue value (in decimals) for the subtitle's background.",
				BACKGROUND_BLUE_MIN_VALUE,
				BACKGROUND_BLUE_MAX_VALUE);
		propBackgroundBlue.setLanguageKey("gui.les_configuration.backgroundBlue");

		// Font

		final int FONT_RED_MIN_VALUE = 0;
		final int FONT_RED_MAX_VALUE = 255;
		final int FONT_RED_DEFAULT_VALUE = 255;
		propFontRed = config.get(
				CATEGORY_NAME_FONT,
				"fontRed", FONT_RED_DEFAULT_VALUE,
				"The RGB red value (in decimals) for the subtitle's background.",
				FONT_RED_MIN_VALUE,
				FONT_RED_MAX_VALUE);
		propFontRed.setLanguageKey("gui.les_configuration.fontRed");

		final int FONT_GREEN_MIN_VALUE = 0;
		final int FONT_GREEN_MAX_VALUE = 255;
		final int FONT_GREEN_DEFAULT_VALUE = 255;
		propFontGreen = config.get(
				CATEGORY_NAME_FONT,
				"fontGreen", FONT_GREEN_DEFAULT_VALUE,
				"The RGB green value (in decimals) for the subtitle's background.",
				FONT_GREEN_MIN_VALUE,
				FONT_GREEN_MAX_VALUE);
		propFontGreen.setLanguageKey("gui.les_configuration.fontGreen");

		final int FONT_BLUE_MIN_VALUE = 0;
		final int FONT_BLUE_MAX_VALUE = 255;
		final int FONT_BLUE_DEFAULT_VALUE = 255;
		propFontBlue = config.get(
				CATEGORY_NAME_FONT,
				"fontBlue", FONT_BLUE_DEFAULT_VALUE,
				"The RGB blue value (in decimals) for the subtitle's background.",
				FONT_BLUE_MIN_VALUE,
				FONT_BLUE_MAX_VALUE);
		propFontBlue.setLanguageKey("gui.les_configuration.fontBlue");

		/*final int BACKGROUND_ALPHA_MIN_VALUE = 0;
		final int BACKGROUND_ALPHA_MAX_VALUE = 255;
		final int BACKGROUND_ALPHA_DEFAULT_VALUE = 255;
		propBackgroundAlpha = config.get(
				CATEGORY_NAME_BACKGROUND,
				"backgroundAlpha", BACKGROUND_ALPHA_DEFAULT_VALUE,
				"The RGB alpha value (in decimals) for the subtitle's background.",
				BACKGROUND_ALPHA_MIN_VALUE,
				BACKGROUND_ALPHA_MAX_VALUE);
		propBackgroundAlpha.setLanguageKey("gui.les_configuration.backgroundAlpha");*/

		/*final boolean BACKGROUND_BLACK_ON_DEFAULT_VALUE = false;
		propBackgroundBlackOn = config.get(CATEGORY_NAME_GENERAL, "backgroundBlackOn", BACKGROUND_BLACK_ON_DEFAULT_VALUE);
		propBackgroundBlackOn.setComment("§c(!) §cOVERRIDES §cALL §cCOLOR §cSETTINGS §c(!) and sets the subtitle's background to all white, cannot be turned while the setting 'All black' is turned on.");
		propBackgroundBlackOn.setLanguageKey("gui.les_configuration.backgroundBlackOn");

		final boolean BACKGROUND_WHITE_ON_DEFAULT_VALUE = false;
		propBackgroundBlackOn = config.get(CATEGORY_NAME_GENERAL, "backgroundBlackOn", BACKGROUND_WHITE_ON_DEFAULT_VALUE);
		propBackgroundBlackOn.setComment("§c(!) §cOVERRIDES §cALL §cCOLOR §cSETTINGS §c(!) and sets the subtitle's background to all white, cannot be turned while the setting 'All black' is turned on.");
		propBackgroundBlackOn.setLanguageKey("gui.les_configuration.backgroundWhiteOn");*/


		List<String> propOrderPosition = new ArrayList<String>();
		propOrderPosition.add(propOverlayPosition.getName());
		config.setCategoryPropertyOrder(CATEGORY_NAME_POSITION, propOrderPosition);

		List<String> propOrderBackground = new ArrayList<String>();
		propOrderBackground.add(propBackgroundRed.getName());
		propOrderBackground.add(propBackgroundGreen.getName());
		propOrderBackground.add(propBackgroundBlue.getName());
		//propOrderBackground.add(propBackgroundAlpha.getName());
		config.setCategoryPropertyOrder(CATEGORY_NAME_BACKGROUND, propOrderBackground);

		List<String> propOrderFont = new ArrayList<String>();
		propOrderFont.add(propFontRed.getName());
		propOrderFont.add(propFontGreen.getName());
		propOrderFont.add(propFontBlue.getName());
		config.setCategoryPropertyOrder(CATEGORY_NAME_FONT, propOrderFont);

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
			fontRed = propFontRed.getInt(FONT_RED_DEFAULT_VALUE);
			if (fontRed > FONT_RED_MAX_VALUE || fontRed < FONT_RED_MIN_VALUE) {
				fontRed = FONT_RED_DEFAULT_VALUE;
			}
			fontGreen = propFontGreen.getInt(FONT_GREEN_DEFAULT_VALUE);
			if (fontGreen > FONT_GREEN_MAX_VALUE || fontGreen < FONT_GREEN_MIN_VALUE) {
				fontGreen = FONT_GREEN_DEFAULT_VALUE;
			}
			fontBlue = propFontBlue.getInt(FONT_BLUE_DEFAULT_VALUE);
			if (fontBlue > FONT_BLUE_MAX_VALUE || fontBlue < FONT_BLUE_MIN_VALUE) {
				fontBlue = FONT_BLUE_DEFAULT_VALUE;
			}
			/*backgroundAlpha = propBackgroundAlpha.getInt(BACKGROUND_ALPHA_DEFAULT_VALUE);
			if (backgroundAlpha > BACKGROUND_ALPHA_MAX_VALUE || backgroundAlpha < BACKGROUND_ALPHA_MIN_VALUE) {
				backgroundAlpha = BACKGROUND_ALPHA_DEFAULT_VALUE;
			}*/

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
		propFontRed.set(fontRed);
		propFontGreen.set(fontGreen);
		propFontBlue.set(fontBlue);

		//propBackgroundAlpha.set(backgroundAlpha);

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
				if (event.getConfigID().equals(CATEGORY_NAME_BACKGROUND) || event.getConfigID().equals(CATEGORY_NAME_POSITION) || event.getConfigID().equals(CATEGORY_NAME_FONT)/*|| event.getConfigID().equals(CATEGORY_NAME_OTHER)*/)
				{
					syncFromGUI();
				}
			}
		}
	}
}