package ch.surech.chronos.server.service;

import ch.surech.chronos.server.entities.EventEntity;
import ch.surech.chronos.server.entities.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class EventServiceTest {

    private EventService sut;

    @BeforeEach
    void setUp() {
        sut = new EventService();
    }

    @Test
    void findEventForUser() {
        // Create a few users
        UserEntity user1 = UserEntity.builder().name("Hans").email("hans@example.com").build();
        UserEntity user2 = UserEntity.builder().name("Peter").email("peter@example.com").build();
        UserEntity user3 = UserEntity.builder().name("Andrea").email("andrea@example.com").build();
        UserEntity user4 = UserEntity.builder().name("Thomas").email("thomas@example.com").build();
        UserEntity user5 = UserEntity.builder().name("Mike").email("mike@example.com").build();

        // Create a few events
        EventEntity event1 = EventEntity.builder().organizer(user1).attendee(user2).attendee(user3).build();
        EventEntity event2 = EventEntity.builder().organizer(user1).attendee(user3).build();
        EventEntity event3 = EventEntity.builder().organizer(user4).attendee(user1).build();
        EventEntity event4 = EventEntity.builder().organizer(user4).attendee(user2).attendee(user3).build();

        // Save all Events
        Stream.of(event1, event2, event3, event4).forEach(sut::saveEvent);

        // Run Test
        List<EventEntity> result = sut.findEventForUser(user1);
        Assertions.assertEquals(3, result.size());
        Assertions.assertTrue(result.contains(event1));
        Assertions.assertTrue(result.contains(event2));
        Assertions.assertTrue(result.contains(event3));

        result = sut.findEventForUser(user2);
        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains(event1));
        Assertions.assertTrue(result.contains(event4));

        result = sut.findEventForUser(user3);
        Assertions.assertEquals(3, result.size());
        Assertions.assertTrue(result.contains(event1));
        Assertions.assertTrue(result.contains(event2));
        Assertions.assertTrue(result.contains(event4));

        result = sut.findEventForUser(user5);
        Assertions.assertTrue(result.isEmpty());
    }
}