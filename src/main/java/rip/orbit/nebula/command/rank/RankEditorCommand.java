package rip.orbit.nebula.command.rank;

import rip.orbit.nebula.rank.menu.editor.RankEditorMenu;
import cc.fyre.proton.command.Command;
import org.bukkit.entity.Player;

/**
 * @author xanderume@gmail (JavaProject)
 */
public class RankEditorCommand {

    @Command(
            names = {"rank editor"},
            permission = "orbit.headstaff",
            hidden = true
    )
    public static void execute(Player player) {
        new RankEditorMenu().openMenu(player);
    }

}
