package les.gui;

import com.google.common.collect.Lists;
import les.config.LESConfiguration;
import les.utils.ColorConverter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.common.config.Configuration;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import static les.config.LESConfiguration.*;
import static les.gui.SubtitleOverlayHandler.*;

public class SubtitleDragGui extends GuiScreen {
    private boolean isButtonPressed = false;
    private static final List<SubtitleOverlayHandler.Subtitle> previewSubtitles = Lists.newArrayList();
    static {
        previewSubtitles.add(new SubtitleOverlayHandler.Subtitle("Example Subtitle", new Vec3d(0, 0, 0)));
        previewSubtitles.add(new SubtitleOverlayHandler.Subtitle("Big ol' Example Subtitle", new Vec3d(0, 0, 0)));
        previewSubtitles.add(new SubtitleOverlayHandler.Subtitle("Hi from Example Subtitle 9876", new Vec3d(0, 0, 0)));
        previewSubtitles.add(new SubtitleOverlayHandler.Subtitle("Example Subtitle 12345 Example Subtitle", new Vec3d(0, 0, 0)));
    }
    public static boolean isGuiOpen = false;
    private boolean dragging;
    private int lastMouseX;
    private int lastMouseY;
    private boolean initialShowSubtitles;
    private int initialScale;
    private int initialBackgroundAlpha;
    private int initialIndex;
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

    private Logger logger = Logger.getLogger("SubtitleDragGui");

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        //Store values for later
        initialShowSubtitles = propShowSubtitles.getBoolean();
        initialScale = propSubtitleScale.getInt();
        initialBackgroundAlpha = propBackgroundAlpha.getInt();
        initialIndex = propIndex.getInt();

        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        buttonList.add(new GuiButton(1, res.getScaledWidth() / 2 - 100, 20, TextFormatting.YELLOW + "Mod Status: " +
                (propShowSubtitles.getBoolean(true) ?  TextFormatting.DARK_GREEN + "Enabled" : TextFormatting.DARK_RED + "Disabled")));

        GuiButton scale = new GuiSlider(new GuiPageButtonList.GuiResponder() {
            @Override
            public void setEntryValue(int id, boolean value) {
            }
            @Override
            public void setEntryValue(int id, float value) {
                if (id == 3) {
                    propSubtitleScale.set((int) value);
                } else if (id == 4) {
                    propBackgroundAlpha.set((int) value);
                }
            }

            @Override
            public void setEntryValue(int id, String value) {
            }

        }, 3, res.getScaledWidth() / 2 - 100, 45,
                "Scale: ", 0.1f, 10, initialScale,
                (id, name, value) -> "Scale: "+ propSubtitleScale.getInt() + "x"
        );
        scale.width = 200;
        buttonList.add(scale);
        GuiButton alpha = new GuiSlider(new GuiPageButtonList.GuiResponder() {
            @Override
            public void setEntryValue(int id, boolean value) {
            }

            @Override
            public void setEntryValue(int id, float value) {
                if (id == 4) {
                    propBackgroundAlpha.set((int) value);
                }
            }

            @Override
            public void setEntryValue(int id, String value) {
            }
        }, 4, res.getScaledWidth() / 2 - 100, 70,
                "Alpha: ", 0, 255, initialBackgroundAlpha,
                (id, name, value) -> "Alpha: " + propBackgroundAlpha.getInt() + "%"
        );
        alpha.width = 200;
        buttonList.add(alpha);

        GuiButton overlayPosition = new GuiButton(6,
                res.getScaledWidth() / 2 - 100,
                95,
                 "Overlay Position: " + TextFormatting.YELLOW + propOverlayPosition.getString());
        buttonList.add(overlayPosition);

        GuiButton showButtons = new GuiButton(7, res.getScaledWidth() / 2 - 100, 120, TextFormatting.YELLOW +"Clear Buttons");
        buttonList.add(showButtons);

        buttonList.add(new GuiButton(5,
                res.getScaledWidth() / 2 - 100,
                145,
                TextFormatting.YELLOW + "Set Values To Default"));
    }


    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        try {
            super.mouseClicked(mouseX, mouseY, button);
        } catch (IOException ignored) {
        }

        if (button == 0) {
            for (GuiButton guiButton : buttonList) {
                if (guiButton.isMouseOver()) return;
            }
            this.dragging = true;
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int action) {
        super.mouseReleased(mouseX, mouseY, action);
        this.dragging = false;
        Configuration config = LESConfiguration.getConfig();
        config.save();
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (this.dragging) {
            int diff = mouseX - this.lastMouseX;
            int xPos =  propXposition.getInt();
            int yPos =  propYposition.getInt();
            xPos = xPos + diff;
            propXposition.set(xPos);
            yPos = yPos + (mouseY - this.lastMouseY);
            propYposition.set(yPos);
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
        }
    }
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == null) return;
        switch (button.id) {
            case 1: {
                boolean getShowSubtitles = propShowSubtitles.getBoolean();
                getShowSubtitles = !getShowSubtitles;
                propShowSubtitles.set(getShowSubtitles);
                button.displayString = "Mod Status: " + (propShowSubtitles.getBoolean() ? TextFormatting.DARK_GREEN + "Enabled" : TextFormatting.DARK_RED + "Disabled");
                break;
            }
            case 5: {
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
                config.get(CATEGORY_NAME_GENERAL, "showSubtitles", true).set(true);
                config.get(CATEGORY_NAME_GENERAL, "subtitleScale", 1D).set(1D);
                config.get(CATEGORY_NAME_BACKGROUND, "backgroundAlpha", 255).set(255);
                config.get(CATEGORY_NAME_POSITION, "overlayPosition", "BOTTOM_RIGHT").set("BOTTOM_RIGHT");
                propIndex.set(0);
                config.save();
                buttonList.clear();
                initGui();
                break;
            }
            case 6: {
                int index = propIndex.getInt();
                index++; // Increment index first
                propIndex.set(index);
                if (index >= POSITION_CHOICES.length) {
                    // Reset index to 0 when it reaches the end of the array
                    index = 0;
                    propIndex.set(index);
                }
                // Now set the overlay position using the updated index
                propOverlayPosition.set(POSITION_CHOICES[index]);
                propXposition.set(0);
                propYposition.set(0);
                button.displayString = "Overlay Position: " + POSITION_CHOICES[index];
                break;
            }
            case 7: {
                buttonList.clear();
                if (!propDisablePopup.getBoolean()) {
                    ITextComponent message = new TextComponentString("You seem to have disabled your GUI buttons, close and open your GUI again to re-enable them. ")
                            .setStyle(new Style().setColor(TextFormatting.DARK_GRAY).setItalic(true));
                    ITextComponent clickable = new TextComponentString("Close your GUI and then click here to disable this message")
                            .setStyle(new Style().setColor(TextFormatting.DARK_GRAY).setItalic(true).setUnderlined(true).setBold(true)
                                    .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/resetbuttons"))
                                    .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to disable this message popup"))));
                    message.appendSibling(clickable);
                    Minecraft.getMinecraft().player.sendMessage(message);
                }
                break;
            }
            /*case 6: {
                LESConfiguration.getConfig().getCategory(CATEGORY_NAME_GENERAL).get("showSubtitles").set(propShowSubtitles.getBoolean());
                LESConfiguration.getConfig().getCategory(CATEGORY_NAME_GENERAL).get("subtitleScale").set(propSubtitleScale.getInt());
                LESConfiguration.getConfig().getCategory(CATEGORY_NAME_BACKGROUND).get("backgroundAlpha").set(propBackgroundAlpha.getInt());
                LESConfiguration.getConfig().save();
                Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Changes applied!"));
                break;
            }*/
        }
    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        int maxLength = 0;
        int captionIndex = 0;
        Iterator<Subtitle> iterator = previewSubtitles.iterator();
            while (iterator.hasNext()) {
                SubtitleOverlayHandler.Subtitle caption = iterator.next();
                maxLength = Math.max(maxLength, mc.fontRenderer.getStringWidth(previewSubtitles.get(3).getString()));
            }
        for (SubtitleOverlayHandler.Subtitle caption : previewSubtitles) {
            boolean showSubtitles = getConfig().get(CATEGORY_NAME_GENERAL, "showSubtitles", true).getBoolean();
            if (showSubtitles) {
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

                switch (position) {
                    case "BOTTOM_CENTER":
                        xPos += (float) resolution.getScaledWidth() / 2;
                        yPos += ((resolution.getScaledHeight() - 75) - (captionIndex * subtitleSpacing));
                        break;
                    case "BOTTOM_LEFT":
                        xPos += halfMaxLength + horizontalSpacing;
                        yPos += ((resolution.getScaledHeight() - 30) - (captionIndex * subtitleSpacing));
                        break;
                    case "CENTER_LEFT":
                        xPos += halfMaxLength + horizontalSpacing;
                        yPos += (((float) resolution.getScaledHeight() / 2) - (((float) (previewSubtitles.size() - 1) / 2) - captionIndex) * subtitleSpacing);
                        break;
                    case "TOP_LEFT":
                        xPos += halfMaxLength + horizontalSpacing;
                        yPos += (captionIndex * subtitleSpacing + 5 + verticalSpacing);
                        break;
                    case "TOP_CENTER":
                        xPos += (float) resolution.getScaledWidth() / 2;
                        yPos += (captionIndex * subtitleSpacing + 5 + verticalSpacing);
                        break;
                    case "TOP_RIGHT":
                        xPos += resolution.getScaledWidth() - halfMaxLength - 2;
                        yPos += (captionIndex * subtitleSpacing + 5 + verticalSpacing);
                        break;
                    case "CENTER_RIGHT":
                        xPos += resolution.getScaledWidth() - halfMaxLength - horizontalSpacing;
                        yPos += (((float) resolution.getScaledHeight() / 2) - (((float) (previewSubtitles.size() - 1) / 2) - captionIndex) * subtitleSpacing);
                        break;
                    default: //if there's any invalid input just show it in the bottom right
                        xPos += resolution.getScaledWidth() - halfMaxLength - 2;
                        yPos += ((resolution.getScaledHeight() - 30) - (captionIndex * subtitleSpacing));
                        break;
                }
                xPos = MathHelper.clamp(xPos, 0, resolution.getScaledWidth() - ((float) subtitleWidth / 2));
                yPos = MathHelper.clamp(yPos, 0, resolution.getScaledHeight() - ((float) subtitleHeight / 2));

                GlStateManager.translate(xPos, yPos, 0);

                GlStateManager.scale((float)propSubtitleScale.getDouble(), (float) propSubtitleScale.getDouble(), 1.0F);

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
    public void onGuiClosed() {
        if (initialShowSubtitles != propShowSubtitles.getBoolean() ||
            initialScale != (float) propSubtitleScale.getDouble()  ||
            initialBackgroundAlpha != propBackgroundAlpha.getInt() ||
            initialIndex != propIndex.getInt()
            ) {
            // Values have changed, save the changes to the config

            Configuration config = LESConfiguration.getConfig();
            if (config != null) {
                // Set the new values
                config.get(CATEGORY_NAME_GENERAL, "index", 0).set(propIndex.getInt());
                config.get(CATEGORY_NAME_GENERAL, "showSubtitles", true).set(propShowSubtitles.getBoolean());
                config.get(CATEGORY_NAME_GENERAL, "subtitleScale", 1f).set(propSubtitleScale.getDouble());
                config.get(CATEGORY_NAME_BACKGROUND, "backgroundAlpha", 255).set(propBackgroundAlpha.getInt());

                // Save the config
                config.save();
            } else {
                logger.info("SubtitleDragGui: No config found, cannot save values");
            }
        }
        isGuiOpen = false;
        super.onGuiClosed();
    }
    @Override
    public boolean doesGuiPauseGame() {

        return false;
    }
}
