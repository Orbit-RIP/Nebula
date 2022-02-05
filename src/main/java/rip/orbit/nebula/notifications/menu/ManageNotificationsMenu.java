package rip.orbit.nebula.notifications.menu;

import cc.fyre.proton.Proton;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.math.IntRange;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.notifications.Notification;
import rip.orbit.nebula.notifications.menu.button.ViewAllNotificationsButton;
import rip.orbit.nebula.notifications.packet.NotificationUpdatePacket;
import rip.orbit.nebula.util.CC;

import java.util.*;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 26/11/2021 / 12:48 AM
 * LBuddyBoy Development / me.lbuddyboy.core.notifcation.menu
 */

@AllArgsConstructor
public class ManageNotificationsMenu extends Menu {

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

	public ManageNotificationsMenu(boolean backButton) {
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
			buttons.put(4, new ViewAllNotificationsButton(true));
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

			if (skipped < range.getMinimumInteger()) {
				skipped++;
				continue;
			}

			buttons.put(FRIEND_SLOTS[slotIndex], new ManagedNotificationButton(notification));
			if (slotIndex >= 14) {
				break;
			} else {
				slotIndex++;
			}
		}

		buttons.put(19, new PreviousPageButton());
		buttons.put(25, new NextPageButton());

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
			if (page < getMaxPages()) {
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
			if (page < getMaxPages()) {
				return Material.REDSTONE_TORCH_ON;
			} else {
				return Material.LEVER;
			}
		}

		@Override
		public void clicked(Player player, int slot, ClickType clickType) {
			if (clickType.isLeftClick() && page < getMaxPages()) {
				page += 1;
				openMenu(player);
			}
		}
	}

	private int getMaxPages() {

		List<Notification> items = new ArrayList<>(Nebula.getInstance().getNotificationHandler().getNotifications());

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

	@AllArgsConstructor
	private class ManagedNotificationButton extends Button {

		private Notification notification;

		@Override
		public String getName(Player var1) {
			return CC.translate("&6" + notification.getTitle());
		}

		@Override
		public List<String> getDescription(Player var1) {
			List<String> lore = new ArrayList<>();

			lore.add("&7Click to delete this global reminder");
			return CC.translate(lore);
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
	}
}
