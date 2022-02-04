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

public class UnbanCommand {

    @Command(
            names = {"unban","pardon"},
            permission = "orbit.mod+"
    )
    public static void execute(CommandSender sender,@Parameter(name = "player")UUID uuid,@Parameter(name = "reason",defaultValue = "No reason specified",wildcard = true)String reason,@Flag(value = "p",description = "Pardon public")boolean broadcast) {

        final Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(uuid,true);

        if (profile.getActivePunishment(RemoveAblePunishment.Type.BAN) == null) {
            sender.sendMessage(profile.getFancyName() + ChatColor.RED + " is not banned!");
            return;
        }

        final RemoveAblePunishment punishment = profile.getActivePunishment(RemoveAblePunishment.Type.BAN);

        punishment.setPardonedAt(System.currentTimeMillis());
        punishment.setPardonedReason(reason);
        punishment.setPardonedSilent(!broadcast);
        punishment.setPardoner(UUIDUtils.uuid(sender.getName()));

        profile.save();

        Proton.getInstance().getPidginHandler().sendPacket(new PunishmentPardonPacket(profile.getUuid(),punishment.toDocument(),false,profile.getFancyName()));

    }

}
