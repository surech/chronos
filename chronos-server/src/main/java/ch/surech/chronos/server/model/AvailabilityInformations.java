package ch.surech.chronos.server.model;

import ch.surech.chronos.server.entities.EventEntity;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
@RequiredArgsConstructor
public class AvailabilityInformations {

    private final Availability availability;

    private final LocalDateTime time;

    private final int timeslot;

    private final EventEntity event;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(time.format(DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm")));
        sb.append(": ");
        sb.append(availability);

        if (event != null) {
            sb.append("by ");
            sb.append(event.getSubject());
            sb.append(" for ");
            Duration duration = Duration.between(event.getStart(), event.getEnd());
            sb.append(duration.toMinutes());
            sb.append(" min");
        }
        return sb.toString();
    }
}
