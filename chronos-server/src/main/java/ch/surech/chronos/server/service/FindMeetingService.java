package ch.surech.chronos.server.service;

import ch.surech.chronos.api.model.Invitee;
import ch.surech.chronos.api.model.MeetingRequest;
import ch.surech.chronos.api.model.PrecentePreferenceType;
import ch.surech.chronos.api.model.Weekdays;
import ch.surech.chronos.server.entities.EventEntity;
import ch.surech.chronos.server.entities.UserEntity;
import ch.surech.chronos.server.entities.UserPrecentePreferenceEntity;
import ch.surech.chronos.server.model.Availability;
import ch.surech.chronos.server.model.MeetingAttendee;
import ch.surech.chronos.server.repo.UserPrecentePreferenceRepository;
import ch.surech.chronos.server.repo.UserRepository;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FindMeetingService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPrecentePreferenceRepository userPrecentePreferenceRepository;

    @Autowired
    private EventService eventService;

    @Value("${chronos.timeslot}")
    private int timeslot;

    public void search(MeetingRequest meetingRequest) {
        List<MeetingAttendee> attendees = loadAttendees(meetingRequest);
    }

    private List<MeetingAttendee> loadAttendees(MeetingRequest request) {
        // Load all users
        List<UserEntity> users = new ArrayList<>();
        users.add(userRepository.findByEmail(request.getOrganizer()));
        request.getInvitees().stream().map(Invitee::getEmail).map(userRepository::findByEmail).forEach(users::add);

        List<MeetingAttendee> result = new ArrayList<>(users.size());

        for (UserEntity user : users) {
            // Load Preferences
            List<UserPrecentePreferenceEntity> preferences = userPrecentePreferenceRepository.findByEMail(user.getEmail());

            // Load Events
            List<EventEntity> events = eventService.findEventForUser(user);

            MeetingAttendee attendee = MeetingAttendee.builder()
                    .user(user)
                    .precentePreferences(preferences)
                    .events(events).build();
            result.add(attendee);
        }

        return result;
    }

    @VisibleForTesting
    protected Availability getAvailability(MeetingAttendee attendee, LocalDateTime time, int duration) {
        // Check user preference
        PrecentePreferenceType precentePreference = getPrecentePreference(attendee.getPrecentePreferences(), time.toLocalTime(), duration);

        // Check working-days
        boolean isWorkday = isWorkday(attendee.getUser().getWorkingDays(), time, duration);

        // Check if we already have meetings in this timeslot
        boolean hasMeeting = hasMeeting(attendee.getEvents(), time, timeslot);

        // Map results to Availabilty
        if(precentePreference == PrecentePreferenceType.NoWork || !isWorkday){
            return Availability.NotAvailable;
        } else if(hasMeeting){
            return Availability.Booked;
        } else if(precentePreference == PrecentePreferenceType.RatherNot){
            return Availability.RatherNot;
        } else if(precentePreference == PrecentePreferenceType.Available) {
            return Availability.Available;
        } else if (precentePreference == PrecentePreferenceType.Preferred) {
            return Availability.Prefered;
        } else {
            // No path should lead us here
            throw new IllegalStateException("No path should lead us here...");
        }
    }

    @VisibleForTesting
    protected boolean isWorkday(EnumSet<Weekdays> workingDays, LocalDateTime time, int duration) {
        DayOfWeek startDay = time.getDayOfWeek();
        DayOfWeek endDay = time.plusMinutes(duration).getDayOfWeek();

        Set<DayOfWeek> wd = workingDays.stream().map(Weekdays::getDayOfWeek).collect(Collectors.toSet());
        return wd.contains(startDay) && wd.contains(endDay);
    }

    @VisibleForTesting
    protected boolean hasMeeting(List<EventEntity> events, LocalDateTime time, int duration) {
        return events.stream().anyMatch(e -> isInTimeslot(e.getStart().toLocalDateTime(), e.getEnd().toLocalDateTime(), time, duration));
    }

    @VisibleForTesting
    protected PrecentePreferenceType getPrecentePreference(List<UserPrecentePreferenceEntity> preferences, LocalTime time, int duration) {
        List<PrecentePreferenceType> precentePreferenceTypes = preferences.stream()
                .filter(pp -> isInTimeslot(pp.getFrom(), pp.getTo(), time, duration))
                .map(UserPrecentePreferenceEntity::getPreference)
                .collect(Collectors.toList());

        PrecentePreferenceType result;
        // By default the is Availabe
        if (precentePreferenceTypes == null || precentePreferenceTypes.isEmpty()) {
            result = PrecentePreferenceType.Available;
        } else if (precentePreferenceTypes.size() == 1) {
            result = precentePreferenceTypes.get(0);
        } else {
            // If multiple Preferences are found, we take the most restrictive
            result = getMostRestrictive(precentePreferenceTypes);
        }

        return result;
    }

    @VisibleForTesting
    protected boolean isInTimeslot(UserPrecentePreferenceEntity pp, LocalTime time, int duration) {
        return isInTimeslot(pp.getFrom(), pp.getTo(), time, duration);
    }

    private boolean isInTimeslot(LocalDateTime from, LocalDateTime to, LocalDateTime time, int duration) {
        LocalDateTime timeTo = time.plusMinutes(duration);
        return (from.isBefore(time.plusMinutes(1)) && to.isAfter(time)) ||
                (from.isBefore(timeTo) && to.isAfter(timeTo)) ||
                (from.isAfter(time) && to.isBefore(timeTo));
    }

    @VisibleForTesting
    protected boolean isInTimeslot(LocalTime from, LocalTime to, LocalTime time, int duration) {
        LocalTime timeTo = time.plusMinutes(duration);
        return (from.isBefore(time.plusMinutes(1)) && to.isAfter(time.minusMinutes(1))) ||
                (from.isBefore(timeTo.plusMinutes(1)) && to.isAfter(timeTo.minusMinutes(1))) ||
                (from.isAfter(time) && to.isBefore(timeTo));
    }

    @VisibleForTesting
    protected PrecentePreferenceType getMostRestrictive(List<PrecentePreferenceType> precentePreferenceTypes) {
        if (precentePreferenceTypes.contains(PrecentePreferenceType.NoWork)) {
            return PrecentePreferenceType.NoWork;
        }
        if (precentePreferenceTypes.contains(PrecentePreferenceType.RatherNot)) {
            return PrecentePreferenceType.RatherNot;
        }
        if (precentePreferenceTypes.contains(PrecentePreferenceType.Available)) {
            return PrecentePreferenceType.Available;
        }
        if (precentePreferenceTypes.contains(PrecentePreferenceType.Preferred)) {
            return PrecentePreferenceType.Preferred;
        }

        // When we're here, strange thinks happend...
        throw new IllegalArgumentException("Unknown Precente-Preference-Type");
    }
}
