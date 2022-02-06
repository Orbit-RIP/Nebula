package rip.orbit.nebula.command.profile.punishment;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.command.CommandSender;
import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.util.CC;

import java.util.UUID;

public class ClearPunishmentsCommand {

    @Command(
            names = {"clearpunishments","clearallpunishments"},
            permission = "orbit.headstaff"
    )
    public static void clearall(CommandSender sender, @Parameter(name = "player") UUID uuid) {
        final Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(uuid,true);

        if (profile.getActivePunishments().isEmpty()) {
            sender.sendMessage(CC.RED + "This player has no punishments!");
            return;
        }

        profile.getActivePunishments().clear();
        profile.save();
    }

}
