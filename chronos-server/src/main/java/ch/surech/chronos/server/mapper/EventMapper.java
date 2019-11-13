package ch.surech.chronos.server.mapper;

import ch.surech.chronos.api.model.Event;
import ch.surech.chronos.api.model.Invitee;
import ch.surech.chronos.api.model.User;
import ch.surech.chronos.server.entities.EventEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class EventMapper {

    @Autowired
    private UserMapper userMapper;

    public Event fromEntity(EventEntity entity) {
        Event.EventBuilder builder = Event.builder();
        builder.subject(entity.getSubject());
        builder.body(entity.getBodyPreview());
        builder.start(entity.getStart());
        builder.end(entity.getEnd());

        // Map users
        User organizer = userMapper.fromEntity(entity.getOrganizer());
        List<Invitee> invitees = entity.getAttendees().stream()
                .filter(Objects::nonNull)
                .map(a -> Invitee.builder().email(a.getEmail()).optional(false).build())
                .collect(Collectors.toList());

        if(entity.getOrganizer() != null) {
            builder.organizer(entity.getOrganizer().getEmail());
        }
        builder.invitees(invitees);

        return builder.build();
    }
}
