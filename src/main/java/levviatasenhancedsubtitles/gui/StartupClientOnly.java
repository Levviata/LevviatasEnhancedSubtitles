package levviatasenhancedsubtitles.gui;

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
public class StartupClientOnly {
	public static void preInitClientOnly()
	{
		SubtitleOverlayHandler.clientPreInit();
	}
	public static void InitClientOnly() {
	}
	public static void postInitClientOnly()
	{
		//RadarOverlayHandler.clientPostInit();
	}
}
