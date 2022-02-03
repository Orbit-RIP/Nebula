package rip.orbit.nebula.command.profile.punishment.pardon;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.profile.attributes.rollback.menu.RollbackHistoryMenu;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RollbacksCommand {

	@Command(
			names = {"rollbacks"},
			permission = "neutron.command.rollbacks"
	)
	public static void execute(Player sender, @Parameter(name = "player") UUID uuid) {
		final Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(uuid,true);
		new RollbackHistoryMenu(profile.getRollbacks()).openMenu(sender);
	}

}
