package cc.fyre.neutron.notifications.command;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.notifications.Notification;
import cc.fyre.neutron.notifications.menu.ManageNotificationsMenu;
import cc.fyre.neutron.notifications.menu.NotificationsMenu;
import cc.fyre.neutron.notifications.packet.NotificationUpdatePacket;
import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 25/11/2021 / 4:31 PM
 * LBuddyBoy Development / me.lbuddyboy.core.notifcation.command
 */
public class NotificationCommands {

	@Command(names = {"sendnotification", "notification send", "notifications send"}, async = true, permission = "lcore.command.notification.send")
	public static void sendNotif(CommandSender sender, @Parameter(name = "title") String title, @Parameter(name = "message", wildcard = true) String message) {
		Notification notification = new Notification(Neutron.getInstance().getNotificationHandler().getNotifications().size() + 1, title.replaceAll("_", " "), message, System.currentTimeMillis(), new ArrayList<>(), false);
		Neutron.getInstance().getNotificationHandler().getNotifications().add(notification);
		Proton.getInstance().getPidginHandler().sendPacket(new NotificationUpdatePacket(notification.getId()));
		sender.sendMessage(ChatColor.GREEN + "Successfully created a notification. /notifications");
	}

	@Command(names = {"managenotifications", "managenotifs", "notification manage", "notifications manage"}, permission = "lcore.command.notification.manage")
	public static void managenotifications(Player sender) {
		new ManageNotificationsMenu().openMenu(sender);
	}

	@Command(names = {"notifications", "checknotifications", "checknotifs"}, permission = "")
	public static void notifications(Player sender) {
		new NotificationsMenu(sender.getUniqueId(), false).openMenu(sender);
	}

}
