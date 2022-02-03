package rip.orbit.nebula.rank.comparator;

import rip.orbit.nebula.rank.Rank;

import java.util.Comparator;

public class RankDateComparator implements Comparator<Rank> {

    @Override
    public int compare(Rank rank,Rank otherRank) {
        return Long.compare(rank.getWeight().get(),otherRank.getWeight().get());
    }

}
