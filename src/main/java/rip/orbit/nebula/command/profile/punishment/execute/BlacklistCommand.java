package rip.orbit.nebula.command.profile.punishment.execute;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.profile.attributes.punishment.impl.RemoveAblePunishment;
import rip.orbit.nebula.profile.attributes.punishment.packet.PunishmentExecutePacket;
import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.flag.Flag;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BlacklistCommand {

    @Command(
            names = {"blacklist"},
            permission = "neutron.command.blacklist"
    )
    public static void execute(CommandSender sender,@Parameter(name = "player") String name,@Parameter(name = "reason",defaultValue = "Cheating",wildcard = true)String reason,@Flag(value = "p",description = "Execute public")boolean broadcast) {

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

        if (profile.getActivePunishment(RemoveAblePunishment.Type.BLACKLIST) != null) {
            sender.sendMessage(profile.getFancyName() + ChatColor.RED + " is already blacklisted!");
            return;
        }

        final RemoveAblePunishment punishment = new RemoveAblePunishment(RemoveAblePunishment.Type.BLACKLIST,UUIDUtils.uuid(sender.getName()), (long) Integer.MAX_VALUE,reason,!broadcast);

        profile.getPunishments().add(punishment);
        profile.save();

        if (player != null) {
            punishment.execute(player);
        }

        Proton.getInstance().getPidginHandler().sendPacket(new PunishmentExecutePacket(profile.getUuid(),punishment.toDocument(),player != null,profile.getFancyName()));
    }

}
