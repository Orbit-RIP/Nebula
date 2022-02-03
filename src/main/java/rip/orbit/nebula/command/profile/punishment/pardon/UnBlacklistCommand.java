package rip.orbit.nebula.command.profile.punishment.pardon;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.profile.attributes.punishment.impl.RemoveAblePunishment;
import rip.orbit.nebula.profile.attributes.punishment.packet.PunishmentPardonPacket;
import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.flag.Flag;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class UnBlacklistCommand {

    @Command(
            names = {"unblacklist"},
            permission = "neutron.command.unblacklist"
    )
    public static void execute(CommandSender sender,@Parameter(name = "player") UUID uuid,@Parameter(name = "reason",defaultValue = "Misconduct",wildcard = true)String reason,@Flag(value = "p",description = "Execute public")boolean broadcast) {

        final Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(uuid,true);

        if (profile.getActivePunishment(RemoveAblePunishment.Type.BLACKLIST) == null) {
            sender.sendMessage(profile.getFancyName() + ChatColor.RED + " is not blacklisted!");
            return;
        }

        final RemoveAblePunishment punishment = profile.getActivePunishment(RemoveAblePunishment.Type.BLACKLIST);

        punishment.setPardonedAt(System.currentTimeMillis());
        punishment.setPardonedReason(reason);
        punishment.setPardonedSilent(!broadcast);
        punishment.setPardoner(UUIDUtils.uuid(sender.getName()));

        profile.save();

        Proton.getInstance().getPidginHandler().sendPacket(new PunishmentPardonPacket(profile.getUuid(),punishment.toDocument(),false,profile.getFancyName()));
    }

}
