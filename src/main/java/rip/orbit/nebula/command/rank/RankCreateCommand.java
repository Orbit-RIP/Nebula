package rip.orbit.nebula.command.rank;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.rank.Rank;
import rip.orbit.nebula.rank.menu.editor.RankEditorMenu;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RankCreateCommand {

    @Command(
            names = {"rank create"},
            permission = "neutron.command.rank.create"
    )
    public static void execute(CommandSender sender,@Parameter(name = "name")String name) {
        if (Nebula.getInstance().isTestServer() && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You can't do this as the server is in developer mode!");
            return;
        }

        Rank rank = Nebula.getInstance().getRankHandler().fromName(name);

        if (rank != null) {
            sender.sendMessage(ChatColor.RED + "Rank " + rank.getFancyName() + ChatColor.RED + " already exists.");
            return;
        }

        rank = new Rank(UUID.randomUUID(),name,UUIDUtils.uuid(sender.getName()));

        Nebula.getInstance().getRankHandler().getCache().put(rank.getUuid(),rank);

        rank.save();

        sender.sendMessage(ChatColor.GOLD + "Created a new rank: " + rank.getFancyName());

        if (sender instanceof Player) {
            new RankEditorMenu().openMenu((Player)sender);
        }

    }

}
