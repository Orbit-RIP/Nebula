package rip.orbit.nebula.profile.menu;

import rip.orbit.nebula.NebulaConstants;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.profile.attributes.punishment.comparator.PunishmentDateComparator;
import rip.orbit.nebula.profile.attributes.punishment.impl.RemoveAblePunishment;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import cc.fyre.proton.util.TimeUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;

@AllArgsConstructor
public class HistoryMenu extends PaginatedMenu {

    @Getter private Profile profile;
    @Getter private RemoveAblePunishment.Type type;

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return this.profile.getName();
    }

    @Override
    public Map<Integer,Button> getAllPagesButtons(Player player) {

        final Map<Integer,Button> toReturn = new HashMap<>();

        this.profile.getPunishments().stream().sorted(new PunishmentDateComparator().reversed()).filter(iPunishment -> iPunishment instanceof RemoveAblePunishment && ((RemoveAblePunishment)iPunishment).getType() == type).forEach(iPunishment -> toReturn.put(toReturn.size(),new Button() {

            @Override
            public String getName(Player player) {
                return ChatColor.RED + TimeUtils.formatIntoCalendarString(new Date(iPunishment.getExecutedAt()));
            }

            @Override
            public List<String> getDescription(Player player) {

                final RemoveAblePunishment removeAblePunishment = (RemoveAblePunishment)iPunishment;

                final List<String> toReturn = new ArrayList<>();

                toReturn.add(ChatColor.GRAY + NebulaConstants.MENU_BAR);
                toReturn.add(ChatColor.YELLOW + "By: " + (player.hasPermission(NebulaConstants.ADMIN_PERMISSION) ? removeAblePunishment.getExecutedByFancyName() : ChatColor.MAGIC + removeAblePunishment.getExecutedByFancyName()));
                toReturn.add(ChatColor.YELLOW + "Silent: " + ChatColor.RED + (removeAblePunishment.getExecutedSilent() ? "Yes":"No"));
                toReturn.add(ChatColor.YELLOW + "Reason: " + ChatColor.RED + removeAblePunishment.getExecutedReason());

                if (removeAblePunishment.isActive() && !removeAblePunishment.isPermanent()) {
                    toReturn.add(ChatColor.GRAY + NebulaConstants.MENU_BAR);
                    toReturn.add(ChatColor.YELLOW + "Expires: " + ChatColor.RED + removeAblePunishment.getRemainingString());
                } else {
                    toReturn.add(ChatColor.GRAY + NebulaConstants.MENU_BAR);
                    toReturn.add(ChatColor.YELLOW + "Expires: " + ChatColor.RED + "Never");
                }

                if (removeAblePunishment.isPardoned()) {
                    toReturn.add(ChatColor.GRAY + NebulaConstants.MENU_BAR);
                    toReturn.add(ChatColor.YELLOW + "Pardoned By: " + removeAblePunishment.getPardonedByFancyName());
                    toReturn.add(ChatColor.YELLOW + "Pardoned At: " + ChatColor.RED + TimeUtils.formatIntoCalendarString(new Date(removeAblePunishment.getPardonedAt())));
                    toReturn.add(ChatColor.YELLOW + "Pardoned Silent: " + ChatColor.RED + (removeAblePunishment.getPardonedSilent() ? "Yes":"No"));
                    toReturn.add(ChatColor.YELLOW + "Pardoned Reason: " + ChatColor.RED + removeAblePunishment.getPardonedReason());
                }

                toReturn.add(ChatColor.GRAY + NebulaConstants.MENU_BAR);

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.WOOL;
            }

            @Override
            public byte getDamageValue(Player player) {

                final RemoveAblePunishment removeAblePunishment = (RemoveAblePunishment)iPunishment;

                if (removeAblePunishment.isActive()) {
                    return removeAblePunishment.isPermanent() ? DyeColor.GREEN.getWoolData():DyeColor.YELLOW.getWoolData();
                }

                return DyeColor.RED.getWoolData();
            }

            @Override
            public void clicked(Player player,int i,ClickType clickType) {
            }
        }));

        return toReturn;
    }

    @Override
    public Map<Integer,Button> getGlobalButtons(Player player) {

        final Map<Integer,Button> toReturn = new HashMap<>();

        toReturn.put(4,new Button() {

            @Override
            public String getName(Player player) {
                return ChatColor.RED + "Filter";
            }

            @Override
            public List<String> getDescription(Player player) {

                final List<String> toReturn = new ArrayList<>();

                toReturn.add(ChatColor.GRAY + NebulaConstants.MENU_BAR);

                for (RemoveAblePunishment.Type type : RemoveAblePunishment.Type.values()) {
                    toReturn.add((HistoryMenu.this.type == type ? ChatColor.GREEN:ChatColor.GRAY) + " » " + type.getReadable() + "s");
                }

                toReturn.add(ChatColor.GRAY + NebulaConstants.MENU_BAR);

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.HOPPER;
            }

            @Override
            public void clicked(Player player,int slot,ClickType clickType) {
                HistoryMenu.this.type = type.next(type);
            }
        });

        return toReturn;
    }
}
