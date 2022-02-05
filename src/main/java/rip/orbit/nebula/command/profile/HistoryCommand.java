package rip.orbit.nebula.command.profile;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.entity.Player;
import rip.orbit.nebula.profile.menu.MainHistoryMenu;

import java.util.UUID;

public class HistoryCommand {

    @Command(
            names = {"history","c","check"},
            permission = "orbit.trialmod"
    )
    public static void execute(Player player,@Parameter(name = "player")UUID uuid) {
        new MainHistoryMenu(uuid).openMenu(player);
    }

}
