package rip.orbit.nebula.profile.menu.profile;

import cc.fyre.proton.Proton;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import rip.orbit.nebula.profile.menu.profile.buttons.OpenFriendsButton;
import rip.orbit.nebula.profile.menu.profile.buttons.OpenGlobalStatsButton;
import rip.orbit.nebula.profile.menu.profile.buttons.OpenNotificationsButton;
import rip.orbit.nebula.profile.menu.profile.buttons.ProfileViewButton;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 02/02/2022 / 8:00 PM
 * Orbit Dev / rip.orbit.nebula.profile.menu
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
			buttons.put(22, new OpenFriendsButton(this.uuid, true));
			buttons.put(4, new ProfileViewButton(this.uuid));
			buttons.put(24, new OpenGlobalStatsButton(this.uuid));
		} else {
			buttons.put(4, new ProfileViewButton(this.uuid));
			buttons.put(21, new OpenFriendsButton(this.uuid, false));
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
