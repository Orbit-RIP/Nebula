package cc.fyre.neutron.command.profile.grant;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.menu.GrantMenu;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GrantCommand {

	@Command(
			names = {"grant"},
			permission = "neutron.command.grant"
	)
	public static void execute(Player sender, @Parameter(name = "player") String name) {

		final Player player = Neutron.getInstance().getServer().getPlayer(name);

		Profile profile;

		if (player != null) {
			profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());
		} else {
			profile = Neutron.getInstance().getProfileHandler().fromName(name, true, true);
		}

		if (profile == null) {
			sender.sendMessage(ChatColor.YELLOW + name + ChatColor.RED + " does not exist in the Mojang database.");
			return;
		}

		new GrantMenu(profile.getUuid()).openMenu(sender);
	}
}
