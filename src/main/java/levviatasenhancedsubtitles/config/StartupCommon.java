package levviatasenhancedsubtitles.config;

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
    LESConfiguration.preInit();
    System.out.println("Config: myInteger=" + LESConfiguration.myInteger
                               + "; myBoolean=" + LESConfiguration.myBoolean
                               + "; myString=" + LESConfiguration.myString);
    System.out.println("Config: myDouble=" + LESConfiguration.myDouble
                               + "; myColour=" + LESConfiguration.myColour);
    System.out.print("Config: myIntList=");
    for (int value : LESConfiguration.myIntList) {
      System.out.print(value + "; ");
    }
    System.out.println();

  }

	public static void initCommon()
	{
	}

	public static void postInitCommon()
	{
	}

}
