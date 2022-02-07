package rip.orbit.nebula.profile.friend.menu.button;

import cc.fyre.proton.Proton;
import cc.fyre.proton.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.command.profile.friend.FriendCommands;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.util.CC;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 04/02/2022 / 9:17 PM
 * Orbit Dev / rip.orbit.nebula.profile.friend.menu.button
 */

@AllArgsConstructor
public class FriendButton extends Button {

	private UUID sender;
	private UUID target;

	@Override
	public String getName(Player var1) {
		return CC.translate("&6" + Proton.getInstance().getUuidCache().name(this.target));
	}

	@Override
	public List<String> getDescription(Player var1) {
		Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(this.target, true);

		List<String> lore = new ArrayList<>();

		lore.add("&7&m-----------------");
		lore.add("&6&l┃ &fRank: &r" + profile.getActiveRank().getFancyName());
		if (profile.getServerProfile().isOnline()) {
			lore.add("&6&l┃ &fCurrent Server: &6" + profile.getServerProfile().getLastServer());
			lore.add("&6&l┃ &fOnline Since: &6" + profile.getServerProfile().getLastSeenString());
			lore.add("&6&l┃ &fFirst Login: &6" + profile.getServerProfile().getFirstLoginString());
		} else {
			lore.add("&6&l┃ &fLast Server: &6" + profile.getServerProfile().getLastServer());
			lore.add("&6&l┃ &fLast Seen: &6" + profile.getServerProfile().getLastSeenString() + " ago");
		}
		lore.add(" ");
		lore.add("&aClick to remove " + profile.getName() + " from your friends list.");
		lore.add("&7&m-----------------");

		return CC.translate(lore);
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
		if (player.getUniqueId() != sender) {
			return;
		}
		FriendCommands.friendRemove(player, target);
	}
}
