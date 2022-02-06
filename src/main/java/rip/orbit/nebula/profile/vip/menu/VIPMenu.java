package rip.orbit.nebula.profile.vip.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.profile.vip.menu.buttons.VIPButton;
import rip.orbit.nebula.profile.vip.menu.buttons.VIPColorButton;
import rip.orbit.nebula.util.CC;
import rip.orbit.nebula.util.ColorUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 05/02/2022 / 8:16 PM
 * Nebula / rip.orbit.nebula.profile.vip.menu
 */
public class VIPMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return CC.translate("&6&lVIP Status");
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(player.getUniqueId());
		int data = ColorUtil.COLOR_GLASS_MAP.get(profile.getServerProfile().getVipStatusColor());

		for (int i = 8; i < 36; i++) {
			buttons.put(i, Button.fromItem(ItemBuilder.of(Material.STAINED_GLASS_PANE).name(" ").data((short) 15).build()));
		}

		for (int i = 0; i < 9; i++) {
			buttons.put(i, Button.fromItem(ItemBuilder.of(Material.STAINED_GLASS_PANE).name(" ").data((short) data).build()));
		}

		for (int i = 36; i < 45; i++) {
			if (buttons.containsKey(i)) continue;
			buttons.put(i, Button.fromItem(ItemBuilder.of(Material.STAINED_GLASS_PANE).name(" ").data((short) data).build()));
		}

		buttons.put(4, new VIPButton());
		buttons.put(21, new VIPColorButton());

		return buttons;
	}

	@Override
	public boolean isUpdateAfterClick() {
		return true;
	}

	@Override
	public boolean isAutoUpdate() {
		return true;
	}
}
