package ch.surech.chronos.server.entities;

import ch.surech.chronos.api.model.PrecentePreferenceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalTime;
import java.time.ZonedDateTime;

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
