package rip.orbit.nebula.profile.attributes.punishment;

import org.bukkit.ChatColor;
import rip.orbit.nebula.profile.attributes.punishment.impl.Punishment;
import rip.orbit.nebula.profile.attributes.punishment.impl.RemoveAblePunishment;
import rip.orbit.nebula.util.CC;

public interface IPunishmentType {

    String getReadable();
    String getExecutedContext();
    @Deprecated String getPardonedContext();

    default String getDisplay() {
        if (this.equals(Punishment.Type.KICK)) {
            return CC.translate("&aKicks");
        } else if (this.equals(Punishment.Type.WARN)) {
            return CC.translate("&bWarns");
        } else if (this.equals(RemoveAblePunishment.Type.BLACKLIST)) {
            return CC.translate("&6&lBlacklists");
        } else if (this.equals(RemoveAblePunishment.Type.MUTE)) {
            return CC.translate("&eMutes");
        } else if (this.equals(RemoveAblePunishment.Type.BAN)) {
            return CC.translate("&cBans");
        }

        return getReadable();
    }

    default ChatColor getChatColor() {
        if (this.equals(Punishment.Type.KICK)) {
            return ChatColor.GREEN;
        } else if (this.equals(Punishment.Type.WARN)) {
            return ChatColor.AQUA;
        } else if (this.equals(RemoveAblePunishment.Type.BLACKLIST)) {
            return ChatColor.BLACK;
        } else if (this.equals(RemoveAblePunishment.Type.MUTE)) {
            return ChatColor.YELLOW;
        } else if (this.equals(RemoveAblePunishment.Type.BAN)) {
            return ChatColor.RED;
        }

        return ChatColor.GRAY;
    }

}
