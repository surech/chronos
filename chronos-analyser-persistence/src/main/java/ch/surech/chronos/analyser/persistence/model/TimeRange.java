package ch.surech.chronos.analyser.persistence.model;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public interface TimeRange {

    public LocalDateTime getStart();

    public LocalDateTime getEnd();
}
