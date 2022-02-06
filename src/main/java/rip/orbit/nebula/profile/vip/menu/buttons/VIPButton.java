package rip.orbit.nebula.profile.vip.menu.buttons;

import cc.fyre.proton.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.util.CC;

import java.util.Arrays;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 05/02/2022 / 8:20 PM
 * Nebula / rip.orbit.nebula.profile.vip.menu.buttons
 */
public class VIPButton extends Button {

	@Override
	public String getName(Player player) {
		return player.getDisplayName();
	}

	@Override
	public List<String> getDescription(Player player) {
		Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(player.getUniqueId());
		if (profile.getServerProfile().isVIPStatus()) {
			return CC.translate(Arrays.asList(
					"&7&m----------------",
					"&6&l┃ &fVIP Status: &aActive",
					"&6&l┃ &fTime Left: &6" + (profile.getServerProfile().getVIPRemainingString()),
					"&6&l┃ &fVIP Color: " + (profile.getServerProfile().getVipStatusColor()) + "Example",
					"&7&m----------------"
			));
		}
		return CC.translate(Arrays.asList(
				"&7&m----------------",
				"&6&l┃ &fVIP Status: &cInactive",
				"&7&m----------------"
		));
	}

	@Override
	public byte getDamageValue(Player player) {
		return 3;
	}

	@Override
	public Material getMaterial(Player player) {
		return Material.SKULL_ITEM;
	}

}
