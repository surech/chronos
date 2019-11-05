package ch.surech.chronos.api.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class Event {

    private String subject;

    private String body;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime start;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime end;

    private String organizer;

    private List<Invitee> invitees;
}
