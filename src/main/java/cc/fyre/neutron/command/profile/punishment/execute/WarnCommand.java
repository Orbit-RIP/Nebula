package cc.fyre.neutron.command.profile.punishment.execute;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.punishment.impl.Punishment;
import cc.fyre.neutron.profile.attributes.punishment.packet.PunishmentExecutePacket;
import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.flag.Flag;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.UUIDUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class WarnCommand {

    @Command(
            names = {"warn"},
            permission = "neutron.command.warn"
    )
    public static void execute(CommandSender sender,@Parameter(name = "player") UUID uuid,@Parameter(name = "reason",defaultValue = "Misconduct",wildcard = true) String reason,@Flag(value = "p",description = "Execute public")boolean broadcast) {

        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(uuid,true);

        final Punishment punishment = new Punishment(Punishment.Type.WARN,UUIDUtils.uuid(sender.getName()),reason,!broadcast);

        profile.getPunishments().add(punishment);
        profile.save();

        final Player player = Neutron.getInstance().getServer().getPlayer(uuid);

        if (player != null) {
            punishment.execute(player);
        }

        Proton.getInstance().getPidginHandler().sendPacket(new PunishmentExecutePacket(profile.getUuid(),punishment.toDocument(),player != null,profile.getFancyName()));
    }

}
