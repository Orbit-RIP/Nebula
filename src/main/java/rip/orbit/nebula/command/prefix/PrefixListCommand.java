package rip.orbit.nebula.command.prefix;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.prefix.Prefix;
import rip.orbit.nebula.prefix.menu.PrefixListMenu;

import cc.fyre.proton.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PrefixListCommand {

    @Command(
            names = {"prefix list"},
            permission = "orbit.owner"
    )
    public static void execute(CommandSender sender) {

        if (!(sender instanceof Player)) {

            for (Prefix prefix : Nebula.getInstance().getPrefixHandler().getSortedValueCache()) {
                sender.sendMessage(prefix.getName() + ChatColor.GRAY + " (" + prefix.getWeight() + ") (" + prefix.getDisplay() + ChatColor.GRAY + ")");
            }

            return;
        }

        new PrefixListMenu().openMenu((Player)sender);
    }

}
