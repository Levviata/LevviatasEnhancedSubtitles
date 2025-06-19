package les.gui;

import com.google.common.collect.Lists;
import les.config.LESConfiguration;
import les.utils.ColorConverter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.config.Configuration;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import static les.config.LESConfiguration.*;
import static les.gui.SubtitleOverlayHandler.*;

public class SubtitleDragGui extends GuiScreen
{

    private static final List<SubtitleOverlayHandler.Subtitle> previewSubtitles = Lists.newArrayList();
    public static boolean isGuiOpen = false;

    static
    {
        previewSubtitles.add(new SubtitleOverlayHandler.Subtitle("Example Subtitle", new Vec3d(0, 0, 0)));
        previewSubtitles.add(new SubtitleOverlayHandler.Subtitle("Big ol' Example Subtitle", new Vec3d(0, 0, 0)));
        previewSubtitles.add(new SubtitleOverlayHandler.Subtitle("Hi from Example Subtitle 9876", new Vec3d(0, 0, 0)));
        previewSubtitles.add(new SubtitleOverlayHandler.Subtitle("Example Subtitle 12345 Example Subtitle", new Vec3d(0, 0, 0)));
    }

    final String[] POSITION_CHOICES = {
            "BOTTOM_RIGHT",
            "BOTTOM_CENTER",
            "BOTTOM_LEFT",
            "CENTER_LEFT",
            "TOP_LEFT",
            "TOP_CENTER",
            "TOP_RIGHT",
            "CENTER_RIGHT"
    };
    private boolean dragging;
    private int lastMouseX;
    private int lastMouseY;

    private boolean initialShowSubtitles;
    private float initialScale;
    private int initialBackgroundAlpha;
    private int initialFontRed;
    private int initialFontGreen;
    private int initialFontBlue;
    private boolean initialLockPosition;
    private int initialBackgroundRed;
    private int initialBackgroundGreen;
    private int initialBackgroundBlue;

    private boolean isNormalOptions = true;
    private static final int TOGGLE_SUBTITLES_BUTTON_ID = 1;
    // ID 2 nowhere to be seen lol
    private static final int SCALE_BUTTON_ID = 3;
    private static final int BACKGROUND_ALPHA_BUTTON_ID = 4;
    private static final int OVERLAY_POSITION_BUTTON_ID = 5;
    private static final int LOCK_POSITION_BUTTON_ID = 6;
    private static final int ADVANCED_OPTIONS_BUTTON_ID = 7;
    private static final int RESET_TO_DEFAULTS_BUTTON_ID = 8;

    private boolean isAdvancedOptions = false;
    private static final int GO_BACK_BUTTON_ID = 9;
    private static final int BACKGROUND_RED_BUTTON_ID = 10;
    private static final int BACKGROUND_GREEN_BUTTON_ID = 11;
    private static final int BACKGROUND_BLUE_BUTTON_ID = 12;
    private static final int FONT_RED_BUTTON_ID = 13;
    private static final int FONT_GREEN_BUTTON_ID = 14;
    private static final int FONT_BLUE_BUTTON_ID = 15;

    private GuiButton showSubtitles;
    private GuiButton scaleSlider;
    private GuiButton alphaSlider;
    private GuiButton overlayPosition;
    private GuiButton lockPosition;
    private GuiButton backgroundRed;
    private GuiButton backgroundGreen;
    private GuiButton backgroundBlue;
    private GuiButton fontRed;
    private GuiButton fontGreen;
    private GuiButton fontBlue;

    private int index;

    private Logger logger = Logger.getLogger("SubtitleDragGui");

    @Override
    public void initGui()
    {
        super.initGui();
        buttonList.clear();

        //Store values for later
        initialShowSubtitles = propShowSubtitles.getBoolean();
        initialScale = (float) propSubtitleScale.getDouble();
        initialBackgroundAlpha = propBackgroundAlpha.getInt();
        initialFontRed = propFontRed.getInt();
        initialFontGreen = propFontGreen.getInt();
        initialFontBlue = propFontBlue.getInt();
        initialLockPosition = propLockPosition.getBoolean();
        initialBackgroundRed = propBackgroundRed.getInt();
        initialBackgroundGreen = propBackgroundGreen.getInt();
        initialBackgroundBlue = propBackgroundBlue.getInt();

        if (isNormalOptions)
        {
            initButtons();
        }
        if (isAdvancedOptions)
        {
            initAdvancedButtons();
        }
    }
    private void initButtons() {
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());

        showSubtitles = new GuiButton(TOGGLE_SUBTITLES_BUTTON_ID, res.getScaledWidth() / 2 - 100, 20, TextFormatting.YELLOW + "Mod Status: " +
                (propShowSubtitles.getBoolean(true) ? TextFormatting.DARK_GREEN + "Enabled" : TextFormatting.DARK_RED + "Disabled"));
        buttonList.add(showSubtitles);

        scaleSlider = new GuiSlider(new GuiPageButtonList.GuiResponder()
        {
            @Override
            public void setEntryValue(int id, boolean value)
            {
            }

            @Override
            public void setEntryValue(int id, float value)
            {
                if (id == SCALE_BUTTON_ID)
                {
                    value = Math.round(value * 10) / 10f;
                    propSubtitleScale.set(value);
                }
            }

            @Override
            public void setEntryValue(int id, String value)
            {
            }

        }, SCALE_BUTTON_ID, res.getScaledWidth() / 2 - 100, 45,
                "Scale: ", 0.1f, 10, initialScale,
                (id, name, value) -> "Scale: " + (float) propSubtitleScale.getDouble() + "x"
        );
        scaleSlider.width = 200;
        buttonList.add(scaleSlider);

        alphaSlider = new GuiSlider(new GuiPageButtonList.GuiResponder()
        {
            @Override
            public void setEntryValue(int id, boolean value)
            {
            }

            @Override
            public void setEntryValue(int id, float value)
            {
                if (id == BACKGROUND_ALPHA_BUTTON_ID)
                {
                    propBackgroundAlpha.set((int) value);
                }
            }

            @Override
            public void setEntryValue(int id, String value)
            {
            }
        }, BACKGROUND_ALPHA_BUTTON_ID, res.getScaledWidth() / 2 - 100, 70,
                "Alpha: ", 0, 255, initialBackgroundAlpha,
                (id, name, value) -> "Alpha: " + propBackgroundAlpha.getInt() + "%"
        );
        alphaSlider.width = 200;
        buttonList.add(alphaSlider);

        overlayPosition = new GuiButton(OVERLAY_POSITION_BUTTON_ID,
                res.getScaledWidth() / 2 - 100,
                95,
                TextFormatting.YELLOW + "Overlay Position: " + propOverlayPosition.getString());
        buttonList.add(overlayPosition);

        lockPosition = new GuiButton(LOCK_POSITION_BUTTON_ID,
                res.getScaledWidth() / 2 - 100,
                120,
                TextFormatting.YELLOW + "Lock Position: " +
                        (propLockPosition.getBoolean(false) ? TextFormatting.DARK_GREEN + "Enabled" : TextFormatting.DARK_RED + "Disabled"));
        buttonList.add(lockPosition);

        GuiButton advancedOptions = new GuiButton(ADVANCED_OPTIONS_BUTTON_ID,
                res.getScaledWidth() / 2 - 100,
                145,
                TextFormatting.YELLOW + "Advanced Options");
        buttonList.add(advancedOptions);

        buttonList.add(new GuiButton(RESET_TO_DEFAULTS_BUTTON_ID,
                res.getScaledWidth() / 2 - 100,
                170,
                TextFormatting.YELLOW + "Reset Values To Default"));
    }

    private void initAdvancedButtons() {
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        GuiButton goBack = new GuiButton(GO_BACK_BUTTON_ID,
                res.getScaledWidth() / 2 - 100,
                20,
                TextFormatting.YELLOW + "<- Go Back");
        buttonList.add(goBack);


        backgroundRed = new GuiSlider(new GuiPageButtonList.GuiResponder()
        {
            @Override
            public void setEntryValue(int id, boolean value)
            {
            }

            @Override
            public void setEntryValue(int id, float value)
            {
                if (id == BACKGROUND_RED_BUTTON_ID)
                {
                    propBackgroundRed.set((int) value);
                }
            }

            @Override
            public void setEntryValue(int id, String value)
            {
            }
        }, BACKGROUND_RED_BUTTON_ID, res.getScaledWidth() / 2 - 100, 45,
                "Background Red: ", 0, 255, initialBackgroundRed,
                (id, name, value) -> TextFormatting.YELLOW + "Background Red: " + propBackgroundRed.getInt() + " (RGB)"
        );
        backgroundRed.width = 200;
        buttonList.add(backgroundRed);

        backgroundGreen = new GuiSlider(new GuiPageButtonList.GuiResponder()
        {
            @Override
            public void setEntryValue(int id, boolean value)
            {
            }

            @Override
            public void setEntryValue(int id, float value)
            {
                if (id == BACKGROUND_GREEN_BUTTON_ID)
                {
                    propBackgroundGreen.set((int) value);
                }
            }

            @Override
            public void setEntryValue(int id, String value)
            {
            }
        }, BACKGROUND_GREEN_BUTTON_ID, res.getScaledWidth() / 2 - 100, 70,
                "Background Green: ", 0, 255, initialBackgroundGreen,
                (id, name, value) -> TextFormatting.YELLOW + "Background Green: " + propBackgroundGreen.getInt() + " (RGB)"
        );
        backgroundGreen.width = 200;
        buttonList.add(backgroundGreen);

        backgroundBlue = new GuiSlider(new GuiPageButtonList.GuiResponder()
        {
            @Override
            public void setEntryValue(int id, boolean value)
            {
            }

            @Override
            public void setEntryValue(int id, float value)
            {
                if (id == BACKGROUND_BLUE_BUTTON_ID)
                {
                    propBackgroundBlue.set((int) value);
                }
            }

            @Override
            public void setEntryValue(int id, String value)
            {
            }
        }, BACKGROUND_BLUE_BUTTON_ID, res.getScaledWidth() / 2 - 100, 95,
                "Background Blue: ", 0, 255, initialBackgroundBlue,
                (id, name, value) -> TextFormatting.YELLOW + "Background Blue: " + propBackgroundBlue.getInt() + " (RGB)"
        );
        backgroundBlue.width = 200;
        buttonList.add(backgroundBlue);

        fontRed = new GuiSlider(new GuiPageButtonList.GuiResponder()
        {
            @Override
            public void setEntryValue(int id, boolean value)
            {
            }

            @Override
            public void setEntryValue(int id, float value)
            {
                if (id == FONT_RED_BUTTON_ID)
                {
                    propFontRed.set((int) value);
                }
            }

            @Override
            public void setEntryValue(int id, String value)
            {
            }
        }, FONT_RED_BUTTON_ID, res.getScaledWidth() / 2 - 100, 120,
                "Font Red: ", 0, 255, initialFontRed,
                (id, name, value) -> TextFormatting.YELLOW + "Font Red: " + propFontRed.getInt() + " (RGB)"
        );
        fontRed.width = 200;
        buttonList.add(fontRed);

        fontGreen = new GuiSlider(new GuiPageButtonList.GuiResponder()
        {
            @Override
            public void setEntryValue(int id, boolean value)
            {
            }

            @Override
            public void setEntryValue(int id, float value)
            {
                if (id == FONT_GREEN_BUTTON_ID)
                {
                    propFontGreen.set((int) value);
                }
            }

            @Override
            public void setEntryValue(int id, String value)
            {
            }
        }, FONT_GREEN_BUTTON_ID, res.getScaledWidth() / 2 - 100, 145,
                "Font Green: ", 0, 255, initialFontGreen,
                (id, name, value) -> TextFormatting.YELLOW + "Font Green: " + propFontGreen.getInt() + " (RGB)"
        );
        fontGreen.width = 200;
        buttonList.add(fontGreen);

        fontBlue = new GuiSlider(new GuiPageButtonList.GuiResponder()
        {
            @Override
            public void setEntryValue(int id, boolean value)
            {
            }

            @Override
            public void setEntryValue(int id, float value)
            {
                if (id == FONT_BLUE_BUTTON_ID)
                {
                    propFontBlue.set((int) value);
                }
            }

            @Override
            public void setEntryValue(int id, String value)
            {
            }
        }, FONT_BLUE_BUTTON_ID, res.getScaledWidth() / 2 - 100, 170,
                "Font Blue: ", 0, 255, initialFontBlue,
                (id, name, value) -> TextFormatting.YELLOW + "Font Blue: " + propFontBlue.getInt() + " (RGB)"
        );
        fontBlue.width = 200;
        buttonList.add(fontBlue);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button)
    {
        try
        {
            super.mouseClicked(mouseX, mouseY, button);
        }
        catch (IOException ignored)
        {
        }

        if (button == 0)
        {
            for (GuiButton guiButton : buttonList)
            {
                if (guiButton.isMouseOver()) return;
            }
            this.dragging = !propLockPosition.getBoolean(); // drag depending on if we have the subtitles locked or not

            if (this.dragging)
            {
                buttonList.clear();
            }

            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int action)
    {
        super.mouseReleased(mouseX, mouseY, action);

        if (this.dragging) {
            this.dragging = false;
            if (isNormalOptions)
            {
                initButtons();
            }
            if (isAdvancedOptions)
            {
                initAdvancedButtons();
            }
        }

        Configuration config = LESConfiguration.getConfig();
        config.save();
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
        if (this.dragging)
        {
            int diff = mouseX - this.lastMouseX;
            int xPos = propXposition.getInt();
            int yPos = propYposition.getInt();
            xPos = xPos + diff;
            propXposition.set(xPos);
            yPos = yPos + (mouseY - this.lastMouseY);
            propYposition.set(yPos);
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button == null) return;
        switch (button.id)
        {
            case TOGGLE_SUBTITLES_BUTTON_ID:
            {
                boolean getShowSubtitles = propShowSubtitles.getBoolean();
                getShowSubtitles = !getShowSubtitles;
                propShowSubtitles.set(getShowSubtitles);
                break;
            }
            case RESET_TO_DEFAULTS_BUTTON_ID:
            {
                // Change back color to normal
                button.displayString = "Reset Values To Default";

                // Reset values to default
                int xPos;
                int yPos;
                xPos = lastPosX;
                yPos = lastPosY;
                propXposition.set(xPos);
                propYposition.set(yPos);
                Configuration config = LESConfiguration.getConfig();
                config.get(CATEGORY_NAME_GENERAL, "showSubtitles", propShowSubtitles.getDefault()).set(propShowSubtitles.getDefault());
                config.get(CATEGORY_NAME_GENERAL, "subtitleScale", propSubtitleScale.getDefault()).set(propSubtitleScale.getDefault());
                config.get(CATEGORY_NAME_BACKGROUND, "backgroundAlpha", propBackgroundAlpha.getDefault()).set(propBackgroundAlpha.getDefault());
                config.get(CATEGORY_NAME_POSITION, "overlayPosition", propOverlayPosition.getDefault()).set( propOverlayPosition.getDefault());
                config.get(CATEGORY_NAME_FONT, "fontRed", propFontRed.getDefault()).set(propFontRed.getDefault());
                config.get(CATEGORY_NAME_FONT, "fontGreen", propFontGreen.getDefault()).set(propFontGreen.getDefault());
                config.get(CATEGORY_NAME_FONT, "fontBlue", propFontBlue.getDefault()).set(propFontBlue.getDefault());
                config.get(CATEGORY_NAME_BACKGROUND, "backgroundRed", propBackgroundRed.getDefault()).set((propBackgroundRed.getDefault()));
                config.get(CATEGORY_NAME_BACKGROUND, "backgroundGreen", propBackgroundGreen.getDefault()).set((propBackgroundGreen.getDefault()));
                config.get(CATEGORY_NAME_BACKGROUND, "backgroundBlue", propBackgroundBlue.getDefault()).set((propBackgroundBlue.getDefault()));
                config.get(CATEGORY_NAME_GENERAL, "lockPosition", propLockPosition.getDefault()).set(propLockPosition.getDefault());
                index = 0;

                config.save();
                buttonList.clear();
                initGui();
                break;
            }
            case OVERLAY_POSITION_BUTTON_ID:
            {
                index++; // Increment index first
                if (index >= POSITION_CHOICES.length)
                {
                    // Reset index to 0 when it reaches the end of the array
                    index = 0;
                }
                // Now set the overlay position using the updated index
                propOverlayPosition.set(POSITION_CHOICES[index]);
                propXposition.set(0);
                propYposition.set(0);
                isNormalOptions = true;
                button.displayString = "Overlay Position: " + POSITION_CHOICES[index];
                break;
            }
            case ADVANCED_OPTIONS_BUTTON_ID:
            {
                buttonList.clear();

                isNormalOptions = false;
                isAdvancedOptions = true;

                initAdvancedButtons();

                break;
            }
            case GO_BACK_BUTTON_ID:
            {
                buttonList.clear();

                isNormalOptions = true;
                isAdvancedOptions = false;

                initButtons();

                break;
            }
            case LOCK_POSITION_BUTTON_ID:
            {
                boolean getLockPosition = propLockPosition.getBoolean();
                getLockPosition = !getLockPosition; // on off toggle
                propLockPosition.set(getLockPosition);
                break;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        // EXTRA maybe check if the button's display string has the yellow format to then stop applying the yellow tint?
        // This might, marginally, improve frame rates which is cool

        if (isNormalOptions)
        {
            if (!propShowSubtitles.isList())
            {
                if (propShowSubtitles.getBoolean() != Boolean.parseBoolean(propShowSubtitles.getDefault()))
                {
                    showSubtitles.displayString = "Mod Status: " +
                            (propShowSubtitles.getBoolean(true) ? TextFormatting.DARK_GREEN + "Enabled" : TextFormatting.DARK_RED + "Disabled");
                } else
                {
                    showSubtitles.displayString = TextFormatting.YELLOW + "Mod Status: " +
                            (propShowSubtitles.getBoolean(true) ? TextFormatting.DARK_GREEN + "Enabled" : TextFormatting.DARK_RED + "Disabled");
                }
            }

            if (!propSubtitleScale.isList())
            {
                if ((float) propSubtitleScale.getDouble() != Float.parseFloat(propSubtitleScale.getDefault()))
                {
                    scaleSlider.displayString = "Scale: " + (float) propSubtitleScale.getDouble() + "x";
                } else
                {
                    scaleSlider.displayString = TextFormatting.YELLOW + "Scale: " + (float) propSubtitleScale.getDouble() + "x";
                }
            }

            if (!propBackgroundAlpha.isList()) // if the property isnt a list
            {
                if (propBackgroundAlpha.getInt() != Integer.parseInt(propBackgroundAlpha.getDefault())) // if not defautl

                {
                    alphaSlider.displayString = "Alpha: " + propBackgroundAlpha.getInt() + "%"; // remove yellow tint.
                } else // otherwise, its default
                {
                    alphaSlider.displayString = TextFormatting.YELLOW + "Alpha: " + propBackgroundAlpha.getInt() + "%"; // give yellow tint.
                }
            }

            if (!propOverlayPosition.isList())
            {
                if (!propOverlayPosition.getString().equals(propOverlayPosition.getDefault())) // not default, give white tint
                {
                    overlayPosition.displayString = "Overlay Position: " + propOverlayPosition.getString();
                } else
                {
                    overlayPosition.displayString = TextFormatting.YELLOW + "Overlay Position: " + propOverlayPosition.getString();
                }
            }

            if (!propLockPosition.isList())
            {
                if (propLockPosition.getBoolean() != Boolean.parseBoolean(propLockPosition.getDefault()))
                {
                    lockPosition.displayString = "Lock Position: " +
                            (propLockPosition.getBoolean(false) ? TextFormatting.DARK_GREEN + "Enabled" : TextFormatting.DARK_RED + "Disabled");
                }
                else
                {
                    lockPosition.displayString = TextFormatting.YELLOW + "Lock Position: " +
                            (propLockPosition.getBoolean(false) ? TextFormatting.DARK_GREEN + "Enabled" : TextFormatting.DARK_RED + "Disabled");
                }
            }
        }
        if (isAdvancedOptions)
        {
            if (!propBackgroundRed.isList())
            {
                if (propBackgroundRed.getInt() != Integer.parseInt(propBackgroundRed.getDefault()))
                {
                    backgroundRed.displayString = "Background Red: " + propBackgroundRed.getInt() + " (RGB)";
                } else
                {
                    backgroundRed.displayString = TextFormatting.YELLOW + "Background Red: " + propBackgroundRed.getInt() + " (RGB)";
                }
            }
            if (!propBackgroundGreen.isList())
            {
                if (propBackgroundGreen.getInt() != Integer.parseInt(propBackgroundGreen.getDefault()))
                {
                    backgroundGreen.displayString = "Background Green: " + propBackgroundGreen.getInt() + " (RGB)";
                } else
                {
                    backgroundGreen.displayString = TextFormatting.YELLOW + "Background Green: " + propBackgroundGreen.getInt() + " (RGB)";
                }
            }
            if (!propBackgroundBlue.isList())
            {
                if (propBackgroundBlue.getInt() != Integer.parseInt(propBackgroundBlue.getDefault()))
                {
                    backgroundBlue.displayString = "Background Blue: " + propBackgroundBlue.getInt() + " (RGB)";
                } else
                {
                    backgroundBlue.displayString = TextFormatting.YELLOW + "Background Blue: " + propBackgroundBlue.getInt() + " (RGB)";
                }
            }

            if (!propFontRed.isList())
            {
                if (propFontRed.getInt() != Integer.parseInt(propFontRed.getDefault()))
                {
                    fontRed.displayString = "Font Red: " + propFontRed.getInt() + " (RGB)";
                } else
                {
                    fontRed.displayString = TextFormatting.YELLOW + "Font Red: " + propFontRed.getInt() + " (RGB)";
                }
            }
            if (!propFontGreen.isList())
            {
                if (propFontGreen.getInt() != Integer.parseInt(propFontGreen.getDefault()))
                {
                    fontGreen.displayString = "Font Green: " + propFontGreen.getInt() + " (RGB)";
                } else
                {
                    fontGreen.displayString = TextFormatting.YELLOW + "Font Green: " + propFontGreen.getInt() + " (RGB)";
                }
            }
            if (!propFontBlue.isList())
            {
                if (propFontBlue.getInt() != Integer.parseInt(propFontBlue.getDefault()))
                {
                    fontBlue.displayString = "Font Blue: " + propFontBlue.getInt() + " (RGB)";
                } else
                {
                    fontBlue.displayString = TextFormatting.YELLOW + "Font Blue: " + propFontBlue.getInt() + " (RGB)";
                }
            }
        }

        int maxLength = 0;
        int captionIndex = 0;
        Iterator<Subtitle> iterator = previewSubtitles.iterator();
        while (iterator.hasNext())
        {
            SubtitleOverlayHandler.Subtitle caption = iterator.next();
            maxLength = Math.max(maxLength, mc.fontRenderer.getStringWidth(previewSubtitles.get(3).getString()));
        }
        for (SubtitleOverlayHandler.Subtitle caption : previewSubtitles)
        {
            boolean showSubtitles = getConfig().get(CATEGORY_NAME_GENERAL, "showSubtitles", true).getBoolean();
            if (showSubtitles)
            {
                int halfMaxLength = maxLength / 2;

                int subtitleHeight = mc.fontRenderer.FONT_HEIGHT;
                int subtitleWidth = mc.fontRenderer.getStringWidth(caption.getString());

                int backgroundAlpha = LESConfiguration.propBackgroundAlpha.getInt();

                int fadeAwayCalculation = MathHelper.floor(MathHelper.clampedLerp(255.0D, 75.0D, (float) (Minecraft.getSystemTime() - caption.getStartTime()) / 3000.0F));

                int backgroundRed = LESConfiguration.propBackgroundRed.getInt();
                int backgroundGreen = LESConfiguration.propBackgroundGreen.getInt();
                int backgroundBlue = LESConfiguration.propBackgroundBlue.getInt();

                int fontRed = LESConfiguration.propFontRed.getInt();
                int fontGreen = LESConfiguration.propFontGreen.getInt();
                int fontBlue = LESConfiguration.propFontBlue.getInt();

                int backgroundColor = ColorConverter.colorToDecimal(backgroundRed, backgroundGreen, backgroundBlue);
                int fontColor = ColorConverter.colorToDecimal(fontRed, fontGreen, fontBlue);

                String position = LESConfiguration.propOverlayPosition.getString();
                float verticalSpacing = 1;
                float horizontalSpacing = 2;
                float subtitleSpacing = 10 * (float) propSubtitleScale.getDouble();
                ScaledResolution resolution = new ScaledResolution(mc);
                float xPos = propXposition.getInt();
                float yPos = propYposition.getInt();

                GlStateManager.pushMatrix();

                switch (position)
                {
                    case "BOTTOM_CENTER":
                        xPos += (float) resolution.getScaledWidth() / 2;
                        yPos += ((resolution.getScaledHeight() - 75) - (captionIndex * subtitleSpacing));
                        index = 1;
                        break;
                    case "BOTTOM_LEFT":
                        xPos += halfMaxLength + horizontalSpacing;
                        yPos += ((resolution.getScaledHeight() - 30) - (captionIndex * subtitleSpacing));
                        index = 2;
                        break;
                    case "CENTER_LEFT":
                        xPos += halfMaxLength + horizontalSpacing;
                        yPos += (((float) resolution.getScaledHeight() / 2) - (((float) (previewSubtitles.size() - 1) / 2) - captionIndex) * subtitleSpacing);
                        index = 3;
                        break;
                    case "TOP_LEFT":
                        xPos += halfMaxLength + horizontalSpacing;
                        yPos += (captionIndex * subtitleSpacing + 5 + verticalSpacing);
                        index = 4;
                        break;
                    case "TOP_CENTER":
                        xPos += (float) resolution.getScaledWidth() / 2;
                        yPos += (captionIndex * subtitleSpacing + 5 + verticalSpacing);
                        index = 5;
                        break;
                    case "TOP_RIGHT":
                        xPos += resolution.getScaledWidth() - halfMaxLength - 2;
                        yPos += (captionIndex * subtitleSpacing + 5 + verticalSpacing);
                        index = 6;
                        break;
                    case "CENTER_RIGHT":
                        xPos += resolution.getScaledWidth() - halfMaxLength - horizontalSpacing;
                        yPos += (((float) resolution.getScaledHeight() / 2) - (((float) (previewSubtitles.size() - 1) / 2) - captionIndex) * subtitleSpacing);
                        index = 7;
                        break;
                    default: // BOTTOM_RIGHT
                        xPos += resolution.getScaledWidth() - halfMaxLength - 2;
                        yPos += ((resolution.getScaledHeight() - 30) - (captionIndex * subtitleSpacing));
                        index = 0;
                        break;
                }
                xPos = MathHelper.clamp(xPos, 0, resolution.getScaledWidth() - ((float) subtitleWidth / 2));
                yPos = MathHelper.clamp(yPos, 0, resolution.getScaledHeight() - ((float) subtitleHeight / 2));

                GlStateManager.translate(xPos, yPos, 0);

                GlStateManager.scale((float) propSubtitleScale.getDouble(), (float) propSubtitleScale.getDouble(), 1.0F);

                drawRect(-halfMaxLength - 1, -subtitleHeight / 2 - 1, halfMaxLength + 1, subtitleHeight / 2 + 1,
                        backgroundAlpha << 24 | backgroundColor);

                GlStateManager.enableBlend();
                mc.fontRenderer.drawString(caption.getString(), -subtitleWidth / 2, -subtitleHeight / 2, 255 << 24 | fontColor);
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
                ++captionIndex;
            }
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }


    @Override
    public void onGuiClosed()
    {
        if (initialShowSubtitles != propShowSubtitles.getBoolean() ||
                initialScale != (float) propSubtitleScale.getDouble() ||
                initialBackgroundAlpha != propBackgroundAlpha.getInt() ||
                initialFontRed != propFontRed.getInt() ||
                initialFontGreen != propFontGreen.getInt() ||
                initialFontBlue != propFontBlue.getInt() ||
                initialLockPosition != propLockPosition.getBoolean()
        )
        {
            // Values have changed, save the changes to the config

            Configuration config = LESConfiguration.getConfig();
            if (config != null)
            {
                // Set the new values
                config.get(CATEGORY_NAME_GENERAL, "showSubtitles", Boolean.parseBoolean(propShowSubtitles.getDefault())).set(propShowSubtitles.getBoolean());
                config.get(CATEGORY_NAME_GENERAL, "subtitleScale", Double.parseDouble(propSubtitleScale.getDefault())).set(propSubtitleScale.getDouble());
                config.get(CATEGORY_NAME_BACKGROUND, "backgroundAlpha", Integer.parseInt(propBackgroundAlpha.getDefault())).set(propBackgroundAlpha.getInt());
                config.get(CATEGORY_NAME_FONT, "fontRed", Integer.parseInt(propFontRed.getDefault())).set(propFontRed.getInt());
                config.get(CATEGORY_NAME_FONT, "fontGreen", Integer.parseInt(propFontGreen.getDefault())).set(propFontGreen.getInt());
                config.get(CATEGORY_NAME_FONT, "fontBlue", Integer.parseInt(propFontBlue.getDefault())).set(propFontBlue.getInt());
                config.get(CATEGORY_NAME_GENERAL, "lockPosition", Boolean.parseBoolean(propLockPosition.getDefault())).set(propLockPosition.getBoolean());
                config.get(CATEGORY_NAME_BACKGROUND, "backgroundRed", Integer.parseInt(propBackgroundRed.getDefault())).set(propBackgroundRed.getInt());
                config.get(CATEGORY_NAME_BACKGROUND, "backgroundGreen", Integer.parseInt(propBackgroundGreen.getDefault())).set(propBackgroundGreen.getInt());
                config.get(CATEGORY_NAME_BACKGROUND, "backgroundBlue", Integer.parseInt(propBackgroundBlue.getDefault())).set(propBackgroundBlue.getInt());

                // Save the config
                config.save();
            } else
            {
                logger.warning("SubtitleDragGui: No config found, cannot save new values to the config.");
            }
        }
        isGuiOpen = false;
        super.onGuiClosed();
    }

    @Override
    public boolean doesGuiPauseGame()
    {

        return false;
    }
}
