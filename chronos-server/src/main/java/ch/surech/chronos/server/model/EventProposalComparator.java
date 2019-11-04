package ch.surech.chronos.server.model;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.EnumSet;

public class EventProposalComparator implements Comparator<EventProposal> {

    private final static EnumSet<Availability> MORE_THE_BETTER = EnumSet.of(Availability.Prefered, Availability.Available);

    @Override
    public int compare(EventProposal ca1, EventProposal ca2) {
        // First compare the availability
        int result = Integer.compare(ca2.getAvailability().getOrder(), ca1.getAvailability().getOrder());

        // If both have the same availablilty, compare the count. The order depends on the availability
        if (result == 0) {
            if (MORE_THE_BETTER.contains(ca1.getAvailability())) {
                result = Integer.compare(ca2.getCount(), ca1.getCount());
            } else {
                result = Integer.compare(ca1.getCount(), ca2.getCount());
            }
        }

        // When still everything is equal, compare the date
        if(result == 0) {
            result = ca1.getStart().compareTo(ca2.getStart());
        }
        return result;

    }
}
