package ch.surech.chronos.chronosanalyser.service;

import ch.surech.chronos.analyser.persistence.model.TimeRange;

import java.time.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import com.google.common.annotations.VisibleForTesting;
import org.springframework.stereotype.Service;

@Service
public class CalendarStateService {

    private static final LocalTime START_WORK_DAY = LocalTime.of(7, 30);
    private static final LocalTime LUNCH_START = LocalTime.of(12, 0);
    private static final LocalTime LUNCH_END = LocalTime.of(13, 0);
    private static final LocalTime END_WORK_DAY = LocalTime.of(17, 0);

    public List<FreeTimeRange> getFreeTime(Collection<? extends TimeRange> calendar, LocalDate date) {
        // create a copy of the list, because we have to work with it
        List<TimeRange> events = new ArrayList<>(calendar);

        // Insert Lunch-Break
        events.add(getLunchBreak(date));

        // Sort events by starttime
        events.sort(Comparator.comparing(TimeRange::getStart));

        // Collection for the free time between events
        List<FreeTimeRange> freeTimes = new ArrayList<>();

        // We start with the beginning of the work day
        LocalDateTime lastEnd = LocalDateTime.of(date, START_WORK_DAY);

        for (TimeRange event : events) {
            // Ignore Events, that are outside of our office hours
            if(event.getStart().isAfter(LocalDateTime.of(date, END_WORK_DAY))){
                break;
            }

            // Easy Case: The next Event starts after the last one
            if (event.getStart().isAfter(lastEnd)) {
                FreeTimeRange freeTime = new FreeTimeRange(lastEnd, event.getStart());
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
            freeTimes.add(new FreeTimeRange(lastEnd, endOfWorkday));
        }

        return freeTimes;
    }

    private TimeRange getLunchBreak(LocalDate date) {
        return new SimpleTimeRange(LocalDateTime.of(date, LUNCH_START), LocalDateTime.of(date, LUNCH_END));
    }

    public int getFreetimeInMinutes(List<FreeTimeRange> freeTime) {
        // Sum the duration of all freetimes
        double seconds = freeTime.stream().map(FreeTimeRange::getDuration).mapToDouble(Duration::getSeconds).sum();

        // We're interessted in the minutes
        return (int) (seconds / 60);
    }

    public double getUsefullness(List<FreeTimeRange> freeTime) {
        return freeTime.stream().mapToDouble(FreeTimeRange::calculateUsefulness).sum();
    }

    @VisibleForTesting
    protected Duration getWorkdayDuration(){
        Duration morning = Duration.between(START_WORK_DAY, LUNCH_START);
        Duration afternoon = Duration.between(LUNCH_END, END_WORK_DAY);
        return morning.plus(afternoon);
    }

    public double getOptimalUsefullness(List<FreeTimeRange> freeTime) {
        // Get all available worktime for one day
        Duration morning = Duration.between(START_WORK_DAY, LUNCH_START);
        Duration afternoon = Duration.between(LUNCH_END, END_WORK_DAY);
        Duration workdayDuration = getWorkdayDuration();

        // Get free time
        Duration freetime = Duration.ofMinutes(getFreetimeInMinutes(freeTime));

        // Now we can calculate the booked time
        Duration bookedTime = workdayDuration.minus(freetime);

        List<FreeTimeRange> optimalFreeTime = new ArrayList<>();

        if(!morning.minus(bookedTime).isNegative()){
            // All meetings fit before the lunch break
            optimalFreeTime.add(new FreeTimeRange(START_WORK_DAY.plus(bookedTime), LUNCH_START));
            optimalFreeTime.add(new FreeTimeRange(LUNCH_END, END_WORK_DAY));
        } else {
            // Morning is full
            LocalTime freeTimeStart = LUNCH_END.plus(bookedTime.minus(morning));
            optimalFreeTime.add(new FreeTimeRange(freeTimeStart, END_WORK_DAY));
        }

        return getUsefullness(optimalFreeTime);
    }
}
