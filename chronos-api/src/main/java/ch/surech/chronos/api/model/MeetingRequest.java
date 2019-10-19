package ch.surech.chronos.api.model;

import ch.surech.chronos.api.model.Invitee;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class MeetingRequest {

    private String subject;

    private String body;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime startRange;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime endRange;

    private int durationInMinutes;

    private String organizer;

    private List<Invitee> invitees;
}
