package rip.orbit.nebula.rank.menu.editor.menu;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.rank.Rank;
import rip.orbit.nebula.rank.menu.editor.RankEditorMenu;
import cc.fyre.proton.menu.menus.ConfirmMenu;

import cc.fyre.proton.util.Callback;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author xanderume@gmail (JavaProject)
 */


public class RankDeleteMenu extends ConfirmMenu {

    public RankDeleteMenu(Rank rank,Player player) {
        super("Delete rank?",(Callback<Boolean>)value -> {

            if (!value) {
                player.sendMessage(ChatColor.RED + "Cancelled deleting rank " + rank.getFancyName() + ChatColor.RED + ".");
            } else {

                if (Nebula.getInstance().getRankHandler().getCache().get(rank.getUuid()) != null) {
                    rank.delete();
                }

                player.sendMessage(ChatColor.GREEN + "Deleted rank " + rank.getFancyName() + ChatColor.GREEN + ".");
            }

            new RankEditorMenu().openMenu(player);
        });
    }

}
