package rip.orbit.nebula.profile.menu.profile;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.profile.attributes.wrapped.WrappedType;
import rip.orbit.nebula.profile.attributes.wrapped.menu.WrappedMenu;
import rip.orbit.nebula.profile.menu.profile.ProfileMenu;
import rip.orbit.nebula.profile.menu.profile.buttons.GlobalStatsButton;
import rip.orbit.nebula.profile.stat.GlobalStatistic;
import rip.orbit.nebula.profile.stat.StatType;
import cc.fyre.proton.Proton;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.menu.buttons.BackButton;
import cc.fyre.proton.util.ItemBuilder;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 02/02/2022 / 8:04 PM
 * Orbit Dev / rip.orbit.nebula.profile.menu
 */

@AllArgsConstructor
public class GlobalStatsMenu extends Menu {

	private static int[] WINDOW_SLOTS_BLACK = {};
	private static int[] WINDOW_SLOTS_GOLD = {0,};

	private UUID uuid;
	private boolean backButton;

	@Override
	public String getTitle(Player player) {
		return (uuid == player.getUniqueId() ? "Your Stats" : "Stats: " + Proton.getInstance().getUuidCache().name(uuid));
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		for (int i = 8; i < 36; i++) {
			buttons.put(i, Button.fromItem(ItemBuilder.of(Material.STAINED_GLASS_PANE).name(" ").data((short) 15).build()));
		}

		for (int i = 0; i < 9; i++) {
			buttons.put(i, Button.fromItem(ItemBuilder.of(Material.STAINED_GLASS_PANE).name(" ").data((short) 1).build()));
		}

		for (int i = 36; i < 45; i++) {
			if (buttons.containsKey(i)) continue;
			buttons.put(i, Button.fromItem(ItemBuilder.of(Material.STAINED_GLASS_PANE).name(" ").data((short) 1).build()));
		}

		buttons.put(4, new BackButton(new ProfileMenu(this.uuid)));

		Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(this.uuid, true);

		for (GlobalStatistic statistic : profile.getGlobalStatistics()) {
			if (statistic.getStatType() == StatType.PRACTICE) {
				buttons.put(20, new GlobalStatsButton(new WrappedMenu(uuid, backButton, WrappedType.PRACTICE, 1), "&6&lPractice Statistics", Arrays.asList(
						"&7&m--------------",
						"&6&l┃ &fWins: &6" + statistic.getKills(),
						"&6&l┃ &fLoses: &6" + statistic.getDeaths(),
						"&6&l┃ &fSeasons Played: &6" + statistic.getSeasonsPlayed(),
						"&7",
						"&6&l┃ &fWin Streak: &6" + statistic.getKillStreak(),
						"&6&l┃ &fHighest Win Streak: &6" + statistic.getHighestKillStreak(),
						" ",
						" &7&oClick to view all of your wrappeds for this gamemode",
						"&7&m--------------"
				), Material.IRON_SWORD));
			} else if (statistic.getStatType() == StatType.KITS) {
				buttons.put(22, new GlobalStatsButton(new WrappedMenu(uuid, backButton, WrappedType.KITS, 1), "&6&lKitmap Statistics", Arrays.asList(
						"&7&m--------------",
						"&6&l┃ &fKills: &6" + statistic.getKills(),
						"&6&l┃ &fDeaths: &6" + statistic.getDeaths(),
						"&6&l┃ &fMaps Played: &6" + statistic.getSeasonsPlayed(),
						"&7",
						"&6&l┃ &fKill Streak: &6" + statistic.getKillStreak(),
						"&6&l┃ &fHighest Kill Streak: &6" + statistic.getHighestKillStreak(),
						"&6&l┃ &fPast Teams: &6" + (statistic.getPastTeams().isEmpty() ? "&cNone" : StringUtils.join(statistic.getPastTeams(), ", ")),
						" ",
						" &7&oClick to view all of your wrappeds for this gamemode",
						"&7&m--------------"
				), Material.BOW));
			} else if (statistic.getStatType() == StatType.HCF) {
				buttons.put(24, new GlobalStatsButton(new WrappedMenu(uuid, backButton, WrappedType.HCF, 1), "&6&lHCF Statistics", Arrays.asList(
						"&7&m--------------",
						"&6&l┃ &fKills: &6" + statistic.getKills(),
						"&6&l┃ &fDeaths: &6" + statistic.getDeaths(),
						"&6&l┃ &fMaps Played: &6" + statistic.getSeasonsPlayed(),
						"&7",
						"&6&l┃ &fKill Streak: &6" + statistic.getKillStreak(),
						"&6&l┃ &fHighest Kill Streak: &6" + statistic.getHighestKillStreak(),
						"&6&l┃ &fPast Teams: &6" + (statistic.getPastTeams().isEmpty() ? "&cNone" : StringUtils.join(statistic.getPastTeams(), ", ")),
						" ",
						" &7&oClick to view all of your wrappeds for this gamemode",
						"&7&m--------------"
				), Material.DIAMOND_SWORD));
			}
		}

		return buttons;
	}

	@Override
	public int size(Player player) {
		return 45;
	}

}
