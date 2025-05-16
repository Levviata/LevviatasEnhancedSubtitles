package les.command;

import les.gui.SubtitleDragGui;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.entity.player.EntityPlayer;

import static les.config.LESConfiguration.getConfig;
import static les.config.LESConfiguration.propDisablePopup;

public class CommandResetButtons extends CommandBase
{

    @Override
    public String getName()
    {
        return "resetbuttons"; // The command name that players will use.
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/resetbuttons"; // Usage instructions for the command.
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
    {
        if (sender instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) sender;
            propDisablePopup.set(true);
            getConfig().save();
            player.sendMessage(new TextComponentString("Successfully disabled GUI notification"));
        }
    }

    // Optional: Override this method if you want to set permissions for who can use the command.
    @Override
    public int getRequiredPermissionLevel()
    {
        return 0; // 0 means everyone can use it, 4 for server admins.
    }
}
