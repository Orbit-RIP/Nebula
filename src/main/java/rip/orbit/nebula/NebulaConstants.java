package rip.orbit.nebula;

import rip.orbit.nebula.profile.Profile;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.nebula.util.CC;

import java.util.stream.Collectors;

public class NebulaConstants {

    public static final String CONSOLE_NAME = ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Console";

    public static final String MONITOR_PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "Monitor" + ChatColor.DARK_GRAY + "]";

    public static final String STAFF_PERMISSION = "orbit.staff";
    public static final String ADMIN_PERMISSION = "orbit.admin";
    public static final String MANAGER_PERMISSION = "orbit.headstaff";

    public static final String SILENT_PREFIX = ChatColor.GRAY + "[Unlisted]";
    public static final String SB_BAR = ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-",20);
    public static final String MENU_BAR = ChatColor.STRIKETHROUGH + StringUtils.repeat("-",18);
    public static final String CHAT_BAR = ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-",48);

    public static final String RANKS_COLLECTION = "ranks";
    public static final String PREFIX_COLLECTION = "prefixes";
    public static final String PROFILE_COLLECTION = "profiles";
    public static final String ROLLBACK_LOG_COLLECTION = "rollback-logs";

    public static String formatChatDisplay(Player player, String message) {
        final String name = player.isDisguised() ? player.getDisguisedName():player.getName();

        final Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(player.getUniqueId());

        String prefix = "";
        if (profile.getActivePrefix() != null) {
            prefix = profile.getActivePrefix().getDisplay() + CC.translate("&r ");
        }

        String addition = "";
        if (profile.getServerProfile().isVIPStatus() && !profile.getActiveGrant().getRank().getName().equals("VIP")) {
            addition = profile.getServerProfile().getVipStatusColor() + Nebula.getInstance().getRankHandler().fromName("VIP").getPrefix();
        }

        return (prefix + profile.getActiveGrant().getRank().getPrefix() + addition
                + ChatColor.getLastColors(profile.getActiveGrant().getRank().getPrefix()) + name + ChatColor.WHITE + ": "
                + profile.getChatColor() + (player.isOp() ? ChatColor.translateAlternateColorCodes('&',message):message)).replaceAll("&h", profile.getServerProfile().getVipStatusColor().toString());
    }

}
