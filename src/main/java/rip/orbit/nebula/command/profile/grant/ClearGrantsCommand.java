package rip.orbit.nebula.command.profile.grant;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.command.CommandSender;
import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.util.CC;

public class ClearGrantsCommand {

    @Command(
            names = {"cleargrants"},
            permission = "orbit.headstaff"
    )
    public static void cleargrants(CommandSender sender, @Parameter(name = "player") String name) {
        final Profile profile = Nebula.getInstance().getProfileHandler().fromName(name);

        if (profile.getActiveGrants().isEmpty()) {
            sender.sendMessage(CC.RED + "This player has no grants!");
            return;
        }

        profile.getActiveGrants().clear();
        profile.save();

    }

}
