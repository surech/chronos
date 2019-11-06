package ch.surech.chronos.server.model;

import java.util.Comparator;

public class AvailabilityComparator implements Comparator<Availability> {

    @Override
    public int compare(Availability a1, Availability a2) {
        return Integer.compare(a1.getOrder(), a2.getOrder());
    }
}
