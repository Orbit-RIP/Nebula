package cc.fyre.neutron.notifications;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.notifications.packet.NotificationUpdatePacket;
import cc.fyre.proton.Proton;
import cc.fyre.proton.pidgin.packet.handler.IncomingPacketHandler;
import cc.fyre.proton.pidgin.packet.listener.PacketListener;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 25/11/2021 / 4:35 PM
 * LBuddyBoy Development / me.lbuddyboy.core.notifcation
 */

@Getter
@Setter
public class NotificationHandler implements PacketListener {

	private MongoCollection<Document> collection;
	private List<Notification> notifications;

	public NotificationHandler() {

		this.notifications = new ArrayList<>();

		this.collection = Neutron.getInstance().getMongoHandler().getMongoDatabase().getCollection("notifications");
		for (Document document : collection.find()) {
			loadNotification(document);
		}

		Proton.getInstance().getPidginHandler().registerPacket(NotificationUpdatePacket.class);
	}

	public List<Notification> getAccurateNotificationList(UUID player) {
		List<Notification> notifs = new ArrayList<>();
		for (Notification notification : this.notifications) {
			if (!notification.getMarkedReadPlayers().contains(player)) {
				notifs.add(notification);
			}
		}
		return notifs;
	}

	public Notification getNotificationById(int id) {
		for (Notification notification : this.notifications) {
			if (notification.getId() == id) {
				return notification;
			}
		}
		return null;
	}

	public void loadNotification(Document document) {
		this.notifications.add(new Notification(document.getInteger("id"), document.getString("title"), document.getString("message"),
				document.getLong("sentAt"), Proton.PLAIN_GSON.<List<UUID>>fromJson(document.getString("readPlayers"), ArrayList.class)
		, false));
	}


	public void deleteNotification(Notification notification) {
		this.notifications.remove(notification);
	}

	public void deleteNotificationFromDB(Notification notification) {
		this.collection.deleteOne(Filters.eq("id", notification.getId()));
	}

	public void saveNotification(Notification notification) {
		Document document = new Document();

		document.put("id", notification.getId());
		document.put("title", notification.getTitle());
		document.put("message", notification.getMessage());
		document.put("sentAt", notification.getSentAt());
		document.put("readPlayers", Proton.PLAIN_GSON.toJson(notification.getMarkedReadPlayers(), ArrayList.class));

		this.collection.replaceOne(Filters.eq("id", notification.getId()), document, new ReplaceOptions().upsert(true));
	}

	@IncomingPacketHandler
	public void onNotificationUpdate(NotificationUpdatePacket packet) {
		Notification notification = Neutron.getInstance().getNotificationHandler().getNotificationById(packet.notiId());

		if (notification.isDeleted()) {
			Neutron.getInstance().getNotificationHandler().deleteNotification(notification);
		} else {
			Neutron.getInstance().getNotificationHandler().deleteNotification(notification);
			Neutron.getInstance().getNotificationHandler().getNotifications().add(notification);
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.sendMessage(ChatColor.GREEN + "A global notification has just been posted! " + ChatColor.GRAY + "(/notifications)");
			}
		}
	}

}
