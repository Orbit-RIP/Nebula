package rip.orbit.nebula.profile.attributes;

import rip.orbit.nebula.profile.Profile;
import rip.orbit.nebula.Nebula;
import lombok.Getter;

import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftHumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.*;

import java.lang.reflect.Field;

import java.util.List;

/**
 * @author xanderume@gmail (JavaProject)
 */
public class ProfilePermissible extends PermissibleBase {

    @Getter private Profile profile;

    public ProfilePermissible(ServerOperator serverOperator) {
        super(serverOperator);

        if (!(serverOperator instanceof Player)) {
            throw new IllegalArgumentException("Cannot inject permissible.");
        }

        this.profile = Nebula.getInstance().getProfileHandler().fromUuid(((Player)serverOperator).getUniqueId());

        try {
            this.inject((Player)serverOperator);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public boolean hasPermission(String permission) {

        if (this.profile == null) return false;

        final List<String> permissions = this.profile.getEffectivePermissions();

        permissions.add(Server.BROADCAST_CHANNEL_USERS);

        if (this.isOp()) {
            permissions.add(Server.BROADCAST_CHANNEL_ADMINISTRATIVE);
        }

        if (permissions.contains("-" + permission) && !permissions.contains("+" + permission)) {
            return false;
        }

        if (this.isOp()) {
            return true;
        }

        return permissions.contains(permission) || permissions.contains("*");
    }

    @Override
    public boolean hasPermission(Permission perm) {

        final List<String> permissions = this.profile.getEffectivePermissions();

        permissions.add(Server.BROADCAST_CHANNEL_USERS);

        if (this.isOp()) {
            permissions.add(Server.BROADCAST_CHANNEL_ADMINISTRATIVE);
        }

        if (permissions.contains("-" + perm.getName()) && !permissions.contains("+" + perm.getName())) {
            return false;
        }

        if (this.isOp()) {
            return true;
        }

        return permissions.contains(perm.getName()) || permissions.contains("*");
    }

    public void inject(Player player) throws NoSuchFieldException,IllegalAccessException {

        final Field permField = this.getPermissibleField(player);

        if (permField == null) {
            return;
        }

        permField.set(player,this);
    }

    private Field getPermissibleField(Player player) throws NoSuchFieldException {

        if (!CraftHumanEntity.class.isAssignableFrom(player.getClass())) {
            return null;
        }

        final Field permField = CraftHumanEntity.class.getDeclaredField("perm");

        permField.setAccessible(true);

        return permField;
    }

}
