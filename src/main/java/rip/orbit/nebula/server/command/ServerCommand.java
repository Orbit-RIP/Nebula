package rip.orbit.nebula.server.command;

import rip.orbit.nebula.Nebula;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import com.mongodb.client.model.Filters;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 31/01/2022 / 11:17 PM
 * Orbit Dev / rip.orbit.nebula.server.command
 */
public class ServerCommand {

	@Command(names = {"scopes delete"}, permission = "orbit.owner")
	public static void execute(CommandSender sender, @Parameter(name = "name") String server) {

		if (!Nebula.getInstance().getServerHandler().getServers().contains(server)) {
			sender.sendMessage(ChatColor.RED + "That server does not exist.");
			return;
		}

		Nebula.getInstance().getServerHandler().getServers().remove(server);
		Nebula.getInstance().getServerHandler().getCollection().deleteOne(Filters.eq("name", server));

		sender.sendMessage(ChatColor.GREEN + "Successfully deleted the " + server + " from the server scopes.");

	}

	@Command(names = {"scopes list"}, permission = "neutron.command.scopes.list")
	public static void execute(CommandSender sender) {

		List<String> servers = Nebula.getInstance().getServerHandler().getServers();

		sender.sendMessage(ChatColor.YELLOW + "Server Scope List " + ChatColor.RED + "(" + servers.size() + ")");

		for (String server : servers) {
			sender.sendMessage(ChatColor.RED + " - " + server);
		}

	}

}
