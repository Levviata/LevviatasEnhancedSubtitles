package levviatasenhancedsubtitles.gui;

import levviatasenhancedsubtitles.config.LESConfiguration;
import net.minecraft.client.gui.Gui;

/*
 * User: TW
 * Date: 2/4/2015
 *
 * The Startup classes for this example are called during startup, in the following order:
 *  preInitCommon
 *  preInitClientOnly
 *  initCommon
 *  initClientOnly
 *  postInitCommon
 *  postInitClientOnly
 *  See MinecraftByExample class for more information
 */
public class StartupCommon
{
	public static void preInitCommon()
	{
    GuiSubtitleOverlay.preInit();
    }
    public static void InitCommon()
    {

    }
	public static void postInitCommon()
	{

	}


}
