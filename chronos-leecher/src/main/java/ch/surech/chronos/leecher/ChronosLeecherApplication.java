package ch.surech.chronos.leecher;

import ch.surech.chronos.analyser.persistence.model.GroupMembers;
import ch.surech.chronos.leecher.mapper.DateTimeTimeZoneMapper;
import ch.surech.chronos.leecher.service.AuthentificationService;
import ch.surech.chronos.leecher.service.CalendarService;
import ch.surech.chronos.leecher.service.GroupService;
import ch.surech.chronos.leecher.service.ImportService;
import ch.surech.chronos.leecher.service.UserService;
import com.microsoft.graph.models.Event;
import com.microsoft.graph.models.Group;
import com.microsoft.graph.models.User;
import com.microsoft.graph.models.UserSettings;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"ch.surech.chronos.analyser.persistence.repo"})
@EntityScan(basePackages = {"ch.surech.chronos.analyser.persistence.model"})
public class ChronosLeecherApplication implements CommandLineRunner {

    private final static Logger LOGGER = LoggerFactory
        .getLogger(ChronosLeecherApplication.class);

    @Autowired
    private AuthentificationService authentificationService;

    @Autowired
    private UserService userService;

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private ImportService importService;

    @Autowired
    private GroupService groupService;

    public static void main(String[] args) {
        SpringApplication.run(ChronosLeecherApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        signIn();

        showUser();

//        this.getUserInGroup();

//        this.showAllInGroup();

        this.showUserSetting();

//        showEventsForOneUser();

        importEventsInDatabase();
    }

    private void signIn() {
        LOGGER.info("Signing in...");
        authentificationService.signIn();
        LOGGER.info("Token: " +authentificationService.getAccessToken());
    }

    private void showUser() {
//        User user = userService.getUser("stefan.urech@sbb.ch");
        User user = userService.getUser("claude.baumann@sbb.ch");
        LOGGER.info("User: [{}] {}", user.id, user.displayName);
    }

    private void importEventsInDatabase() {
        LOGGER.info("Run import...");
        importService.runImport(List.of("stefan.urech@sbb.ch"));
        LOGGER.info("Import complete...");
    }

    private void showEventsForOneUser() {
        // Load user
        User user = userService.getUser("stefan.urech@sbb.ch");

        // Load events for that user
        List<Event> events = calendarService.getEventsFromCalendar(user);
        events.sort(Comparator.comparing(o -> DateTimeTimeZoneMapper.toZonedDateTime(o.start)));

        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            LocalDateTime start = DateTimeTimeZoneMapper.toZonedDateTime(event.start);
            LOGGER.info("[{}] {}: {}", i, start.format(DateTimeFormatter.ISO_LOCAL_DATE), event.subject);
        }
    }

    private void showUserSetting(){
//        User user = userService.getUser("claude.baumann@sbb.ch");
        User user = userService.getUser("stefan.urech@sbb.ch");
        UserSettings mailboxSettings = userService.getMailboxSettings(user);
        LOGGER.info(mailboxSettings.id);
    }

    private void getUserInGroup(){
        // Load User, which is a group
        String groupName = "DL TIMO-Factory";
        List<Group> groups = groupService.searchGroupByDisplayName(groupName);

        // We expect exacly one group
        if (groups.size() != 1) {
            throw new IllegalArgumentException("Found non or more then one Group for '" + groupName + '"');
        }
        Group group = groups.get(0);

        // Get members of this group
        GroupMembers membersInGroup = groupService.getMembersInGroup(group);
        List<Group> mGroups = membersInGroup.getGroups();
        List<User> mUsers = membersInGroup.getUsers();

        for (int i = 0; i < mGroups.size(); i++) {
            LOGGER.info("[{}] {}", i, mGroups.get(i).displayName);
        }
        for (int i = 0; i < mUsers.size(); i++) {
            LOGGER.info("[{}] {}", i, mUsers.get(i).displayName);
        }
    }

    private void showAllInGroup(){
        // Load User, which is a group
        String groupName = "DL TIMO-Factory";
        List<Group> groups = groupService.searchGroupByDisplayName(groupName);

        // We expect exacly one group
        if (groups.size() != 1) {
            throw new IllegalArgumentException("Found non or more then one Group for '" + groupName + '"');
        }
        Group group = groups.get(0);

        List<User> users = groupService.getAllUsersInGroup(group);
        for (int i = 0; i < users.size(); i++) {
            LOGGER.info("[{}] {}", i, users.get(i).displayName);
        }
    }
}
