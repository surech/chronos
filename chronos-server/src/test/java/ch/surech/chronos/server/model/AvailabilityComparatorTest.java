package ch.surech.chronos.server.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AvailabilityComparatorTest {

    @Test
    void compare() {
        List<Availability> availabilities = new ArrayList<>();
        availabilities.add(Availability.NotAvailable);
        availabilities.add(Availability.Booked);
        availabilities.add(Availability.Available);
        availabilities.add(Availability.RatherNot);
        availabilities.add(Availability.Prefered);

        // Order
        availabilities.sort(new AvailabilityComparator());
        assertEquals(Availability.NotAvailable, availabilities.get(0));
        assertEquals(Availability.Booked, availabilities.get(1));
        assertEquals(Availability.RatherNot, availabilities.get(2));
        assertEquals(Availability.Available, availabilities.get(3));
        assertEquals(Availability.Prefered, availabilities.get(4));
    }
}