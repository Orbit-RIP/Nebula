package rip.orbit.nebula.profile.attributes.api;

import rip.orbit.nebula.Nebula;
import rip.orbit.nebula.NebulaConstants;
import cc.fyre.proton.uuid.UUIDCache;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author xanderume@gmail (JavaProject)
 */
public interface Pardonable {

    UUID getPardoner();

    Long getPardonedAt();

    String getPardonedReason();

    default String getPardonedByFancyName() {

        if (this.getPardoner().equals(UUIDCache.CONSOLE_UUID)) {
            return NebulaConstants.CONSOLE_NAME;
        }

        final Player player = Nebula.getInstance().getServer().getPlayer(this.getPardoner());

        if (player != null) {
            return player.getDisplayName();
        }

        return Nebula.getInstance().getProfileHandler().fromUuid(this.getPardoner(),true).getFancyName();
    }

    boolean isPardoned();
}
