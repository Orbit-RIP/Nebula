package rip.orbit.nebula.notifications.menu.button;

import cc.fyre.proton.Proton;
import cc.fyre.proton.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.notifications.Notification;
import rip.orbit.nebula.notifications.packet.NotificationUpdatePacket;
import rip.orbit.nebula.util.CC;

import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 25/11/2021 / 7:17 PM
 * LBuddyBoy Development / me.lbuddyboy.core.notifcation.menu.button
 */

public class ReadNotificationsButton extends Button {

	@Override
	public String getName(Player var1) {
		return CC.translate("&a&lRead All Notifications");
	}

	@Override
	public List<String> getDescription(Player var1) {
		return null;
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
		for (Notification notification : Nebula.getInstance().getNotificationHandler().getNotifications()) {
			if (!notification.getMarkedReadPlayers().contains(player.getUniqueId())) {
				notification.getMarkedReadPlayers().add(player.getUniqueId());
				Nebula.getInstance().getNotificationHandler().saveNotification(notification);
				Proton.getInstance().getPidginHandler().sendPacket(new NotificationUpdatePacket(notification.getId()));
			}
		}
	}
}
