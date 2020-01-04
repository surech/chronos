package ch.surech.chronos.server.web;

import ch.surech.chronos.server.entities.EventEntity;
import ch.surech.chronos.server.entities.UserEntity;
import ch.surech.chronos.server.model.Availability;
import ch.surech.chronos.server.model.AvailabilityInformations;
import ch.surech.chronos.server.model.MeetingAttendee;
import ch.surech.chronos.server.service.EventService;
import ch.surech.chronos.server.service.FindMeetingService;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

@Controller
public class WebController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebController.class);

    @Autowired
    private EventService eventService;

    @Autowired
    private FindMeetingService findMeetingService;

    @GetMapping(value = "/")
    public String index(Model model){
        return "welcome";
    }

    @GetMapping(value = "/availabilties")
    public String availabilties(Model model) {

        // Load all Users attending to an event
        Collection<UserEntity> usersWithEvents = eventService.getUsersWithEvents();

        // Load additional informationen per User
        List<MeetingAttendee> attendees = findMeetingService.loadAttendees(usersWithEvents);

        // Calculate Availability for all attendees
        Multimap<MeetingAttendee, AvailabilityInformations> availabilties = ArrayListMultimap.create();

        // Displaying from today for the next three weeks
        LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);
        LocalDateTime end = start.plusWeeks(3);
        int timeslot = findMeetingService.getTimeslot();

        for (MeetingAttendee attendee : attendees) {
            for (LocalDateTime time = start; time.isBefore(end); time = time.plusMinutes(timeslot)) {
                // Ignore Weedends and very early or very late times
                if (time.getDayOfWeek() == DayOfWeek.SATURDAY ||
                        time.getDayOfWeek() == DayOfWeek.SUNDAY ||
                        time.getHour() < 5 ||
                        time.getHour() > 19) {
                    continue;
                }

                // Add Metainformations
                AvailabilityInformations.AvailabilityInformationsBuilder builder = AvailabilityInformations.builder();
                builder.time(time);
                builder.timeslot(timeslot);

                // Get Availability
                Availability availability = findMeetingService.getAvailability(attendee, time, timeslot);
                builder.availability(availability);

                // Find Event, if available
                if(availability == Availability.Booked){
                    EventEntity meeting = findMeetingService.getMeeting(attendee.getEvents(), time, timeslot);
                    builder.event(meeting);
                }
                availabilties.put(attendee, builder.build());
            }
        }

        model.addAttribute("availabilties", availabilties);
        model.addAttribute("latestAddedEvent", eventService.getLatestAddedEvent());
        return "availabilities";
    }
}
