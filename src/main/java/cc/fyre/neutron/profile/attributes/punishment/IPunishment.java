package cc.fyre.neutron.profile.attributes.punishment;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.NeutronConstants;
import cc.fyre.neutron.profile.attributes.api.Executable;
import cc.fyre.neutron.profile.attributes.punishment.impl.Punishment;
import cc.fyre.neutron.profile.attributes.punishment.impl.RemoveAblePunishment;
import cc.fyre.proton.util.TimeUtils;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface IPunishment extends Executable {

    Type getIType();

    UUID getUuid();

    Boolean getExecutedSilent();

    Document toDocument();

    enum Type {

        NORMAL,
        REMOVE_ABLE

    }

    default void broadcast(String punishedFancyName) {

        String message = punishedFancyName + ChatColor.GREEN + " has been ";

        boolean silent = false;

        if (this instanceof Punishment) {

            final Punishment punishment = (Punishment) this;

            silent = punishment.getExecutedSilent();
            message += (silent ? ChatColor.YELLOW + "silently " + ChatColor.GREEN : "") + punishment.getType().getExecutedContext() + " by " + punishment.getExecutedByFancyName() + ChatColor.GREEN + ".";
        } else if (this instanceof RemoveAblePunishment) {

            final RemoveAblePunishment punishment = (RemoveAblePunishment) this;

            silent = punishment.isPardoned() ? punishment.getPardonedSilent():punishment.getExecutedSilent();

            if (punishment.isPardoned()) {
                message += ChatColor.GREEN + (silent ? ChatColor.YELLOW + "silently " + ChatColor.GREEN : "") + punishment.getType().getPardonedContext() + " by " + punishment.getPardonedByFancyName() + ChatColor.GREEN + ".";
            } else {
                message += ChatColor.GREEN + (silent ? ChatColor.YELLOW + "silently " + ChatColor.GREEN : "") + punishment.getType().getExecutedContext() + (punishment.isPermanent() ? "":" for " + TimeUtils.formatIntoDetailedString((int)(punishment.getDuration()/1000))) + " by " + punishment.getExecutedByFancyName() + ChatColor.GREEN + ".";
            }

        }

//        message = (silent ? NeutronConstants.SILENT_PREFIX + ChatColor.GREEN + " ":"") + message;

        if (!silent) {
            Neutron.getInstance().getServer().broadcastMessage(message);
        } else {

            for (Player loopPlayer : Neutron.getInstance().getServer().getOnlinePlayers()) {

                if (!loopPlayer.hasPermission(NeutronConstants.STAFF_PERMISSION)) {
                    continue;
                }

                loopPlayer.sendMessage(message);
            }
        }


    }

    void execute(Player player);
}
