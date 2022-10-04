package ch.surech.chronos.chronosanalyser.service;

import ch.surech.chronos.analyser.persistence.model.ImportedEvent;
import ch.surech.chronos.analyser.persistence.repo.ImportedEventRepository;
import ch.surech.chronos.analyser.persistence.repo.ParticipantRepository;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import liquibase.repackaged.org.apache.commons.collections4.MultiMapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalyseFreeTimeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyseFreeTimeService.class);

    @Autowired
    private ImportedEventRepository importedEventRepository;

    @Autowired
    private CalendarStateService calendarStateService;

    public void analyse(){
        // Load all users whose calendar should be analyzed.
        List<String> users = loadUsers();

        for (String user : users) {
            // Load all Events for this user
            List<ImportedEvent> events = importedEventRepository.getCalendarFromUser(user);
            LOGGER.info("Event Count: {}", events.size());

            // Group Events by Date
            Multimap<LocalDate, ImportedEvent> groupedEvents = groupAndSort(events);

            for (LocalDate day : groupedEvents.keySet()) {
                // Get all events for that day
                Collection<ImportedEvent> dayEvents = groupedEvents.get(day);

                // Get the free times in this day
                List<Duration> freeTime = calendarStateService.getFreeTime(dayEvents, day);

                // TODO: Daten in ein geeignetes Konstrukt überführen
            }
        }
    }

    private Multimap<LocalDate, ImportedEvent> groupAndSort(List<ImportedEvent> events) {
        // Comparator.comparing(TimeRange::getStart)
        Multimap<LocalDate, ImportedEvent> result = TreeMultimap.create(Comparator.naturalOrder(), Comparator.comparing(ImportedEvent::getStart));
        events.forEach(e -> result.put(e.getStart().toLocalDate(), e));
        return result;
    }

    private List<String> loadUsers() {
        return List.of("stefan.urech@sbb.ch");
    }
}
