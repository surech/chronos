package ch.surech.chronos.server.mapper;

import ch.surech.chronos.api.model.UserPrecentePreference;
import ch.surech.chronos.server.entities.UserEntity;
import ch.surech.chronos.server.entities.UserPrecentePreferenceEntity;
import org.springframework.stereotype.Component;

@Component
public class UserPrecentePreferenceMapper {

    public UserPrecentePreferenceEntity toEntity(UserPrecentePreference preference, UserEntity user) {
        UserPrecentePreferenceEntity.UserPrecentePreferenceEntityBuilder builder = UserPrecentePreferenceEntity.builder();
        builder.from(preference.getFrom());
        builder.to(preference.getTo());
        builder.preference(preference.getPreference());
        builder.user(user);

        return builder.build();
    }

    public UserPrecentePreference fromEntity(UserPrecentePreferenceEntity entity) {
        UserPrecentePreference.UserPrecentePreferenceBuilder builder = UserPrecentePreference.builder();
        builder.from(entity.getFrom());
        builder.to(entity.getTo());
        builder.preference(entity.getPreference());
        builder.id(entity.getId());

        return builder.build();
    }
}
