package les.config;

import les.LES;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiConfigEntries.CategoryEntry;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LESGuiFactory implements IModGuiFactory
{
    //this class is accessed when Forge needs a GUI made relating to your mod (e.g. config GUI)

    @Override
    public void initialize(Minecraft minecraftInstance)
    {
        //needed to implement IModGuiFactory but doesn't need to do anything
        // for the configuration gui
    }

    // called when your GUI needs to be created
    public GuiScreen createConfigGui(GuiScreen parentScreen)
    {
        return new LESConfigGui(parentScreen);
    }

    public boolean hasConfigGui() {
        return true;
    }

    // This function is needed for implementation only, the config gui does not require them
    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
    {
        return null;
    }

    //This class inherits from GuiConfig, a specialized GuiScreen designed to display your
    // configuration categories
    public static class LESConfigGui extends GuiConfig
    {
        public LESConfigGui(GuiScreen parentScreen)
        {
            //I18n function basically "translates" or localizes the given key using the appropriate .lang file
            super(parentScreen, getConfigElements(), LES.MODID,
                    false, false, I18n.format("gui.les_configuration.mainTitle"));
        }

        private static List<IConfigElement> getConfigElements()
        {
            List<IConfigElement> list = new ArrayList<IConfigElement>();
            //Add the two buttons that will go to each config category edit screen
            list.add(new DummyCategoryElement("positionCfg", "gui.les_configuration.ctgy.position", CategoryEntryPosition.class));
            list.add(new DummyCategoryElement("backgroundCfg", "gui.les_configuration.ctgy.background", CategoryEntryBackground.class));
            list.add(new DummyCategoryElement("fontCfg", "gui.les_configuration.ctgy.font", CategoryEntryFont.class));
            list.add(new DummyCategoryElement("mainCfg", "gui.les_configuration.ctgy.general", CategoryEntryGeneral.class));
            //list.add(new DummyCategoryElement("miscCfg", "gui.config.ctgy.other", CategoryEntryOther.class));
            return list;
        }

        //the next two classes are the edit screens for each configuration category
        public static class CategoryEntryPosition extends CategoryEntry
        {
            public CategoryEntryPosition(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
            {
                super(owningScreen, owningEntryList, prop);
            }


            @Override
            protected GuiScreen buildChildScreen()
            {
                //The following GuiConfig object specifies the configID of the object and thus will force-save
                // when closed.
                //Parent GuiConfig object's entryList will also be refreshed to reflect the changes.
                // --see GuiFactory of Forge for more info
                //Additionally, Forge best practices say to put the path to the config file for the category as
                // the title for the category config screen

                Configuration configuration = LESConfiguration.getConfig();
                ConfigElement cat_general = new ConfigElement(configuration.getCategory(LESConfiguration.CATEGORY_NAME_POSITION));
                List<IConfigElement> propertiesOnThisScreen = cat_general.getChildElements();
                String windowTitle = configuration.toString();

                return new GuiConfig(this.owningScreen, propertiesOnThisScreen,
                        this.owningScreen.modID,
                        LESConfiguration.CATEGORY_NAME_POSITION,
                        this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart,
                        this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart,
                        windowTitle);
                //this is a complicated object that specifies the category's gui screen, to better understand
                // how it works, look into the definitions of the called functions and objects
            }
        }
        public static class CategoryEntryBackground extends CategoryEntry {
            public CategoryEntryBackground(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
                super(owningScreen, owningEntryList, prop);
            }

        @Override
        protected GuiScreen buildChildScreen()
        {
            //The following GuiConfig object specifies the configID of the object and thus will force-save
            // when closed.
            //Parent GuiConfig object's entryList will also be refreshed to reflect the changes.
            // --see GuiFactory of Forge for more info
            //Additionally, Forge best practices say to put the path to the config file for the category as
            // the title for the category config screen

            Configuration configuration = LESConfiguration.getConfig();
            ConfigElement cat_general = new ConfigElement(configuration.getCategory(LESConfiguration.CATEGORY_NAME_BACKGROUND));
            List<IConfigElement> propertiesOnThisScreen = cat_general.getChildElements();
            String windowTitle = configuration.toString();

            return new GuiConfig(this.owningScreen, propertiesOnThisScreen,
                    this.owningScreen.modID,
                    LESConfiguration.CATEGORY_NAME_BACKGROUND,
                    this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart,
                    this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart,
                    windowTitle);
            //this is a complicated object that specifies the category's gui screen, to better understand
            // how it works, look into the definitions of the called functions and objects
        }
    }
        public static class CategoryEntryGeneral extends CategoryEntry
        {
            public CategoryEntryGeneral(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
            {
                super(owningScreen, owningEntryList, prop);
            }

            @Override
            protected GuiScreen buildChildScreen()
            {
                Configuration configuration = LESConfiguration.getConfig();
                ConfigElement cat_general = new ConfigElement(configuration.getCategory(LESConfiguration.CATEGORY_NAME_GENERAL));
                List<IConfigElement> propertiesOnThisScreen = cat_general.getChildElements();
                String windowTitle = configuration.toString();

                return new GuiConfig(this.owningScreen, propertiesOnThisScreen,
                        this.owningScreen.modID,
                        LESConfiguration.CATEGORY_NAME_GENERAL,
                        this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart,
                        this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart,
                        windowTitle);
                //this is a complicated object that specifies the category's gui screen, to better understand
                // how it works, look into the definitions of the called functions and objects
            }
        }
    }
    public static class CategoryEntryFont extends CategoryEntry
    {
        public CategoryEntryFont(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
        {
            super(owningScreen, owningEntryList, prop);
        }


        @Override
        protected GuiScreen buildChildScreen()
        {
            //The following GuiConfig object specifies the configID of the object and thus will force-save
            // when closed.
            //Parent GuiConfig object's entryList will also be refreshed to reflect the changes.
            // --see GuiFactory of Forge for more info
            //Additionally, Forge best practices say to put the path to the config file for the category as
            // the title for the category config screen

            Configuration configuration = LESConfiguration.getConfig();
            ConfigElement cat_general = new ConfigElement(configuration.getCategory(LESConfiguration.CATEGORY_NAME_FONT));
            List<IConfigElement> propertiesOnThisScreen = cat_general.getChildElements();
            String windowTitle = configuration.toString();

            return new GuiConfig(this.owningScreen, propertiesOnThisScreen,
                    this.owningScreen.modID,
                    LESConfiguration.CATEGORY_NAME_FONT,
                    this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart,
                    this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart,
                    windowTitle);
            //this is a complicated object that specifies the category's gui screen, to better understand
            // how it works, look into the definitions of the called functions and objects
        }
    }
}
