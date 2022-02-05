package rip.orbit.nebula.notifications.menu.button;

import cc.fyre.proton.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.orbit.nebula.notifications.menu.ManageNotificationsMenu;
import rip.orbit.nebula.util.CC;

import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 25/11/2021 / 7:17 PM
 * LBuddyBoy Development / me.lbuddyboy.core.notifcation.menu.button
 */

@AllArgsConstructor
public class ManageNotificationButton extends Button {

	private boolean backButton;

	@Override
	public String getName(Player var1) {
		return CC.translate("&6&lManage Notifications");
	}

	@Override
	public List<String> getDescription(Player var1) {
		return CC.translate(
				"&7&m--------------",
				"&eClick to manage all global notifications",
				"&7&m--------------"
		);
	}

	@Override
	public Material getMaterial(Player var1) {
		return Material.ANVIL;
	}

	@Override
	public void clicked(Player player, int slot, ClickType clickType) {
		new ManageNotificationsMenu(backButton).openMenu(player);
	}
}
