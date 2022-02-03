package rip.orbit.nebula.command.profile;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.profile.attributes.punishment.impl.RemoveAblePunishment;
import rip.orbit.nebula.profile.menu.HistoryMenu;
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
        final Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(uuid,true);

        new HistoryMenu(profile, RemoveAblePunishment.Type.BAN).openMenu(player);
    }

}
