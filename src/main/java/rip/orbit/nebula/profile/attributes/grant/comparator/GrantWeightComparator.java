package rip.orbit.nebula.profile.attributes.grant.comparator;

import rip.orbit.nebula.profile.attributes.grant.Grant;
import rip.orbit.nebula.rank.comparator.RankWeightComparator;

import java.util.Comparator;

public class GrantWeightComparator implements Comparator<Grant> {

    @Override
    public int compare(Grant grant,Grant otherGrant) {
        return new RankWeightComparator().compare(grant.getRank(),otherGrant.getRank());
    }
}
