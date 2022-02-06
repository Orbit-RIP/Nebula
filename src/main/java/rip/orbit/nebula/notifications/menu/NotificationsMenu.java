package rip.orbit.nebula.notifications.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.menu.buttons.BackButton;
import org.apache.commons.lang.math.IntRange;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.notifications.Notification;
import rip.orbit.nebula.notifications.menu.button.ManageNotificationButton;
import rip.orbit.nebula.notifications.menu.button.NotificationButton;
import rip.orbit.nebula.notifications.menu.button.ReadNotificationsButton;
import rip.orbit.nebula.notifications.menu.button.ViewAllReadNotificationsButton;
import rip.orbit.nebula.profile.menu.profile.ProfileMenu;
import rip.orbit.nebula.util.CC;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 25/11/2021 / 4:32 PM
 * LBuddyBoy Development / me.lbuddyboy.core.notifcation.menu
 */

public class NotificationsMenu extends Menu {

	private UUID uuid;
	private boolean backButton;

	private int page = 1;

	private static int[] BLACK_HOLDER_SLOTS = {
			9, 10, 17, 16,
			18, 26, 27, 28, 35, 34
	};

	private static int[] FRIEND_SLOTS = {
			11, 12, 13, 14, 15,
			20, 21, 22, 23, 24,
			29, 30, 31, 32, 33
	};

	public NotificationsMenu(UUID uuid, boolean backButton) {
		this.uuid = uuid;
		this.backButton = backButton;
	}


	@Override
	public String getTitle(Player var1) {
		return "Notifications";
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		for (int i = 0; i < 9; i++) {
			buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 1, " "));
		}

		for (int i = 36; i < 45; i++) {
			buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 1, " "));
		}

		for (int slot : BLACK_HOLDER_SLOTS) {
			buttons.put(slot, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, " "));
		}

		if (backButton) {
			buttons.put(2, new ViewAllReadNotificationsButton(true));
			buttons.put(4, new BackButton(new ProfileMenu(this.uuid)));
			buttons.put(6, new ReadNotificationsButton());
		} else {
			buttons.put(3, new ViewAllReadNotificationsButton(false));
			buttons.put(5, new ReadNotificationsButton());
		}

		IntRange range;
		if (page == 1) {
			range = new IntRange(1, FRIEND_SLOTS.length);
		} else {
			range = new IntRange(((page - 1) * 15) + 1, page * FRIEND_SLOTS.length);
		}

		int skipped = 1;
		int slotIndex = 0;
		for (Notification notification : Nebula.getInstance().getNotificationHandler().getNotifications()) {
			if (notification.getMarkedReadPlayers().contains(uuid)) continue;

			if (skipped < range.getMinimumInteger()) {
				skipped++;
				continue;
			}

			buttons.put(FRIEND_SLOTS[slotIndex], new NotificationButton(notification));
			if (slotIndex >= 14) {
				break;
			} else {
				slotIndex++;
			}
		}

		buttons.put(19, new PreviousPageButton());
		buttons.put(25, new NextPageButton());

		if (player.hasPermission("orbit.headstaff")) {
			buttons.put(44, new ManageNotificationButton(backButton));
		}

		return buttons;
	}

	@Override
	public int size(Player player) {
		return 45;
	}

	private class PreviousPageButton extends Button {
		@Override
		public String getName(Player player) {
			if (page > 1) {
				return CC.RED + CC.BOLD + "Previous Page";
			} else {
				return CC.GRAY + CC.BOLD + "No Previous Page";
			}
		}

		@Override
		public List<String> getDescription(Player player) {
			return Collections.emptyList();
		}

		@Override
		public Material getMaterial(Player player) {
			if (page > 1) {
				return Material.REDSTONE_TORCH_ON;
			} else {
				return Material.LEVER;
			}
		}

		@Override
		public void clicked(Player player, int slot, ClickType clickType) {
			if (clickType.isLeftClick() && page > 1) {
				page -= 1;
				openMenu(player);
			}
		}
	}

	private class NextPageButton extends Button {
		@Override
		public String getName(Player player) {
			if (page < getMaxPages(player)) {
				return CC.RED + CC.BOLD + "Next Page";
			} else {
				return CC.GRAY + CC.BOLD + "No Next Page";
			}
		}

		@Override
		public List<String> getDescription(Player player) {
			return Collections.emptyList();
		}

		@Override
		public Material getMaterial(Player player) {
			if (page < getMaxPages(player)) {
				return Material.REDSTONE_TORCH_ON;
			} else {
				return Material.LEVER;
			}
		}

		@Override
		public void clicked(Player player, int slot, ClickType clickType) {
			if (clickType.isLeftClick() && page < getMaxPages(player)) {
				page += 1;
				openMenu(player);
			}
		}
	}

	private int getMaxPages(Player player) {

		List<Notification> items = Nebula.getInstance().getNotificationHandler().getNotifications().stream().filter(notification -> !notification.getMarkedReadPlayers().contains(player.getUniqueId())).collect(Collectors.toList());

		if (items.size() == 0) {
			return 1;
		} else {
			return (int) Math.ceil(items.size() / (double) (FRIEND_SLOTS.length));
		}
	}

	@Override
	public boolean isAutoUpdate() {
		return true;
	}
}
