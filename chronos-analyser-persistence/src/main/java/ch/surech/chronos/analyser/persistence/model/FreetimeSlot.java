package ch.surech.chronos.analyser.persistence.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "freetime_slot")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreetimeSlot {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "day_fk")
    private FreetimeDay day;

    @Column(name = "start", columnDefinition = "TIMESTAMP")
    private LocalDateTime start;

    @Column(name = "\"end\"", columnDefinition = "TIMESTAMP")
    private LocalDateTime end;

    @Column(name = "duration_in_minutes", nullable = false)
    private int durationInMinutes;

    @Column(name = "usefulness", nullable = false)
    private double usefulness;
}
