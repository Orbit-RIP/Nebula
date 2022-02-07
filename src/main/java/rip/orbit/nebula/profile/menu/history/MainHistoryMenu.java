package rip.orbit.nebula.profile.menu.history;

import cc.fyre.proton.Proton;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.profile.attributes.punishment.IPunishment;
import rip.orbit.nebula.profile.attributes.punishment.impl.Punishment;
import rip.orbit.nebula.profile.attributes.punishment.impl.RemoveAblePunishment;
import rip.orbit.nebula.profile.menu.history.buttons.SelectPunishmentTypeButton;
import rip.orbit.nebula.util.CC;

import java.util.*;

@AllArgsConstructor
public class MainHistoryMenu extends Menu {

    @Getter
    private UUID uuid;

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

    @Override
    public String getTitle(Player player) {
        return "Punishment History";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {

        final Map<Integer, Button> buttons = new HashMap<>();

        Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(uuid);

        for (int i = 0; i < 9; i++) {
            buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, " "));
        }

        buttons.put(9, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, " "));
        buttons.put(17, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, " "));

        for (int i = 17; i < 27; i++) {
            buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, " "));
        }

        int[] SLOTS = {11, 12, 13, 14, 15};
        buttons.put(4, new Button() {
            @Override
            public String getName(Player player) {
                return CC.translate("&6&l" + Proton.getInstance().getUuidCache().name(uuid));
            }

            @Override
            public List<String> getDescription(Player player) {
                Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(uuid, true);
                List<String> lore = new ArrayList<>();

                lore.add(CC.MENU_BAR);
                lore.add("&6&l┃ &fRank: &r" + profile.getActiveRank().getFancyName());
                if (profile.getServerProfile().isOnline()) {
                    lore.add("&6&l┃ &fCurrent Server: &6" + profile.getServerProfile().getLastServer());
                    lore.add("&6&l┃ &fOnline Since: &6" + profile.getServerProfile().getLastSeenString());
                } else {
                    lore.add("&6&l┃ &fLast Server: &6" + profile.getServerProfile().getLastServer());
                    lore.add("&6&l┃ &fLast Seen: &6" + profile.getServerProfile().getLastSeenString() + " ago");
                }
                lore.add(CC.MENU_BAR);
                return CC.translate(lore);
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.NAME_TAG;
            }
        });


        int i = 0;
        for (Punishment.Type type : Punishment.Type.values()) {
            buttons.put(SLOTS[i], new SelectPunishmentTypeButton(type, uuid));
            ++i;
        }

        for (RemoveAblePunishment.Type type : RemoveAblePunishment.Type.values()) {
            buttons.put(SLOTS[i], new SelectPunishmentTypeButton(type, uuid));
            ++i;
        }

        return buttons;
    }

    @Override
    public int size(Player player) {
        return 27;
    }
}
