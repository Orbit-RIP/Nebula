package rip.orbit.nebula.notifications.menu.button;

import rip.orbit.nebula.notifications.menu.ReadNotificationsMenu;
import rip.orbit.nebula.util.CC;
import cc.fyre.proton.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 25/11/2021 / 7:56 PM
 * LBuddyBoy Development / me.lbuddyboy.core.notifcation.menu.button
 */

@AllArgsConstructor
public class ViewAllReadNotificationsButton extends Button {

	private boolean backButton;

	@Override
	public String getName(Player var1) {
		return CC.GOLD + "View All Read Notifications";
	}

	@Override
	public List<String> getDescription(Player var1) {
		return null;
	}

	@Override
	public Material getMaterial(Player var1) {
		return Material.BOOKSHELF;
	}

	@Override
	public void clicked(Player player, int slot, ClickType clickType) {
		new ReadNotificationsMenu(player.getUniqueId(), backButton).openMenu(player);
	}
}
