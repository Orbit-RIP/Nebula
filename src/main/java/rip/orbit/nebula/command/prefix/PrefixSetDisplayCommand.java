package rip.orbit.nebula.command.prefix;

import rip.orbit.nebula.prefix.Prefix;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class PrefixSetDisplayCommand {

    @Command(
            names = {"prefix setdisplay","prefix changedisplay","prefix modifydisplay", "tag setdisplay"},
            permission = "orbit.owner"
    )
    public static void execute(CommandSender sender,@Parameter(name = "prefix") Prefix prefix,@Parameter(name = "display",wildcard = true)String display) {

        if (prefix.getDisplay().equalsIgnoreCase(display)) {
            sender.sendMessage(prefix.getName() + ChatColor.RED + "'s prefix is already " + ChatColor.WHITE + prefix.getName() + ChatColor.RED + ".");
            return;
        }

        prefix.setDisplay(ChatColor.translateAlternateColorCodes('&',display));
        prefix.save();

        sender.sendMessage(ChatColor.GOLD + "Changed " + ChatColor.WHITE + prefix.getName() + ChatColor.GOLD + "'s prefix to " + prefix.getDisplay() + ChatColor.GOLD + ".");
    }

}
