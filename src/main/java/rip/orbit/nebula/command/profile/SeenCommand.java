package rip.orbit.nebula.command.profile;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.profile.Profile;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class SeenCommand {

    @Command(
            names = {"seen","lastonline"},
            permission = "neutron.command.seen"
    )
    public static void execute(CommandSender sender,@Parameter(name = "player")UUID uuid) {
        final Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(uuid,true);

        if (profile.getServerProfile().isOnline()) {
            sender.sendMessage(profile.getFancyName() + ChatColor.GOLD + " is currently" + ChatColor.GREEN + " online " + ChatColor.GOLD + "on " + ChatColor.WHITE + profile.getServerProfile().getLastServer() + ChatColor.GOLD + ".");
            return;
        }

        sender.sendMessage(profile.getFancyName() + ChatColor.GOLD + " was last seen " + ChatColor.WHITE + profile.getServerProfile().getLastSeenString() + ChatColor.GOLD + " ago on " + ChatColor.WHITE + profile.getServerProfile().getLastServer() + ChatColor.GOLD + ".");
    }

}
