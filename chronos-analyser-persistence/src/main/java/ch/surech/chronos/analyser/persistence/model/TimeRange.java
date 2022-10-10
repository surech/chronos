package ch.surech.chronos.analyser.persistence.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public abstract class TimeRange {

    public abstract LocalDateTime getStart();

    public abstract LocalDateTime getEnd();

    public Duration getDuration(){
        return Duration.between(getStart(), getEnd());
    }
}
