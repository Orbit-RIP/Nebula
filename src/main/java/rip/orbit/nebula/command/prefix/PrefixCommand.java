package rip.orbit.nebula.command.prefix;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.profile.menu.PrefixMenu;
import cc.fyre.proton.command.Command;
import org.bukkit.entity.Player;

public class PrefixCommand {

    @Command(
            names = {"prefix", "tag"},
            permission = ""
     )
    public static void execute(Player player) {
        new PrefixMenu(Nebula.getInstance().getProfileHandler().fromUuid(player.getUniqueId())).openMenu(player);
    }

}