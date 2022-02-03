package rip.orbit.nebula.prefix.comparator;

import rip.orbit.nebula.prefix.Prefix;

import java.util.Comparator;

public class PrefixWeightComparator implements Comparator<Prefix> {

    @Override
    public int compare(Prefix prefix,Prefix otherPrefix) {
        return Integer.compare(prefix.getWeight().get(),otherPrefix.getWeight().get());
    }

}
