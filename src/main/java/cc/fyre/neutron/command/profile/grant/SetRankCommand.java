package cc.fyre.neutron.command.profile.grant;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.grant.Grant;
import cc.fyre.neutron.profile.attributes.grant.packet.GrantApplyPacket;
import cc.fyre.neutron.rank.Rank;
import cc.fyre.neutron.util.DurationWrapper;
import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 31/01/2022 / 10:52 PM
 * Orbit Dev / cc.fyre.neutron.command.profile.grant
 */
public class SetRankCommand {

	@Command(
			names = {"setrank"},
			permission = "neutron.command.setrank"
	)
	public static void execute(CommandSender sender, @Parameter(name = "player") String name,
							   @Parameter(name = "rank") Rank rank,
							   @Parameter(name = "duration") DurationWrapper duration,
							   @Parameter(name = "reason", defaultValue = "No reason specified.", wildcard = true) String reason,
							   @Parameter(name = "scopes (Global) (ex: Global,HCF)") String scopeString) {

		final Player player = Neutron.getInstance().getServer().getPlayer(name);

		String scope = (scopeString.contains("GLOBAL") ? "GLOBAL" : scopeString);
		List<String> scopes = new ArrayList<>();
		if (!scope.equals("GLOBAL")) {
			scopes.addAll(Arrays.asList(scope.split(",")));
		} else {
			scopes.add(scope);
		}

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

		if (profile.getActiveGrant(rank) != null) {
			sender.sendMessage(profile.getFancyName() + ChatColor.RED + " already has an active " + rank.getFancyName() + ChatColor.RED + " grant!");
			return;
		}

		if (duration.isPermanent()) {
			reason = duration.getSource() + " " + reason;
		}

		final Grant grant = new Grant(rank, UUIDUtils.uuid(sender.getName()), duration.getDuration(), reason);

		grant.getScopes().addAll(scopes);

		profile.getGrants().add(grant);
		profile.recalculateGrants();
		profile.save();

		if (player == null) {
			Proton.getInstance().getPidginHandler().sendPacket(new GrantApplyPacket(profile.getUuid(), grant.toDocument()));
		}

		sender.sendMessage(ChatColor.GOLD + "You have granted " + profile.getFancyName() + " " + rank.getFancyName() + ChatColor.GOLD + " rank.");
	}

}
