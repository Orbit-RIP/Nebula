package rip.orbit.nebula.profile.attributes.punishment.comparator;

import rip.orbit.nebula.profile.attributes.punishment.IPunishment;

import java.util.Comparator;

public class PunishmentDateComparator implements Comparator<IPunishment> {

    @Override
    public int compare(IPunishment iPunishment,IPunishment otherIPunishment) {
        return Long.compare(iPunishment.getExecutedAt(),otherIPunishment.getExecutedAt());
    }
}
