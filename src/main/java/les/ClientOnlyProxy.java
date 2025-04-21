package les;

import les.gui.SubtitleOverlayHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

/**
 * ClientProxy is used to set up the mod and start it running on normal minecraft.  It contains all the code that should run on the
 *   client side only.
 *   For more background information see here http://greyminecraftcoder.blogspot.com/2013/11/how-forge-starts-up-your-code.html
 */
public class ClientOnlyProxy extends CommonProxy
{
  public static KeyBinding guiOpen;
  public static KeyBinding toggleSubtitles;

  /**
   * Run before anything else. Read your config, create blocks, items, etc. and register them with the GameRegistry
   */
  public void preInit()
  {
    super.preInit();
    MinecraftForge.EVENT_BUS.register(new SubtitleOverlayHandler());
    guiOpen = new KeyBinding("key.guiOpen.desc", Keyboard.KEY_P, "key.categories.les_keybindings");
    ClientRegistry.registerKeyBinding(guiOpen);
    toggleSubtitles = new KeyBinding("key.toggleSubtitles.desc", Keyboard.KEY_K, "key.categories.les_keybindings");
    ClientRegistry.registerKeyBinding(toggleSubtitles);
    les.config.StartupClientOnly.preInitClientOnly();

    les.gui.StartupClientOnly.preInitClientOnly();
  }

  /**
   * Do your mod setup. Build whatever data structures you care about. Register recipes,
   * send FMLInterModComms messages to other mods.
   */
  public void init()
  {
    super.init();
    les.config.StartupClientOnly.initClientOnly();

    les.gui.StartupClientOnly.InitClientOnly();
  }

  /**
   * Handle interaction with other mods, complete your setup based on this.
   */
  public void postInit()
  {
    super.postInit();
    les.config.StartupClientOnly.postInitClientOnly();

    les.gui.StartupClientOnly.postInitClientOnly();
  }

  @Override
  public boolean playerIsInCreativeMode(EntityPlayer player) {
    if (player instanceof EntityPlayerMP) {
      EntityPlayerMP entityPlayerMP = (EntityPlayerMP)player;
      return entityPlayerMP.interactionManager.isCreative();
    } else if (player instanceof EntityPlayerSP) {
      return Minecraft.getMinecraft().playerController.isInCreativeMode();
    }
    return false;
  }

  @Override
  public boolean isDedicatedServer() {return false;}

}
