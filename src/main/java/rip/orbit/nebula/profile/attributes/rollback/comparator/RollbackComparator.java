package rip.orbit.nebula.profile.attributes.rollback.comparator;

import rip.orbit.nebula.profile.attributes.rollback.Rollback;

import java.util.Comparator;

public class RollbackComparator implements Comparator<Rollback> {

    @Override
    public int compare(Rollback rollback,Rollback otherRollback) {
        return Long.compare(rollback.getExecutedAt(),otherRollback.getExecutedAt());
    }
}