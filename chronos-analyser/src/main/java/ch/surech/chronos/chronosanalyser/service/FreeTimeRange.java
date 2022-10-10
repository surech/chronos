package ch.surech.chronos.chronosanalyser.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class FreeTimeRange extends SimpleTimeRange {
    public FreeTimeRange(LocalDateTime start, LocalDateTime end) {
        super(start, end);
    }

    public FreeTimeRange(LocalTime start, LocalTime end) {
        super(LocalDateTime.of(LocalDate.now(), start), LocalDateTime.of(LocalDate.now(), end));
    }

    public double calculateUsefulness(){
        // Get minutes for further calulations
        double hour = getDuration().getSeconds() / 60d / 60d;

        // caculate usefullness
        return (hour*hour)/2d;
    }
}
