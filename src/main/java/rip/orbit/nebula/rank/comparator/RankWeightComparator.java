package rip.orbit.nebula.rank.comparator;

import rip.orbit.nebula.rank.Rank;

import java.util.Comparator;

public class RankWeightComparator implements Comparator<Rank> {

    @Override
    public int compare(Rank rank,Rank otherRank) {
        return Integer.compare(rank.getWeight().get(),otherRank.getWeight().get());
    }
}
