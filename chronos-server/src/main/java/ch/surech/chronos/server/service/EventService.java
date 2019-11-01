package ch.surech.chronos.server.service;

import ch.surech.chronos.server.entities.EventEntity;
import ch.surech.chronos.server.entities.UserEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final List<EventEntity> events = new ArrayList<>();

    public EventEntity saveEvent(EventEntity event) {
        events.add(event);
        return event;
    }

    public List<EventEntity> findEventForUser(UserEntity user){
        return events.stream().filter(e -> containsUser(e, user)).collect(Collectors.toList());
    }

    private boolean containsUser(EventEntity event, UserEntity user) {
        return user.equals(event.getOrganizer()) || event.getAttendees().contains(user);
    }
}
