package ch.surech.chronos.chronosanalyser.service;

import ch.surech.chronos.analyser.persistence.model.TimeRange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CalendarStateServiceTest {

    private CalendarStateService sut;

    @BeforeEach
    void setUp() {
        sut = new CalendarStateService();
    }

    @Test
    void getFreeTimeSimple() {
        // Create some events
        LocalDate today = LocalDate.now();

        List<TimeRange> calendar = new ArrayList<>();
        calendar.add(createEvent(today, 8, 45, 9, 0));
        calendar.add(createEvent(today, 11, 0, 12, 0));
        calendar.add(createEvent(today, 14, 0, 14, 30));
        List<FreeTimeRange> freeTime = sut.getFreeTime(calendar, today);

        assertEquals(4, freeTime.size());
        assertEquals(createDuration(1, 15), freeTime.get(0).getDuration());
        assertEquals(createDuration(2, 0), freeTime.get(1).getDuration());
        assertEquals(createDuration(1, 0), freeTime.get(2).getDuration());
        assertEquals(createDuration(2, 30), freeTime.get(3).getDuration());
    }

    @Test
    void getFreeTimeOverlapping() {
        // Create some events
        LocalDate today = LocalDate.now();

        List<TimeRange> calendar = new ArrayList<>();
        calendar.add(createEvent(today, 9, 0, 10, 0));
        calendar.add(createEvent(today, 9, 30, 10, 15));
        calendar.add(createEvent(today, 14, 0, 15, 0));
        calendar.add(createEvent(today, 14, 15, 14, 45));
        List<FreeTimeRange> freeTime = sut.getFreeTime(calendar, today);

        assertEquals(4, freeTime.size());
        assertEquals(createDuration(1, 30), freeTime.get(0).getDuration());
        assertEquals(createDuration(1, 45), freeTime.get(1).getDuration());
        assertEquals(createDuration(1, 0), freeTime.get(2).getDuration());
        assertEquals(createDuration(2, 0), freeTime.get(3).getDuration());
    }

    @Test
    void getFreeTimeEdge() {
        // Create some events
        LocalDate today = LocalDate.now();

        List<TimeRange> calendar = new ArrayList<>();
        calendar.add(createEvent(today, 7, 30, 9, 0));
        calendar.add(createEvent(today, 11, 0, 12, 0));
        calendar.add(createEvent(today, 13, 0, 14, 0));
        calendar.add(createEvent(today, 16, 0, 17, 0));
        List<FreeTimeRange> freeTime = sut.getFreeTime(calendar, today);

        assertEquals(2, freeTime.size());
        assertEquals(createDuration(2, 0), freeTime.get(0).getDuration());
        assertEquals(createDuration(2, 0), freeTime.get(1).getDuration());
    }

    @Test
    void getFreeTimeOverEdge() {
        // Create some events
        LocalDate today = LocalDate.now();

        List<TimeRange> calendar = new ArrayList<>();
        calendar.add(createEvent(today, 6, 30, 9, 0));
        calendar.add(createEvent(today, 11, 0, 12, 15));
        calendar.add(createEvent(today, 12, 45, 14, 0));
        calendar.add(createEvent(today, 16, 0, 17, 45));
        List<FreeTimeRange> freeTime = sut.getFreeTime(calendar, today);

        assertEquals(2, freeTime.size());
        assertEquals(createDuration(2, 0), freeTime.get(0).getDuration());
        assertEquals(createDuration(2, 0), freeTime.get(1).getDuration());
    }

    @Test
    void getFreeTimeFull() {
        // Create some events
        LocalDate today = LocalDate.now();

        List<TimeRange> calendar = new ArrayList<>();
        calendar.add(createEvent(today, 7, 30, 10, 0));
        calendar.add(createEvent(today, 10, 0, 12, 0));
        calendar.add(createEvent(today, 13, 0, 15, 0));
        calendar.add(createEvent(today, 15, 0, 17, 0));
        List<FreeTimeRange> freeTime = sut.getFreeTime(calendar, today);

        assertTrue(freeTime.isEmpty());
    }

    @Test
    void testGetFreetimeInMinutes() {
        LocalDate today = LocalDate.now();

        List<FreeTimeRange> freeTime = new ArrayList<>();
        freeTime.add(createEvent(today, 7, 30, 10, 0)); // 2.5 h
        freeTime.add(createEvent(today, 11, 0, 12, 0)); // 1 h
        freeTime.add(createEvent(today, 13, 0, 13, 45)); // 45 m
        freeTime.add(createEvent(today, 15, 0, 17, 0)); // 2h

        int result = sut.getFreetimeInMinutes(freeTime);
        assertEquals(150+60+45+120, result);
    }

    private Duration createDuration(int hour, int minutes){
        return Duration.ofHours(hour).plusMinutes(minutes);
    }

    private FreeTimeRange createEvent(LocalDate date, int startHour, int startMinute, int endHour, int endMinute) {
        LocalTime start = LocalTime.of(startHour, startMinute);
        LocalTime end = LocalTime.of(endHour, endMinute);

        return new FreeTimeRange(LocalDateTime.of(date, start), LocalDateTime.of(date, end));
    }

    @Test
    void testGetWorkdayDuration() {
        Duration result = sut.getWorkdayDuration();
        assertEquals(8.5*60*60, result.getSeconds());
    }

    @Test
    void testGetOptimalUsefullness() {
        LocalDate today = LocalDate.now();

        List<TimeRange> calendar = new ArrayList<>();
        calendar.add(createEvent(today, 7, 30, 8, 0));
        calendar.add(createEvent(today, 8, 45, 9, 0));
        calendar.add(createEvent(today, 10, 0, 11, 0));
        calendar.add(createEvent(today, 14, 0, 14, 30));
        List<FreeTimeRange> freeTime = sut.getFreeTime(calendar, today);

        double usefullness = sut.getUsefullness(freeTime);
        double optimalUsefullness = sut.getOptimalUsefullness(freeTime);
        assertEquals(4.90625, usefullness);
        assertEquals(10.53125, optimalUsefullness);
    }

    @Test
    void testGetOptimalUsefullnessPacked() {
        LocalDate today = LocalDate.now();

        List<TimeRange> calendar = new ArrayList<>();
        calendar.add(createEvent(today, 7, 30, 8, 0));
        calendar.add(createEvent(today, 8, 45, 9, 0));
        calendar.add(createEvent(today, 9, 30, 9, 45));
        calendar.add(createEvent(today, 10, 0, 10, 30));
        calendar.add(createEvent(today, 11, 0, 12, 0));
        calendar.add(createEvent(today, 13, 0, 14, 0));
        calendar.add(createEvent(today, 14, 30, 16, 0));
        List<FreeTimeRange> freeTime = sut.getFreeTime(calendar, today);

        double usefullness = sut.getUsefullness(freeTime);
        double optimalUsefullness = sut.getOptimalUsefullness(freeTime);
        assertEquals(1.1875, usefullness);
        assertEquals(6.125, optimalUsefullness);
    }

    @Test
    void testGetOptimalUsefullnessFull() {
        LocalDate today = LocalDate.now();

        List<TimeRange> calendar = new ArrayList<>();
        calendar.add(createEvent(today, 7, 30, 10, 0));
        calendar.add(createEvent(today, 10, 0, 12, 0));
        calendar.add(createEvent(today, 13, 0, 15, 0));
        calendar.add(createEvent(today, 15, 0, 17, 0));
        List<FreeTimeRange> freeTime = sut.getFreeTime(calendar, today);

        double usefullness = sut.getUsefullness(freeTime);
        double optimalUsefullness = sut.getOptimalUsefullness(freeTime);
        assertEquals(0, usefullness);
        assertEquals(0, optimalUsefullness);
    }
}