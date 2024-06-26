package rip.orbit.nebula.profile.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.command.parameter.DurationWrapperParameter;
import rip.orbit.nebula.command.profile.grant.SetRankCommand;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.profile.attributes.grant.GrantBuilder;
import rip.orbit.nebula.profile.attributes.grant.listener.GrantListener;
import rip.orbit.nebula.rank.Rank;
import rip.orbit.nebula.util.CC;
import rip.orbit.nebula.util.ColorUtil;
import rip.orbit.nebula.util.DurationWrapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 31/01/2022 / 11:12 PM
 * Orbit Dev / rip.orbit.nebula.profile.menu
 */

@AllArgsConstructor
@Getter
public class ScopesMenu extends Menu {

	private GrantBuilder grantBuilder;
	private List<String> scopes;

	@Override
	public String getTitle(Player player) {
		return "Scopes";
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		int i = -1;
		for (String server : Nebula.getInstance().getServerHandler().getServers()) {
			buttons.put(++i, new ScopeButton(this, server));
		}
		buttons.put(++i, new ScopeButton(this, "GLOBAL"));

		buttons.put(49, new ConfirmButton(this));

		return buttons;
	}

	@Override
	public int size(Player player) {
		return 54;
	}

	@Override
	public void onClose(Player player) {
		GrantListener.grantBuilderMap.remove(player);
		player.sendMessage(ChatColor.RED + "Grant process cancelled.");
	}

	@AllArgsConstructor
	public class ConfirmButton extends Button {

		private ScopesMenu menu;

		@Override
		public String getName(Player var1) {
			return ChatColor.GREEN + "CONFIRM";
		}

		@Override
		public List<String> getDescription(Player var1) {
			return Arrays.asList(
					ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------------",
					ChatColor.YELLOW + "Click to confirm these grants.",
					ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------------"
			);
		}

		@Override
		public byte getDamageValue(Player player) {
			return ColorUtil.COLOR_MAP.get(ChatColor.GREEN).getWoolData();
		}

		@Override
		public Material getMaterial(Player var1) {
			return Material.WOOL;
		}

		@Override
		public void clicked(Player player, int slot, ClickType clickType) {
			if (menu.getScopes().isEmpty()) {
				menu.getScopes().add("GLOBAL");
				return;
			}

			Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(menu.grantBuilder.target);
			Rank rank = Nebula.getInstance().getRankHandler().fromUuid(menu.grantBuilder.rankUUID);

			DurationWrapper duration = new DurationWrapperParameter().transform(player, menu.grantBuilder.time);
			String reason = menu.grantBuilder.reason;

			SetRankCommand.execute(player, grantBuilder.targetName, rank, duration, reason, StringUtils.join(scopes, ","));

			GrantListener.grantBuilderMap.remove(player);
			player.closeInventory();
		}
	}

	@AllArgsConstructor
	public class ScopeButton extends Button {

		private ScopesMenu menu;
		private String server;

		@Override
		public String getName(Player var1) {
			return CC.translate("&6&l" + this.server);
		}

		@Override
		public List<String> getDescription(Player var1) {
			if (menu.scopes.contains(server)) {
				return Arrays.asList(
						ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------------",
						ChatColor.YELLOW + "Click to remove the " + server + " scope.",
						ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------------"
				);
			}
			return Arrays.asList(
					ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------------",
					ChatColor.YELLOW + "Click to select the " + server + " scope.",
					ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------------"
			);
		}

		@Override
		public byte getDamageValue(Player player) {
			if (menu.scopes.contains(server)) {
				return ColorUtil.COLOR_MAP.get(ChatColor.RED).getWoolData();
			}
			return ColorUtil.COLOR_MAP.get(ChatColor.GREEN).getWoolData();
		}

		@Override
		public Material getMaterial(Player var1) {
			return Material.WOOL;
		}

		@Override
		public void clicked(Player player, int slot, ClickType clickType) {

			if (menu.scopes.contains(server)) {
				menu.scopes.remove(this.server);
				return;
			}

			menu.scopes.add(this.server);

		}
	}

}
