package rip.orbit.nebula.command.rank;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.rank.Rank;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class RankRenameCommand {

    @Command(
            names = {"rank setname"},
            permission = "orbit.headstaff"
    )
    public static void execute(CommandSender sender,@Parameter(name = "rank") Rank rank,@Parameter(name = "name")String name) {
        if (Nebula.getInstance().isTestServer() && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You can't do this as the server is in developer mode!");
            return;
        }

        if (rank.getName().equalsIgnoreCase(name)) {
            sender.sendMessage(rank.getFancyName() + ChatColor.RED + "'s name is already " + ChatColor.WHITE + rank.getName() + ChatColor.RED + ".");
            return;
        }

        final String oldName = rank.getName();

        rank.setName(name);
        rank.save();

        sender.sendMessage(ChatColor.GOLD + "Renamed " + oldName + " to" + ChatColor.GRAY + " -> " + ChatColor.WHITE + name + ChatColor.GOLD + ".");
    }

}
