package rip.orbit.nebula.command.profile.sibling;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.profile.Profile;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class SiblingAddCommand {

    @Command(
            names = {"sibling add"},
            permission = "neutron.command.sibling.add"
    )
    public static void execute(CommandSender sender,@Parameter(name = "player")UUID uuid,@Parameter(name = "sibling")UUID sibling) {
        final Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(uuid,true);

        if (profile.getSiblings().size() >= 3) {
            sender.sendMessage(profile.getFancyName() + ChatColor.RED + " already has " + ChatColor.WHITE + 3 + ChatColor.RED + " siblings.");
            return;
        }

        if (profile.getSiblings().contains(sibling)) {
            sender.sendMessage(ChatColor.RED + "This player is already a sibling.");
            return;
        }

        profile.getSiblings().add(sibling);
        profile.save();

        final Profile siblingProfile = Nebula.getInstance().getProfileHandler().fromUuid(sibling,true);

        sender.sendMessage(siblingProfile.getFancyName() + ChatColor.GOLD + " is now a sibling of " + profile.getFancyName() + ChatColor.GOLD + ".");
    }

}
