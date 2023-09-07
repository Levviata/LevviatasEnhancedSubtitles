package levviatasenhancedsubtitles.gui;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ModEventSubscriber {

    // Subscribe to the RenderGameOverlayEvent event
    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        // Check if it's the subtitles overlay being rendered
        if (event.getType() == RenderGameOverlayEvent.ElementType.SUBTITLES) {
            // Instantiate your custom GuiSubtitleOverlay
            Minecraft clientIn = Minecraft.getMinecraft();
            Minecraft client = Minecraft.getMinecraft();
            SubtitleOverlayHandler customSubtitleOverlay = new SubtitleOverlayHandler(clientIn, client);

            // Render your custom subtitles
            customSubtitleOverlay.renderSubtitles(event.getResolution());

            // Disable the default subtitles rendering
            event.setCanceled(true);
        }
    }
}