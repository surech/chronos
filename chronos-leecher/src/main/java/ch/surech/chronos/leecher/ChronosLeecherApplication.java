package ch.surech.chronos.leecher;

import ch.surech.chronos.leecher.mapper.DateTimeTimeZoneMapper;
import ch.surech.chronos.leecher.service.AuthentificationService;
import ch.surech.chronos.leecher.service.CalendarService;
import ch.surech.chronos.leecher.service.GraphService;
import ch.surech.chronos.leecher.service.UserService;
import com.microsoft.graph.models.Event;
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.UserRequestBuilder;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChronosLeecherApplication implements CommandLineRunner {

    private final static Logger LOGGER = LoggerFactory
        .getLogger(ChronosLeecherApplication.class);

    @Autowired
    private AuthentificationService authentificationService;

    @Autowired
    private UserService userService;

    @Autowired
    private CalendarService calendarService;

    public static void main(String[] args) {
        SpringApplication.run(ChronosLeecherApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        LOGGER.info("Signing in...");
        authentificationService.signIn();
        LOGGER.info("Token: " +authentificationService.getAccessToken());

        User user = userService.getUser("stefan.urech@sbb.ch");
        LOGGER.info("User: " + user.displayName);

//        user = userService.getUser("simon.blaser@sbb.ch");
//        LOGGER.info("User: " + user.displayName);

        List<Event> events = calendarService.getEventsFromCalendar(user);
        events.sort((o1, o2) -> DateTimeTimeZoneMapper.toZonedDateTime(o1.start).compareTo(DateTimeTimeZoneMapper.toZonedDateTime(o2.start)));

        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            ZonedDateTime start = DateTimeTimeZoneMapper.toZonedDateTime(event.start);
            LOGGER.info("[{}] {}: {}", i, start.format(DateTimeFormatter.ISO_LOCAL_DATE), event.subject);
        }
    }
}
