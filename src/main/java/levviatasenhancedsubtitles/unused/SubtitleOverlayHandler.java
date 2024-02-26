package levviatasenhancedsubtitles.unused;

import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;

import levviatasenhancedsubtitles.config.LESConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ISoundEventListener;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class SubtitleOverlayHandler extends Gui implements ISoundEventListener
{

    private final Minecraft minecraft = Minecraft.getMinecraft();
    static final List<Subtitle> subtitles = Lists.newArrayList();
    private boolean isListening;

    public static void clientPreInit()
    {
        MinecraftForge.EVENT_BUS.register(new SubtitleOverlayHandler());
    }
    public static void preInit()
    {

    }
    @SubscribeEvent(receiveCanceled = true)
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        // Check if it's the subtitles overlay being rendered
        if (event.getType() == RenderGameOverlayEvent.ElementType.SUBTITLES) {

            SubtitleOverlayHandler handler = new SubtitleOverlayHandler();
            event.setCanceled(true);
            handler.render(event.getResolution());

        }
    }

    public void render(ScaledResolution resolution)
    {
        if (!this.isListening && minecraft.gameSettings.showSubtitles)
        {
            minecraft.getSoundHandler().addListener(this);
            this.isListening = true;
        }
        else if (this.isListening && !minecraft.gameSettings.showSubtitles)
        {
            minecraft.getSoundHandler().removeListener(this);
            this.isListening = false;
        }

        if (this.isListening && !subtitles.isEmpty())
        {
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

                // Now you can use fadedColor as the color for your subtitles

                int fadeAway = fadeAwayCalculation << 16 | fadeAwayCalculation << 8 | fadeAwayCalculation;


                int backgroundRed = LESConfiguration.propBackgroundRed.getInt();
                int backgroundGreen = LESConfiguration.propBackgroundGreen.getInt();
                int backgroundBlue = LESConfiguration.propBackgroundBlue.getInt();
                int backgroundAlpha = 255;
                //int backgroundSubtitleAlphaCalculation = LESConfiguration.propBackgroundAlpha.getInt();

                // Combine the red, green, and blue components into a single decimal color value
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
                GlStateManager.translate(xTranslate, yTranslate, 0.0F);

                GlStateManager.scale(1.0F, 1.0F, 1.0F);

                /*"drawRect" Draws a rectangle with the specified inputs and also the color of it, or in more complex words:

                The drawRect method in the provided code is responsible for drawing a filled rectangle on the screen. This method takes in the coordinates and color as parameters to define the size and appearance of the rectangle.

                Here's the code snippet that calls the drawRect method:

                drawRect(-halfMaxLength - 1, -subtitleHeight / 2 - 1, halfMaxLength + 1, subtitleHeight / 2 + 1, -872415232);
                The drawRect method is called with the following parameters:

                    x1: -halfMaxLength - 1
                    y1: -subtitleHeight / 2 - 1
                    x2: halfMaxLength + 1
                    y2: subtitleHeight / 2 + 1
                    color: -872415232
                These parameters specify the position and dimensions of the rectangle to be drawn. The x1 and y1 coordinates represent the top-left corner of the rectangle, while x2 and y2 represent the bottom-right corner. The color parameter determines the color of the filled rectangle.

                In summary, the drawRect method is used in the provided code to draw a filled rectangle on the screen with specified dimensions and color.*/
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
                    /*else if (d0 <= 0.00D || d0 >= -0.00D)
                    {
                        minecraft.fontRenderer.drawString("B", halfMaxLength - minecraft.fontRenderer.getStringWidth("B"), -subtitleHeight / 2, fadeAway + 16777216);
                        minecraft.fontRenderer.drawString("B", halfMaxLength, -subtitleHeight / 2, fadeAway + 16777216);
                    }
                } else if (d0 <= 0.01D || d0 >= -0.01D) {
                    minecraft.fontRenderer.drawString("F", halfMaxLength - minecraft.fontRenderer.getStringWidth("B"), -subtitleHeight / 2, fadeAway + 16777216);
                    minecraft.fontRenderer.drawString("F", -halfMaxLength, -subtitleHeight / 2, fadeAway + 16777216);*/
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
    public void soundPlay(ISound soundIn, SoundEventAccessor accessor) {
        // Your custom implementation here
        if (accessor.getSubtitle() != null) {
            String subtitleText = accessor.getSubtitle().getFormattedText();

            if (!SubtitleOverlayHandler.subtitles.isEmpty()) {
                for (Subtitle caption : SubtitleOverlayHandler.subtitles) {
                    if (caption.getString().equals(subtitleText)) {
                        caption.refresh(new Vec3d(soundIn.getXPosF(), soundIn.getYPosF(), soundIn.getZPosF()));
                        return;
                    }
                }
            }
            SubtitleOverlayHandler.subtitles.add(new Subtitle(subtitleText, new Vec3d(soundIn.getXPosF(), soundIn.getYPosF(), soundIn.getZPosF())));
        }
    }
    public static class Subtitle
    {
        private final String subtitle;
        private long startTime;
        private Vec3d location;

        public Subtitle(String subtitleIn, Vec3d locationIn)
        {
            this.subtitle = subtitleIn;
            this.location = locationIn;
            this.startTime = Minecraft.getSystemTime();
        }

        public String getString()
        {
            return this.subtitle;
        }

        public long getStartTime()
        {
            return this.startTime;
        }

        public Vec3d getLocation()
        {
            return this.location;
        }

        public void refresh(Vec3d locationIn)
        {
            this.location = locationIn;
            this.startTime = Minecraft.getSystemTime();
        }
    }
}
