package rip.orbit.nebula.command.profile.permission;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.profile.Profile;

import rip.orbit.nebula.profile.packet.PermissionRemovePacket;
import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PermissionRemoveCommand {

    @Command(
            names = {"profile permission remove","permission remove"},
            permission = "neutron.command.permission.remove"
    )
    public static void execute(CommandSender sender,@Parameter(name = "player") UUID uuid,@Parameter(name = "permission")String permission) {

        final Player player = Nebula.getInstance().getServer().getPlayer(uuid);

        Profile profile;

        if (player != null) {
            profile = Nebula.getInstance().getProfileHandler().fromUuid(player.getUniqueId());
        } else {
            profile = Nebula.getInstance().getProfileHandler().fromUuid(uuid,true);
        }

        if (!profile.getPermissions().contains(permission)) {
            sender.sendMessage(profile.getFancyName() + ChatColor.RED + " does not have the permission " + ChatColor.WHITE + permission + ChatColor.RED + ".");
            return;
        }

        if (Nebula.getInstance().isTestServer() && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You can't remove a permission node from this player as the server is in developer mode!");
            return;
        }

        profile.getPermissions().remove(permission);
        profile.save();

        if (player == null) {
            Proton.getInstance().getPidginHandler().sendPacket(new PermissionRemovePacket(uuid,permission));
        }

        sender.sendMessage(ChatColor.GOLD + "Removed permission "+ ChatColor.WHITE + permission + ChatColor.GOLD + " for " + profile.getFancyName() + ChatColor.GOLD + ".");
    }

}
