package rip.orbit.nebula.profile.attributes.wrapped.menu.button;

import cc.fyre.proton.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import rip.orbit.nebula.profile.attributes.wrapped.IWrapped;
import rip.orbit.nebula.profile.attributes.wrapped.WrappedType;
import rip.orbit.nebula.util.CC;

import java.util.Arrays;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 07/02/2022 / 8:52 PM
 * Nebula / rip.orbit.nebula.profile.attributes.wrapped.menu.button
 */

@AllArgsConstructor
public class WrappedButton extends Button {

	private IWrapped wrapped;

	@Override
	public String getName(Player player) {
		return CC.translate("&6&l" + wrapped.getName());
	}

	@Override
	public List<String> getDescription(Player player) {
		if (wrapped.getWrappedType() == WrappedType.PRACTICE) {
			return CC.translate(Arrays.asList(
					"&7&m----------------",
					"&6&l┃ &fWins: &6" + wrapped.getKills(),
					"&6&l┃ &fLoses: &6" + wrapped.getDeaths(),
					"&6&l┃ &fPlay Time: &6" + wrapped.getPlayTime(),
					"&6&l┃ &fUnique Logins: &6" + wrapped.getUniqueLogins(),
					"&6&l┃ &fMost Used Ability Item: &6" + wrapped.mostUsedPartnerItem(),
					"&6&l┃ &fHighest WinStreak: &6" + wrapped.getHighestKillStreak(),
					"&7&m----------------"
			));
		}
		return CC.translate(Arrays.asList(
				"&7&m----------------",
				"&6&l┃ &fKills: &6" + wrapped.getKills(),
				"&6&l┃ &fDeaths: &6" + wrapped.getDeaths(),
				"&6&l┃ &fPlay Time: &6" + wrapped.getPlayTime(),
				"&6&l┃ &fUnique Logins: &6" + wrapped.getUniqueLogins(),
				"&6&l┃ &fMost Used Ability Item: &6" + wrapped.mostUsedPartnerItem(),
				"&6&l┃ &fMost Used Item: &6" + wrapped.mostUsedItem(),
				"&6&l┃ &fHighest WinStreak: &6" + wrapped.getHighestKillStreak(),
				"&7&m----------------"
		));
	}

	@Override
	public Material getMaterial(Player player) {
		return Material.BOOK;
	}
}
