package rip.orbit.nebula.notifications.menu.button;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.notifications.Notification;
import rip.orbit.nebula.notifications.packet.NotificationUpdatePacket;
import rip.orbit.nebula.util.CC;
import cc.fyre.proton.Proton;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.util.ItemBuilder;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 25/11/2021 / 7:17 PM
 * LBuddyBoy Development / me.lbuddyboy.core.notifcation.menu.button
 */

@AllArgsConstructor
public class NotificationButton extends Button {

	private Notification notification;

	@Override
	public String getName(Player var1) {
		return "";
	}

	@Override
	public List<String> getDescription(Player var1) {
		return null;
	}

	@Override
	public Material getMaterial(Player var1) {
		return null;
	}

	@Override
	public ItemStack getButtonItem(Player player) {
		Material material;
		String name;
		List<String> lore = new ArrayList<>();
		int data;

		if (notification.getMarkedReadPlayers().contains(player.getUniqueId())) {
			data = 0;
			material = Material.BOOK;
			name = CC.GOLD + notification.getSentAtDate();
			lore.add("&7&m-----------------");
			lore.add("&6&l┃ &fMessage: " + notification.getMessage());
			lore.add("&7&m-----------------");
		} else {
			data = 0;
			material = Material.BOOK_AND_QUILL;
			name = CC.GOLD + notification.getSentAtDate();
			lore.add("&7&m-----------------");
			lore.add("&6&l┃ &fMessage: " + notification.getMessage());
			lore.add(" ");
			lore.add("&7&oClick to mark this notification as read.");
			lore.add("&7&m-----------------");
		}

		return ItemBuilder.of(material).name(name).setLore(lore).data((short) data).build();
	}

	@Override
	public void clicked(Player player, int slot, ClickType clickType) {
		if (!notification.getMarkedReadPlayers().contains(player.getUniqueId())) {
			notification.getMarkedReadPlayers().add(player.getUniqueId());
			Nebula.getInstance().getNotificationHandler().saveNotification(notification);
			Proton.getInstance().getPidginHandler().sendPacket(new NotificationUpdatePacket(notification.getId()));
		}
	}
}
