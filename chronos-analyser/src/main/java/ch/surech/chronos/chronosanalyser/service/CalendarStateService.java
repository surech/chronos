package ch.surech.chronos.chronosanalyser.service;

import ch.surech.chronos.analyser.persistence.model.TimeRange;

import java.time.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CalendarStateService {

    private static final LocalTime LUNCH_START = LocalTime.of(12, 0);
    private static final LocalTime LUNCH_END = LocalTime.of(13, 0);

    private static final LocalTime START_WORK_DAY = LocalTime.of(7, 30);

    private static final LocalTime END_WORK_DAY = LocalTime.of(17, 0);

    public void doIt() {

    }

    public List<Duration> getFreeTime(List<TimeRange> calendar, LocalDate date) {
        // create a copy of the list, because we have to work with it
        List<TimeRange> events = new ArrayList<>(calendar);

        // Insert Lunch-Break
        events.add(getLunchBreak(date));

        // Sort events by starttime
        events.sort(Comparator.comparing(TimeRange::getStart));

        // Collection for the free time between events
        List<Duration> freeTimes = new ArrayList<>();

        // We start with the beginning of the work day
        LocalDateTime lastEnd = LocalDateTime.of(date, START_WORK_DAY);

        for (TimeRange event : events) {
            // Easy Case: The next Event starts after the last one
            if (event.getStart().isAfter(lastEnd)) {
                Duration freeTime = Duration.between(lastEnd, event.getStart());
                freeTimes.add(freeTime);

                lastEnd = event.getEnd();
            }
            // Harder Case: Overlapping Event
            else {
                if (event.getEnd().isAfter(lastEnd)) {
                    lastEnd = event.getEnd();
                }
            }
        }

        // All Events proceeded. Check, if we have some freetime left will the end of the working day
        LocalDateTime endOfWorkday = LocalDateTime.of(date, END_WORK_DAY);
        if (endOfWorkday.isAfter(lastEnd)) {
            freeTimes.add(Duration.between(lastEnd, endOfWorkday));
        }

        return freeTimes;
    }

    private TimeRange getLunchBreak(LocalDate date) {
        return new SimpleTimeRange(LocalDateTime.of(date, LUNCH_START), LocalDateTime.of(date, LUNCH_END));
    }
}
