package rip.orbit.nebula.listener;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.NebulaConstants;
import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.profile.attributes.punishment.impl.RemoveAblePunishment;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatListener implements Listener {

    @Getter private Map<UUID,Long> lastMessageCache = new HashMap<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncChat(AsyncPlayerChatEvent event) {

        if (event.isCancelled()) {
            return;
        }

        final Profile profile = Nebula.getInstance().getProfileHandler().fromUuid(event.getPlayer().getUniqueId());

        if (profile.getActivePrefix() == null) {
            event.setFormat(NebulaConstants.formatChatDisplay(profile.getPlayer(), event.getMessage()));
        } else {
            event.setFormat(profile.getActivePrefix().getDisplay() + " " + NebulaConstants.formatChatDisplay(profile.getPlayer(), event.getMessage()));
        }

        if (profile.getActivePunishment(RemoveAblePunishment.Type.MUTE) != null) {

            final RemoveAblePunishment punishment = profile.getActivePunishment(RemoveAblePunishment.Type.MUTE);

            event.getPlayer().sendMessage(ChatColor.RED + "You are currently muted and can not type in public chat.");
            event.getPlayer().sendMessage(ChatColor.RED + "Expires: " + ChatColor.YELLOW + punishment.getRemainingString());
            event.getPlayer().sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase an unmute at donate.orbit.rip (/buy).");

            event.setCancelled(true);

        }

    }

}
