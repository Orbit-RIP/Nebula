package rip.orbit.nebula.profile.attributes.wrapped.command;

import cc.fyre.proton.command.Command;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.profile.attributes.wrapped.IWrapped;
import rip.orbit.nebula.profile.attributes.wrapped.WrappedType;
import rip.orbit.nebula.profile.attributes.wrapped.menu.ExampleWrapMenu;
import rip.orbit.nebula.util.CC;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 07/02/2022 / 6:10 PM
 * Nebula / rip.orbit.nebula.profile.attributes.wrap.command
 */
public class ExampleWrapCommand {

	@Command(names = "examplewraps", permission = "op")
	public static void examplewraps(Player sender) {
		new ExampleWrapMenu(sender.getUniqueId()).openMenu(sender);
	}

	@Command(names = {"sendexamplewrap"}, permission = "op")
	public static void sendexamplewrap(CommandSender sender) {

		List<UUID> toReturn = new ArrayList<>();

		for (Document document : Nebula.getInstance().getProfileHandler().getCollection().find()) {

			if (document == null) continue;

			UUID uuid = UUID.fromString(document.getString("uuid"));

			toReturn.add(uuid);
		}

		int i = 0;
		for (UUID uuid : toReturn) {
			Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(uuid, true);

			if (profile != null) {
				IWrapped wrapped = new IWrapped("Kits Map 1", WrappedType.KITS);
				wrapped.setPlayTime("20 hours");
				wrapped.setKills(1);
				wrapped.setDeaths(2);
				wrapped.setHighestKillStreak(19);
				profile.getWraps().add(wrapped);

				profile.save();
				++i;
			}

		}

		sender.sendMessage(CC.translate("&aSent out the wrap to " + i + " profiles."));

	}

}
