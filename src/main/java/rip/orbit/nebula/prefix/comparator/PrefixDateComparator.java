package rip.orbit.nebula.prefix.comparator;

import rip.orbit.nebula.prefix.Prefix;

import java.util.Comparator;

public class PrefixDateComparator implements Comparator<Prefix> {

    @Override
    public int compare(Prefix prefix,Prefix otherPrefix) {
        return Long.compare(prefix.getWeight().get(),otherPrefix.getWeight().get());
    }

}
