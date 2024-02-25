package levviatasenhancedsubtitles.gui;


import levviatasenhancedsubtitles.module.Module;
import levviatasenhancedsubtitles.setting.BooleanSetting;
import levviatasenhancedsubtitles.setting.KeybindSetting;
import org.lwjgl.input.Keyboard;

public class SubtitleConfigGUI extends Module {
    public static final BooleanSetting showHUD=new BooleanSetting(
            "Show Subtitle Config HUD Panels","showHUDSubtitle",
            "Whether to show the HUD panels in the ClickGUI.",()->true,true);
    public static final KeybindSetting keybind=new KeybindSetting(
            "Keybind","keybindSubtitle",
            "The key to toggle the module.",()->true, Keyboard.KEY_SEMICOLON);
    public SubtitleConfigGUI() {
        super("Subtitle Config","Module containing subtitle config settings.",()->true,true);
        settings.add(showHUD);
        settings.add(keybind);
    }
}
