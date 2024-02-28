package levviatasenhancedsubtitles.gui;

import com.google.common.collect.Lists;
import levviatasenhancedsubtitles.config.LESConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ISoundEventListener;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.gui.GuiButton;
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

import static com.ibm.java.diagnostics.utils.Context.logger;
import static levviatasenhancedsubtitles.gui.SubtitleOverlayHandler.xTranslateRef;
import static levviatasenhancedsubtitles.gui.SubtitleOverlayHandler.yTranslateRef;

public class SubtitleDragGui extends GuiScreen {
    private boolean dragging;
    private int lastMouseX;
    private int lastMouseY;
    private boolean isListening;
    private SubtitleOverlayHandler.Subtitle selectedSubtitle = null;
    static boolean shouldPauseGame;
    private boolean isButtonPressed = false;
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

    public SubtitleDragGui() {
        // Initialize your draggable button here with id, x, y, and text
        this.draggableButton = new DraggableGuiButton(0, (int) xTranslateRef, (int) yTranslateRef, "text");
    }
    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        // Check if it's the subtitles overlay being rendered
        if (event.getType() == RenderGameOverlayEvent.ElementType.SUBTITLES) {
            // Instantte your handler with the necessary context if required
            if (isGuiOpen)
                event.setCanceled(true);
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(this.draggableButton);
    }
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == draggableButton) {
            isButtonPressed = !isButtonPressed; // Toggle the button state
        }
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (isButtonPressed) {
            // Update button position to follow the mouse
            draggableButton.x = mouseX - draggableButton.width / 2;
            draggableButton.y = mouseY - draggableButton.height / 2;
        }

        // Draw the button
        draggableButton.drawButton(this.mc, mouseX, mouseY, partialTicks);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (isButtonPressed) {
            // Update button position to follow the mouse
            draggableButton.x = mouseX - draggableButton.width / 2;
            draggableButton.y = mouseY - draggableButton.height / 2;
        }
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
