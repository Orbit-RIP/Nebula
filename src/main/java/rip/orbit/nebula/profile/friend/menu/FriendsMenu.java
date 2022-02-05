package rip.orbit.nebula.profile.friend.menu;

import cc.fyre.proton.Proton;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.menu.buttons.BackButton;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.math.IntRange;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.profile.friend.menu.button.FilterFriendsButton;
import rip.orbit.nebula.profile.friend.menu.button.FriendButton;
import rip.orbit.nebula.profile.friend.menu.button.ViewFriendRequestsButton;
import rip.orbit.nebula.profile.menu.profile.ProfileMenu;
import rip.orbit.nebula.util.CC;

import java.util.*;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 04/02/2022 / 9:05 PM
 * Orbit Dev / rip.orbit.nebula.profile.friend.menu
 */

public class FriendsMenu extends Menu {

	private UUID uuid;
	private boolean back;
	@Getter
	@Setter private boolean online = true;

	private static int[] BLACK_HOLDER_SLOTS = {
			9, 10, 17, 16,
			18, 26, 27, 28, 35, 34
	};

	private static int[] FRIEND_SLOTS = {
			11, 12, 13, 14, 15,
			20, 21, 22, 23, 24,
			29, 30, 31, 32, 33
	};

	private int page = 1;

	public FriendsMenu(UUID uuid, boolean back) {
		this.uuid = uuid;
		this.back = back;
	}

	@Override
	public String getTitle(Player player) {
		return (uuid == player.getUniqueId() ? "Your Friends" : "Friends: " + Proton.getInstance().getUuidCache().name(uuid));
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {

		Map<Integer, Button> buttons = new HashMap<>();

		Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(this.uuid, true);

		for (int i = 0; i < 9; i++) {
			buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 1, " "));
		}

		for (int i = 36; i < 45; i++) {
			buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 1, " "));
		}

		for (int slot : BLACK_HOLDER_SLOTS) {
			buttons.put(slot, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, " "));
		}

		if (uuid == player.getUniqueId()) {
			buttons.put(2, new ViewFriendRequestsButton(this.uuid, back));
		}

		if (back) {
			buttons.put(4, new BackButton(new ProfileMenu(this.uuid)));
		}

		buttons.put(6, new FilterFriendsButton(this.uuid, this));

		IntRange range;
		if (page == 1) {
			range = new IntRange(1, FRIEND_SLOTS.length);
		} else {
			range = new IntRange(((page - 1) * 15) + 1, page * FRIEND_SLOTS.length);
		}

		int skipped = 1;
		int slotIndex = 0;
		for (UUID friend : profile.getFriends()) {
			Profile friendProfile = Nebula.getInstance().getProfileHandler().fromUuid(friend, true);
			if (isOnline()) {
				if (!friendProfile.getServerProfile().isOnline()) continue;
			} else {
				if (friendProfile.getServerProfile().isOnline()) continue;
			}
			if (skipped < range.getMinimumInteger()) {
				skipped++;
				continue;
			}

			buttons.put(FRIEND_SLOTS[slotIndex], new FriendButton(this.uuid, friend));
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

		Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(this.uuid, true);

		List<UUID> items = new ArrayList<>(profile.getFriends());

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
