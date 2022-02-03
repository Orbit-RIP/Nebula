package rip.orbit.nebula.command.profile.disguise;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.profile.Profile;
import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;

import org.bukkit.entity.Player;

import java.util.UUID;

public class UnDisguiseCommand {

    @Command(
            names = {"undisguise"},
            permission = "neutron.command.undisguise"
    )
    public static void execute(Player player,@Parameter(name = "player",defaultValue = "self")UUID uuid) {

        if (player.getUniqueId().equals(uuid)) {

            final Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(player.getUniqueId());

            if (profile.getDisguiseProfile() == null) {
                player.sendMessage(ChatColor.RED + "You are not disguised.");
                return;
            }

            profile.setDisguiseProfile(null);
            profile.save();

            player.undisguise();
            return;
        }

        if (!player.hasPermission("neutron.command.undisguise.others")) {
            player.sendMessage(Proton.getInstance().getCommandHandler().getCommandConfiguration().getNoPermissionMessage());
            return;
        }

        final Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(uuid,true);

        if (profile.getDisguiseProfile() == null) {
            player.sendMessage(ChatColor.RED + "You are not disguised.");
            return;
        }

        profile.setDisguiseProfile(null);
        profile.save();

        final Player target = profile.getPlayer();

        if (target != null) {
            target.undisguise();
        }

        player.sendMessage(profile.getFancyName() + ChatColor.GOLD + " is no longer disguised.");

    }
}
