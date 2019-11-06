package ch.surech.chronos.server.entities;

import ch.surech.chronos.api.model.PrecentePreferenceType;
import ch.surech.chronos.api.model.Weekdays;
import ch.surech.chronos.server.utils.WeekdayConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalTime;
import java.time.Period;
import java.util.EnumSet;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_precente_preference")
public class UserPrecentePreferenceEntity extends BaseEntity {

    @Column(name = "start", columnDefinition = "TIME")
    private LocalTime from;

    @Column(name = "end", columnDefinition = "TIME")
    private LocalTime to;

    @Column(name = "preference_type")
    @Enumerated(EnumType.STRING)
    private PrecentePreferenceType preference;

    @ManyToOne
    @JoinColumn(name = "chronos_user_fk")
    private UserEntity user;
}
