package levviatasenhancedsubtitles.gui;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;

import levviatasenhancedsubtitles.config.LESConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiSubtitleOverlay;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;

public class SubtitleOverlayHandler extends GuiSubtitleOverlay
{


    public SubtitleOverlayHandler(Minecraft clientIn, Minecraft client) {
        super(clientIn);
        this.minecraft = client;
    }

    public static void clientPreInit() {
        SubtitleSoundHandler customSoundListener = new SubtitleSoundHandler();
        MinecraftForge.EVENT_BUS.register(customSoundListener);
    }
    public static void preInit() {

    }
    private final Minecraft minecraft;
    static final List<Subtitle> subtitles = Lists.newArrayList();
    private boolean enabled;

    public void renderSubtitles(ScaledResolution scaledResolution)
    {
        super.renderSubtitles(scaledResolution);
        if (!this.enabled && minecraft.gameSettings.showSubtitles)
        {
            minecraft.getSoundHandler().addListener(this);
            this.enabled = true;
        }
        else if (this.enabled && !minecraft.gameSettings.showSubtitles)
        {
            minecraft.getSoundHandler().removeListener(this);
            this.enabled = false;
        }

        if (this.enabled && !subtitles.isEmpty())
        {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            Vec3d vec3d = new Vec3d(minecraft.player.posX, minecraft.player.posY + (double)minecraft.player.getEyeHeight(), minecraft.player.posZ);
            Vec3d vec3d1 = (new Vec3d(0.0D, 0.0D, -1.0D)).rotatePitch(-minecraft.player.rotationPitch * 0.017453292F).rotateYaw(-minecraft.player.rotationYaw * 0.017453292F);
            Vec3d vec3d2 = (new Vec3d(0.0D, 1.0D, 0.0D)).rotatePitch(-minecraft.player.rotationPitch * 0.017453292F).rotateYaw(-minecraft.player.rotationYaw * 0.017453292F);
            Vec3d vec3d3 = vec3d1.crossProduct(vec3d2);
            int captionIndex = 0;
            int maxLength = 0;
            Iterator<Subtitle> iterator = subtitles.iterator();

            while (iterator.hasNext())
            {
                Subtitle guisubtitleoverlay$subtitle = iterator.next();

                if (guisubtitleoverlay$subtitle.getStartTime() + 3000L <= Minecraft.getSystemTime())
                {
                    iterator.remove();
                }
                else
                {
                    maxLength = Math.max(maxLength, minecraft.fontRenderer.getStringWidth(guisubtitleoverlay$subtitle.getString()));
                }
            }

            maxLength = maxLength + minecraft.fontRenderer.getStringWidth("<") + minecraft.fontRenderer.getStringWidth(" ") + minecraft.fontRenderer.getStringWidth(">") + minecraft.fontRenderer.getStringWidth(" ");

            for (Subtitle guisubtitleoverlay$subtitle1 : subtitles)
            {
                // We get the contents of the current sent subtitle
                String Caption1 = guisubtitleoverlay$subtitle1.getString();

                Vec3d vec3d4 = guisubtitleoverlay$subtitle1.getLocation().subtract(vec3d).normalize();
                double d0 = -vec3d3.dotProduct(vec3d4);
                double d1 = -vec3d1.dotProduct(vec3d4);
                boolean flag = d1 > 0.5D;

                int halfMaxLength = maxLength / 2;

                int subtitleHeight = minecraft.fontRenderer.FONT_HEIGHT;
                int subtitleWidth = minecraft.fontRenderer.getStringWidth(Caption1);

                int l1 = MathHelper.floor(MathHelper.clampedLerp(255.0D, 75.0D, (float)(Minecraft.getSystemTime() - guisubtitleoverlay$subtitle1.getStartTime()) / 3000.0F));
                int textColor = l1 << 16 | l1 << 8 | l1;
                GlStateManager.pushMatrix();

                //Change happens here

                String position = LESConfiguration.overlayPosition;

                int verticalSpacing = 10;
                float xTranslate = (float) minecraft.displayWidth - (float) halfMaxLength;
                float yTranslate = (float) (minecraft.displayHeight / 2) - (float) (((subtitles.size() - 1) / 2) - captionIndex) * verticalSpacing;

                /*switch (position) {
                    case "BOTTOM_CENTER":
                        xTranslate = (float) Display.getWidth() / 2;
                        yTranslate = (float) (Display.getHeight() - 50) - (float) (captionIndex * verticalSpacing);
                        break;
                    case "BOTTOM_LEFT":
                        xTranslate = (float) halfMaxLength;
                        yTranslate = (float) (Display.getHeight() - 30) - (float) (captionIndex * verticalSpacing);
                        break;
                    case "CENTER_LEFT":
                        xTranslate = (float) halfMaxLength;
                        yTranslate = (float) (Display.getHeight() / 2) - (float) (captionIndex * verticalSpacing - 10);
                        break;
                    case "TOP_LEFT":
                        xTranslate = (float) halfMaxLength;
                        yTranslate = (float) (captionIndex * verticalSpacing + 5);
                        break;
                    case "TOP_CENTER":
                        xTranslate = (float) Display.getWidth() / 2;
                        yTranslate = (float) (captionIndex * verticalSpacing + 5);
                        break;
                    case "TOP_RIGHT":
                        xTranslate = (float) Display.getWidth() - (float) halfMaxLength;
                        yTranslate = (float) (captionIndex * verticalSpacing + 5);
                        break;
                    case "CENTER_RIGHT":
                        xTranslate = (float) Display.getWidth() - (float) halfMaxLength;
                        yTranslate = (float) (Display.getHeight() / 2) - (float) (((this.subtitles.size() - 1) / 2) - captionIndex) * verticalSpacing;
                        break;
                    default: //if there's any invalid input just show it in the bottom right
                        xTranslate = (float) Display.getWidth() - (float) halfMaxLength - 2.0F;
                        yTranslate = (float) (Display.getHeight() - 30) - (float) (captionIndex * verticalSpacing + 5);
                        break;
                }*/

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
                drawRect(-halfMaxLength - 1, -subtitleHeight / 2 - 1, halfMaxLength + 1, subtitleHeight / 2 + 1, -872415232);

                GlStateManager.enableBlend();

                if (!flag)
                {
                    if (d0 > 0.0D)
                    {
                        minecraft.fontRenderer.drawString(">", halfMaxLength - minecraft.fontRenderer.getStringWidth(">"), -subtitleHeight / 2, textColor + -16777216);
                    }
                    else if (d0 < 0.0D)
                    {
                        minecraft.fontRenderer.drawString("<", -halfMaxLength, -subtitleHeight / 2, textColor + -16777216);
                    }
                }

                minecraft.fontRenderer.drawString(Caption1, -subtitleWidth / 2, -subtitleHeight / 2, textColor + -16777216);
                GlStateManager.popMatrix();
                ++captionIndex;
            }

            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
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