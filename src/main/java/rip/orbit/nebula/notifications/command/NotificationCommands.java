package rip.orbit.nebula.notifications.command;

import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.notifications.Notification;
import rip.orbit.nebula.notifications.menu.ManageNotificationsMenu;
import rip.orbit.nebula.notifications.menu.NotificationsMenu;
import rip.orbit.nebula.notifications.packet.NotificationUpdatePacket;

import java.util.ArrayList;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 25/11/2021 / 4:31 PM
 * LBuddyBoy Development / me.lbuddyboy.core.notifcation.command
 */
public class NotificationCommands {

	@Command(names = {"sendnotification", "notification send", "notifications send"}, async = true, permission = "lcore.command.notification.send")
	public static void sendNotif(CommandSender sender, @Parameter(name = "title") String title, @Parameter(name = "message", wildcard = true) String message) {
		Notification notification = new Notification(Nebula.getInstance().getNotificationHandler().getNotifications().size() + 1, title.replaceAll("_", " "), message, System.currentTimeMillis(), new ArrayList<>(), false);
		Nebula.getInstance().getNotificationHandler().getNotifications().add(notification);
		Proton.getInstance().getPidginHandler().sendPacket(new NotificationUpdatePacket(notification.getId()));
		sender.sendMessage(ChatColor.GREEN + "Successfully created a notification. /notifications");
	}

	@Command(names = {"managenotifications", "managenotifs", "notification manage", "notifications manage"}, permission = "orbit.headstaff")
	public static void managenotifications(Player sender) {
		new ManageNotificationsMenu(false).openMenu(sender);
	}

	@Command(names = {"notifications", "checknotifications", "checknotifs"}, permission = "")
	public static void notifications(Player sender) {
		new NotificationsMenu(sender.getUniqueId(), false).openMenu(sender);
	}

}
