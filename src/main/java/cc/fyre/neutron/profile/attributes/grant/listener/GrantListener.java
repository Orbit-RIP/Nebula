package cc.fyre.neutron.profile.attributes.grant.listener;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.grant.GrantBuilder;
import cc.fyre.neutron.profile.menu.ScopesMenu;
import cc.fyre.neutron.rank.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 31/01/2022 / 11:01 PM
 * Orbit Dev / cc.fyre.neutron.profile.attributes.grant.listener
 */
public class GrantListener implements Listener {

	public static Map<Player, GrantBuilder> grantBuilderMap = new ConcurrentHashMap<>();

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {

		Player p = event.getPlayer();
		if (grantBuilderMap.containsKey(p)) {

			event.setCancelled(true);

			if (event.getMessage().equalsIgnoreCase("cancel")) {
				grantBuilderMap.remove(p);
				p.sendMessage(ChatColor.RED + "Grant process cancelled.");
				return;
			}

			GrantBuilder grantBuilder = grantBuilderMap.get(p);
			Rank rank = Neutron.getInstance().getRankHandler().fromUuid(grantBuilder.rankUUID);
			Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(grantBuilder.target);

			if (grantBuilder.reason == null) {

				grantBuilder.reason = event.getMessage();

				p.sendMessage(ChatColor.YELLOW + "Type in the amount of time the " + rank.getFancyName() + ChatColor.YELLOW + " rank should last for " + profile.getName() + ChatColor.YELLOW + ".");

				return;
			}

			if (grantBuilder.time == null) {
				grantBuilder.time = event.getMessage();

				grantBuilder.scopes = new ArrayList<>();

				Bukkit.getScheduler().runTask(Neutron.getInstance(), () -> {
					new ScopesMenu(grantBuilder, grantBuilder.scopes).openMenu(p);
				});

			}

		}

	}

}
