package rip.orbit.nebula.profile.friend.menu.button;

import cc.fyre.proton.Proton;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.util.TimeUtils;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.command.profile.friend.FriendCommands;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.profile.friend.FriendRequest;
import rip.orbit.nebula.profile.friend.packet.FriendRequestRemovePacket;
import rip.orbit.nebula.util.CC;
import rip.orbit.nebula.util.JavaUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 04/02/2022 / 9:17 PM
 * Orbit Dev / rip.orbit.nebula.profile.friend.menu.button
 */

@AllArgsConstructor
public class FriendRequestButton extends Button {

	private FriendRequest request;

	@Override
	public String getName(Player var1) {
		Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(request.getSender(), true);
		return CC.translate(profile.getFancyName());
	}

	@Override
	public List<String> getDescription(Player var1) {
		return CC.translate(Arrays.asList(
				"&7&m-----------",
				"&6&l┃ &fExpires In: &6" + TimeUtils.formatIntoDetailedString((int) (((request.getSentAt() + JavaUtils.parse("5m")) - System.currentTimeMillis()) / 1000)),
				" ",
				"&7&oLeft click to accept " + getName(var1) + "'s&7&o friend request",
				"&7&oRight click to deny " + getName(var1) + "'s&7&o friend request",
				"&7&m-----------"
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
	public ItemStack getButtonItem(Player player) {
		ItemStack stack = super.getButtonItem(player);
		SkullMeta meta = (SkullMeta) stack.getItemMeta();

		meta.setOwner(Proton.getInstance().getUuidCache().name(request.getSender()));
		stack.setItemMeta(meta);

		return stack;
	}

	@Override
	public void clicked(Player player, int slot, ClickType clickType) {
		if (clickType.isLeftClick()) {
			FriendCommands.friendAccept(player, this.request.getSender());
		} else {
			Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(player.getUniqueId());
			Profile targetProfile = Nebula.getInstance().getProfileHandler().fromUuid(request.getSender());

			profile.getFriendRequests().remove(request);

			Player target = Bukkit.getPlayer(request.getSender());
			if (target != null) {
				targetProfile.getFriendRequests().remove(request);
				targetProfile.save();

				target.sendMessage(CC.translate("&7&m---------------------"));
				target.sendMessage(CC.translate("&6&l[FRIEND REQUEST] &6" + player.getName() + " &fhas just rejected your friend request."));
				target.sendMessage(CC.translate("&7&m---------------------"));
			} else {
				Proton.getInstance().getPidginHandler().sendPacket(new FriendRequestRemovePacket(player.getUniqueId(), request.getSender()));
			}
		}
	}
}
