package rip.orbit.nebula.command.profile.namemc;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.uuid.UUIDCache;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.profile.attributes.grant.Grant;
import rip.orbit.nebula.rank.Rank;
import rip.orbit.nebula.util.CC;
import rip.orbit.nebula.util.namemc.NameMCVerification;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 05/02/2022 / 2:21 AM
 * Orbit Dev / rip.orbit.nebula.command.profile.namemc
 */
public class NameMCCommand {

	@Command(names = {"claimrank", "freerank", "namemc", "namemcrank"}, permission = "", async = true)
	public static void freeRank(Player sender) {

		Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(sender.getUniqueId());

		if (profile.getServerProfile().isClaimedNameMC()) {
			sender.sendMessage(CC.translate("&cYou have already liked our NameMC page!"));
			return;
		}

		if (!NameMCVerification.hasLiked(sender)) {
			sender.sendMessage(CC.translate("&cYou have not liked our NameMC page!"));
			sender.sendMessage(CC.translate("&cClick here - https://namemc.com/server/orbit.rip"));
			return;
		}

		profile.getServerProfile().setClaimedNameMC(true);

		Bukkit.broadcastMessage(CC.translate("&6&l[NAME MC] " + profile.getNameWithRank() + " &fhas just claimed their &e&nfree&f Star Rank by &b&nliking&f our &9&lNameMC Page&f! &7(/namemc)"));

		Rank rank = Nebula.getInstance().getRankHandler().fromName("Star");

		Grant grant = new Grant(rank, UUIDCache.CONSOLE_UUID, (long) Integer.MAX_VALUE, "Claimed NameMC");

		profile.getGrants().add(grant);
		profile.recalculateGrants();
	}

}
