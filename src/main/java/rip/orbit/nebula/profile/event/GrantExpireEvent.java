package rip.orbit.nebula.profile.event;

import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.profile.attributes.grant.Grant;
import rip.orbit.nebula.util.BukkitEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class GrantExpireEvent extends BukkitEvent {

    @Getter private Profile profile;
    @Getter private Grant grant;
    @Getter private Grant newGrant;

    public Player getPlayer() {
        return this.profile.getPlayer();
    }
}
