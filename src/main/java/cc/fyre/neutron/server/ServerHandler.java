package cc.fyre.neutron.server;

import cc.fyre.neutron.Neutron;
import com.mongodb.client.MongoCollection;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 31/01/2022 / 11:14 PM
 * Orbit Dev / cc.fyre.neutron.server
 */

@Getter
public class ServerHandler {

	private final List<String> servers;
	private final MongoCollection<Document> collection;

	public ServerHandler() {

		this.servers = new ArrayList<>();
		this.collection = Neutron.getInstance().getMongoHandler().getMongoDatabase().getCollection("servers");

		for (Document document : this.collection.find()) {
			if (document == null) continue;

			String server = document.getString("name");
			this.servers.add(server);

			Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Loaded " + server + " server scope.");
		}

		String server = Neutron.getInstance().getNetwork().getName();
		if (!this.servers.contains(server)) {

			this.servers.add(server);

			Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Created " + server + " server scope.");
		}

	}

}
