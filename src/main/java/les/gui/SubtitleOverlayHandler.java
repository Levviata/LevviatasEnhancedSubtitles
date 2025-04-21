package les.gui;

import com.google.common.collect.Lists;
import les.config.LESConfiguration;
import les.utils.ColorConverter;
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
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Iterator;
import java.util.List;

import static les.config.LESConfiguration.*;
import static les.gui.SubtitleDragGui.isGuiOpen;

public class SubtitleOverlayHandler extends Gui implements ISoundEventListener
{
    private final Minecraft mc = Minecraft.getMinecraft();
    private boolean isListening;
    public static final List<Subtitle> subtitles = Lists.newArrayList();
    private static final List<SubtitleOverlayHandler.Subtitle> previewSubtitles = Lists.newArrayList();
    static {
        previewSubtitles.add(new SubtitleOverlayHandler.Subtitle("Example Subtitle", new Vec3d(0, 0, 0)));
        previewSubtitles.add(new SubtitleOverlayHandler.Subtitle("Big ol' Example Subtitle", new Vec3d(0, 0, 0)));
        previewSubtitles.add(new SubtitleOverlayHandler.Subtitle("Hi from Example Subtitle 9876", new Vec3d(0, 0, 0)));
        previewSubtitles.add(new SubtitleOverlayHandler.Subtitle("Example Subtitle 12345 Example Subtitle", new Vec3d(0, 0, 0)));
    }
    public static int lastPosX;
    public static int lastPosY;
    @SubscribeEvent(receiveCanceled = true)
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        // Check if it's the subtitles overlay being rendered
        if (event.getType() == RenderGameOverlayEvent.ElementType.SUBTITLES) {
            // Instantte your handler with the necessary context if required
            event.setCanceled(true);
            render();
        }
    }
    public void render() {
        boolean showSubtitles = getConfig().get(CATEGORY_NAME_GENERAL, "showSubtitles", true).getBoolean();
        if (showSubtitles && !isGuiOpen) {
        ScaledResolution resolution = new ScaledResolution(mc);
        if (!this.isListening && mc.gameSettings.showSubtitles) {
            mc.getSoundHandler().addListener(this);
            this.isListening = true;
        } else if (this.isListening && !mc.gameSettings.showSubtitles) {
            mc.getSoundHandler().removeListener(this);
            this.isListening = false;
        }

        if (this.isListening && !subtitles.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            Vec3d playerPosition = new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ);
            Vec3d zPlayerDirection = (new Vec3d(0.0D, 0.0D, -1.0D)).rotatePitch(-mc.player.rotationPitch * 0.017453292F).rotateYaw(-mc.player.rotationYaw * 0.017453292F);
            Vec3d yPlayerDirection = (new Vec3d(0.0D, 1.0D, 0.0D)).rotatePitch(-mc.player.rotationPitch * 0.017453292F).rotateYaw(-mc.player.rotationYaw * 0.017453292F);
            Vec3d vec3d3 = zPlayerDirection.crossProduct(yPlayerDirection);
            int maxLength = 0;
            Iterator<Subtitle> iterator = subtitles.iterator();
            if (!isGuiOpen) {
                while (iterator.hasNext()) {
                    SubtitleOverlayHandler.Subtitle caption = iterator.next();
                    if (caption.getStartTime() + 3000L <= Minecraft.getSystemTime()) {

                        iterator.remove();
                    } else {

                        maxLength = Math.max(maxLength, mc.fontRenderer.getStringWidth(caption.getString()));
                    }
                }
            }

            maxLength = maxLength + mc.fontRenderer.getStringWidth("<") + mc.fontRenderer.getStringWidth(" ") + mc.fontRenderer.getStringWidth(">") + mc.fontRenderer.getStringWidth(" ");

            int captionIndex = 0;
            for (Subtitle caption : subtitles) {
                String Caption1 = caption.getString();

                Vec3d vec3d4 = caption.getLocation().subtract(playerPosition).normalize();
                double d0 = -vec3d3.dotProduct(vec3d4);
                double d1 = -zPlayerDirection.dotProduct(vec3d4);
                boolean flag = d1 > 0.5D;

                int halfMaxLength = maxLength / 2;

                int subtitleHeight = mc.fontRenderer.FONT_HEIGHT;
                int subtitleWidth = mc.fontRenderer.getStringWidth(Caption1);

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

                GlStateManager.pushMatrix();

                //Change happens here

                String position = LESConfiguration.propOverlayPosition.getString();
                float verticalSpacing = 1;
                float horizontalSpacing = 2;
                float subtitleSpacing = 10 * (float) propSubtitleScale.getDouble();
                float xPos = propXposition.getInt();
                float yPos = propYposition.getInt();


                // ... existing switch statement ...
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
                        yPos += (((float) resolution.getScaledHeight() / 2) - (((float) (subtitles.size() - 1) / 2) - captionIndex) * subtitleSpacing);
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
                        yPos += (((float) resolution.getScaledHeight() / 2) - (((float) (subtitles.size() - 1) / 2) - captionIndex) * subtitleSpacing);
                        break;
                    default: //if there's any invalid input just show it in the bottom right
                        xPos += resolution.getScaledWidth() - halfMaxLength - 2;
                        yPos += ((resolution.getScaledHeight() - 30) - (captionIndex * subtitleSpacing));
                        break;
                }

                xPos = MathHelper.clamp(xPos, 0, resolution.getScaledWidth() - ((float) subtitleWidth / 2));
                yPos = MathHelper.clamp(yPos, 0, resolution.getScaledHeight() - ((float) subtitleHeight / 2));
                // Check if the subtitle is being dragged and update its position

                GlStateManager.translate(xPos, yPos, 0);

                GlStateManager.scale(LESConfiguration.propSubtitleScale.getInt(), LESConfiguration.propSubtitleScale.getInt(), 1.0F);
                drawRect(-halfMaxLength - 1, -subtitleHeight / 2 - 1, halfMaxLength + 1, subtitleHeight / 2 + 1,
                        backgroundAlpha << 24 | backgroundColor);
                GlStateManager.enableBlend();
                if (!flag) {
                    if (d0 > 0.00D) {
                        mc.fontRenderer.drawString(">", halfMaxLength - mc.fontRenderer.getStringWidth(">"), -subtitleHeight / 2, fadeAwayCalculation << 24 | fontColor);
                    } else if (d0 < -0.00D) {
                        mc.fontRenderer.drawString("<", -halfMaxLength, -subtitleHeight / 2, fadeAwayCalculation << 24 | fontColor);
                    }
                }
                mc.fontRenderer.drawString(Caption1, -subtitleWidth / 2, -subtitleHeight / 2, fadeAwayCalculation << 24 | fontColor);
                GlStateManager.popMatrix();
                ++captionIndex;
            }

            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }}
    @Override
    public void soundPlay(ISound soundIn, SoundEventAccessor accessor) {
        // Your custom implementation here
        if (accessor.getSubtitle() != null) {
            String subtitleText = accessor.getSubtitle().getFormattedText();

            if(!isGuiOpen) {
                if (!subtitles.isEmpty()) {
                    for (SubtitleOverlayHandler.Subtitle caption : subtitles) {
                        if (caption.getString().equals(subtitleText)) {
                            caption.refresh(new Vec3d(soundIn.getXPosF(), soundIn.getYPosF(), soundIn.getZPosF()));
                            return;
                        }
                    }
                }
                subtitles.add(new SubtitleOverlayHandler.Subtitle(subtitleText, new Vec3d(soundIn.getXPosF(), soundIn.getYPosF(), soundIn.getZPosF())));
            }
        }
    }
    public static class Subtitle
    {
        final String subtitle;
        long startTime;
        Vec3d location;
        String text;
        public Subtitle(String subtitleIn, Vec3d locationIn)
        {
            this.subtitle = subtitleIn;
            this.location = locationIn;
            this.startTime = Minecraft.getSystemTime();
        }
        public void setText(String textIn) {
            this.text = textIn;
        }
        // Getters and setters for other fields...
        public String getText() {
            return text;
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
