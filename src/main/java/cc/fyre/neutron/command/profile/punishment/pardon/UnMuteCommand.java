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
import org.bukkit.entity.Player;

import java.util.UUID;

public class UnMuteCommand {

    @Command(
            names = {"unmute"},
            permission = "neutron.command.unmute"
    )
    public static void execute(CommandSender sender,@Parameter(name = "player") UUID uuid,@Parameter(name = "reason",defaultValue = "Misconduct",wildcard = true)String reason,@Flag(value = "p",description = "Execute public")boolean broadcast) {

        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(uuid,true);

        if (profile.getActivePunishment(RemoveAblePunishment.Type.MUTE) == null) {
            sender.sendMessage(profile.getFancyName() + ChatColor.RED + " is not muted!");
            return;
        }

        final RemoveAblePunishment punishment = profile.getActivePunishment(RemoveAblePunishment.Type.MUTE);

        punishment.setPardonedAt(System.currentTimeMillis());
        punishment.setPardonedReason(reason);
        punishment.setPardonedSilent(!broadcast);
        punishment.setPardoner(UUIDUtils.uuid(sender.getName()));

        profile.save();

        final Player player = Neutron.getInstance().getServer().getPlayer(uuid);

        Proton.getInstance().getPidginHandler().sendPacket(new PunishmentPardonPacket(profile.getUuid(),punishment.toDocument(),player != null,profile.getFancyName()));
    }

}
