package levviatasenhancedsubtitles.gui;

import com.google.common.collect.Lists;
import levviatasenhancedsubtitles.LES;
import levviatasenhancedsubtitles.config.LESConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import static levviatasenhancedsubtitles.config.LESConfiguration.*;

public class SubtitleDragGui extends GuiScreen {
    private boolean isButtonPressed = false;
    private static final List<SubtitleOverlayHandler.Subtitle> previewSubtitles = Lists.newArrayList();
    static {
        previewSubtitles.add(new SubtitleOverlayHandler.Subtitle("Example Subtitle", new Vec3d(0, 0, 0)));
        previewSubtitles.add(new SubtitleOverlayHandler.Subtitle("Big ol' Example Subtitle", new Vec3d(0, 0, 0)));
        previewSubtitles.add(new SubtitleOverlayHandler.Subtitle("Longgggggggggggggggggggggggg Boy Example Subtitle", new Vec3d(0, 0, 0)));
        previewSubtitles.add(new SubtitleOverlayHandler.Subtitle("Astronomoussssssssssssssssssssssssssssssss Example Subtitle", new Vec3d(0, 0, 0)));
    }
    public static boolean isGuiOpen = false;
    private boolean dragging;
    private int lastMouseX;
    private int lastMouseY;
    private int getValueAndSetValue;
    public void setGetValueAndSetValue(int inInt) {
        this.getValueAndSetValue = inInt;
    }
    private Logger logger = Logger.getLogger("SubtitleDragGui");

    @Override
    public void initGui() {
        super.initGui();
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        buttonList.add(new GuiButton(1, res.getScaledWidth() / 2 - 100, 20, "Mod Status: " +
                (propShowSubtitles.getBoolean(true) ? "Enabled" : "Disabled")));
        buttonList.add(new GuiSlider(new GuiPageButtonList.GuiResponder() {
            @Override
            public void setEntryValue(int id, boolean value) {

            }
            @Override
            public void setEntryValue(int id, float value) {
                value = propScale.getInt();
            }

            @Override
            public void setEntryValue(int id, String value) {

            }

        }, 3, res.getScaledWidth() / 2 - 100, 40,
                "Scale: ", 0, 10, 1,
                new GuiSlider.FormatHelper() {

                    @Override
                    public String getText(int id, String name, float value) {
                        propScale.set(value);
                        return "Scale: " + value + "x";
                    }
                }
                ));


        buttonList.add(new GuiSlider(new GuiPageButtonList.GuiResponder() {
            @Override
            public void setEntryValue(int id, boolean value) {

            }

            @Override
            public void setEntryValue(int id, float value) {
                value = propBackgroundAlpha.getInt();
            }

            @Override
            public void setEntryValue(int id, String value) {

            }
        }, 4, res.getScaledWidth() / 2 - 100, 80,
                "Alpha: ", 0, 255, 255,
                new GuiSlider.FormatHelper() {
                    @Override
                    public String getText(int id, String name, float value) {
                        propBackgroundAlpha.set(value);
                        return "Alpha: " + value + "%";
                    }
                }));


        buttonList.add(new GuiButton(5, res.getScaledWidth() / 2 - 100, 120, "Set Values To Default"));
        buttonList.add(new GuiButton(6, res.getScaledWidth() / 2 - 100, 100, "Apply Changes"));
    }
    @Override
    public void updateScreen() {
        super.updateScreen();
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
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (this.dragging) {
            int diff = mouseX - this.lastMouseX;
            xPosition = xPosition + diff;
            yPosition = yPosition + (mouseY - this.lastMouseY);
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
                button.displayString = "Mod Status: " + (propShowSubtitles.getBoolean() ? "Enabled" : "Disabled");
                break;
            }
            case 5: {
                boolean setShowSubtitles = true;
                propShowSubtitles.set(setShowSubtitles);
                xPosition = 5;
                yPosition = 5;
                int setScale = 1;
                propScale.set(setScale);
                int setBackgroundAlpha = 255;
                propBackgroundAlpha.set(setBackgroundAlpha);
                getConfig().save();
                buttonList.clear();
                initGui();
                break;
            }
            case 6: {
                Configuration config = LESConfiguration.getConfig();

                if (config == null) {
                    logger.info("SubtitleDragGui: No config found, cannot save values");
                    return;
                }
                break;
            }
        }
    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }


    @Override
    public void onGuiClosed() {
        getConfig().save();
        isGuiOpen = false;
        super.onGuiClosed();
    }
    @Override
    public boolean doesGuiPauseGame() {

        return false;
    }
}
