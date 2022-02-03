package rip.orbit.nebula.command.profile.disguise;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.profile.disguise.DisguiseProfile;
import rip.orbit.nebula.rank.Rank;
import rip.orbit.nebula.util.MojangUtil;
import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DisguiseCommand {

    @Command(
            names = {"disguise"},
            permission = "neutron.command.disguise",
            async = true
            //Async as we have to send a request to mojang for skin
    )
    public static void execute(Player player,@Parameter(name = "name")String name,@Parameter(name = "rank",defaultValue = "Default")Rank rank,@Parameter(name = "uuid",defaultValue = "self")UUID uuid) {

        if (Nebula.getInstance().getServer().getPlayerByDisguise(name) != null) {
            player.sendMessage(ChatColor.RED + "Someone is already disguised as this player!");
            return;
        }

        if (!player.getUniqueId().equals(uuid) && !player.hasPermission("neutron.command.disguise.others")) {
            player.sendMessage(Proton.getInstance().getCommandHandler().getCommandConfiguration().getNoPermissionMessage());
            return;
        }

        Profile profile;

        if (player.getUniqueId().equals(uuid)) {
            profile = Nebula.getInstance().getProfileHandler().fromUuid(player.getUniqueId());
        } else {
            profile = Nebula.getInstance().getProfileHandler().fromUuid(uuid,true);
        }

        final String[] property = MojangUtil.getSkinFromName(name);

        DisguiseProfile disguiseProfile;

        if (property == null) {
            disguiseProfile = new DisguiseProfile(name,rank.getUuid(),null,null);
        } else {
            disguiseProfile = new DisguiseProfile(name,rank.getUuid(),property[0],property[1]);
        }

        profile.setDisguiseProfile(disguiseProfile);
        profile.save();

        profile.refreshDisplayName();

        final Player target = Nebula.getInstance().getServer().getPlayer(uuid);

        if (target != null) {

            if (target.isDisguised()) {
                target.undisguise();
            }

            target.disguise(name);
            target.sendMessage(ChatColor.GOLD + "Now disguised as: " + ChatColor.WHITE + target.getDisplayName());

            if (!player.getUniqueId().equals(target.getUniqueId())) {
                target.sendMessage(profile.getFancyName() + ChatColor.GOLD + " is now disguised as: " + target.getDisplayName());
            }

        } else {
            player.sendMessage(profile.getFancyName() + ChatColor.GOLD + " is not online, they will be disguised as " + name + ChatColor.GOLD + " once they logon.");
        }
    }

}
