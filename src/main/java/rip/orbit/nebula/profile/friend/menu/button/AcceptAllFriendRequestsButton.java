package rip.orbit.nebula.profile.friend.menu.button;

import cc.fyre.proton.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.command.profile.friend.FriendCommands;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.profile.friend.FriendRequest;
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
public class AcceptAllFriendRequestsButton extends Button {

	private UUID target;

	@Override
	public String getName(Player var1) {
		return CC.translate("&a&lAccept All Friend Requests");
	}

	@Override
	public List<String> getDescription(Player var1) {
		return CC.translate(Arrays.asList(
				"",
				"&aClick to accept all sent friend requests.",
				""
		));
	}

	@Override
	public Material getMaterial(Player var1) {
		return Material.WOOL;
	}

	@Override
	public byte getDamageValue(Player player) {
		return 5;
	}

	@Override
	public void clicked(Player player, int slot, ClickType clickType) {

		Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(target, true);

		for (FriendRequest request : profile.getFriendRequests()) {
			FriendCommands.friendAccept(player, this.target, Nebula.getInstance().getProfileHandler().fromUuid(player.getUniqueId()), profile);
		}

	}
}
