package ch.surech.chronos.api.model;

import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
public class UserPrecentePreference {
    private long id;

    private ZonedDateTime from;
    private ZonedDateTime to;

    private PrecentePreferenceType preference;
}
