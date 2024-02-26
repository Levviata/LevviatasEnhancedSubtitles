package levviatasenhancedsubtitles.gui;

import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.base.IToggleable;
import com.lukflug.panelstudio.base.SimpleToggleable;
import com.lukflug.panelstudio.component.*;
import com.lukflug.panelstudio.hud.HUDGUI;
import com.lukflug.panelstudio.layout.IComponentAdder;
import com.lukflug.panelstudio.layout.ILayout;
import com.lukflug.panelstudio.layout.PanelAdder;
import com.lukflug.panelstudio.layout.PanelLayout;
import com.lukflug.panelstudio.mc12.MinecraftHUDGUI;
import com.lukflug.panelstudio.popup.IPopupPositioner;
import com.lukflug.panelstudio.popup.MousePositioner;
import com.lukflug.panelstudio.setting.IClient;
import com.lukflug.panelstudio.theme.*;
import levviatasenhancedsubtitles.component.DraggableButton;
import levviatasenhancedsubtitles.module.ClickGUIModule;
import levviatasenhancedsubtitles.module.HUDEditorModule;
import levviatasenhancedsubtitles.setting.BooleanSetting;
import levviatasenhancedsubtitles.setting.ColorSetting;
import levviatasenhancedsubtitles.setting.IntegerSetting;
import net.minecraft.util.text.TextFormatting;
import com.lukflug.panelstudio.theme.ITheme;
import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.base.SimpleToggleable;
import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.widget.ToggleButton;
import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

public class DraggableHUDGUI extends MinecraftHUDGUI {
    private final GUIInterface inter;
    private final HUDGUI gui;
    public DraggableHUDGUI () {
        IClient client=Category.getClient();

        inter=new GUIInterface(true) {
            @Override
            protected String getResourcePrefix() {
                return "levviatasenhancedsubtitles:";
            }
        };
        // Instantiating theme ...
        ITheme theme=new OptimizedTheme(new ThemeSelector(inter));
        IToggleable guiToggle=new SimpleToggleable(false);
        IToggleable hudToggle=new SimpleToggleable(false) {
            @Override
            public boolean isOn() {
                return guiToggle.isOn()? HUDEditorModule.showHUD.isOn():super.isOn();
            }
        };
        gui=new HUDGUI(inter,theme.getDescriptionRenderer(),(IPopupPositioner)new MousePositioner(new Point(10,10)),guiToggle,hudToggle);



    }

    @Override
    protected GUIInterface getInterface() {
        return inter;
    }

    @Override
    protected int getScrollSpeed() {
        return 10;
    }

    @Override
    protected HUDGUI getGUI() {
        return gui;
    }
    private class ThemeSelector implements IThemeMultiplexer {
        protected Map<ClickGUIModule.Theme,ITheme> themes=new EnumMap<ClickGUIModule.Theme,ITheme>(ClickGUIModule.Theme.class);

        public ThemeSelector (IInterface inter) {
            BooleanSetting clearGradient=new BooleanSetting("Gradient","gradient","Whether the title bars should have a gradient.",()->ClickGUIModule.theme.getValue()== ClickGUIModule.Theme.Clear,true);
            BooleanSetting ignoreDisabled=new BooleanSetting("Ignore Disabled","ignoreDisabled","Have the rainbow drawn for disabled containers.",()->ClickGUIModule.theme.getValue()== ClickGUIModule.Theme.Rainbow,false);
            BooleanSetting buttonRainbow=new BooleanSetting("Button Rainbow","buttonRainbow","Have a separate rainbow for each component.",()->ClickGUIModule.theme.getValue()== ClickGUIModule.Theme.Rainbow,false);
            IntegerSetting rainbowGradient=new IntegerSetting("Rainbow Gradient","rainbowGradient","How fast the rainbow should repeat.",()->ClickGUIModule.theme.getValue()== ClickGUIModule.Theme.Rainbow,150,50,300);
            ClickGUIModule.theme.subSettings.add(clearGradient);
            ClickGUIModule.theme.subSettings.add(ignoreDisabled);
            ClickGUIModule.theme.subSettings.add(buttonRainbow);
            ClickGUIModule.theme.subSettings.add(rainbowGradient);
            addTheme(ClickGUIModule.Theme.Clear,new ClearTheme(new ThemeScheme(ClickGUIModule.Theme.Clear),()->clearGradient.getValue(),9,3,1,": "+TextFormatting.GRAY));
            addTheme(ClickGUIModule.Theme.GameSense,new GameSenseTheme(new ThemeScheme(ClickGUIModule.Theme.GameSense),9,4,5,": "+ TextFormatting.GRAY));
            addTheme(ClickGUIModule.Theme.Rainbow,new RainbowTheme(new ThemeScheme(ClickGUIModule.Theme.Rainbow),()->ignoreDisabled.getValue(),()->buttonRainbow.getValue(),()->rainbowGradient.getValue(),9,3,": "+TextFormatting.GRAY));
            addTheme(ClickGUIModule.Theme.Windows31,new Windows31Theme(new ThemeScheme(ClickGUIModule.Theme.Windows31),9,2,9,": "+TextFormatting.DARK_GRAY));
            addTheme(ClickGUIModule.Theme.Impact,new ImpactTheme(new ThemeScheme(ClickGUIModule.Theme.Impact),9,4));
        }

        @Override
        public ITheme getTheme() {
            return themes.getOrDefault(ClickGUIModule.theme.getValue(),themes.get(ClickGUIModule.Theme.GameSense));
        }

        private void addTheme (ClickGUIModule.Theme key, ITheme value) {
            themes.put(key,new OptimizedTheme(value));
            value.loadAssets(inter);
        }


        private class ThemeScheme implements IColorScheme {
            private final ClickGUIModule.Theme themeValue;
            private final String themeName;

            public ThemeScheme (ClickGUIModule.Theme themeValue) {
                this.themeValue=themeValue;
                this.themeName=themeValue.toString().toLowerCase();
            }

            @Override
            public void createSetting (ITheme theme, String name, String description, boolean hasAlpha, boolean allowsRainbow, Color color, boolean rainbow) {
                ClickGUIModule.theme.subSettings.add(new ColorSetting(name,themeName+"-"+name,description,()->ClickGUIModule.theme.getValue()==themeValue,hasAlpha,allowsRainbow,color,rainbow));
            }

            @Override
            public Color getColor (String name) {
                return ((ColorSetting)ClickGUIModule.theme.subSettings.stream().filter(setting->setting.configName.equals(themeName+"-"+name)).findFirst().orElse(null)).getValue();
            }
        }
    }
}