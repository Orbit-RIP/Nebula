package rip.orbit.nebula.profile.attributes.punishment;

import org.bukkit.Bukkit;
import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.NebulaConstants;
import rip.orbit.nebula.profile.attributes.api.Executable;
import rip.orbit.nebula.profile.attributes.punishment.impl.Punishment;
import rip.orbit.nebula.profile.attributes.punishment.impl.RemoveAblePunishment;
import cc.fyre.proton.util.TimeUtils;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.nebula.util.CC;
import rip.orbit.nebula.util.fanciful.FancyMessage;

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
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("orbit.staff")) {
                    FancyMessage fancyMessage = new FancyMessage(message);
                    fancyMessage.tooltip(CC.translate("&6Reason: &f" + this.getExecutedReason(), (this instanceof RemoveAblePunishment ? "&6Duration: &f" + ((RemoveAblePunishment)this).getDurationString() : "")));
                    fancyMessage.send(player);
                } else {
                    Nebula.getInstance().getServer().broadcastMessage(message);
                }
            }
        } else {

            for (Player loopPlayer : Nebula.getInstance().getServer().getOnlinePlayers()) {

                if (!loopPlayer.hasPermission(NebulaConstants.STAFF_PERMISSION)) {
                    continue;
                }

                FancyMessage fancyMessage = new FancyMessage(message);
                fancyMessage.tooltip(CC.translate("&6Reason: &f" + this.getExecutedReason(), (this instanceof RemoveAblePunishment ? "&6Duration: &f" + ((RemoveAblePunishment)this).getDurationString() : "")));

                fancyMessage.send(loopPlayer);
            }
        }


    }

    void execute(Player player);
}
