package ch.surech.chronos.server.entities;

import ch.surech.chronos.api.model.Weekdays;
import ch.surech.chronos.server.utils.WeekdayConverter;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "email", callSuper = false)
@Table(name = "chronos_user")
public class UserEntity extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @OneToMany(mappedBy = "user")
    @Singular
    private List<UserPrecentePreferenceEntity> precentePreferences;

    @Column(name = "working_days")
    @Convert(converter = WeekdayConverter.class)
    private EnumSet<Weekdays> workingDays;
}
