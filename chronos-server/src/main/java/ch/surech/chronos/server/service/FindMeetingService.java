package ch.surech.chronos.server.service;

import ch.surech.chronos.api.model.Invitee;
import ch.surech.chronos.api.model.MeetingRequest;
import ch.surech.chronos.api.model.PrecentePreferenceType;
import ch.surech.chronos.api.model.Weekdays;
import ch.surech.chronos.server.entities.EventEntity;
import ch.surech.chronos.server.entities.UserEntity;
import ch.surech.chronos.server.entities.UserPrecentePreferenceEntity;
import ch.surech.chronos.server.model.*;
import ch.surech.chronos.server.repo.UserPrecentePreferenceRepository;
import ch.surech.chronos.server.repo.UserRepository;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FindMeetingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FindMeetingService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPrecentePreferenceRepository userPrecentePreferenceRepository;

    @Autowired
    private EventService eventService;

    @Value("${chronos.timeslot}")
    private int timeslot;

    /**
     * Used Timeslot
     * @return Timeslot in Minutes
     */
    public int getTimeslot() {
        return timeslot;
    }

    public EventEntity search(MeetingRequest meetingRequest) {
        List<MeetingAttendee> attendees = loadAttendees(meetingRequest);
        if (attendees == null || attendees.isEmpty()) {
            throw new IllegalStateException("Attendees not found, event can't be created");
        }

        SortedSet<EventProposal> availabilities = collectAvailabilities(attendees, meetingRequest.getStartRange().toLocalDateTime(), meetingRequest.getEndRange().toLocalDateTime(), meetingRequest.getDurationInMinutes(), timeslot);

        EventProposal availability = availabilities.first();
        if (availability.getAvailability().isBookable()) {
            EventEntity event = createEvent(meetingRequest, availability);
            return event;
        } else {
            throw new IllegalStateException("No free Meeting-Places found");
        }
    }

    private EventEntity createEvent(MeetingRequest meetingRequest, EventProposal proposal) {
        EventEntity.EventEntityBuilder builder = EventEntity.builder();

        // Get Organizer
        UserEntity organizer = userRepository.findByEmail(meetingRequest.getOrganizer());

        // Get Attendees
        List<UserEntity> invitees = meetingRequest.getInvitees().stream()
                .map(Invitee::getEmail)
                .map(userRepository::findByEmail)
                .collect(Collectors.toList());

        // Create Event
        builder.attendees(invitees);
        builder.subject(meetingRequest.getSubject());
        builder.bodyPreview(meetingRequest.getBody());
        builder.organizer(organizer);
        builder.createdAt(ZonedDateTime.now());
        builder.start(ZonedDateTime.of(proposal.getStart(), ZoneId.systemDefault()));
        builder.end(ZonedDateTime.of(proposal.getEnd(), ZoneId.systemDefault()));
        EventEntity event = builder.build();

        // Save event
        EventEntity savedEvent = eventService.saveEvent(event);
        return savedEvent;
    }

    @VisibleForTesting
    protected SortedSet<EventProposal> collectAvailabilities(List<MeetingAttendee> attendees, LocalDateTime startRange, LocalDateTime endRange, int duration, int interval) {
        // Create Cache for speedup the search
        Map<LocalDateTime, CollectedAvailability> cache = new HashMap<>();

        SortedSet<EventProposal> result = new TreeSet<>(new EventProposalComparator());
        for (LocalDateTime time = startRange; time.isBefore(endRange); time = time.plusMinutes(interval)) {
            LocalDateTime start = LocalDateTime.now();
            EventProposal proposal = getEventProposal(attendees, time, duration, interval, cache);
            Duration laufzeit = Duration.between(start, LocalDateTime.now());
            LOGGER.info("Duration for one Slot: {} ms", laufzeit.toMillis());
            result.add(proposal);
        }

        return result;
    }

    private EventProposal getEventProposal(List<MeetingAttendee> attendees, LocalDateTime time, int duration, int interval, Map<LocalDateTime, CollectedAvailability> cache) {
        // Check all time-slots
        List<CollectedAvailability> allCollectedAvailabilities = new ArrayList<>();
        for (LocalDateTime start = time; start.isBefore(time.plusMinutes(duration)); start = start.plusMinutes(interval)) {
            // Check Cache
            CollectedAvailability collectedAvailability = cache.get(start);
            if(collectedAvailability == null){
                // Get Availability for all Attendees
                collectedAvailability = getAvailabilities(attendees, start, interval);
                cache.put(start, collectedAvailability);
            }

            allCollectedAvailabilities.add(collectedAvailability);
        }

        return mergeAvailabilities(allCollectedAvailabilities, time, duration);
    }

    private CollectedAvailability getAvailabilities(List<MeetingAttendee> attendees, LocalDateTime start, int interval) {
        List<Availability> availabilities = new ArrayList<>(attendees.size());
        for (MeetingAttendee attendee : attendees) {
            Availability availability = getAvailability(attendee, start, interval);
            availabilities.add(availability);
        }
        return collectAvailability(availabilities);
    }

    @VisibleForTesting
    protected EventProposal mergeAvailabilities(List<CollectedAvailability> allCollectedAvailabilities, LocalDateTime time, int duration) {
        // Find most restrictive
        List<Availability> availabilities = allCollectedAvailabilities.stream().map(CollectedAvailability::getAvailability).collect(Collectors.toList());
        Availability mostRestrictiveAvailability = getMostRestrictiveAvailability(availabilities);

        // Sum this kind of availability
        int count = allCollectedAvailabilities.stream()
                .filter(a -> a.getAvailability() == mostRestrictiveAvailability)
                .mapToInt(CollectedAvailability::getCount)
                .sum();

        return EventProposal.eventBuilder()
                .availability(mostRestrictiveAvailability)
                .count(count)
                .start(time)
                .duration(duration)
                .build();
    }

    @VisibleForTesting
    protected CollectedAvailability collectAvailability(List<Availability> availabilities) {
        // Find most restrictive
        Availability mostRestrictiveAvailability = getMostRestrictiveAvailability(availabilities);

        // Count this kind of availability
        final Availability countable = mostRestrictiveAvailability == Availability.Available
                ? Availability.Prefered
                : mostRestrictiveAvailability;

        long count = availabilities.stream().filter(a -> a == countable).count();
        return CollectedAvailability.builder().availability(countable).count((int) count).build();
    }

    private List<MeetingAttendee> loadAttendees(MeetingRequest request) {
        // Load all users
        Set<UserEntity> users = new HashSet<>();
        UserEntity organizer = userRepository.findByEmail(request.getOrganizer());
        if(organizer != null) {
            users.add(organizer);
        }

        request.getInvitees().stream()
                .map(Invitee::getEmail)
                .map(userRepository::findByEmail)
                .filter(Objects::nonNull)
                .distinct()
                .forEach(users::add);

        return loadAttendees(users);
    }

    public List<MeetingAttendee> loadAttendees(Collection<UserEntity> users){
        List<MeetingAttendee> result = new ArrayList<>(users.size());

        for (UserEntity user : users) {
            // Ignore empty users
            if(user == null){
                continue;
            }

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

    public Availability getAvailability(MeetingAttendee attendee, LocalDateTime time, int duration) {
        // Check user preference
        PrecentePreferenceType precentePreference = getPrecentePreference(attendee.getPrecentePreferences(), time.toLocalTime(), duration);

        // Check working-days
        boolean isWorkday = isWorkday(attendee.getUser().getWorkingDays(), time, duration);

        // Check if we already have meetings in this timeslot
        boolean hasMeeting = hasMeeting(attendee.getEvents(), time, timeslot);

        // Map results to Availabilty
        if (precentePreference == PrecentePreferenceType.NoWork || !isWorkday) {
            return Availability.NotAvailable;
        } else if (hasMeeting) {
            return Availability.Booked;
        } else if (precentePreference == PrecentePreferenceType.RatherNot) {
            return Availability.RatherNot;
        } else if (precentePreference == PrecentePreferenceType.Available) {
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

    public EventEntity getMeeting(List<EventEntity> events, LocalDateTime time, int duration){
        Optional<EventEntity> first = events.stream().filter(e -> isInTimeslot(e.getStart().toLocalDateTime(), e.getEnd().toLocalDateTime(), time, duration)).findFirst();
        return first.orElse(null);
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

    @VisibleForTesting
    protected Availability getMostRestrictiveAvailability(List<Availability> availabilities) {
        availabilities.sort(new AvailabilityComparator());
        return availabilities.get(0);
    }
}
