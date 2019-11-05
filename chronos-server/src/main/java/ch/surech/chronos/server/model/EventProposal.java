package ch.surech.chronos.server.model;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class EventProposal extends CollectedAvailability {

    private final LocalDateTime start;
    private final int duration;

    @Builder(builderMethodName = "eventBuilder")
    public EventProposal(Availability availability, int count, LocalDateTime start, int duration) {
        super(availability, count);
        this.start = start;
        this.duration = duration;
    }

    public LocalDateTime getEnd(){
        return start.plusMinutes(duration);
    }
}
