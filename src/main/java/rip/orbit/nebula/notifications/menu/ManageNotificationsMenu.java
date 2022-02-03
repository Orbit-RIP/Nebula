package rip.orbit.nebula.notifications.menu;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.notifications.Notification;
import rip.orbit.nebula.notifications.packet.NotificationUpdatePacket;
import cc.fyre.proton.Proton;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 26/11/2021 / 12:48 AM
 * LBuddyBoy Development / me.lbuddyboy.core.notifcation.menu
 */
public class ManageNotificationsMenu extends PaginatedMenu {
	@Override
	public String getPrePaginatedTitle(Player var1) {
		return "Notifications Manager";
	}

	@Override
	public Map<Integer, Button> getAllPagesButtons(Player var1) {
		Map<Integer, Button> buttons = new HashMap<>();

		int i = 0;
		for (Notification notification : Nebula.getInstance().getNotificationHandler().getNotifications()) {
			buttons.put(i, new Button() {
				@Override
				public String getName(Player var1) {
					return notification.getTitle();
				}

				@Override
				public List<String> getDescription(Player var1) {
					List<String> lore = new ArrayList<>();

					lore.add("&7Click to delete this global reminder");
					return lore;
				}

				@Override
				public Material getMaterial(Player var1) {
					return Material.BOOK;
				}

				@Override
				public void clicked(Player player, int slot, ClickType clickType) {
					player.sendMessage(ChatColor.GREEN + "Successfully deleted that notification.");

					Nebula.getInstance().getNotificationHandler().deleteNotification(notification);
					Nebula.getInstance().getNotificationHandler().deleteNotificationFromDB(notification);

					Proton.getInstance().getPidginHandler().sendPacket(new NotificationUpdatePacket(notification.getId()));
				}
			});
			++i;
		}

		return buttons;
	}

	@Override
	public boolean isAutoUpdate() {
		return true;
	}
}
