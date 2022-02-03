package rip.orbit.nebula.profile.attributes.api;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.NebulaConstants;
import cc.fyre.proton.uuid.UUIDCache;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface Executable {

    UUID getExecutor();

    Long getExecutedAt();

    String getExecutedReason();

    default String getExecutedByFancyName() {

        if (this.getExecutor().equals(UUIDCache.CONSOLE_UUID)) {
            return NebulaConstants.CONSOLE_NAME;
        }

        final Player player = Nebula.getInstance().getServer().getPlayer(this.getExecutor());

        if (player != null) {
            return player.getDisplayName();
        }

        return Nebula.getInstance().getProfileHandler().fromUuid(this.getExecutor(),true).getFancyName();
    }

}
