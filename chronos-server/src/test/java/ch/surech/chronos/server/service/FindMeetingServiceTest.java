package ch.surech.chronos.server.service;

import ch.surech.chronos.api.model.PrecentePreferenceType;
import ch.surech.chronos.api.model.Weekdays;
import ch.surech.chronos.server.entities.EventEntity;
import ch.surech.chronos.server.entities.UserEntity;
import ch.surech.chronos.server.entities.UserPrecentePreferenceEntity;
import ch.surech.chronos.server.model.Availability;
import ch.surech.chronos.server.model.MeetingAttendee;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

class FindMeetingServiceTest {

    private FindMeetingService sut;

    @BeforeEach
    void setUp() {
        sut = new FindMeetingService();
    }

    @Test
    void getMostRestrictive() {
        List<PrecentePreferenceType> types = List.of(PrecentePreferenceType.NoWork);
        PrecentePreferenceType result = sut.getMostRestrictive(types);
        Assertions.assertEquals(PrecentePreferenceType.NoWork, result);

        types = List.of(PrecentePreferenceType.RatherNot, PrecentePreferenceType.Available, PrecentePreferenceType.Preferred);
        result = sut.getMostRestrictive(types);
        Assertions.assertEquals(PrecentePreferenceType.RatherNot, result);

        types = List.of(PrecentePreferenceType.Available, PrecentePreferenceType.Preferred);
        result = sut.getMostRestrictive(types);
        Assertions.assertEquals(PrecentePreferenceType.Available, result);
    }

    @Test
    void getPrecentePreference() {
        // Simple test without any preferences
        List<UserPrecentePreferenceEntity> preferences = new ArrayList<>();
        PrecentePreferenceType result = sut.getPrecentePreference(preferences, LocalTime.of(12, 00), 15);
        Assertions.assertEquals(PrecentePreferenceType.Available, result);

        // Create (almost) full preference-chain
        preferences.add(UserPrecentePreferenceEntity.builder()
                .from(LocalTime.of(0, 0))
                .to(LocalTime.of(7, 59))
                .preference(PrecentePreferenceType.NoWork)
                .build());
        preferences.add(UserPrecentePreferenceEntity.builder()
                .from(LocalTime.of(8, 0))
                .to(LocalTime.of(11, 44))
                .preference(PrecentePreferenceType.Preferred)
                .build());
        preferences.add(UserPrecentePreferenceEntity.builder()
                .from(LocalTime.of(11, 45))
                .to(LocalTime.of(12, 44))
                .preference(PrecentePreferenceType.RatherNot)
                .build());
        // Afternoon is undefined...
        preferences.add(UserPrecentePreferenceEntity.builder()
                .from(LocalTime.of(17, 0))
                .to(LocalTime.of(23, 59))
                .preference(PrecentePreferenceType.NoWork)
                .build());

        // Let's run some tests
        result = sut.getPrecentePreference(preferences, LocalTime.of(5, 0), 15);
        Assertions.assertEquals(PrecentePreferenceType.NoWork, result);

        result = sut.getPrecentePreference(preferences, LocalTime.of(9, 15), 15);
        Assertions.assertEquals(PrecentePreferenceType.Preferred, result);

        result = sut.getPrecentePreference(preferences, LocalTime.of(11, 40), 15);
        Assertions.assertEquals(PrecentePreferenceType.RatherNot, result);

        result = sut.getPrecentePreference(preferences, LocalTime.of(11, 45), 15);
        Assertions.assertEquals(PrecentePreferenceType.RatherNot, result);

        result = sut.getPrecentePreference(preferences, LocalTime.of(12, 13), 15);
        Assertions.assertEquals(PrecentePreferenceType.RatherNot, result);

        result = sut.getPrecentePreference(preferences, LocalTime.of(12, 44), 15);
        Assertions.assertEquals(PrecentePreferenceType.RatherNot, result);

        result = sut.getPrecentePreference(preferences, LocalTime.of(15, 49), 15);
        Assertions.assertEquals(PrecentePreferenceType.Available, result);

        result = sut.getPrecentePreference(preferences, LocalTime.of(22, 0), 15);
        Assertions.assertEquals(PrecentePreferenceType.NoWork, result);
    }

    @Test
    void isInTimeslot() {
        UserPrecentePreferenceEntity preferenceEntity = UserPrecentePreferenceEntity.builder().from(LocalTime.of(12, 0)).to(LocalTime.of(12, 59)).build();

        Assertions.assertEquals(false, sut.isInTimeslot(preferenceEntity, LocalTime.of(9, 0), 30));
        Assertions.assertEquals(true, sut.isInTimeslot(preferenceEntity, LocalTime.of(11, 45), 30));
        Assertions.assertEquals(true, sut.isInTimeslot(preferenceEntity, LocalTime.of(12, 0), 30));
        Assertions.assertEquals(true, sut.isInTimeslot(preferenceEntity, LocalTime.of(12, 45), 30));
        Assertions.assertEquals(false, sut.isInTimeslot(preferenceEntity, LocalTime.of(13, 0), 30));
        Assertions.assertEquals(false, sut.isInTimeslot(preferenceEntity, LocalTime.of(14, 0), 30));
        Assertions.assertEquals(true, sut.isInTimeslot(preferenceEntity, LocalTime.of(11, 45), 120));
    }

    @Test
    void hasMeeting() {
        List<EventEntity> events = new ArrayList<>();
        LocalDate date = LocalDate.of(2019, 3, 8);
        Assertions.assertFalse(sut.hasMeeting(events, LocalDateTime.of(date, LocalTime.of(12, 0)), 15));

        events.add(EventEntity.builder().start(buildZDT(date, -1, 9, 0)).end(buildZDT(date, -1, 10, 0)).build());
        events.add(EventEntity.builder().start(buildZDT(date, -1, 11, 0)).end(buildZDT(date, -1, 12, 0)).build());
        events.add(EventEntity.builder().start(buildZDT(date, 0, 8, 45)).end(buildZDT(date, 0, 9, 0)).build());
        events.add(EventEntity.builder().start(buildZDT(date, 0, 8, 30)).end(buildZDT(date, 0, 10, 0)).build());
        events.add(EventEntity.builder().start(buildZDT(date, 0, 13, 30)).end(buildZDT(date, 0, 15, 0)).build());
        events.add(EventEntity.builder().start(buildZDT(date, 1, 10, 0)).end(buildZDT(date, 1, 11, 30)).build());
        events.add(EventEntity.builder().start(buildZDT(date, 1, 14, 0)).end(buildZDT(date, 1, 15, 0)).build());

        Assertions.assertTrue(sut.hasMeeting(events, buildLDT(date, 0, 13, 30), 15));
        Assertions.assertTrue(sut.hasMeeting(events, buildLDT(date, 0, 14, 30), 45));
        Assertions.assertTrue(sut.hasMeeting(events, buildLDT(date, 0, 8, 45), 15));
        Assertions.assertTrue(sut.hasMeeting(events, buildLDT(date, -1, 9, 0), 15));
        Assertions.assertTrue(sut.hasMeeting(events, buildLDT(date, -1, 9, 0), 60));
        Assertions.assertTrue(sut.hasMeeting(events, buildLDT(date, -1, 8, 45), 90));
        Assertions.assertFalse(sut.hasMeeting(events, buildLDT(date, -2, 8, 45), 90));
        Assertions.assertFalse(sut.hasMeeting(events, buildLDT(date, 0, 7, 0), 15));
        Assertions.assertFalse(sut.hasMeeting(events, buildLDT(date, 0, 8, 15), 15));
        Assertions.assertFalse(sut.hasMeeting(events, buildLDT(date, 0, 15, 0), 15));
    }

    private ZonedDateTime buildZDT(LocalDate baseDate, int dayOffset, int hour, int minutes) {
        LocalTime time = LocalTime.of(hour, minutes);
        LocalDate date = baseDate.plusDays(dayOffset);
        return ZonedDateTime.of(date, time, ZoneId.systemDefault());
    }

    private LocalDateTime buildLDT(LocalDate baseDate, int dayOffset, int hour, int minutes) {
        LocalTime time = LocalTime.of(hour, minutes);
        LocalDate date = baseDate.plusDays(dayOffset);
        return LocalDateTime.of(date, time);
    }

    @Test
    void getAvailability() {
        List<UserPrecentePreferenceEntity> preferences = new ArrayList<>();

        // Create (almost) full preference-chain
        preferences.add(UserPrecentePreferenceEntity.builder()
                .from(LocalTime.of(0, 0))
                .to(LocalTime.of(7, 59))
                .preference(PrecentePreferenceType.NoWork)
                .build());
        preferences.add(UserPrecentePreferenceEntity.builder()
                .from(LocalTime.of(8, 0))
                .to(LocalTime.of(11, 44))
                .preference(PrecentePreferenceType.Preferred)
                .build());
        preferences.add(UserPrecentePreferenceEntity.builder()
                .from(LocalTime.of(11, 45))
                .to(LocalTime.of(12, 44))
                .preference(PrecentePreferenceType.RatherNot)
                .build());
        // Afternoon is undefined...
        preferences.add(UserPrecentePreferenceEntity.builder()
                .from(LocalTime.of(17, 0))
                .to(LocalTime.of(23, 59))
                .preference(PrecentePreferenceType.NoWork)
                .build());

        // Base-Date is a Wednesday
        LocalDate baseDate = LocalDate.of(2019, 11, 6);
        Assertions.assertEquals(DayOfWeek.WEDNESDAY, baseDate.getDayOfWeek());

        // Create Events
        List<EventEntity> events = new ArrayList<>();
        Assertions.assertFalse(sut.hasMeeting(events, LocalDateTime.of(baseDate, LocalTime.of(12, 0)), 15));

        events.add(EventEntity.builder().start(buildZDT(baseDate, -1, 9, 0)).end(buildZDT(baseDate, -1, 10, 0)).build());
        events.add(EventEntity.builder().start(buildZDT(baseDate, -1, 11, 0)).end(buildZDT(baseDate, -1, 12, 0)).build());
        events.add(EventEntity.builder().start(buildZDT(baseDate, 0, 8, 45)).end(buildZDT(baseDate, 0, 9, 0)).build());
        events.add(EventEntity.builder().start(buildZDT(baseDate, 0, 8, 30)).end(buildZDT(baseDate, 0, 10, 0)).build());
        events.add(EventEntity.builder().start(buildZDT(baseDate, 0, 13, 30)).end(buildZDT(baseDate, 0, 15, 0)).build());
        events.add(EventEntity.builder().start(buildZDT(baseDate, 1, 10, 0)).end(buildZDT(baseDate, 1, 11, 30)).build());
        events.add(EventEntity.builder().start(buildZDT(baseDate, 1, 14, 0)).end(buildZDT(baseDate, 1, 15, 0)).build());

        // Create User with working-days (80%)
        EnumSet<Weekdays> workingDays = EnumSet.of(Weekdays.Monday, Weekdays.Tuesday, Weekdays.Wednesday, Weekdays.Thursday);
        UserEntity user = UserEntity.builder().workingDays(workingDays).build();

        // Now Build an attendee
        MeetingAttendee attendee = MeetingAttendee.builder().precentePreferences(preferences).events(events).user(user).build();

        // Let's run some tests
        Assertions.assertEquals(Availability.Available, sut.getAvailability(attendee, buildLDT(baseDate, 0, 15, 0), 15));
        Assertions.assertEquals(Availability.Available, sut.getAvailability(attendee, buildLDT(baseDate, 0, 15, 15), 15));
        Assertions.assertEquals(Availability.Booked, sut.getAvailability(attendee, buildLDT(baseDate, -1, 9, 0), 15));
        Assertions.assertEquals(Availability.Booked, sut.getAvailability(attendee, buildLDT(baseDate, -1, 9, 15), 15));
        Assertions.assertEquals(Availability.NotAvailable, sut.getAvailability(attendee, buildLDT(baseDate, 1, 19, 0), 15));
        Assertions.assertEquals(Availability.Prefered, sut.getAvailability(attendee, buildLDT(baseDate, 1, 9, 0), 15));
        Assertions.assertEquals(Availability.RatherNot, sut.getAvailability(attendee, buildLDT(baseDate, 0, 12, 0), 15));

        // Weekend
        Assertions.assertEquals(Availability.NotAvailable, sut.getAvailability(attendee, buildLDT(baseDate, 3, 15, 0), 15));
    }

    @Test
    void isWorkday() {
        // Create Working-Days (80%)
        EnumSet<Weekdays> workingDays = EnumSet.of(Weekdays.Monday, Weekdays.Tuesday, Weekdays.Thursday, Weekdays.Friday);

        // This Base-Date is a Monday
        LocalDate base = LocalDate.of(2019, 11, 4);
        Assertions.assertEquals(DayOfWeek.MONDAY, base.getDayOfWeek());

        // Run some tests
        Assertions.assertTrue(sut.isWorkday(workingDays, buildLDT(base, 0, 12, 0), 15));
        Assertions.assertTrue(sut.isWorkday(workingDays, buildLDT(base, 1, 12, 0), 15));
        Assertions.assertFalse(sut.isWorkday(workingDays, buildLDT(base, 2, 12, 0), 15));
        Assertions.assertTrue(sut.isWorkday(workingDays, buildLDT(base, 3, 12, 0), 15));
        Assertions.assertTrue(sut.isWorkday(workingDays, buildLDT(base, 4, 12, 0), 15));
        Assertions.assertFalse(sut.isWorkday(workingDays, buildLDT(base, 5, 12, 0), 15));
        Assertions.assertFalse(sut.isWorkday(workingDays, buildLDT(base, 6, 12, 0), 15));
    }
}