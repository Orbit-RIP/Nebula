package cc.fyre.neutron.command.profile;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.punishment.impl.RemoveAblePunishment;
import cc.fyre.neutron.profile.menu.HistoryMenu;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.entity.Player;

import java.util.UUID;

public class HistoryCommand {

    @Command(
            names = {"history","c","check"},
            permission = "neutron.command.history"
    )
    public static void execute(Player player,@Parameter(name = "player")UUID uuid) {
        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(uuid,true);

        new HistoryMenu(profile,RemoveAblePunishment.Type.BAN).openMenu(player);
    }

}
