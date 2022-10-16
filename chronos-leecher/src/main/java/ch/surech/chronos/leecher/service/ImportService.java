package ch.surech.chronos.leecher.service;

import ch.surech.chronos.analyser.persistence.model.Person;
import ch.surech.chronos.leecher.mapper.ImportedEventMapper;
import ch.surech.chronos.leecher.mapper.PersonMapper;
import com.microsoft.graph.http.GraphServiceException;
import com.microsoft.graph.models.Event;
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

    public void runImport(List<String> importUsers){
        for (String userId : importUsers) {
            LOGGER.info("Importing Calendar for User {}", userId);
            List<Event> events = null;

            try {
                // Get User and save it
                User user = userService.getUser(userId);
                Person person = PersonMapper.toModel(user);
                person.setOrganisation(personService.extractOrganisationFromName(user.displayName));

                // If user allready exists, then we go to the next one
                if(personService.exists(person)){
                    LOGGER.info("Person {} allready exists. Ignoring...", person.getUserPrincipalName());
                    continue;
                } else {
                    personService.save(person);
                }
            } catch (GraphServiceException e){
                LOGGER.warn("Error loading user {} from Graph", userId);
                continue;
            }

            try {
                // Get events
                events = calendarService.getEventsFromCalendar(userId);
            } catch (GraphServiceException e) {
                if (e.getServiceError().code.equals("ErrorItemNotFound")) {
                    LOGGER.warn("No calendar for {} found", userId);
                    continue;
                }else if(e.getServiceError().code.equals("ErrorAccessDenied")){
                    LOGGER.warn("No access to calender from {}", userId);
                    continue;
                } else {
                    throw e;
                }
            }

            events.stream().map(ImportedEventMapper::toModel).forEach(importedEventService::save);
        }
    }
}
