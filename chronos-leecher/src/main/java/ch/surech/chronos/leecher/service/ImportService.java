package ch.surech.chronos.leecher.service;

import ch.surech.chronos.analyser.persistence.model.Person;
import ch.surech.chronos.leecher.mapper.ImportedEventMapper;
import ch.surech.chronos.leecher.mapper.PersonMapper;
import com.microsoft.graph.http.GraphServiceException;
import com.microsoft.graph.models.Event;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import com.microsoft.graph.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ImportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImportService.class);

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private ImportedEventService importedEventService;

    @Autowired
    private UserService userService;

    @Autowired
    private PersonService personService;

    private final static LocalDateTime IMPORT_START = LocalDateTime.of(2022, Month.AUGUST, 2, 0, 0, 0, 0);
    private final static LocalDateTime IMPORT_END = LocalDateTime.of(2022, Month.SEPTEMBER, 18, 23, 59, 59, 0);

    public void runImport(List<User> users) {
        for (User user : users) {
            LOGGER.info("Importing Calendar for User {}", user.userPrincipalName);
            List<Event> events = null;

            // Get User and save it
            Person person = PersonMapper.toModel(user);
            person.setOrganisation(personService.extractOrganisationFromName(user.displayName));

            // If user allready exists, then we go to the next one
            if (personService.exists(person)) {
                LOGGER.info("Person {} allready exists. Ignoring...", person.getUserPrincipalName());
                continue;
            } else {
                personService.save(person);
            }

            try {
                // Get events
                events = calendarService.getEventsFromCalendar(user.userPrincipalName, IMPORT_START, IMPORT_END);
            } catch (GraphServiceException e) {
                if (e.getServiceError().code.equals("ErrorItemNotFound")) {
                    LOGGER.warn("No calendar for {} found", user.userPrincipalName);
                    continue;
                } else if (e.getServiceError().code.equals("ErrorAccessDenied")) {
                    LOGGER.warn("No access to calender from {}", user.userPrincipalName);
                    continue;
                } else {
                    throw e;
                }
            }

            events.stream().map(ImportedEventMapper::toModel).forEach(importedEventService::save);
        }
    }
}
