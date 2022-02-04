package rip.orbit.nebula.command.profile.grant;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.profile.menu.GrantMenu;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GrantCommand {

	@Command(
			names = {"grant"},
			permission = "orbit.headstaff"
	)
	public static void execute(Player sender, @Parameter(name = "player") String name) {

		final Player player = Nebula.getInstance().getServer().getPlayer(name);

		Profile profile;

		if (player != null) {
			profile = Nebula.getInstance().getProfileHandler().fromUuid(player.getUniqueId());
		} else {
			profile = Nebula.getInstance().getProfileHandler().fromName(name, true, true);
		}

		if (profile == null) {
			sender.sendMessage(ChatColor.YELLOW + name + ChatColor.RED + " does not exist in the Mojang database.");
			return;
		}

		new GrantMenu(profile.getUuid()).openMenu(sender);
	}
}
