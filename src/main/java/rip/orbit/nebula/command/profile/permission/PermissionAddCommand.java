package rip.orbit.nebula.command.profile.permission;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.profile.Profile;

import rip.orbit.nebula.profile.packet.PermissionAddPacket;

import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PermissionAddCommand {

    @Command(
            names = {"profile permission add","permission add","addindividualperm"},
            permission = "orbit.headstaff"
    )
    public static void execute(CommandSender sender,@Parameter(name = "player")UUID uuid,@Parameter(name = "permission")String permission) {

        final Player player = Nebula.getInstance().getServer().getPlayer(uuid);

        Profile profile;

        if (player != null) {
            profile = Nebula.getInstance().getProfileHandler().fromUuid(player.getUniqueId());
        } else {
            profile = Nebula.getInstance().getProfileHandler().fromUuid(uuid,true);
        }

        if (profile.getPermissions().contains(permission)) {
            sender.sendMessage(profile.getFancyName() + ChatColor.RED + " already has the permission " + ChatColor.WHITE + permission + ChatColor.RED + ".");
            return;
        }

        if (Nebula.getInstance().isTestServer() && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You can't add a permission node to this player as the server is in developer mode!");
            return;
        }

        profile.getPermissions().add(permission);
        profile.save();

        if (player == null) {
            Proton.getInstance().getPidginHandler().sendPacket(new PermissionAddPacket(uuid,permission));
        }

        sender.sendMessage(ChatColor.GOLD + "Granted "+  profile.getFancyName() + ChatColor.GOLD + " permission " + ChatColor.WHITE + permission + ChatColor.GOLD + ".");
    }
}
