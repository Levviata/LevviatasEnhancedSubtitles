package levviatasenhancedsubtitles.gui;

import com.google.common.collect.Lists;
import levviatasenhancedsubtitles.unused.SubtitleDragGui;
import levviatasenhancedsubtitles.unused.SubtitleOverlayHandler;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ISoundEventListener;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;

import java.util.Iterator;
import java.util.List;

public class DraggableGuiButton extends GuiButton implements ISoundEventListener {
    private boolean dragging;
    private int lastMouseX;
    private int lastMouseY;
    private boolean isListening;
    static float xTranslate;
    static float yTranslate;
    private SubtitleOverlayHandler.Subtitle selectedSubtitle = null;
    static boolean shouldPauseGame;
    public static final List<SubtitleOverlayHandler.Subtitle> subtitles = Lists.newArrayList();
    private static final List<SubtitleOverlayHandler.Subtitle> previewSubtitles = Lists.newArrayList();
    static {
        previewSubtitles.add(new SubtitleOverlayHandler.Subtitle("Example Subtitle", new Vec3d(0, 0, 0)));
        previewSubtitles.add(new SubtitleOverlayHandler.Subtitle("Big ol' Example Subtitle", new Vec3d(0, 0, 0)));
        previewSubtitles.add(new SubtitleOverlayHandler.Subtitle("Longgggggggggggggggggggggggg Boy Example Subtitle", new Vec3d(0, 0, 0)));
        previewSubtitles.add(new SubtitleOverlayHandler.Subtitle("Astronomoussssssssssssssssssssssssssssssss Example Subtitle", new Vec3d(0, 0, 0)));
    }
    public static boolean isGuiOpen = false;


    public static void clientPreInit()
    {
        MinecraftForge.EVENT_BUS.register(new SubtitleDragGui());
    }
    public static void preInit()
    {

    }


    public DraggableGuiButton(int buttonId, int x, int y, String buttonText) {
        super(buttonId, x, y, buttonText);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        int maxLength = 0;
        Iterator<SubtitleOverlayHandler.Subtitle> iterator = subtitles.iterator();

        while (iterator.hasNext()) {
            SubtitleOverlayHandler.Subtitle caption = iterator.next();

            if (caption.getStartTime() + 3000L <= Minecraft.getSystemTime()) {

                iterator.remove();
            } else {
                maxLength = Math.max(maxLength, mc.fontRenderer.getStringWidth(caption.getString()));

            }
        }

        maxLength = maxLength + mc.fontRenderer.getStringWidth("<") + mc.fontRenderer.getStringWidth(" ") + mc.fontRenderer.getStringWidth(">") + mc.fontRenderer.getStringWidth(" ");
        int captionIndex = 0;
        for (SubtitleOverlayHandler.Subtitle subtitle : previewSubtitles) {
            int halfMaxLength = maxLength / 2;

            int subtitleHeight = mc.fontRenderer.FONT_HEIGHT;
            int subtitleWidth = mc.fontRenderer.getStringWidth(subtitle.getText());

            GlStateManager.pushMatrix();

            GlStateManager.translate(xTranslate + x, yTranslate + y, 0);

            GlStateManager.scale(1.0F, 1.0F, 1.0F);

            drawRect(-halfMaxLength - 1, -subtitleHeight / 2 - 1, halfMaxLength + 1, subtitleHeight / 2 + 1,
                    0xFF000000);

            GlStateManager.enableBlend();

            mc.fontRenderer.drawString(subtitle.getText(), -subtitleWidth / 2, -subtitleHeight / 2, 16777216);

            GlStateManager.popMatrix();

        }

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
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
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            if (this.dragging) {
                this.x += mouseX - this.lastMouseX;
                this.y += mouseY - this.lastMouseY;
            }
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
        }
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            this.dragging = true;
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        this.dragging = false;
    }
}