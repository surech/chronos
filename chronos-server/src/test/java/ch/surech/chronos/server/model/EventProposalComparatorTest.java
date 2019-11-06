package ch.surech.chronos.server.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventProposalComparatorTest {

    @Test
    void compareTo() {
        // Create a few different dates
        LocalDateTime base = LocalDateTime.now();
        LocalDateTime t1 = base;
        LocalDateTime t2 = base.plusDays(1);
        LocalDateTime t3 = base.plusDays(2);
        LocalDateTime t4 = base.plusDays(3);

        // Create different Availabilities
        List<EventProposal> availabilities = new ArrayList<>();
        availabilities.add(EventProposal.eventBuilder().availability(Availability.Available).count(5).start(t1).build());
        availabilities.add(EventProposal.eventBuilder().availability(Availability.Available).count(3).start(t3).build());
        availabilities.add(EventProposal.eventBuilder().availability(Availability.Available).count(3).start(t2).build());
        availabilities.add(EventProposal.eventBuilder().availability(Availability.Available).count(0).start(t4).build());
        availabilities.add(EventProposal.eventBuilder().availability(Availability.Prefered).count(3).build());
        availabilities.add(EventProposal.eventBuilder().availability(Availability.Prefered).count(1).build());
        availabilities.add(EventProposal.eventBuilder().availability(Availability.RatherNot).count(4).build());
        availabilities.add(EventProposal.eventBuilder().availability(Availability.RatherNot).count(2).build());
        availabilities.add(EventProposal.eventBuilder().availability(Availability.Booked).count(5).build());
        availabilities.add(EventProposal.eventBuilder().availability(Availability.Booked).count(2).build());
        availabilities.add(EventProposal.eventBuilder().availability(Availability.NotAvailable).count(4).build());
        availabilities.add(EventProposal.eventBuilder().availability(Availability.NotAvailable).count(2).build());

        // Sort the list
        availabilities.sort(new EventProposalComparator());

        // Check result
        check(availabilities.get(0), Availability.Prefered, 3, null);
        check(availabilities.get(1), Availability.Prefered, 1, null);
        check(availabilities.get(2), Availability.Available, 5, null);
        check(availabilities.get(3), Availability.Available, 3, t2);
        check(availabilities.get(4), Availability.Available, 3, t3);
        check(availabilities.get(5), Availability.Available, 0, null);
        check(availabilities.get(6), Availability.RatherNot, 2, null);
        check(availabilities.get(7), Availability.RatherNot, 4, null);
        check(availabilities.get(8), Availability.Booked, 2, null);
        check(availabilities.get(9), Availability.Booked, 5, null);
        check(availabilities.get(10), Availability.NotAvailable, 2, null);
        check(availabilities.get(11), Availability.NotAvailable, 4, null);
    }

    private void check(EventProposal collectedAvailability, Availability prefered, int count, LocalDateTime start) {
        assertEquals(prefered, collectedAvailability.getAvailability());
        assertEquals(count, collectedAvailability.getCount());

        if(start != null){
            assertEquals(start, collectedAvailability.getStart());
        }
    }
}