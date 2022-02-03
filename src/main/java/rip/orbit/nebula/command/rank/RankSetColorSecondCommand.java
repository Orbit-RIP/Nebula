package rip.orbit.nebula.command.rank;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.rank.Rank;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author xanderume@gmail (JavaProject)
 */
public class RankSetColorSecondCommand {

    @Command(
            names = {"rank setsecondcolor"},
            permission = "neutron.command.rank.setcolor"
    )
    public static void execute(CommandSender sender,@Parameter(name = "rank") Rank rank,@Parameter(name = "color") ChatColor color) {
        if (Nebula.getInstance().isTestServer() && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You can't do this as the server is in developer mode!");
            return;
        }

        if (rank.getSecondColor() != null && rank.getSecondColor() == color) {
            sender.sendMessage(rank.getFancyName() + ChatColor.RED + "'s second color is already " + rank.getColor() + rank.getColor().name() + ChatColor.RED + ".");
            return;
        }

        final String oldFancyName = rank.getFancyName();

        rank.setSecondColor(color);
        rank.save();

        sender.sendMessage(ChatColor.GOLD + "Changed " + oldFancyName + ChatColor.GOLD + "'s color to: " + rank.getSecondColor() + rank.getSecondColor().name());
    }

}
