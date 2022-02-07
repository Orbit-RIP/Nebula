package rip.orbit.nebula.profile.menu.history;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.menu.buttons.BackButton;
import cc.fyre.proton.util.TimeUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang.math.IntRange;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.NebulaConstants;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.profile.attributes.punishment.IPunishment;
import rip.orbit.nebula.profile.attributes.punishment.IPunishmentType;
import rip.orbit.nebula.profile.attributes.punishment.comparator.PunishmentDateComparator;
import rip.orbit.nebula.profile.attributes.punishment.impl.Punishment;
import rip.orbit.nebula.profile.attributes.punishment.impl.RemoveAblePunishment;
import rip.orbit.nebula.profile.menu.history.MainHistoryMenu;
import rip.orbit.nebula.util.CC;

import java.util.*;
import java.util.stream.Collectors;

public class HistoryMenu extends Menu {

	@Getter
	private UUID uuid;
	@Getter
	private IPunishmentType type;

	private static int[] SLOTS = {10, 11, 12, 13, 14, 15, 16};

	public HistoryMenu(UUID uuid, IPunishmentType type) {
		this.uuid = uuid;
		this.type = type;
	}

	private int page = 1;

	@Override
	public String getTitle(Player player) {
		Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(uuid, true);

		return profile.getName();
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {

		final Map<Integer, Button> toReturn = new HashMap<>();

		Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(uuid, true);

		IntRange range;
		if (page == 1) {
			range = new IntRange(1, SLOTS.length);
		} else {
			range = new IntRange(((page - 1) * 7) + 1, page * SLOTS.length);
		}

		int skipped = 1;
		int slotIndex = 0;
		for (IPunishment punishment : getSortedPunishments(profile)) {
			if (skipped < range.getMinimumInteger()) {
				skipped++;
				continue;
			}

			toReturn.put(SLOTS[slotIndex], new PunishmentButton(punishment));
			if (slotIndex >= 6) {
				break;
			} else {
				slotIndex++;
			}
		}

		toReturn.put(4, new BackButton(new MainHistoryMenu(uuid)));
		toReturn.put(0, new PreviousPageButton());
		toReturn.put(8, new NextPageButton());

		return toReturn;
	}

	@Override
	public boolean isPlaceholder() {
		return true;
	}

	@Override
	public int size(Player player) {
		return 27;
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

		List<IPunishment> items = new ArrayList<>(getSortedPunishments(profile));

		if (items.size() == 0) {
			return 1;
		} else {
			return (int) Math.ceil(items.size() / (double) (SLOTS.length));
		}
	}

	@Override
	public boolean isAutoUpdate() {
		return true;
	}

	public List<IPunishment> getSortedPunishments(Profile profile) {
		return profile.getPunishments().stream().sorted(new PunishmentDateComparator().reversed()).filter(iPunishment -> (iPunishment instanceof RemoveAblePunishment ? ((RemoveAblePunishment) iPunishment).getType() == type : ((Punishment) iPunishment).getType() == type)).collect(Collectors.toList());
	}

	@AllArgsConstructor
	private class PunishmentButton extends Button {

		private IPunishment iPunishment;

		@Override
		public String getName(Player player) {
			return ChatColor.RED + TimeUtils.formatIntoCalendarString(new Date(iPunishment.getExecutedAt()));
		}

		@Override
		public List<String> getDescription(Player player) {

            final List<String> toReturn = new ArrayList<>();

            if (iPunishment instanceof RemoveAblePunishment) {
                final RemoveAblePunishment removeAblePunishment = (RemoveAblePunishment) iPunishment;

                toReturn.add(ChatColor.GRAY + NebulaConstants.MENU_BAR);
                toReturn.add(ChatColor.YELLOW + "Punisher: " + (player.hasPermission("orbit.admin") ? removeAblePunishment.getExecutedByFancyName() : ChatColor.MAGIC + removeAblePunishment.getExecutedByFancyName()));
                toReturn.add(ChatColor.YELLOW + "Silent?: " + ChatColor.RED + (removeAblePunishment.getExecutedSilent() ? "Yes" : "No"));
                toReturn.add(ChatColor.YELLOW + "Reason: " + ChatColor.RED + removeAblePunishment.getExecutedReason());

                if (removeAblePunishment.isActive() && !removeAblePunishment.isPermanent()) {
                    toReturn.add(ChatColor.GRAY + NebulaConstants.MENU_BAR);
                    toReturn.add(ChatColor.YELLOW + "Expires: " + ChatColor.RED + removeAblePunishment.getRemainingString());
                } else {
                    toReturn.add(ChatColor.GRAY + NebulaConstants.MENU_BAR);
                    toReturn.add(ChatColor.YELLOW + "Expires: " + ChatColor.RED + "Never");
                }

                if (removeAblePunishment.isPardoned()) {
                    toReturn.add(ChatColor.GRAY + NebulaConstants.MENU_BAR);
                    toReturn.add(ChatColor.YELLOW + "Removed By: " + removeAblePunishment.getPardonedByFancyName());
                    toReturn.add(ChatColor.YELLOW + "Removed At: " + ChatColor.RED + TimeUtils.formatIntoCalendarString(new Date(removeAblePunishment.getPardonedAt())));
                    toReturn.add(ChatColor.YELLOW + "Was Silent?: " + ChatColor.RED + (removeAblePunishment.getPardonedSilent() ? "Yes" : "No"));
                    toReturn.add(ChatColor.YELLOW + "Removal Reason: " + ChatColor.RED + removeAblePunishment.getPardonedReason());
                }
            } else {
                toReturn.add(ChatColor.GRAY + NebulaConstants.MENU_BAR);
                toReturn.add(ChatColor.YELLOW + "By: " + (player.hasPermission(NebulaConstants.ADMIN_PERMISSION) ? iPunishment.getExecutedByFancyName() : ChatColor.MAGIC + iPunishment.getExecutedByFancyName()));
                toReturn.add(ChatColor.YELLOW + "Silent: " + ChatColor.RED + (iPunishment.getExecutedSilent() ? "Yes" : "No"));
                toReturn.add(ChatColor.YELLOW + "Reason: " + ChatColor.RED + iPunishment.getExecutedReason());
            }

			toReturn.add(ChatColor.GRAY + NebulaConstants.MENU_BAR);

			return toReturn;
		}

		@Override
		public Material getMaterial(Player player) {
			return Material.WOOL;
		}

		@Override
		public byte getDamageValue(Player player) {

            if (iPunishment instanceof RemoveAblePunishment) {
                final RemoveAblePunishment removeAblePunishment = (RemoveAblePunishment) iPunishment;

                if (removeAblePunishment.isActive()) {
                    return removeAblePunishment.isPermanent() ? DyeColor.GREEN.getWoolData() : DyeColor.YELLOW.getWoolData();
                }

                return DyeColor.RED.getWoolData();
            } else {
                return 3;
            }
		}

		@Override
		public void clicked(Player player, int i, ClickType clickType) {
		}
	}

}
