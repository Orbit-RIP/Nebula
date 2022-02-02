package cc.fyre.neutron.command.profile.grant;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.menu.GrantsMenu;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GrantsCommand {

    @Command(
            names = {"grants"},
            permission = "neutron.command.grants"
    )
    public static void execute(Player player,@Parameter(name = "player") UUID uuid) {
        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(uuid,true);

        new GrantsMenu(profile).openMenu(player);
    }

}
