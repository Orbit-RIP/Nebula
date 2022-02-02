package cc.fyre.neutron.command.rank.metadata;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.rank.Rank;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author xanderume@gmail (JavaProject)
 */
public class RankMetaDataAddCommand {

    @Command(
            names = {"rank metadata add"},
            permission = "neutron.command.rank.metadata.add"
    )
    public static void execute(CommandSender sender,@Parameter(name = "rank") Rank rank,@Parameter(name = "metadata")String metaData,@Parameter(name = "metadataValue")String metaDataValue) {
        if (Neutron.getInstance().isTestServer() && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You can't do this as the server is in developer mode!");
            return;
        }

        if (rank.hasMetaData(metaData)) {
            sender.sendMessage(rank.getFancyName() + ChatColor.RED + " already has " + ChatColor.WHITE + metaData + ChatColor.RED + " metadata.");
            return;
        }

        rank.getMetaData().addProperty(metaData,metaDataValue);
        rank.save();

        sender.sendMessage(ChatColor.GOLD + "Added metadata " + ChatColor.WHITE + metaData + ChatColor.GOLD + " with value " + ChatColor.WHITE + metaDataValue + " to " + rank.getFancyName() + ChatColor.GOLD + ".");
    }

}
