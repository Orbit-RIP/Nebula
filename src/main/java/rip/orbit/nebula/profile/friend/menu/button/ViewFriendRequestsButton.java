package rip.orbit.nebula.profile.friend.menu.button;

import cc.fyre.proton.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.orbit.nebula.profile.friend.menu.FriendRequestsMenu;
import rip.orbit.nebula.util.CC;

import java.util.List;
import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 04/02/2022 / 9:17 PM
 * Orbit Dev / rip.orbit.nebula.profile.friend.menu.button
 */

@AllArgsConstructor
public class ViewFriendRequestsButton extends Button {

	private UUID target;
	private boolean back;

	@Override
	public String getName(Player var1) {
		return CC.translate("&6Friend Requests");
	}

	@Override
	public List<String> getDescription(Player var1) {
		return null;
	}

	@Override
	public Material getMaterial(Player var1) {
		return Material.BOOKSHELF;
	}

	@Override
	public void clicked(Player player, int slot, ClickType clickType) {
		new FriendRequestsMenu(this.target, back).openMenu(player);
	}

}
