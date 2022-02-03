package rip.orbit.nebula.rank.menu.editor.menu;

import rip.orbit.nebula.rank.Rank;
import rip.orbit.nebula.rank.menu.editor.RankEditorMenu;
import rip.orbit.nebula.rank.menu.editor.menu.attribute.*;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.menu.buttons.BackButton;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import rip.orbit.nebula.rank.menu.editor.menu.attribute.*;

import java.util.*;

/**
 * @author xanderume@gmail (JavaProject)
 */
@AllArgsConstructor
public class RankModifyAttributesMenu extends Menu {

    @Getter private Rank rank;

    @Getter private static List<UUID> ignoreSave = new ArrayList<>();

    @Override
    public String getTitle(Player player) {
        return "Edit rank attributes";
    }

    @Override
    public boolean isUpdateAfterClick() {
        return true;
    }

    @Override
    public Map<Integer,Button> getButtons(Player player) {

        final Map<Integer,Button> toReturn = new HashMap<>();

        toReturn.put(0,new RankModifyWeightButton(this.rank));
        toReturn.put(1,new RankModifyColorButton(this.rank));
        toReturn.put(2,new RankModifyPrefixButton(this.rank));
        toReturn.put(3,new RankModifyPermissionsButton(this.rank));
        toReturn.put(4,new RankModifyInheritsButton(this.rank));

        toReturn.put(8,new BackButton(new RankEditorMenu()));
        return toReturn;
    }

    @Override
    public void onClose(Player player) {

        if (!ignoreSave.contains(this.rank.getUuid())) {
            this.rank.save();
        } else {
            ignoreSave.remove(this.rank.getUuid());
        }

    }
}
