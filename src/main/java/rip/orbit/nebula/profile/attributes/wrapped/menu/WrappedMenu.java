package rip.orbit.nebula.profile.attributes.wrapped.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.menu.buttons.BackButton;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.math.IntRange;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.notifications.Notification;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.profile.attributes.wrapped.IWrapped;
import rip.orbit.nebula.profile.attributes.wrapped.WrappedType;
import rip.orbit.nebula.profile.attributes.wrapped.menu.button.WrappedButton;
import rip.orbit.nebula.profile.menu.profile.GlobalStatsMenu;
import rip.orbit.nebula.profile.menu.profile.ProfileMenu;
import rip.orbit.nebula.util.CC;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 07/02/2022 / 8:49 PM
 * Nebula / rip.orbit.nebula.profile.attributes.wrapped.menu
 */

@AllArgsConstructor
public class WrappedMenu extends Menu {

	private UUID uuid;
	private boolean backButton;
	private WrappedType type;

	private int page = 1;

	private static int[] BLACK_HOLDER_SLOTS = {
			9, 10, 17, 16,
			18, 26, 27, 28, 35, 34
	};

	private static int[] WRAP_SLOTS = {
			11, 12, 13, 14, 15,
			20, 21, 22, 23, 24,
			29, 30, 31, 32, 33
	};

	@Override
	public String getTitle(Player var1) {
		return "Wrapped Overview";
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
			buttons.put(4, new BackButton(new GlobalStatsMenu(this.uuid, backButton)));
		}

		IntRange range;
		if (page == 1) {
			range = new IntRange(1, WRAP_SLOTS.length);
		} else {
			range = new IntRange(((page - 1) * 15) + 1, page * WRAP_SLOTS.length);
		}

		int skipped = 1;
		int slotIndex = 0;

		Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(this.uuid, true);
		for (IWrapped wrapped : profile.getWraps()) {

			if (type == wrapped.getWrappedType()) continue;

			if (skipped < range.getMinimumInteger()) {
				skipped++;
				continue;
			}

			buttons.put(WRAP_SLOTS[slotIndex], new WrappedButton(wrapped));
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
		List<IWrapped> items = profile.getWraps().stream().filter(w -> w.getWrappedType() != this.type).collect(Collectors.toList());

		if (items.size() == 0) {
			return 1;
		} else {
			return (int) Math.ceil(items.size() / (double) (WRAP_SLOTS.length));
		}
	}

	@Override
	public boolean isAutoUpdate() {
		return true;
	}
}

