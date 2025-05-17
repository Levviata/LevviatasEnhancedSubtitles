package les.config;

import les.LES;

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

public class LESConfiguration
{

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
    public static Property propShowSubtitles;
    public static Property propSubtitleScale;
    public static Property propXposition;
    public static Property propYposition;
    public static Property propIndex;
    private static int xPosition;
    private static int yPosition;
    private static String initialPositionPreset;
    private static double scale;
    private static boolean showSubtitles = true;
    private static int backgroundRed;
    private static int backgroundGreen;
    private static int backgroundBlue;
    private static int backgroundAlpha;
    private static int fontRed;
    private static int fontGreen;
    private static int fontBlue;
    private static int index;
    public static final String CATEGORY_NAME_POSITION = "category_position";
    public static final String CATEGORY_NAME_BACKGROUND = "category_background";
    public static final String CATEGORY_NAME_FONT = "category_font";
    public static final String CATEGORY_NAME_GENERAL = "category_general";

    public static void preInit()
    {

        File configFile = new File(Loader.instance().getConfigDir(), "EnhancedSubtitles.cfg");

        config = new Configuration(configFile);

        syncFromFile();
    }

    public static void clientPreInit()
    {
        MinecraftForge.EVENT_BUS.register(new ConfigEventHandler());
    }

    public static Configuration getConfig()
    {
        return config;
    }

    public static void syncFromFile()
    {
        syncConfig(true, true);
    }

    public static void syncFromGUI()
    {
        syncConfig(false, true);
    }

    public static void syncFromFields()
    {
        syncConfig(false, false);
    }

    private static void syncConfig(boolean loadConfigFromFile, boolean readFieldsFromConfig)
    {
        if (loadConfigFromFile)
        {
            config.load();
        }
//        propDisablePopup = config.get(CATEGORY_NAME_GENERAL, "disablePopup", false, "Whether to disable the popup that shows when you disable your GUI buttons or not.");
//        propDisablePopup.setLanguageKey("gui.les_configuration.disablePopup");

        propShowSubtitles = config.get(CATEGORY_NAME_GENERAL, "showSubtitles", true, "Whether to show the subtitles or not.");
        propShowSubtitles.setLanguageKey("gui.les_configuration.showSubtitles");

        propIndex = config.get(CATEGORY_NAME_GENERAL, "index", 0, "Internal value that defines which overlay position is currently shown, I recommend not touching this.");
        propIndex.setLanguageKey("gui.les_configuration.index");

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

        final int BACKGROUND_ALPHA_MIN_VALUE = 0;
        final int BACKGROUND_ALPHA_MAX_VALUE = 255;
        final int BACKGROUND_ALPHA_DEFAULT_VALUE = 255;
        propBackgroundAlpha = config.get(
                CATEGORY_NAME_BACKGROUND,
                "backgroundAlpha", BACKGROUND_ALPHA_DEFAULT_VALUE,
                "The RGB alpha value (in decimals) for the subtitle's background.",
                BACKGROUND_ALPHA_MIN_VALUE,
                BACKGROUND_ALPHA_MAX_VALUE);
        propBackgroundAlpha.setLanguageKey("gui.les_configuration.backgroundAlpha");

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

        final double SUBTITLE_SCALE_MIN_VALUE = 0.1;
        final double SUBTITLE_SCALE_MAX_VALUE = 10;
        final double SUBTITLE_SCALE_DEFAULT_VALUE = 1;
        propSubtitleScale = config.get(
                CATEGORY_NAME_GENERAL,
                "subtitleScale", SUBTITLE_SCALE_DEFAULT_VALUE,
                "The scale that the subtitles should be rendered at.",
                SUBTITLE_SCALE_MIN_VALUE,
                SUBTITLE_SCALE_MAX_VALUE);
        propSubtitleScale.setLanguageKey("gui.les_configuration.subtitleScale");

        final int X_POSITION_MIN_VALUE = -10000;
        final int X_POSITION_MAX_VALUE = 10000;
        final int X_POSITION_DEFAULT_VALUE = 0;
        propXposition = config.get(
                CATEGORY_NAME_POSITION,
                "xOffset", X_POSITION_DEFAULT_VALUE,
                "The offset of the subtitle's position on the X axis.",
                X_POSITION_MIN_VALUE,
                X_POSITION_MAX_VALUE);
        propXposition.setLanguageKey("gui.les_configuration.xOffset");

        final int Y_POSITION_MIN_VALUE = -10000;
        final int Y_POSITION_MAX_VALUE = 10000;
        final int Y_POSITION_DEFAULT_VALUE = 0;
        propYposition = config.get(
                CATEGORY_NAME_POSITION,
                "yOffset", Y_POSITION_DEFAULT_VALUE,
                "The offset of the subtitle's position on the Y axis.",
                Y_POSITION_MIN_VALUE,
                Y_POSITION_MAX_VALUE);
        propYposition.setLanguageKey("gui.les_configuration.yOffset");

        List<String> propOrderPosition = new ArrayList<String>();
        propOrderPosition.add(propOverlayPosition.getName());
        propOrderPosition.add(propXposition.getName());
        propOrderPosition.add(propYposition.getName());
        config.setCategoryPropertyOrder(CATEGORY_NAME_POSITION, propOrderPosition);

        List<String> propOrderBackground = new ArrayList<String>();
        propOrderBackground.add(propBackgroundRed.getName());
        propOrderBackground.add(propBackgroundGreen.getName());
        propOrderBackground.add(propBackgroundBlue.getName());
        propOrderBackground.add(propBackgroundAlpha.getName());
        config.setCategoryPropertyOrder(CATEGORY_NAME_BACKGROUND, propOrderBackground);

        List<String> propOrderFont = new ArrayList<String>();
        propOrderFont.add(propFontRed.getName());
        propOrderFont.add(propFontGreen.getName());
        propOrderFont.add(propFontBlue.getName());
        config.setCategoryPropertyOrder(CATEGORY_NAME_FONT, propOrderFont);

        List<String> propOrderGeneral = new ArrayList<String>();
        propOrderGeneral.add(propShowSubtitles.getName());
        propOrderGeneral.add(propSubtitleScale.getName());
        config.setCategoryPropertyOrder(CATEGORY_NAME_GENERAL, propOrderGeneral);

        if (readFieldsFromConfig)
        {

            showSubtitles = propShowSubtitles.getBoolean(true);
            backgroundRed = propBackgroundRed.getInt(BACKGROUND_RED_DEFAULT_VALUE);
            if (backgroundRed > BACKGROUND_RED_MAX_VALUE || backgroundRed < BACKGROUND_RED_MIN_VALUE)
            {
                backgroundRed = BACKGROUND_RED_DEFAULT_VALUE;
            }
            backgroundGreen = propBackgroundGreen.getInt(BACKGROUND_GREEN_DEFAULT_VALUE);
            if (backgroundGreen > BACKGROUND_GREEN_MAX_VALUE || backgroundGreen < BACKGROUND_GREEN_MIN_VALUE)
            {
                backgroundGreen = BACKGROUND_GREEN_DEFAULT_VALUE;
            }
            backgroundBlue = propBackgroundBlue.getInt(BACKGROUND_BLUE_DEFAULT_VALUE);
            if (backgroundBlue > BACKGROUND_BLUE_MAX_VALUE || backgroundBlue < BACKGROUND_BLUE_MIN_VALUE)
            {
                backgroundBlue = BACKGROUND_BLUE_DEFAULT_VALUE;
            }
            backgroundAlpha = propBackgroundAlpha.getInt(BACKGROUND_ALPHA_DEFAULT_VALUE);
            if (backgroundAlpha > BACKGROUND_ALPHA_MAX_VALUE || backgroundAlpha < BACKGROUND_ALPHA_MIN_VALUE)
            {
                backgroundAlpha = BACKGROUND_ALPHA_DEFAULT_VALUE;
            }
            fontRed = propFontRed.getInt(FONT_RED_DEFAULT_VALUE);
            if (fontRed > FONT_RED_MAX_VALUE || fontRed < FONT_RED_MIN_VALUE)
            {
                fontRed = FONT_RED_DEFAULT_VALUE;
            }
            fontGreen = propFontGreen.getInt(FONT_GREEN_DEFAULT_VALUE);
            if (fontGreen > FONT_GREEN_MAX_VALUE || fontGreen < FONT_GREEN_MIN_VALUE)
            {
                fontGreen = FONT_GREEN_DEFAULT_VALUE;
            }
            fontBlue = propFontBlue.getInt(FONT_BLUE_DEFAULT_VALUE);
            if (fontBlue > FONT_BLUE_MAX_VALUE || fontBlue < FONT_BLUE_MIN_VALUE)
            {
                fontBlue = FONT_BLUE_DEFAULT_VALUE;
            }

            scale = propSubtitleScale.getDouble(SUBTITLE_SCALE_DEFAULT_VALUE);
            if (scale > SUBTITLE_SCALE_MAX_VALUE || scale < SUBTITLE_SCALE_MIN_VALUE)
            {
                scale = SUBTITLE_SCALE_DEFAULT_VALUE;
            }
            // If overlayPosition can't get any config it just simply defaults to "BOTTOM_RIGHT"
            overlayPosition = propOverlayPosition.getString();
            boolean overlayMatched = false;
            for (String entry : POSITION_CHOICES)
            {
                if (entry.equals(overlayPosition))
                {
                    overlayMatched = true;
                    break;
                }
            }
            if (!overlayMatched)
            {
                overlayPosition = OVERLAY_POSITION_DEFAULT_VALUE;
            }

            xPosition = propXposition.getInt(X_POSITION_DEFAULT_VALUE);
            if (xPosition > X_POSITION_MAX_VALUE || xPosition < X_POSITION_MIN_VALUE)
            {
                xPosition = X_POSITION_DEFAULT_VALUE;
            }
            yPosition = propYposition.getInt(Y_POSITION_DEFAULT_VALUE);
            if (yPosition > Y_POSITION_MAX_VALUE || yPosition < Y_POSITION_MIN_VALUE)
            {
                yPosition = Y_POSITION_DEFAULT_VALUE;
            }
            initialPositionPreset = propOverlayPosition.getString();
            index = propIndex.getInt(0);
        }

        propShowSubtitles.set(showSubtitles);
        propOverlayPosition.set(overlayPosition);
        propBackgroundRed.set(backgroundRed);
        propBackgroundGreen.set(backgroundGreen);
        propBackgroundBlue.set(backgroundBlue);
        propFontRed.set(fontRed);
        propFontGreen.set(fontGreen);
        propFontBlue.set(fontBlue);
        propSubtitleScale.set(scale);
        propXposition.set(xPosition);
        propYposition.set(yPosition);
        propBackgroundAlpha.set(backgroundAlpha);
        propIndex.set(index);

        if (config.hasChanged())
        {
            config.save();
        }
    }

    private static Configuration config = null;

    public static class ConfigEventHandler
    {

        @SubscribeEvent(priority = EventPriority.NORMAL)
        public void onEvent(ConfigChangedEvent.OnConfigChangedEvent event)
        {
            if (LES.MODID.equals(event.getModID()))
            {
                if (!initialPositionPreset.equals(propOverlayPosition.getString()))
                {
                    propXposition.set(0);
                    propYposition.set(0);
                }
                if (event.getConfigID().equals(CATEGORY_NAME_BACKGROUND) || event.getConfigID().equals(CATEGORY_NAME_POSITION) || event.getConfigID().equals(CATEGORY_NAME_FONT) || event.getConfigID().equals(CATEGORY_NAME_GENERAL))
                {
                    syncFromGUI();
                }
            }
        }
    }
}