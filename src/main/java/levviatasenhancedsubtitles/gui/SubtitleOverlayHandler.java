package levviatasenhancedsubtitles.gui;

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

import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;

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
        final String subtitle;
        long startTime;
        Vec3d location;
        String text;
        int x, y;
        int width, height;
        boolean isDragging;

        public Subtitle(String subtitleIn, Vec3d locationIn)
        {
            this.subtitle = subtitleIn;
            this.location = locationIn;
            this.startTime = Minecraft.getSystemTime();
        }
        public void setPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }

        public boolean isMouseOver(int mouseX, int mouseY) {
            return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
        }

        // Getters and setters for other fields...
        public String getText() {
            return text;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public void setDragging(boolean dragging) {
            isDragging = dragging;
        }

        public boolean isDragging() {
            return isDragging;
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
