package rip.orbit.nebula.command.profile;

import rip.orbit.nebula.profile.menu.profile.ProfileMenu;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 02/02/2022 / 8:00 PM
 * Orbit Dev / rip.orbit.nebula.command.profile
 */
public class ProfileCommand {

	@Command(names = {"p", "profile"}, permission = "")
	public static void profile(Player sender, @Parameter(name = "target", defaultValue = "self") UUID target) {
		new ProfileMenu(target).openMenu(sender);
	}

}
