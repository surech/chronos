package ch.surech.chronos.leecher.mapper;

import ch.surech.chronos.analyser.persistence.model.Person;
import com.microsoft.graph.models.User;

public final class PersonMapper {

    public static Person toModel(User user){
        Person.PersonBuilder builder = Person.builder();
        builder.graphId(user.id);
        builder.surname(user.surname);
        builder.givenName(user.givenName);
        builder.displayName(user.displayName);
        builder.mail(user.mail);
        builder.userPrincipalName(user.userPrincipalName);
        return builder.build();
    }
}
