package ch.surech.chronos.server.model;

import lombok.*;

@Getter
@Builder
@RequiredArgsConstructor
public class CollectedAvailability implements Comparable<CollectedAvailability> {
    private final Availability availability;
    private final int count;


    @Override
    public int compareTo(CollectedAvailability collectedAvailability) {
        // First compare the availability
        int result = Integer.compare(this.availability.getOrder(), collectedAvailability.availability.getOrder());

        // If both have the same availablilty, compare the count
        if (result == 0) {
            result = Integer.compare(count, collectedAvailability.count);
        }
        return result;
    }
}
