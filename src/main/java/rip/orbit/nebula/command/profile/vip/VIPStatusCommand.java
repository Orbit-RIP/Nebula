package rip.orbit.nebula.command.profile.vip;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.uuid.UUIDCache;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.profile.attributes.grant.Grant;
import rip.orbit.nebula.profile.vip.menu.VIPMenu;
import rip.orbit.nebula.rank.Rank;
import rip.orbit.nebula.util.DurationWrapper;

import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 05/02/2022 / 8:47 PM
 * Nebula / rip.orbit.nebula.command.profile.vip
 */
public class VIPStatusCommand {

	@Command(names = {"vipstatus", "vip"}, permission = "")
	public static void vipstatuscheck(Player sender) {
		new VIPMenu().openMenu(sender);
	}

	@Command(names = {"vipstatus give"}, permission = "orbit.headadmin")
	public static void vipstatus(CommandSender sender, @Parameter(name = "target") UUID target, @Parameter(name = "duration") DurationWrapper dura, @Parameter(name = "reason", wildcard = true) String reason) {
		Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(target);

		profile.getServerProfile().setVipStatus(true);
		profile.getServerProfile().setVipStatusAddedAt(System.currentTimeMillis());
		profile.getServerProfile().setVipStatusDuration(dura.getDuration());

		Rank rank = Nebula.getInstance().getRankHandler().fromName("VIP");

		Grant grant = new Grant(rank, UUIDCache.CONSOLE_UUID, (long) dura.getDuration(), reason);
		grant.getScopes().add("GLOBAL");

		profile.getGrants().add(grant);
		profile.recalculateGrants();

		profile.save();
	}

}
