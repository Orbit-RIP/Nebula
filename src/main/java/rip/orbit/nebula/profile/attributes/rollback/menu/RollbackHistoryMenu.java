package rip.orbit.nebula.profile.attributes.rollback.menu;

import rip.orbit.nebula.profile.attributes.rollback.Rollback;
import rip.orbit.nebula.profile.attributes.rollback.comparator.RollbackComparator;
import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.util.FormatUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class RollbackHistoryMenu extends Menu {

    @Getter private List<Rollback> rollbacks;

    @Override
    public String getTitle(Player player) {
        return ChatColor.RED + "Rollbacks";
    }

    @Override
    public Map<Integer,Button> getButtons(Player player) {

        final Map<Integer,Button> toReturn = new HashMap<>();

        for (Rollback rollback : this.rollbacks.stream().sorted(new RollbackComparator()).collect(Collectors.toList())) {

            toReturn.put(toReturn.size(),new Button() {

                @Override
                public String getName(Player player) {
                    return ChatColor.RED + TimeUtils.formatIntoCalendarString(new Date(rollback.getExecutedAt()));
                }

                @Override
                public List<String> getDescription(Player player) {

                    final List<String> toReturn = new ArrayList<>();

                    toReturn.add(ChatColor.YELLOW + "By: " + rollback.getExecutedByFancyName());
                    toReturn.add(ChatColor.YELLOW + "For: " + Nebula.getInstance().getProfileHandler().fromUuid(rollback.getExecutorTarget(),true).getFancyName());
                    toReturn.add(ChatColor.YELLOW + "Time: " + FormatUtil.millisToRoundedTime(rollback.getDuration(),true));
                    toReturn.add(ChatColor.YELLOW + "Type: " + rollback.getType().name());
                    toReturn.add(ChatColor.YELLOW + "Reason: " + rollback.getExecutedReason());

                    return toReturn;
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.EMPTY_MAP;
                }

            });
        }

        return toReturn;
    }

}
