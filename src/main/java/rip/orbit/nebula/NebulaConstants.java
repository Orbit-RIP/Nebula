package rip.orbit.nebula;

import rip.orbit.nebula.profile.Profile;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class NebulaConstants {

    public static final String CONSOLE_NAME = ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Console";

    public static final String MONITOR_PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.BLUE + "Monitor" + ChatColor.DARK_GRAY + "]";

    public static final String STAFF_PERMISSION = "neutron.staff";
    public static final String ADMIN_PERMISSION = "neutron.admin";
    public static final String MANAGER_PERMISSION = "neutron.manager";

    public static final String SILENT_PREFIX = ChatColor.GRAY + "[Unlisted]";
    public static final String SB_BAR = ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-",20);
    public static final String MENU_BAR = ChatColor.STRIKETHROUGH + StringUtils.repeat("-",18);
    public static final String CHAT_BAR = ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-",48);

    public static final String RANKS_COLLECTION = "ranks";
    public static final String PREFIX_COLLECTION = "prefixes";
    public static final String PROFILE_COLLECTION = "profiles";
    public static final String ROLLBACK_LOG_COLLECTION = "rollback-logs";

    public static String formatChatDisplay(Player player,String message) {

        final String name = player.isDisguised() ? player.getDisguisedName():player.getName();

        final Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(player.getUniqueId());

        return profile.getActiveGrant().getRank().getPrefix() + (profile.getActivePrefix() == null ? "":profile.getActivePrefix().getDisplay())
                + ChatColor.getLastColors(profile.getActiveGrant().getRank().getPrefix()) + name + ChatColor.WHITE + ": "
                + profile.getChatColor() + (player.isOp() ? ChatColor.translateAlternateColorCodes('&',message):message);
    }

}
