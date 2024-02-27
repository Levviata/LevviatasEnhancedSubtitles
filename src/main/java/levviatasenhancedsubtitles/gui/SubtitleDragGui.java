package levviatasenhancedsubtitles.gui;

import com.google.common.collect.Lists;
import levviatasenhancedsubtitles.config.LESConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ISoundEventListener;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@Mod.EventBusSubscriber
public class SubtitleDragGui extends GuiScreen implements ISoundEventListener {
    private final Minecraft minecraft = Minecraft.getMinecraft();
    static final List<SubtitleOverlayHandler.Subtitle> subtitles = Lists.newArrayList();
    private boolean isListening;
    private boolean isDragging = false;
    private int dragX = 0;
    private int dragY = 0;
    private int lastMouseX = 0;
    private int lastMouseY = 0;
    private SubtitleOverlayHandler.Subtitle selectedSubtitle = null;
    public static void clientPreInit()
    {
        MinecraftForge.EVENT_BUS.register(new SubtitleDragGui());
    }
    public static void preInit()
    {

    }
    @SubscribeEvent(receiveCanceled = true)
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        // Check if it's the subtitles overlay being rendered
        if (event.getType() == RenderGameOverlayEvent.ElementType.SUBTITLES) {

            Minecraft mc = Minecraft.getMinecraft();
            float partialTicks = event.getPartialTicks();

            // Get the current mouse position
            ScaledResolution scaled = new ScaledResolution(mc);
            int mouseX = Mouse.getX() * scaled.getScaledWidth() / mc.displayWidth;
            int mouseY = scaled.getScaledHeight() - Mouse.getY() * scaled.getScaledHeight() / mc.displayHeight - 1;

            // Instantiate your handler with the necessary context if required
            SubtitleDragGui handler = new SubtitleDragGui(/* pass any required parameters */);
            event.setCanceled(true);
            handler.drawScreen(mouseX, mouseY, partialTicks);

        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
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
            Iterator<SubtitleOverlayHandler.Subtitle> iterator = subtitles.iterator();

            while (iterator.hasNext())
            {
                SubtitleOverlayHandler.Subtitle caption = iterator.next();

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
            for (SubtitleOverlayHandler.Subtitle caption : subtitles)
            {
                String Caption1 = caption.getString();

                Vec3d vec3d4 = caption.getLocation().subtract(playerPosition).normalize();
                double d0 = -vec3d3.dotProduct(vec3d4);
                double d1 = -zPlayerDirection.dotProduct(vec3d4);
                boolean flag = d1 > 0.5D;

                int halfMaxLength = maxLength / 2;

                int subtitleHeight = minecraft.fontRenderer.FONT_HEIGHT;
                int subtitleWidth = minecraft.fontRenderer.getStringWidth(Caption1);

                int fadeAwayCalculation = MathHelper.floor(MathHelper.clampedLerp(255.0D, 75.0D, (float)(Minecraft.getSystemTime() - caption.getStartTime()) / 3000.0F));
                int fadeAway = fadeAwayCalculation << 16 | fadeAwayCalculation << 8 | fadeAwayCalculation;

                int red = LESConfiguration.propBackgroundRed.getInt();
                int green = LESConfiguration.propBackgroundGreen.getInt();
                int blue = LESConfiguration.propBackgroundBlue.getInt();
                int backgroundSubtitleAlphaCalculation = LESConfiguration.propBackgroundAlpha.getInt();
                int backgroundSubtitleColor = (backgroundSubtitleAlphaCalculation << 24) | (red << 16) | (green << 8) | blue;

                GlStateManager.pushMatrix();

                //Change happens here

                String position = LESConfiguration.propOverlayPosition.getString();
                ScaledResolution resolution = new ScaledResolution(minecraft);
                int verticalSpacing = 1;
                int horizontalSpacing = 2;
                int subtitleSpacing = 10;
                float xTranslate = 0;
                float yTranslate = 0;

                // ... existing switch statement ...

                // Check if the subtitle is being dragged and update its position
                if (caption.isDragging()) {
                    xTranslate = mouseX - ((float) subtitleWidth / 2);
                    yTranslate = mouseY - ((float) subtitleHeight / 2);
                    caption.setPosition((int) xTranslate, (int) yTranslate);
                } else {
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
                }

                GlStateManager.translate(xTranslate, yTranslate, 0.0F);

                GlStateManager.scale(1.0F, 1.0F, 1.0F);

                //drawRect(-halfMaxLength - 1, -subtitleHeight / 2 - 1, halfMaxLength + 1, subtitleHeight / 2 + 1, backgroundSubtitleColor);

                drawRect((int) xTranslate - halfMaxLength - 1, (int) yTranslate - subtitleHeight / 2 - 1,
                        (int) xTranslate + halfMaxLength + 1, (int) yTranslate + subtitleHeight / 2 + 1,
                        backgroundSubtitleColor);
                GlStateManager.enableBlend();

                if (!flag) {
                    if (d0 > 0.00D) {
                        minecraft.fontRenderer.drawString(">",
                                (int) xTranslate + halfMaxLength - minecraft.fontRenderer.getStringWidth(">"),
                                (int) yTranslate - subtitleHeight / 2,
                                fadeAway + 16777216);
                    } else if (d0 < -0.00D) {
                        minecraft.fontRenderer.drawString("<",
                                (int) xTranslate - halfMaxLength,
                                (int) yTranslate - subtitleHeight / 2,
                                fadeAway + 16777216);
                    }
                }

                minecraft.fontRenderer.drawString(Caption1,
                        (int) xTranslate - subtitleWidth / 2,
                        (int) yTranslate - subtitleHeight / 2,
                        fadeAway + 16777216);
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

                if (!SubtitleDragGui.subtitles.isEmpty()) {
                for (SubtitleOverlayHandler.Subtitle caption : SubtitleDragGui.subtitles) {
                    if (caption.getString().equals(subtitleText)) {
                        caption.refresh(new Vec3d(soundIn.getXPosF(), soundIn.getYPosF(), soundIn.getZPosF()));
                        return;
                    }
                }
            }
            SubtitleDragGui.subtitles.add(new SubtitleOverlayHandler.Subtitle(subtitleText, new Vec3d(soundIn.getXPosF(), soundIn.getYPosF(), soundIn.getZPosF())));
        }
    }
    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0) {
            for (SubtitleOverlayHandler.Subtitle subtitle : subtitles) {
                if (subtitle.isMouseOver(mouseX, mouseY)) {
                    this.selectedSubtitle = subtitle;
                    subtitle.isDragging = true;
                    break;
                }
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        if (this.selectedSubtitle != null) {
            this.selectedSubtitle.isDragging = false;
            this.selectedSubtitle = null;
        }
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        if (this.selectedSubtitle != null && this.selectedSubtitle.isDragging) {
            this.selectedSubtitle.setPosition(mouseX - this.selectedSubtitle.width / 2, mouseY - this.selectedSubtitle.height / 2);
        }
    }
    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }
}
