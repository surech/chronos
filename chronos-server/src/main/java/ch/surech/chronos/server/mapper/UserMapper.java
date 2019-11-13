package ch.surech.chronos.server.mapper;

import ch.surech.chronos.api.model.Invitee;
import ch.surech.chronos.api.model.User;
import ch.surech.chronos.server.entities.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserEntity toEntity(User user){
        UserEntity.UserEntityBuilder builder = UserEntity.builder();
        builder.name(user.getName());
        builder.email(user.getEmail());
        builder.workingDays(user.getWorkingDays());

        return builder.build();
    }

    public User fromEntity(UserEntity entity){
        if(entity == null){
            return null;
        }

        User.UserBuilder builder = User.builder();
        builder.name(entity.getName());
        builder.email(entity.getEmail());
        builder.workingDays(entity.getWorkingDays());

        return builder.build();
    }
}
