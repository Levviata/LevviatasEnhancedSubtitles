package les.handlers;

import les.ClientOnlyProxy;
import les.gui.SubtitleDragGui;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static les.config.LESConfiguration.propShowSubtitles;

@Mod.EventBusSubscriber
public class KeybindPressHandler {
    private static final Logger logger = LogManager.getLogger("SubtitleDragGui");
   /* @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (ClientOnlyProxy.guiOpen.isPressed()) {


            ; // Switches between false and true
        }
    }*/
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (ClientOnlyProxy.guiOpen.isPressed()) {
                SubtitleDragGui handler = new SubtitleDragGui();

                Minecraft.getMinecraft().displayGuiScreen(handler);
                handler.initGui();

                logger.info("Subtitle GUI opened");
                SubtitleDragGui.isGuiOpen = true;
            }
            if(ClientOnlyProxy.toggleSubtitles.isPressed()) {
                boolean subtitles = propShowSubtitles.getBoolean();
                propShowSubtitles.set(!subtitles);
                logger.info("Subtitle GUI toggled");
            }
        }
    }
}
