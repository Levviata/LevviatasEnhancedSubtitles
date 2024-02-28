package levviatasenhancedsubtitles.unused;

import com.google.common.collect.Lists;
import levviatasenhancedsubtitles.config.LESConfiguration;
import levviatasenhancedsubtitles.gui.DraggableGuiButton;
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
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import java.util.Iterator;
import java.util.List;

@Mod.EventBusSubscriber
public class SubtitleDragGui extends GuiScreen implements ISoundEventListener {
    private boolean dragging;
    private int lastMouseX;
    private int lastMouseY;
    private boolean isListening;
    static float xTranslate;
    static float yTranslate;
    private SubtitleOverlayHandler.Subtitle selectedSubtitle = null;
    static boolean shouldPauseGame;
    public static boolean shouldCloseGui = false;
    public static DraggableGuiButton handler;
    public static final List<SubtitleOverlayHandler.Subtitle> subtitles = Lists.newArrayList();
    private static final List<SubtitleOverlayHandler.Subtitle> previewSubtitles = Lists.newArrayList();
    static {
        previewSubtitles.add(new SubtitleOverlayHandler.Subtitle("Example Subtitle", new Vec3d(0, 0, 0)));
        previewSubtitles.add(new SubtitleOverlayHandler.Subtitle("Big ol' Example Subtitle", new Vec3d(0, 0, 0)));
        previewSubtitles.add(new SubtitleOverlayHandler.Subtitle("Longgggggggggggggggggggggggg Boy Example Subtitle", new Vec3d(0, 0, 0)));
        previewSubtitles.add(new SubtitleOverlayHandler.Subtitle("Astronomoussssssssssssssssssssssssssssssss Example Subtitle", new Vec3d(0, 0, 0)));
    }
    public static boolean isGuiOpen = false;
    private final DraggableGuiButton draggableButton;
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
            event.setCanceled(true);
            drawScreen(mouseX, mouseY, partialTicks);
        }
    }

    public SubtitleDragGui() {
        // Initialize your draggable button here with id, x, y, and text
        this.draggableButton = new DraggableGuiButton(0, (int) xTranslate, (int) yTranslate, "");
    }



    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        ScaledResolution resolution = new ScaledResolution(mc);
        if (!this.isListening && mc.gameSettings.showSubtitles)
        {
            mc.getSoundHandler().addListener(this);
            this.isListening = true;
        }
        else if (this.isListening && !mc.gameSettings.showSubtitles)
        {
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
            Iterator<SubtitleOverlayHandler.Subtitle> iterator = subtitles.iterator();
            if (!isGuiOpen) {
                while (iterator.hasNext()) {
                    SubtitleOverlayHandler.Subtitle caption = iterator.next();

                    if (caption.getStartTime() + 3000L <= Minecraft.getSystemTime()) {

                        iterator.remove();
                    } else {
                        maxLength = Math.max(maxLength, mc.fontRenderer.getStringWidth(caption.getString()));

                    }
                }
            } else {
                Lists.<SubtitleOverlayHandler.Subtitle>newArrayList(subtitles).forEach(subtitles::remove);
                subtitles.addAll(previewSubtitles);
                maxLength = Math.max(maxLength, mc.fontRenderer.getStringWidth(previewSubtitles.get(3).getString()));
            }

            maxLength = maxLength + mc.fontRenderer.getStringWidth("<") + mc.fontRenderer.getStringWidth(" ") + mc.fontRenderer.getStringWidth(">") + mc.fontRenderer.getStringWidth(" ");

            int captionIndex = 0;
            for (SubtitleOverlayHandler.Subtitle caption : subtitles)
            {
                String Caption1 = caption.getString();

                Vec3d vec3d4 = caption.getLocation().subtract(playerPosition).normalize();
                double d0 = -vec3d3.dotProduct(vec3d4);
                double d1 = -zPlayerDirection.dotProduct(vec3d4);
                boolean flag = d1 > 0.5D;

                int halfMaxLength = maxLength / 2;

                int subtitleHeight = mc.fontRenderer.FONT_HEIGHT;
                int subtitleWidth = mc.fontRenderer.getStringWidth(Caption1);

                int fadeAwayCalculation = MathHelper.floor(MathHelper.clampedLerp(255.0D, 75.0D, (float)(Minecraft.getSystemTime() - caption.getStartTime()) / 3000.0F));
                int fadeAway = 0;
                if (!isGuiOpen) {
                    fadeAway = fadeAwayCalculation << 16 | fadeAwayCalculation << 8 | fadeAwayCalculation;
                }
                if (isGuiOpen) {
                    fadeAway = 255 << 16 | 255 << 8 | 255; //sets alpha to 255 (aRGB)
                }
                int red = LESConfiguration.propBackgroundRed.getInt();
                int green = LESConfiguration.propBackgroundGreen.getInt();
                int blue = LESConfiguration.propBackgroundBlue.getInt();

                GlStateManager.pushMatrix();

                //Change happens here

                String position = LESConfiguration.propOverlayPosition.getString();
                int verticalSpacing = 1;
                int horizontalSpacing = 2;
                int subtitleSpacing = 10;

                // ... existing switch statement ...
                switch (position) {
                    case "BOTTOM_CENTER":
                        xTranslate += (float) resolution.getScaledWidth() / 2;
                        yTranslate += (float) (resolution.getScaledHeight() - 75) - (float) (captionIndex * subtitleSpacing);
                        caption.setPosition((int) xTranslate, (int) yTranslate);
                        break;
                    case "BOTTOM_LEFT":
                        xTranslate += (float) halfMaxLength + horizontalSpacing;
                        yTranslate += (float) (resolution.getScaledHeight() - 30) - (float) (captionIndex * subtitleSpacing);
                        caption.setPosition((int) xTranslate, (int) yTranslate);
                        break;
                    case "CENTER_LEFT":
                        xTranslate += (float) halfMaxLength + horizontalSpacing;
                        yTranslate += (float) (resolution.getScaledHeight() / 2) - (float) (((subtitles.size() - 1) / 2) - captionIndex) * subtitleSpacing;
                        caption.setPosition((int) xTranslate, (int) yTranslate);
                        break;
                    case "TOP_LEFT":
                        xTranslate += (float) halfMaxLength + horizontalSpacing;
                        yTranslate += (float) (captionIndex * subtitleSpacing + 5 + verticalSpacing);
                        caption.setPosition((int) xTranslate, (int) yTranslate);
                        break;
                    case "TOP_CENTER":
                        xTranslate += (float) resolution.getScaledWidth() / 2;
                        yTranslate += (float) (captionIndex * subtitleSpacing + 5 + verticalSpacing);
                        caption.setPosition((int) xTranslate, (int) yTranslate);
                        break;
                    case "TOP_RIGHT":
                        xTranslate += (float) resolution.getScaledWidth() - (float) halfMaxLength - 2;
                        yTranslate += (float) (captionIndex * subtitleSpacing + 5 + verticalSpacing);
                        caption.setPosition((int) xTranslate, (int) yTranslate);
                        break;
                    case "CENTER_RIGHT":
                        xTranslate += (float) resolution.getScaledWidth() - (float) halfMaxLength - horizontalSpacing;
                        yTranslate += (float) (resolution.getScaledHeight() / 2) - (float) (((subtitles.size() - 1) / 2) - captionIndex) * subtitleSpacing;
                        caption.setPosition((int) xTranslate, (int) yTranslate);
                        break;
                    default: //if there's any invalid input just show it in the bottom right
                        xTranslate += (float) resolution.getScaledWidth() - (float) halfMaxLength - 2;
                        yTranslate += (float) (resolution.getScaledHeight() - 30) - (float) (captionIndex * subtitleSpacing);
                        caption.setPosition((int) xTranslate, (int) yTranslate);
                        break;
                }
                xTranslate =  MathHelper.clamp(caption.getX(), 0, resolution.getScaledWidth() - (subtitleWidth / 2));
                yTranslate =  MathHelper.clamp(caption.getY(), 0, resolution.getScaledHeight() - (subtitleHeight / 2));
                // Check if the subtitle is being dragged and update its position

                GlStateManager.translate(xTranslate, yTranslate, 0.0F);

                GlStateManager.scale(1.0F, 1.0F, 1.0F);

                drawRect(-halfMaxLength - 1, -subtitleHeight / 2 - 1, halfMaxLength + 1, subtitleHeight / 2 + 1,
                        0xFF000000);
                GlStateManager.enableBlend();
                if (!flag) {
                    if (d0 > 0.00D)
                    {
                        mc.fontRenderer.drawString(">", halfMaxLength - mc.fontRenderer.getStringWidth(">"), -subtitleHeight / 2, fadeAway + 16777216);
                    }
                    else if (d0 < -0.00D)
                    {
                        mc.fontRenderer.drawString("<", -halfMaxLength, -subtitleHeight / 2, fadeAway + 16777216);
                    }
                }

                mc.fontRenderer.drawString(Caption1, -subtitleWidth / 2, -subtitleHeight / 2, fadeAway + 16777216);
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

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (clickedMouseButton == 0) { // 0 is the left mouse button
            draggableButton.mouseDragged(this.mc, mouseX, mouseY);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        draggableButton.mouseReleased(mouseX, mouseY);
    }

    public static void exitGui() {
        Minecraft minecraft = Minecraft.getMinecraft();
        minecraft.displayGuiScreen(null);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        DraggableGuiButton.isGuiOpen = false;
    }

    @Override
    public boolean doesGuiPauseGame() {

        return false;
    }
}
