package rip.orbit.nebula.rank.menu.editor;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.rank.menu.editor.button.RankInfoButton;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;

import cc.fyre.proton.menu.buttons.CloseButton;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * @author xanderume@gmail (JavaProject)
 */
public class RankEditorMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "Rank Editor";
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

    @Override
    public int size(Player player) {
        return Nebula.getInstance().getRankHandler().getMenuRows() * 9;
    }

    @Override
    public Map<Integer,Button> getButtons(Player player) {

        final Map<Integer,Button> toReturn = new HashMap<>();

        Nebula.getInstance().getRankHandler().getSortedValueCache().forEach(rank -> toReturn.put(toReturn.size(),new RankInfoButton(rank,player)));

        toReturn.put((this.size(player) - 1),new CloseButton());
        return toReturn;
    }


}
