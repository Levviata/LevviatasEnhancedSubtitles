package levviatasenhancedsubtitles.gui;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ISoundEventListener;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.util.math.Vec3d;

public class SoundHandler implements ISoundEventListener {

    @Override
    public void soundPlay(ISound soundIn, SoundEventAccessor accessor) {
        // Your custom implementation here
        if (accessor.getSubtitle() != null) {
            String s = accessor.getSubtitle().getFormattedText();

            if (!GuiSubtitleOverlay.subtitles.isEmpty()) {
                for (GuiSubtitleOverlay.Subtitle guisubtitleoverlay$subtitle : GuiSubtitleOverlay.subtitles) {
                    if (guisubtitleoverlay$subtitle.getString().equals(s)) {
                        guisubtitleoverlay$subtitle.refresh(new Vec3d((double) soundIn.getXPosF(), (double) soundIn.getYPosF(), (double) soundIn.getZPosF()));
                        return;
                    }
                }
            }

            GuiSubtitleOverlay.subtitles.add(new GuiSubtitleOverlay.Subtitle(s, new Vec3d((double) soundIn.getXPosF(), (double) soundIn.getYPosF(), (double) soundIn.getZPosF())));
        }
    }
}
