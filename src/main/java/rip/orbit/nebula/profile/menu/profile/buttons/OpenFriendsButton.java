package rip.orbit.nebula.profile.menu.profile.buttons;

import cc.fyre.proton.Proton;
import cc.fyre.proton.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.orbit.nebula.profile.friend.menu.FriendsMenu;
import rip.orbit.nebula.util.CC;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 02/02/2022 / 8:02 PM
 * Orbit Dev / rip.orbit.nebula.profile.menu.button
 */

@AllArgsConstructor
public class OpenFriendsButton extends Button {

	private UUID uuid;
	private boolean backButton;

	@Override
	public String getName(Player var1) {
		return CC.translate("&6&lFriends");
	}

	@Override
	public List<String> getDescription(Player var1) {
		if (var1.getUniqueId() == uuid) {
			return CC.translate(Arrays.asList(
					"&7&m-----------------",
					"&6&l┃ &fClick to view your friends.",
					"&7&m-----------------"
			));
		}
		return CC.translate(Arrays.asList(
				"&7&m-----------------",
				"&6&l┃ &fClick to view " + Proton.getInstance().getUuidCache().name(this.uuid) + "'s friends.",
				"&7&m-----------------"
		));
	}

	@Override
	public Material getMaterial(Player var1) {
		return Material.SKULL_ITEM;
	}

	@Override
	public byte getDamageValue(Player player) {
		return 3;
	}

	@Override
	public void clicked(Player player, int slot, ClickType clickType) {
		new FriendsMenu(this.uuid, this.backButton).openMenu(player);
	}
}
