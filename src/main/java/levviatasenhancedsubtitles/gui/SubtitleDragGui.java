package levviatasenhancedsubtitles.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

import static levviatasenhancedsubtitles.gui.SubtitleOverlayHandler.isGuiOpen;
import static levviatasenhancedsubtitles.gui.SubtitleOverlayHandler.subtitles;

@Mod.EventBusSubscriber
public class SubtitleDragGui extends GuiScreen {
    private SubtitleOverlayHandler.Subtitle selectedSubtitle = null;
    static boolean shouldPauseGame;

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
    public static boolean setPauseGame(boolean shouldPauseGame) {
        return SubtitleDragGui.shouldPauseGame = shouldPauseGame;

    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        isGuiOpen = false;
    }

    @Override
    public boolean doesGuiPauseGame() {

        return shouldPauseGame;
    }
}
