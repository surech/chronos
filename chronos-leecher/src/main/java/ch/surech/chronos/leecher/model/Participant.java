package ch.surech.chronos.leecher.model;

import com.microsoft.graph.models.AttendeeType;
import com.microsoft.graph.models.ResponseType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "participant")
@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Participant {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "attendee_type")
    @Enumerated(EnumType.STRING)
    private AttendeeType attendeeType;

    @ManyToOne
    @JoinColumn(name = "event_fk")
    private ImportedEvent event;

    @Column(name = "response_status")
    @Enumerated(EnumType.STRING)
    private ResponseType responseStatus;
}
