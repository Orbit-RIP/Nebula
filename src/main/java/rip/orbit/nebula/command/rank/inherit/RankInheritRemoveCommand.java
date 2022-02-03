package rip.orbit.nebula.command.rank.inherit;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.rank.Rank;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class RankInheritRemoveCommand {

    @Command(
            names = {"rank inherit remove"},
            permission = "neutron.command.rank.inherit.remove"
    )
    public static void execute(CommandSender sender,@Parameter(name = "rank") Rank rank,@Parameter(name = "inherit")Rank inherit) {
        if (Nebula.getInstance().isTestServer() && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You can't do this as the server is in developer mode!");
            return;
        }

        if (!rank.getInherits().contains(inherit)) {
            sender.sendMessage(rank.getFancyName() + ChatColor.RED + " does not have " + inherit.getFancyName() + ChatColor.RED + " inherited.");
            return;
        }

        rank.getInherits().remove(inherit);
        rank.save();

        sender.sendMessage(ChatColor.GOLD + "Removed " + inherit.getFancyName() + ChatColor.GOLD + " from " + rank.getFancyName() + ChatColor.GOLD + "'s inherits.");
    }

}
