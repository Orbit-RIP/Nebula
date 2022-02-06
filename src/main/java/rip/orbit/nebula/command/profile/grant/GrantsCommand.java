package rip.orbit.nebula.command.profile.grant;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.profile.menu.grants.GrantsMenu;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GrantsCommand {

    @Command(
            names = {"grants"},
            permission = "orbit.admin"
    )
    public static void execute(Player player,@Parameter(name = "player") UUID uuid) {
        final Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(uuid,true);

        new GrantsMenu(profile).openMenu(player);
    }

}
