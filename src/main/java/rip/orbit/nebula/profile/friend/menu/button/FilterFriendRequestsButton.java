package rip.orbit.nebula.profile.friend.menu.button;

import cc.fyre.proton.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.orbit.nebula.profile.friend.menu.FriendRequestsMenu;
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
public class FilterFriendRequestsButton extends Button {

	private UUID uuid;
	private FriendRequestsMenu menu;

	@Override
	public String getName(Player var1) {
		return CC.translate((!menu.isOutgoing() ? "&c&lOutgoing Friend Requests" : "&a&lIncoming Friend Requests"));
	}

	@Override
	public List<String> getDescription(Player var1) {
		if (!menu.isOutgoing()) {
			return CC.translate(Arrays.asList(
					"",
					"&cClick to view all friend requests sent by you.",
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
		return (byte) (!menu.isOutgoing() ? 14 : 5);
	}

	@Override
	public void clicked(Player player, int slot, ClickType clickType) {
		menu.setOutgoing(!menu.isOutgoing());
	}
}
