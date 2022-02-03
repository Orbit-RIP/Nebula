package cc.fyre.neutron.profile.menu;

import cc.fyre.neutron.profile.menu.button.OpenGlobalStatsButton;
import cc.fyre.neutron.profile.menu.button.OpenNotificationsButton;
import cc.fyre.neutron.profile.menu.button.ProfileViewButton;
import cc.fyre.proton.Proton;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 02/02/2022 / 8:00 PM
 * Orbit Dev / cc.fyre.neutron.profile.menu
 */

@AllArgsConstructor
public class ProfileMenu extends Menu {

	private UUID uuid;

	@Override
	public String getTitle(Player player) {
		return (uuid == player.getUniqueId() ? "Your Profile" : "Profile: " + Proton.getInstance().getUuidCache().name(uuid));
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		for (int i = 8; i < 36; i++) {
			buttons.put(i, Button.fromItem(ItemBuilder.of(Material.STAINED_GLASS_PANE).name(" ").data((short) 15).build()));
		}

		for (int i = 0; i < 9; i++) {
			buttons.put(i, Button.fromItem(ItemBuilder.of(Material.STAINED_GLASS_PANE).name(" ").data((short) 1).build()));
		}

		for (int i = 36; i < 45; i++) {
			if (buttons.containsKey(i)) continue;
			buttons.put(i, Button.fromItem(ItemBuilder.of(Material.STAINED_GLASS_PANE).name(" ").data((short) 1).build()));
		}

		if (this.uuid == player.getUniqueId()) {
			buttons.put(20, new OpenNotificationsButton(this.uuid, true));
			buttons.put(22, new ProfileViewButton(this.uuid));
			buttons.put(24, new OpenGlobalStatsButton(this.uuid));
		} else {
			buttons.put(21, new ProfileViewButton(this.uuid));
			buttons.put(23, new OpenGlobalStatsButton(this.uuid));
		}

		return buttons;
	}

	@Override
	public boolean isAutoUpdate() {
		return true;
	}

	@Override
	public int size(Player player) {
		return 45;
	}
}
