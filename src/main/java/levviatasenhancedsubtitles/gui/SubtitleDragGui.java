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

import static levviatasenhancedsubtitles.gui.SubtitleOverlayHandler.subtitles;

@Mod.EventBusSubscriber
public class SubtitleDragGui extends GuiScreen {
    private SubtitleOverlayHandler.Subtitle selectedSubtitle = null;

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
