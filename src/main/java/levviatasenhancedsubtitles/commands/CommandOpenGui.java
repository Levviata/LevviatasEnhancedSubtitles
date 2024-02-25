package levviatasenhancedsubtitles.commands;

import levviatasenhancedsubtitles.gui.PositionOverlayHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandOpenGui extends CommandBase {
    private final Logger logger = Logger.getLogger("levviatasenhancedsubtitles");

    @Override
    public String getName() {
        return "opensubtitlesgui";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/opensubtitlesgui";
    }
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return Collections.emptyList(); // No arguments, so return an empty list
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (server.isDedicatedServer()) {
            sender.sendMessage(new TextComponentString("This command can only be executed on the client side."));
        } else {
            // Schedule the GUI to be opened on the client thread
            Minecraft.getMinecraft().addScheduledTask(() ->
                    Minecraft.getMinecraft().displayGuiScreen(new PositionOverlayHandler()));
            logger.log(Level.INFO, "Scheduled command to open subtitle config GUI.");
        }
    }
}