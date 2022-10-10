package ch.surech.chronos.analyser.persistence.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "freetime_day")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreetimeDay {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "person_fk")
    private Person person;

    @Column(name = "date", columnDefinition = "date", nullable = false)
    private LocalDate date;

    @Column(name = "freetime_in_minutes", nullable = false)
    private int freetimeInMinutes;

    @Column(name = "usefulness", nullable = false)
    private double usefulness;

    @Column(name = "usefulness_optimal", nullable = false)
    private double usefulnessOptimal;

    @Column(name = "potential", nullable = false)
    private double potential;

    @OneToMany(mappedBy = "day")
    @Singular
    private List<FreetimeSlot> slots;
}
