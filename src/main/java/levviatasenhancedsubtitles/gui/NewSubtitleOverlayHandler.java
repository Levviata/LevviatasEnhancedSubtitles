package levviatasenhancedsubtitles.gui;

import com.google.common.collect.Lists;
import com.lukflug.panelstudio.base.IToggleable;
import com.lukflug.panelstudio.base.SimpleToggleable;
import com.lukflug.panelstudio.container.GUI;
import com.lukflug.panelstudio.mc12.MinecraftGUI;
import levviatasenhancedsubtitles.config.LESConfiguration;
import levviatasenhancedsubtitles.module.HUDEditorModule;
import levviatasenhancedsubtitles.setting.KeybindSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ISoundEventListener;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.Iterator;
import java.util.List;
@Mod.EventBusSubscriber
public class NewSubtitleOverlayHandler extends MinecraftGUI implements ISoundEventListener {
    private final Minecraft minecraft = Minecraft.getMinecraft();
    private static final List<Subtitle> subtitles = Lists.newArrayList();
    private boolean isListening;
    private int xTranslate, yTranslate;
    private int width, height;
    private int maxLength = 0;
    private final int halfMaxLength = maxLength / 2;
    private int buttonX;
    private int buttonY;
    private boolean dragging = false;
    private int lastMouseX;
    private int lastMouseY;
    private static int mouseXRef, mouseYRef;
    private static float partialTicksRef;
    public static final KeybindSetting keybindGUI=new KeybindSetting("Keybind","keybind","The key to toggle the module.",()->true, Keyboard.KEY_P);

    public static final KeybindSetting keybindHUD=new KeybindSetting("Keybind","keybind","The key to toggle the module.",()->true, Keyboard.KEY_P);
    IToggleable guiToggle=new SimpleToggleable(false) {
        @Override
        public boolean isOn() {
            return HUDEditorModule.showHUD.isOn();
        }
    };
    IToggleable hudToggle=new SimpleToggleable(false) {
        @Override
        public boolean isOn() {
            return guiToggle.isOn()? HUDEditorModule.showHUD.isOn():super.isOn();
        }
    };
    public static void clientPreInit() {
        MinecraftForge.EVENT_BUS.register(new NewSubtitleOverlayHandler());
    }

    public static void preInit() {

    }

    @SubscribeEvent(receiveCanceled = true)
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        // Check if it's the subtitles overlay being rendered
        if (event.getType() == RenderGameOverlayEvent.ElementType.SUBTITLES) {

            NewSubtitleOverlayHandler handler = new NewSubtitleOverlayHandler();
            event.setCanceled(true);
            handler.drawScreen(mouseXRef, mouseYRef, partialTicksRef);
        }
    }

    @Override
    protected void renderGUI() {
        drawButton();
    }
    /*@Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        partialTicksRef = partialTicks;
        mouseXRef = mouseX;
        mouseYRef = mouseY;
    }*/

    @Override
    protected int getScrollSpeed() {
        return 0;
    }

    private void drawButton() {
        ScaledResolution resolution = new ScaledResolution(minecraft);
        if (!this.isListening && minecraft.gameSettings.showSubtitles) {
            minecraft.getSoundHandler().addListener(this);
            this.isListening = true;
        } else if (this.isListening && !minecraft.gameSettings.showSubtitles) {
            minecraft.getSoundHandler().removeListener(this);
            this.isListening = false;
        }

        if (this.isListening && !subtitles.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            Vec3d playerPosition = new Vec3d(minecraft.player.posX, minecraft.player.posY + (double)minecraft.player.getEyeHeight(), minecraft.player.posZ);
            Vec3d zPlayerDirection = (new Vec3d(0.0D, 0.0D, -1.0D)).rotatePitch(-minecraft.player.rotationPitch * 0.017453292F).rotateYaw(-minecraft.player.rotationYaw * 0.017453292F);
            Vec3d yPlayerDirection = (new Vec3d(0.0D, 1.0D, 0.0D)).rotatePitch(-minecraft.player.rotationPitch * 0.017453292F).rotateYaw(-minecraft.player.rotationYaw * 0.017453292F);
            Vec3d vec3d3 = zPlayerDirection.crossProduct(yPlayerDirection);
            int maxLength = 0;
            Iterator<Subtitle> iterator = subtitles.iterator();

            while (iterator.hasNext())
            {
                Subtitle caption = iterator.next();

                if (caption.getStartTime() + 3000L <= Minecraft.getSystemTime())
                {
                    iterator.remove();
                }
                else
                {
                    maxLength = Math.max(maxLength, minecraft.fontRenderer.getStringWidth(caption.getString()));
                }
            }

            maxLength = maxLength + minecraft.fontRenderer.getStringWidth("<") + minecraft.fontRenderer.getStringWidth(" ") + minecraft.fontRenderer.getStringWidth(">") + minecraft.fontRenderer.getStringWidth(" ");

            int captionIndex = 0;
            for (Subtitle caption : subtitles)
            {
                // We get the contents of the current sent subtitle
                String Caption1 = caption.getString();

                Vec3d vec3d4 = caption.getLocation().subtract(playerPosition).normalize();
                double d0 = -vec3d3.dotProduct(vec3d4);
                double d1 = -zPlayerDirection.dotProduct(vec3d4);
                boolean flag = d1 > 0.5D;

                int halfMaxLength = maxLength / 2;

                int subtitleHeight = minecraft.fontRenderer.FONT_HEIGHT;
                int subtitleWidth = minecraft.fontRenderer.getStringWidth(Caption1);

                /*
                + Calculates the alpha value of the current caption based on the time it
                + has been on screen
                */
                int fadeAwayCalculation = MathHelper.floor(MathHelper.clampedLerp(255.0D, 75.0D, (float)(Minecraft.getSystemTime() - caption.getStartTime()) / 3000.0F));
                // Assuming caption.getStartTime() returns the time when the subtitle was first displayed
                long elapsedTime = Minecraft.getSystemTime() - caption.getStartTime();

                // User-defined starting and ending alpha values
                int startAlpha = 255; // Fully opaque
                int endAlpha = 75; // Fully transparent

                // Calculate the new alpha value based on the elapsed time
                int newAlpha = MathHelper.floor(MathHelper.clampedLerp(startAlpha, endAlpha, (float)elapsedTime / 3000.0F));

                // Ensure the new alpha value is clamped between 0 and 255
                newAlpha = MathHelper.clamp(newAlpha, 0, 255);

                // User-defined RGB color values
                int userRed = LESConfiguration.propFontRed.getInt();
                int userGreen = LESConfiguration.propFontGreen.getInt();
                int userBlue = LESConfiguration.propFontBlue.getInt();

                // Combine the new alpha value with the RGB color values
                int fadedColor = (newAlpha << 24) | (userRed << 16) | (userGreen << 8) | userBlue;


                int fadeAway = fadeAwayCalculation << 16 | fadeAwayCalculation << 8 | fadeAwayCalculation;


                int backgroundRed = LESConfiguration.propBackgroundRed.getInt();
                int backgroundGreen = LESConfiguration.propBackgroundGreen.getInt();
                int backgroundBlue = LESConfiguration.propBackgroundBlue.getInt();
                int backgroundAlpha = 255;

                int backgroundSubtitleColor = (backgroundAlpha << 24) | (backgroundRed << 16) | (backgroundGreen << 8) | backgroundBlue;

                int fontRed = LESConfiguration.propFontRed.getInt();
                int fontGreen = LESConfiguration.propFontGreen.getInt();
                int fontBlue = LESConfiguration.propFontBlue.getInt();
                int fontAlpha = 255;

                int fontSubtitleColor = (fontAlpha << 24) | (fontRed << 16) | (fontGreen << 8) | fontBlue;

                int fadeAwayWithColor = (fadeAwayCalculation << 16) | (fadeAwayCalculation << 8) | fadeAwayCalculation | (fontSubtitleColor & 0x00FFFFFF);

                GlStateManager.pushMatrix();

                //Change happens here

                String position = LESConfiguration.propOverlayPosition.getString();

                int verticalSpacing = 1;
                int horizontalSpacing = 2;
                int subtitleSpacing = 10;
                float xTranslate, yTranslate;

                switch (position) {
                    case "BOTTOM_CENTER":
                        xTranslate = (float) resolution.getScaledWidth() / 2;
                        yTranslate = (float) (resolution.getScaledHeight() - 75) - (float) (captionIndex * subtitleSpacing);
                        break;
                    case "BOTTOM_LEFT":
                        xTranslate = (float) halfMaxLength + horizontalSpacing;
                        yTranslate = (float) (resolution.getScaledHeight() - 30) - (float) (captionIndex * subtitleSpacing);
                        break;
                    case "CENTER_LEFT":
                        xTranslate = (float) halfMaxLength + horizontalSpacing;
                        yTranslate = (float) (resolution.getScaledHeight() / 2) - (float) (((subtitles.size() - 1) / 2) - captionIndex) * subtitleSpacing;
                        break;
                    case "TOP_LEFT":
                        xTranslate = (float) halfMaxLength + horizontalSpacing;
                        yTranslate = (float) (captionIndex * subtitleSpacing + 5 + verticalSpacing);
                        break;
                    case "TOP_CENTER":
                        xTranslate = (float) resolution.getScaledWidth() / 2;
                        yTranslate = (float) (captionIndex * subtitleSpacing + 5 + verticalSpacing);
                        break;
                    case "TOP_RIGHT":
                        xTranslate = (float) resolution.getScaledWidth() - (float) halfMaxLength - 2;
                        yTranslate = (float) (captionIndex * subtitleSpacing + 5 + verticalSpacing);
                        break;
                    case "CENTER_RIGHT":
                        xTranslate = (float) resolution.getScaledWidth() - (float) halfMaxLength - horizontalSpacing;
                        yTranslate = (float) (resolution.getScaledHeight() / 2) - (float) (((subtitles.size() - 1) / 2) - captionIndex) * subtitleSpacing;
                        break;
                    default: //if there's any invalid input just show it in the bottom right
                        xTranslate = (float) resolution.getScaledWidth() - (float) halfMaxLength - 2;
                        yTranslate = (float) (resolution.getScaledHeight() - 30) - (float) (captionIndex * subtitleSpacing);
                        break;
                }

                //"GlStateManager.translate" sets the position, parameters used: (x, y, z). Example of method with parameters: GlStateManager.translate(x, y, z)
                GlStateManager.translate(xTranslate - buttonX, yTranslate - buttonY, 0.0F);

                GlStateManager.scale(1.0F, 1.0F, 1.0F);

                drawRect(-halfMaxLength - 1, -subtitleHeight / 2 - 1, halfMaxLength + 1, subtitleHeight / 2 + 1, backgroundSubtitleColor);

                GlStateManager.enableBlend();

                if (!flag) {
                    if (d0 > 0.00D)
                    {
                        minecraft.fontRenderer.drawString(">", halfMaxLength - minecraft.fontRenderer.getStringWidth(">"), -subtitleHeight / 2, fadeAway);
                    }
                    else if (d0 < -0.00D)
                    {
                        minecraft.fontRenderer.drawString("<", -halfMaxLength, -subtitleHeight / 2, fadeAwayWithColor);
                    }
                }

                minecraft.fontRenderer.drawString(Caption1, -subtitleWidth / 2, -subtitleHeight / 2,  fadedColor);
                GlStateManager.popMatrix();
                ++captionIndex;
            }

            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }
    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) { // Left click
            if (isMouseOverButton(mouseX, mouseY)) {
                this.dragging = true;
                this.lastMouseX = mouseX;
                this.lastMouseY = mouseY;
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        this.dragging = false;
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (this.dragging) {
            this.buttonX += mouseX - this.lastMouseX;
            this.buttonY += mouseY - this.lastMouseY;
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
        }
    }

    private boolean isMouseOverButton(int mouseX, int mouseY) {
        return mouseX >= this.buttonX && mouseX < this.buttonX + this.width &&
                mouseY >= this.buttonY && mouseY < this.buttonY + this.height;
    }
        @Override
        public void soundPlay(ISound soundIn, SoundEventAccessor accessor){
            // Your custom implementation here
            if (accessor.getSubtitle() != null) {
                String subtitleText = accessor.getSubtitle().getFormattedText();

                if (!subtitles.isEmpty()) {
                    for (Subtitle caption : subtitles) {
                        if (caption.getString().equals(subtitleText)) {
                            caption.refresh(new Vec3d(soundIn.getXPosF(), soundIn.getYPosF(), soundIn.getZPosF()));
                            return;
                        }
                    }
                }
                subtitles.add(new Subtitle(subtitleText, new Vec3d(soundIn.getXPosF(), soundIn.getYPosF(), soundIn.getZPosF())));
            }
        }

    @Override
    protected GUI getGUI() {
        return null;
    }

    @Override
    protected GUIInterface getInterface() {
        return null;
    }

    @Override
    public void enterGUI() {
        super.enterGUI();
    }

    private static class Subtitle {
        private final String subtitle;
        private long startTime;
        private Vec3d location;

        public Subtitle(String subtitleIn, Vec3d locationIn) {
            this.subtitle = subtitleIn;
            this.location = locationIn;
            this.startTime = Minecraft.getSystemTime();
        }

        public String getString() {
            return this.subtitle;
        }

        public long getStartTime() {
            return this.startTime;
        }

        public Vec3d getLocation() {
            return this.location;
        }

        public void refresh(Vec3d locationIn) {
            this.location = locationIn;
            this.startTime = Minecraft.getSystemTime();
        }
    }
}
