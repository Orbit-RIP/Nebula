package rip.orbit.nebula.command.profile.punishment.execute;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.profile.attributes.punishment.impl.RemoveAblePunishment;

import rip.orbit.nebula.profile.attributes.punishment.packet.PunishmentExecutePacket;
import rip.orbit.nebula.util.DurationWrapper;
import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.flag.Flag;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BanCommand {

    @Command(
            names = {"ban","tempban", "b", "tb"},
            permission = "orbit.trialmod"
    )
    public static void execute(CommandSender sender, @Parameter(name = "player") String name, @Parameter(name = "duration") DurationWrapper duration, @Parameter(name = "reason",defaultValue = "No reason specified",wildcard = true)String reason, @Flag(value = "p",description = "Execute silent")boolean broadcast) {

        final Player player = Nebula.getInstance().getServer().getPlayer(name);

        Profile profile;

        if (player != null) {
            profile = Nebula.getInstance().getProfileHandler().fromUuid(player.getUniqueId());
        } else {
            profile = Nebula.getInstance().getProfileHandler().fromName(name,true,true);
        }

        if (profile == null) {
            sender.sendMessage(ChatColor.YELLOW + name + ChatColor.RED + " does not exist in the Mojang database.");
            return;
        }

        if (profile.getActivePunishment(RemoveAblePunishment.Type.BAN) != null) {
            sender.sendMessage(profile.getFancyName() + ChatColor.RED + " is already banned!");
            return;
        }

        if (duration.isPermanent()) {
            reason = duration.getSource() + " " + reason;
        }

        final RemoveAblePunishment punishment = new RemoveAblePunishment(RemoveAblePunishment.Type.BAN,UUIDUtils.uuid(sender.getName()),duration.getDuration(),reason,!broadcast);

        profile.getPunishments().add(punishment);
        profile.save();

        if (player != null) {
            punishment.execute(player);
        }

        Proton.getInstance().getPidginHandler().sendPacket(new PunishmentExecutePacket(profile.getUuid(),punishment.toDocument(),player != null,profile.getFancyName()));
    }
}
