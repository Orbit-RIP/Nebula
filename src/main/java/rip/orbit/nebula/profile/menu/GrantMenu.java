package rip.orbit.nebula.profile.menu;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.profile.attributes.grant.GrantBuilder;
import rip.orbit.nebula.profile.attributes.grant.listener.GrantListener;
import rip.orbit.nebula.rank.Rank;
import rip.orbit.nebula.util.CC;
import rip.orbit.nebula.util.ColorUtil;
import cc.fyre.proton.Proton;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 31/01/2022 / 10:46 PM
 * Orbit Dev / rip.orbit.nebula.profile.menu
 */

@AllArgsConstructor
public class GrantMenu extends PaginatedMenu {

	private UUID target;

	@Override
	public String getPrePaginatedTitle(Player player) {
		return "Granting: " + Proton.getInstance().getUuidCache().name(this.target);
	}

	@Override
	public Map<Integer, Button> getAllPagesButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		int i = -1;

		for (Rank rank : Nebula.getInstance().getRankHandler().getSortedValueCache()) {
			buttons.put(++i, new GrantButton(this.target, rank));
		}

		return buttons;
	}

	@AllArgsConstructor
	public class GrantButton extends Button {

		private UUID target;
		private Rank rank;

		@Override
		public String getName(Player var1) {
			return rank.getFancyName();
		}

		@Override
		public List<String> getDescription(Player var1) {
			return Arrays.asList(
					ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------------",
					ChatColor.YELLOW + "Click to select the " + rank.getFancyName() + ChatColor.YELLOW + " rank.",
					ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------------"
			);
		}

		@Override
		public byte getDamageValue(Player player) {
			return ColorUtil.COLOR_MAP.get(rank.getColor()).getWoolData();
		}

		@Override
		public Material getMaterial(Player var1) {
			return Material.WOOL;
		}

		@Override
		public void clicked(Player player, int slot, ClickType clickType) {

			Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(this.target);

			if (profile.getActiveGrant(rank) != null) {
				player.sendMessage(profile.getFancyName() + ChatColor.RED + " already has an active " + rank.getFancyName() + ChatColor.RED + " grant!");
				return;
			}

			player.closeInventory();
			player.sendMessage(ChatColor.YELLOW + "Type in the reason for granting " + profile.getName() + ChatColor.YELLOW + " the " + rank.getFancyName() + ChatColor.YELLOW + " rank.");
			player.sendMessage(ChatColor.YELLOW + "Type " + CC.RED + "cancel " + CC.YELLOW + "to cancel the granting process");

			GrantBuilder grantBuilder = new GrantBuilder();
			grantBuilder.target = this.target;
			grantBuilder.targetName = profile.getName();
			grantBuilder.rankUUID = rank.getUuid();

			GrantListener.grantBuilderMap.put(player, grantBuilder);

		}
	}

}
