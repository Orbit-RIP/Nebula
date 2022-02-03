package rip.orbit.nebula.command.rank.permission;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.rank.Rank;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class RankPermissionRemoveCommand {

    @Command(
            names = {"rank permission remove"},
            permission = "neutron.command.rank.permission.remove"
    )
    public static void execute(CommandSender sender,@Parameter(name = "rank") Rank rank,@Parameter(name = "permission")String permission) {
        if (Nebula.getInstance().isTestServer() && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You can't do this as the server is in developer mode!");
            return;
        }

        if (!rank.getPermissions().contains(permission)) {
            sender.sendMessage(rank.getFancyName() + ChatColor.RED + " does not have the permission node " + ChatColor.WHITE + permission + ChatColor.RED + ".");
            return;
        }

        rank.getPermissions().remove(permission);
        rank.save();

        sender.sendMessage(ChatColor.GOLD + "Removed permission node " + ChatColor.WHITE + permission + ChatColor.GOLD + " to " + rank.getFancyName() + ChatColor.GOLD + ".");
    }
}
