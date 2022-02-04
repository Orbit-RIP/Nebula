package rip.orbit.nebula.command.profile.sibling;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.profile.comparator.ProfileWeightComparator;
import rip.orbit.nebula.profile.menu.SiblingsMenu;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.flag.Flag;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SiblingListCommand {

    @Command(
            names = {"sibling list"},
            permission = "orbit.admin"
    )
    public static void execute(CommandSender sender,@Parameter(name = "player") UUID uuid,@Flag(value = "g", description = "show gui")boolean gui) {

        final Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(uuid,true);

        if (profile.getSiblings().isEmpty()) {
            sender.sendMessage(profile.getFancyName() + ChatColor.RED + " does not have any siblings.");
            return;
        }

        final List<Profile> siblings = profile.getSiblings().stream().map(sibling -> Nebula.getInstance().getProfileHandler().fromUuid(sibling,true)).sorted(new ProfileWeightComparator()).collect(Collectors.toList());

        if (sender instanceof Player && gui) {
            new SiblingsMenu(profile,siblings).openMenu((Player)sender);
            return;
        }

        sender.sendMessage(profile.getFancyName() + ChatColor.GOLD + "'s siblings:");

        for (Profile sibling : siblings) {
            sender.sendMessage(sibling.getFancyName());
        }

    }
}
