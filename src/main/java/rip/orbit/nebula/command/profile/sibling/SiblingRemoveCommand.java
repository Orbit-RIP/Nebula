package rip.orbit.nebula.command.profile.sibling;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.profile.Profile;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class SiblingRemoveCommand {

    @Command(
            names = {"sibling remove"},
            permission = "orbit.admin"
    )
    public static void execute(CommandSender sender,@Parameter(name = "player") UUID uuid,@Parameter(name = "sibling")UUID sibling) {

        final Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(uuid,true);

        if (!profile.getSiblings().contains(sibling)) {
            sender.sendMessage(ChatColor.RED + "This player is is not a sibling of " + profile.getFancyName() + ChatColor.RED + ".");
            return;
        }

        profile.getSiblings().remove(sibling);
        profile.save();

        final Profile siblingProfile = Nebula.getInstance().getProfileHandler().fromUuid(sibling,true);

        sender.sendMessage(siblingProfile.getFancyName() + ChatColor.GOLD + " is no longer a sibling of " + profile.getFancyName() + ChatColor.GOLD + ".");
    }

}
