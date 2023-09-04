package levviatasenhancedsubtitles.gui;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;

import levviatasenhancedsubtitles.OverlayPosition;
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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.IGuiHandler;
import org.lwjgl.opengl.Display;

public class GuiSubtitleOverlay extends Gui implements ISoundEventListener
{

    private final Minecraft client;
    private final List<Subtitle> subtitles = Lists.<Subtitle>newArrayList();
    private boolean enabled;

    public GuiSubtitleOverlay(Minecraft clientIn)
    {
        this.client = clientIn;
    }



    public static void preInit() {

    }


    public void renderSubtitles(ScaledResolution resolution)
    {
        if (!this.enabled && this.client.gameSettings.showSubtitles)
        {
            this.client.getSoundHandler().addListener((ISoundEventListener) this);
            this.enabled = true;
        }
        else if (this.enabled && !this.client.gameSettings.showSubtitles)
        {
            this.client.getSoundHandler().removeListener((ISoundEventListener) this);
            this.enabled = false;
        }

        if (this.enabled && !subtitles.isEmpty())
        {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            Vec3d vec3d = new Vec3d(this.client.player.posX, this.client.player.posY + (double)this.client.player.getEyeHeight(), this.client.player.posZ);
            Vec3d vec3d1 = (new Vec3d(0.0D, 0.0D, -1.0D)).rotatePitch(-this.client.player.rotationPitch * 0.017453292F).rotateYaw(-this.client.player.rotationYaw * 0.017453292F);
            Vec3d vec3d2 = (new Vec3d(0.0D, 1.0D, 0.0D)).rotatePitch(-this.client.player.rotationPitch * 0.017453292F).rotateYaw(-this.client.player.rotationYaw * 0.017453292F);
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
                    maxLength = Math.max(maxLength, this.client.fontRenderer.getStringWidth(guisubtitleoverlay$subtitle.getString()));
                }
            }

            maxLength = maxLength + this.client.fontRenderer.getStringWidth("<") + this.client.fontRenderer.getStringWidth(" ") + this.client.fontRenderer.getStringWidth(">") + this.client.fontRenderer.getStringWidth(" ");

            for (Subtitle guisubtitleoverlay$subtitle1 : this.subtitles)
            {
                // We get the contents of the current sent subtitle
                String Caption1 = guisubtitleoverlay$subtitle1.getString();

                Vec3d vec3d4 = guisubtitleoverlay$subtitle1.getLocation().subtract(vec3d).normalize();
                double d0 = -vec3d3.dotProduct(vec3d4);
                double d1 = -vec3d1.dotProduct(vec3d4);
                boolean flag = d1 > 0.5D;

                int halfMaxLength = maxLength / 2;

                int subtitleHeight = this.client.fontRenderer.FONT_HEIGHT;
                int subtitleWidth = this.client.fontRenderer.getStringWidth(Caption1);

                int l1 = MathHelper.floor(MathHelper.clampedLerp(255.0D, 75.0D, (float)(Minecraft.getSystemTime() - guisubtitleoverlay$subtitle1.getStartTime()) / 3000.0F));
                int textColor = l1 << 16 | l1 << 8 | l1;
                GlStateManager.pushMatrix();

                //Change happens here

                OverlayPosition position = LESConfiguration.overlayPosition;

                float xTranslate, yTranslate;

                int verticalSpacing = 10;
                switch (position) {
                    case BOTTOM_CENTER:
                        xTranslate = (float) Display.getWidth() / 2;
                        yTranslate = (float) (Display.getHeight() - 50) - (float) (captionIndex * verticalSpacing);
                        break;
                    case BOTTOM_LEFT:
                        xTranslate = (float) halfMaxLength;
                        yTranslate = (float) (Display.getHeight() - 30) - (float) (captionIndex * verticalSpacing);
                        break;
                    case CENTER_LEFT:
                        xTranslate = (float) halfMaxLength;
                        yTranslate = (float) (Display.getHeight() / 2) - (float) (captionIndex * verticalSpacing - 10);
                        break;
                    case TOP_LEFT:
                        xTranslate = (float) halfMaxLength;
                        yTranslate = (float) (captionIndex * verticalSpacing + 5);
                        break;
                    case TOP_CENTER:
                        xTranslate = (float) Display.getWidth() / 2;
                        yTranslate = (float) (captionIndex * verticalSpacing + 5);
                        break;
                    case TOP_RIGHT:
                        xTranslate = (float) Display.getWidth() - (float) halfMaxLength;
                        yTranslate = (float) (captionIndex * verticalSpacing + 5);
                        break;
                    case CENTER_RIGHT:
                        xTranslate = (float) Display.getWidth() - (float) halfMaxLength;
                        yTranslate = (float) (Display.getHeight() / 2) - (float) (((this.subtitles.size() - 1) / 2) - captionIndex) * verticalSpacing;
                        break;
                    default: //if there's any invalid input just show it in the bottom right
                        xTranslate = (float) Display.getWidth() - (float) halfMaxLength - 2.0F;
                        yTranslate = (float) (Display.getHeight() - 30) - (float) (captionIndex * verticalSpacing + 5);
                        break;
                }

                //"GlStateManager.translate" sets the position, parameters used: (x, y, z). Example of method with parameters: GlStateManager.translate(x, y, z)
                GlStateManager.translate(xTranslate, yTranslate, 0.0F);

                GlStateManager.scale(1.0F, 1.0F, 1.0F);
                drawRect(-halfMaxLength - 1, -subtitleHeight / 2 - 1, halfMaxLength + 1, subtitleHeight / 2 + 1, -872415232);
                GlStateManager.enableBlend();

                if (!flag)
                {
                    if (d0 > 0.0D)
                    {
                        this.client.fontRenderer.drawString(">", halfMaxLength - this.client.fontRenderer.getStringWidth(">"), -subtitleHeight / 2, textColor + -16777216);
                    }
                    else if (d0 < 0.0D)
                    {
                        this.client.fontRenderer.drawString("<", -halfMaxLength, -subtitleHeight / 2, textColor + -16777216);
                    }
                }

                this.client.fontRenderer.drawString(Caption1, -subtitleWidth / 2, -subtitleHeight / 2, textColor + -16777216);
                GlStateManager.popMatrix();
                ++captionIndex;
            }

            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    public void soundPlay(ISound soundIn, SoundEventAccessor accessor)
    {
        if (accessor.getSubtitle() != null)
        {
            String s = accessor.getSubtitle().getFormattedText();

            if (!this.subtitles.isEmpty())
            {
                for (Subtitle guisubtitleoverlay$subtitle : this.subtitles)
                {
                    if (guisubtitleoverlay$subtitle.getString().equals(s))
                    {
                        guisubtitleoverlay$subtitle.refresh(new Vec3d((double)soundIn.getXPosF(), (double)soundIn.getYPosF(), (double)soundIn.getZPosF()));
                        return;
                    }
                }
            }

            this.subtitles.add(new Subtitle(s, new Vec3d((double) soundIn.getXPosF(), (double) soundIn.getYPosF(), (double) soundIn.getZPosF())));
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
