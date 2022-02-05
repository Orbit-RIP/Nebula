package rip.orbit.nebula.command.profile.friend;

import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.profile.friend.FriendRequest;
import rip.orbit.nebula.profile.friend.menu.FriendsMenu;
import rip.orbit.nebula.profile.friend.packet.FriendRemovePacket;
import rip.orbit.nebula.profile.friend.packet.FriendRequestAcceptPacket;
import rip.orbit.nebula.profile.friend.packet.FriendRequestSendPacket;
import rip.orbit.nebula.util.CC;
import rip.orbit.nebula.util.fanciful.FancyMessage;

import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 02/02/2022 / 9:48 PM
 * Orbit Dev / rip.orbit.nebula.command.profile.friend
 */
public class FriendCommands {

	@Command(names = {"block", "ignore", "ignore add"}, permission = "")
	public static void block(Player sender, @Parameter(name = "target") UUID target) {

		Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(sender.getUniqueId());

		if (profile.getBlocked().contains(target)) {
			sender.sendMessage(CC.translate("&cYou already have that person blocked."));
			return;
		}

		profile.getBlocked().add(target);
		profile.save();

		sender.sendMessage(CC.translate("&aYou have just blocked " + Proton.getInstance().getUuidCache().name(target) + "."));

	}

	@Command(names = {"unblock", "unignore", "ignore remove"}, permission = "")
	public static void unblock(Player sender, @Parameter(name = "target") UUID target) {

		Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(sender.getUniqueId());

		if (!profile.getBlocked().contains(target)) {
			sender.sendMessage(CC.translate("&cYou do not have that person blocked."));
			return;
		}

		profile.getBlocked().remove(target);
		profile.save();

		sender.sendMessage(CC.translate("&aYou have just unblocked " + Proton.getInstance().getUuidCache().name(target) + "."));

	}

	@Command(names = {"friend add"}, permission = "")
	public static void friendAdd(Player sender, @Parameter(name = "target") UUID target) {

		Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(sender.getUniqueId());
		Profile targetProfile = Nebula.getInstance().getProfileHandler().fromUuid(target, true);

		if (profile.getFriendRequestFromSender(target) != null) {
			friendAccept(sender, target, profile, targetProfile);
			return;
		}

		if (sender.getUniqueId().equals(target)) {
			sender.sendMessage(CC.translate("&cYou can't friend yourself!"));
			return;
		}

		if (targetProfile.getBlocked().contains(sender.getUniqueId())) {
			sender.sendMessage(CC.translate("&cThat player has you blocked."));
			return;
		}

		if (targetProfile.getFriendRequestFromSender(sender.getUniqueId()) != null) {
			sender.sendMessage(CC.translate("&cYou already have a friend request outgoing for that person."));
			return;
		}

		if (profile.getFriends().contains(target)) {
			sender.sendMessage(CC.translate("&cYou are already friends with that person."));
			return;
		}

		FriendRequest request = new FriendRequest();
		request.setSender(sender.getUniqueId());
		request.setTarget(target);
		request.setSentAt(System.currentTimeMillis());

		profile.getFriendRequests().add(request);
		targetProfile.getFriendRequests().add(request);

		profile.save();
		targetProfile.save();

		Player targetPlayer = Bukkit.getPlayer(target);
		if (targetPlayer == null) {
			Proton.getInstance().getPidginHandler().sendPacket(new FriendRequestSendPacket(sender.getUniqueId(), target));
		} else {
			targetPlayer.sendMessage(CC.translate("&7&m---------------------"));
			FancyMessage message = new FancyMessage(CC.translate("&6&l[FRIEND REQUEST] &6" + sender.getName() + " &fhas just sent you a friend request. You have &65 minutes&f to accept it."));
			message.tooltip(CC.translate("&7Click here to accept the friend request."));
			message.command("/friend accept " + sender.getName());
			message.send(targetPlayer);
			targetPlayer.sendMessage(CC.translate("&7&m---------------------"));
		}

		sender.sendMessage(CC.translate("&aSuccessfully sent a friend request to " + targetProfile.getName() + "."));

	}

	@Command(names = {"friend accept"}, permission = "")
	public static void friendAccept(Player sender, @Parameter(name = "target") UUID target) {

		Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(sender.getUniqueId());
		Profile targetProfile = Nebula.getInstance().getProfileHandler().fromUuid(target, true);

		if (profile.getFriendRequestFromSender(target) == null) {
			sender.sendMessage(CC.translate("&cCould not find a friend request from that player."));
			return;
		}

		if (profile.getFriends().contains(target)) {
			sender.sendMessage(CC.translate("&cYou are already friends with that person."));
			return;
		}

		friendAccept(sender, target, profile, targetProfile);

	}

	public static void friendAccept(Player sender, UUID target, Profile profile, Profile targetProfile) {
		profile.getFriendRequests().removeIf(friendRequest -> friendRequest.getSender().toString().equals(target.toString()));

		profile.getFriends().add(target);
		targetProfile.getFriends().add(sender.getUniqueId());

		profile.save();

		Player targetPlayer = Bukkit.getPlayer(target);
		if (targetPlayer == null) {
			Proton.getInstance().getPidginHandler().sendPacket(new FriendRequestAcceptPacket(sender.getUniqueId(), target));
		} else {
			targetProfile.getFriendRequests().removeIf(friendRequest -> friendRequest.getSender().toString().equals(target.toString()));
			targetProfile.save();

			targetPlayer.sendMessage(CC.translate("&7&m---------------------"));
			targetPlayer.sendMessage(CC.translate("&6&l[FRIEND REQUEST] &6" + sender.getName() + " &fhas just accepted your friend request."));
			targetPlayer.sendMessage(CC.translate("&7&m---------------------"));
		}

		sender.sendMessage(CC.translate("&aSuccessfully accepted " + targetProfile.getName() + " as a friend."));
	}

	@Command(names = {"friend remove"}, permission = "")
	public static void friendRemove(Player sender, @Parameter(name = "target") UUID target) {

		Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(sender.getUniqueId());
		Profile targetProfile = Nebula.getInstance().getProfileHandler().fromUuid(target, true);

		if (!profile.getFriends().contains(target)) {
			sender.sendMessage(CC.translate("&cYou are not friends with that person."));
			return;
		}

		profile.getFriends().remove(target);
		targetProfile.getFriends().remove(sender.getUniqueId());

		profile.save();
		targetProfile.save();

		Player targetPlayer = Bukkit.getPlayer(target);
		if (targetPlayer == null) {
			Proton.getInstance().getPidginHandler().sendPacket(new FriendRemovePacket(sender.getUniqueId(), target));
		} else {
			targetPlayer.sendMessage(CC.translate("&7&m---------------------"));
			targetPlayer.sendMessage(CC.translate("&6&l[FRIEND REQUEST] &6" + sender.getName() + " &fhas just removed you as a friend."));
			targetPlayer.sendMessage(CC.translate("&7&m---------------------"));
		}

		sender.sendMessage(CC.translate("&aSuccessfully removed " + targetProfile.getName() + " as a friend."));

	}

	@Command(names = "friends", permission = "")
	public static void friends(Player sender, @Parameter(name = "target", defaultValue = "self") UUID target) {
		new FriendsMenu(target, false).openMenu(sender);
	}

}
