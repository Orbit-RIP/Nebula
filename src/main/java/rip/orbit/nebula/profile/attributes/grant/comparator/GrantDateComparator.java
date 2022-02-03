package rip.orbit.nebula.profile.attributes.grant.comparator;

import rip.orbit.nebula.profile.attributes.grant.Grant;

import java.util.Comparator;

public class GrantDateComparator implements Comparator<Grant> {

    @Override
    public int compare(Grant grant,Grant otherGrant) {
        return Long.compare(grant.getExecutedAt(),otherGrant.getExecutedAt());
    }
}