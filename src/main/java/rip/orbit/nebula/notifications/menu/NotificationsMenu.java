package rip.orbit.nebula.notifications.menu;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.notifications.Notification;
import rip.orbit.nebula.notifications.menu.button.NotificationButton;
import rip.orbit.nebula.notifications.menu.button.ViewAllReadNotificationsButton;
import rip.orbit.nebula.profile.menu.ProfileMenu;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.buttons.BackButton;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 25/11/2021 / 4:32 PM
 * LBuddyBoy Development / me.lbuddyboy.core.notifcation.menu
 */

@AllArgsConstructor
public class NotificationsMenu extends PaginatedMenu {

	private UUID uuid;
	private boolean backButton;

	@Override
	public String getPrePaginatedTitle(Player var1) {
		return "Notifications";
	}

	@Override
	public Map<Integer, Button> getAllPagesButtons(Player var1) {
		Map<Integer, Button> buttons = new HashMap<>();

		int i = 0;
		for (Notification notification : Nebula.getInstance().getNotificationHandler().getNotifications()) {
			if (notification.getMarkedReadPlayers().contains(uuid)) continue;
			buttons.put(i, new NotificationButton(notification));
			++i;
		}

		return buttons;
	}

	@Override
	public Map<Integer, Button> getGlobalButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		if (backButton) {
			buttons.put(3, new ViewAllReadNotificationsButton(true));
			buttons.put(5, new BackButton(new ProfileMenu(this.uuid)));
		} else {
			buttons.put(4, new ViewAllReadNotificationsButton(false));
		}

		return buttons;
	}

	@Override
	public boolean isAutoUpdate() {
		return true;
	}
}
