package cc.fyre.neutron.listener;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.punishment.impl.RemoveAblePunishment;
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

        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(event.getPlayer().getUniqueId());

        event.setFormat(profile.getFancyName() + ChatColor.GRAY + ": " + ChatColor.WHITE + event.getMessage());

        if (profile.getActivePunishment(RemoveAblePunishment.Type.MUTE) != null) {

            final RemoveAblePunishment punishment = profile.getActivePunishment(RemoveAblePunishment.Type.MUTE);

            event.getPlayer().sendMessage(ChatColor.RED + "You are currently muted.");
            event.getPlayer().sendMessage(ChatColor.RED + "Expires: " + ChatColor.YELLOW + punishment.getRemainingString());

            event.setCancelled(true);

        }

    }

}
