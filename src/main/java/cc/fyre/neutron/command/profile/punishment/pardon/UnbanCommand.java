package cc.fyre.neutron.command.profile.punishment.pardon;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.punishment.impl.RemoveAblePunishment;
import cc.fyre.neutron.profile.attributes.punishment.packet.PunishmentPardonPacket;
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
            permission = "neutron.command.unban"
    )
    public static void execute(CommandSender sender,@Parameter(name = "player")UUID uuid,@Parameter(name = "reason",defaultValue = "Misconduct",wildcard = true)String reason,@Flag(value = "p",description = "Pardon public")boolean broadcast) {

        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(uuid,true);

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
