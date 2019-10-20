package ch.surech.chronos.api.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;
import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
public class UserPrecentePreference {
    private long id;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime from;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime to;

    private PrecentePreferenceType preference;
}
