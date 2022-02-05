package rip.orbit.nebula.profile.friend.menu.button;

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
 * 04/02/2022 / 11:20 PM
 * Orbit Dev / rip.orbit.nebula.profile.friend.menu.button
 */

@AllArgsConstructor
public class FilterFriendsButton extends Button {

	private UUID uuid;
	private FriendsMenu menu;

	@Override
	public String getName(Player var1) {
		return CC.translate((menu.isOnline() ? "&c&lOffline Friends" : "&a&lOnline Friends"));
	}

	@Override
	public List<String> getDescription(Player var1) {
		if (menu.isOnline()) {
			return CC.translate(Arrays.asList(
					"",
					"&cClick to view all offline friends.",
					""
			));
		} else {
			return CC.translate(Arrays.asList(
					"",
					"&aClick to view all online friends.",
					""
			));
		}
	}

	@Override
	public Material getMaterial(Player var1) {
		return Material.WOOL;
	}

	@Override
	public byte getDamageValue(Player player) {
		return (byte) (menu.isOnline() ? 14 : 5);
	}

	@Override
	public void clicked(Player player, int slot, ClickType clickType) {
		menu.setOnline(!menu.isOnline());
	}
}
