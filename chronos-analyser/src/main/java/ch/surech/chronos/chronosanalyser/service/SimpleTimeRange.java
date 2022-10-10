package ch.surech.chronos.chronosanalyser.service;

import ch.surech.chronos.analyser.persistence.model.TimeRange;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Getter
@AllArgsConstructor
public class SimpleTimeRange extends TimeRange {
    private final LocalDateTime start;
    private final LocalDateTime end;
}
