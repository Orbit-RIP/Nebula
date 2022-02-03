package rip.orbit.nebula.profile.comparator;

import rip.orbit.nebula.profile.Profile;

import java.util.Comparator;

public class ProfileWeightComparator implements Comparator<Profile> {

    @Override
    public int compare(Profile profile,Profile otherProfile) {
        return Integer.compare(profile.getActiveGrant().getRank().getWeight().get(),otherProfile.getActiveGrant().getRank().getWeight().get());
    }

}
