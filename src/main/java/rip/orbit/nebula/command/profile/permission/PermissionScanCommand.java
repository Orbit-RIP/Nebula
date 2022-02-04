package rip.orbit.nebula.command.profile.permission;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.profile.Profile;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.flag.Flag;
import cc.fyre.proton.command.param.Parameter;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class PermissionScanCommand {

    @Command(
            names = {"profiles scan","profiles scan"},
            async = true,
            permission = "orbit.owner"
    )
    public static void execute(CommandSender sender, @Parameter(name = "permission") String permission, @Flag(value = {"e","effective"})boolean effective) {

        new Thread(() -> {

            for (Document document : Nebula.getInstance().getProfileHandler().getCollection().find()) {

                final Profile profile = new Profile(document);

                if (!effective && !profile.getPermissions().contains(permission) || effective && !profile.getEffectivePermissions().contains(permission)) {
                    continue;
                }

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', profile.getName() + " &7has &f" + permission + "&7."));
            }
        }).start();
    }

    @Command(
            names = {"profiles scan remove","profiles scan remove"},
            async = true,
            permission = "neutron.command.permission.scan"
    )
    public static void remove(CommandSender sender, @Parameter(name = "permission") String permission, @Flag(value = {"e","effective"})boolean effective) {

        new Thread(() -> {

            for (Document document : Nebula.getInstance().getProfileHandler().getCollection().find()) {

                final Profile profile = new Profile(document);

                if (!effective && !profile.getPermissions().contains(permission) || effective && !profile.getEffectivePermissions().contains(permission)) {
                    continue;
                }

                if (effective) {
                    profile.getGrants().clear();
                } else {
                    profile.getPermissions().remove(permission);
                }
                profile.save();

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', profile.getName() + " &7has &f" + permission + "&7. &cREMOVED"));
            }
        }).start();
    }

}
