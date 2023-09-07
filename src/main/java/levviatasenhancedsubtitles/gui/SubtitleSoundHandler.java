package levviatasenhancedsubtitles.gui;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ISoundEventListener;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.util.math.Vec3d;

public class SubtitleSoundHandler implements ISoundEventListener {
    @Override
    public void soundPlay(ISound soundIn, SoundEventAccessor accessor) {
        // Your custom implementation here
        if (accessor.getSubtitle() != null) {
            String s = accessor.getSubtitle().getFormattedText();

            if (!SubtitleOverlayHandler.subtitles.isEmpty()) {
                for (SubtitleOverlayHandler.Subtitle guisubtitleoverlay$subtitle : SubtitleOverlayHandler.subtitles) {
                    if (guisubtitleoverlay$subtitle.getString().equals(s)) {
                        guisubtitleoverlay$subtitle.refresh(new Vec3d((double) soundIn.getXPosF(), (double) soundIn.getYPosF(), (double) soundIn.getZPosF()));
                        return;
                    }
                }
            }

            SubtitleOverlayHandler.subtitles.add(new SubtitleOverlayHandler.Subtitle(s, new Vec3d((double) soundIn.getXPosF(), (double) soundIn.getYPosF(), (double) soundIn.getZPosF())));
        }
    }
}
