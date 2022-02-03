package rip.orbit.nebula.command.profile;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.profile.comparator.ProfileWeightComparator;
import rip.orbit.nebula.profile.menu.AltsMenu;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.flag.Flag;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


public class AltsCommand {

    @Command(
            names = {"alts","alternates"},
            permission = "neutron.command.alts"
    )
    public static void execute(CommandSender sender,@Parameter(name = "player")UUID uuid,@Flag(value = "g", description = "show gui")boolean gui) {
        final Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(uuid,true);
        final List<Profile> alts = profile.findAltsAsync().stream().map(Profile::new).sorted(new ProfileWeightComparator()).collect(Collectors.toList());

        if (alts.isEmpty()) {
            sender.sendMessage(profile.getFancyName() + ChatColor.RED + " does not have any alts.");
            return;
        }

        if (sender instanceof Player && gui) {
            new AltsMenu(profile,alts).openMenu((Player)sender);
            return;
        }

        sender.sendMessage(profile.getFancyName() + ChatColor.GOLD + "'s alts:");

        for (Profile alt : alts) {
            sender.sendMessage(alt.getFancyName());
        }

    }
}
