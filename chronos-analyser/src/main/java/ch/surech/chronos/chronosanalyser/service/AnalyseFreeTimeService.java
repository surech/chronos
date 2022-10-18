package ch.surech.chronos.chronosanalyser.service;

import ch.surech.chronos.analyser.persistence.model.FreetimeDay;
import ch.surech.chronos.analyser.persistence.model.FreetimeSlot;
import ch.surech.chronos.analyser.persistence.model.ImportedEvent;
import ch.surech.chronos.analyser.persistence.model.Person;
import ch.surech.chronos.analyser.persistence.repo.FreetimeDayRepository;
import ch.surech.chronos.analyser.persistence.repo.FreetimeSlotRepository;
import ch.surech.chronos.analyser.persistence.repo.ImportedEventRepository;
import ch.surech.chronos.analyser.persistence.repo.PersonRepository;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalyseFreeTimeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyseFreeTimeService.class);

    private final static LocalDate IMPORT_START = LocalDate.of(2022, Month.AUGUST, 22);
    private final static LocalDate IMPORT_END = LocalDate.of(2022, Month.SEPTEMBER, 18);

    @Autowired
    private ImportedEventRepository importedEventRepository;

    @Autowired
    private CalendarStateService calendarStateService;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private FreetimeDayRepository freetimeDayRepository;

    @Autowired
    private FreetimeSlotRepository freetimeSlotRepository;

    public void analyse(){
        // Load all users whose calendar should be analyzed.
        Iterable<Person> users = loadUsers();

        for (Person user : users) {
            // Load all Events for this user
            List<ImportedEvent> events = importedEventRepository.getCalendarFromUserForExport(user.getUserPrincipalName());
            LOGGER.info("Event Count: {}", events.size());

            // Group Events by Date
            Multimap<LocalDate, ImportedEvent> groupedEvents = groupAndSort(events);

            // Iterate over every day
            List<LocalDate> dateRange = this.generateDateList(IMPORT_START, IMPORT_END);
            for (LocalDate day : dateRange) {
                // Ignore Weekends
                DayOfWeek dayOfWeek = day.getDayOfWeek();
                if(dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY){
                    continue;
                }

                // Get all events for that day
                Collection<ImportedEvent> dayEvents = groupedEvents.get(day);

                // Filter Events
                List<ImportedEvent> filteredEvents = dayEvents.stream().filter(this::filterEvent).collect(Collectors.toList());

                // Get the free times in this day
                List<FreeTimeRange> freeTimes = calendarStateService.getFreeTime(filteredEvents, day);

                // Build the database-entity for the whole day
                FreetimeDay.FreetimeDayBuilder freetimeDayBuilder = FreetimeDay.builder();
                freetimeDayBuilder.person(user);
                freetimeDayBuilder.date(day);
                freetimeDayBuilder.freetimeInMinutes(calendarStateService.getFreetimeInMinutes(freeTimes));

                double usefullness = calendarStateService.getUsefullness(freeTimes);
                double optimalUsefullness = calendarStateService.getOptimalUsefullness(freeTimes);

                freetimeDayBuilder.usefulness(usefullness);
                freetimeDayBuilder.usefulnessOptimal(optimalUsefullness);
                freetimeDayBuilder.potential(optimalUsefullness - usefullness);

                for (FreeTimeRange freeTime : freeTimes) {
                    // Build the database-entity for this specific timeslot
                    FreetimeSlot.FreetimeSlotBuilder freetimeSlotBuilder = FreetimeSlot.builder();
                    freetimeSlotBuilder.start(freeTime.getStart());
                    freetimeSlotBuilder.end(freeTime.getEnd());
                    freetimeSlotBuilder.durationInMinutes((int) freeTime.getDuration().toMinutes());
                    freetimeSlotBuilder.usefulness(freeTime.calculateUsefulness());

                    FreetimeSlot slot = freetimeSlotBuilder.build();
                    freetimeDayBuilder.slot(slot);
                }

                FreetimeDay freetimeDay = freetimeDayBuilder.build();
                freetimeDay.getSlots().forEach(s -> s.setDay(freetimeDay));

                freetimeDayRepository.save(freetimeDay);
                freetimeSlotRepository.saveAll(freetimeDay.getSlots());
            }
        }
    }

    private boolean filterEvent(ImportedEvent event){
        int attendeeCount = event.getAttendees().size();
        return attendeeCount > 1;
    }


    private Multimap<LocalDate, ImportedEvent> groupAndSort(List<ImportedEvent> events) {
        // Comparator.comparing(TimeRange::getStart)
        Multimap<LocalDate, ImportedEvent> result = TreeMultimap.create(Comparator.naturalOrder(), Comparator.comparing(ImportedEvent::getStart));
        events.forEach(e -> result.put(e.getStart().toLocalDate(), e));
        return result;
    }

    private Iterable<Person> loadUsers() {
        return personRepository.findAll();
    }

    @VisibleForTesting
    protected List<LocalDate> generateDateList(LocalDate start, LocalDate end){
        List<LocalDate> result = new ArrayList<>();
        LocalDate current = start;
        while(current.isBefore(end)){
            result.add(current);
            current = current.plusDays(1);
        }
        result.add(end);
        return result;
    }
}
