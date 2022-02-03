package rip.orbit.nebula.server;

import rip.orbit.nebula.Nebula;
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
 * Orbit Dev / rip.orbit.nebula.server
 */

@Getter
public class ServerHandler {

	private final List<String> servers;
	private final MongoCollection<Document> collection;

	public ServerHandler() {

		this.servers = new ArrayList<>();
		this.collection = Nebula.getInstance().getMongoHandler().getMongoDatabase().getCollection("servers");

		for (Document document : this.collection.find()) {
			if (document == null) continue;

			String server = document.getString("name");
			this.servers.add(server);

			Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Loaded " + server + " server scope.");
		}

		String server = Nebula.getInstance().getNetwork().getName();
		if (!this.servers.contains(server)) {

			this.servers.add(server);

			Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Created " + server + " server scope.");
		}

	}

}
