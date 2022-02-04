package rip.orbit.nebula.command.profile.permission;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.profile.Profile;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.flag.Flag;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PermissionListCommand {

    @Command(
            names = {"profile permission list","permission list"},
            permission = "orbit.headstaff"
    )
    public static void execute(CommandSender sender,@Parameter(name = "player") UUID uuid,@Flag(value = {"e","effective"})boolean effective) {

        final Player player = Nebula.getInstance().getServer().getPlayer(uuid);

        Profile profile;

        if (player != null) {
            profile = Nebula.getInstance().getProfileHandler().fromUuid(player.getUniqueId());
        } else {
            profile = Nebula.getInstance().getProfileHandler().fromUuid(uuid,true);
        }

        sender.sendMessage(profile.getFancyName() + ChatColor.GOLD + "'s permissions:");

        for (String permission : effective ? profile.getEffectivePermissions():profile.getPermissions()) {
            sender.sendMessage(ChatColor.GRAY + " - " + ChatColor.WHITE + permission + " " + (effective ? (profile.getEffectivePermissions().contains(permission) ? ChatColor.GRAY + "(Effective)":""):""));
        }
    }

}
