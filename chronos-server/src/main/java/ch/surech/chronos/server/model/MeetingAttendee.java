package ch.surech.chronos.server.model;

import ch.surech.chronos.server.entities.EventEntity;
import ch.surech.chronos.server.entities.UserEntity;
import ch.surech.chronos.server.entities.UserPrecentePreferenceEntity;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MeetingAttendee {
    private UserEntity user;
    private List<UserPrecentePreferenceEntity> precentePreferences;

    private List<EventEntity> events;
}
