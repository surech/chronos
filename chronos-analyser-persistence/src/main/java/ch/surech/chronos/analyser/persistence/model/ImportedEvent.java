package ch.surech.chronos.analyser.persistence.model;

import com.microsoft.graph.models.EventType;
import com.microsoft.graph.models.FreeBusyStatus;
import com.microsoft.graph.models.Importance;
import com.microsoft.graph.models.LocationType;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;

@Entity
@Table(name = "importedevent")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportedEvent implements TimeRange {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "icaluid")
    private String iCalUId;

    @Column(name = "subject")
    private String subject;

    @Column(name = "body_preview")
    private String bodyPreview;

    @Column(name = "importance")
    @Enumerated(EnumType.STRING)
    private Importance importance;

    @Column(name = "start", columnDefinition = "TIMESTAMP")
    private LocalDateTime start;

    @Column(name = "\"end\"", columnDefinition = "TIMESTAMP")
    private LocalDateTime end;

    @Column(name = "location_name")
    private String locationName;

    @Column(name = "location_uri")
    private String locationUri;

    @Column(name = "location_type")
    @Enumerated(EnumType.STRING)
    private LocationType locationType;

    @Column(name = "location_id")
    private String locationId;

    @Column(name = "is_all_day")
    private boolean isAllDay;

    @Column(name = "is_cancelled")
    private boolean isCancelled;

    @Column(name = "is_organizer")
    private boolean isOrganizer;

    @Column(name = "series_master_id")
    private String seriesMasterId;

    @Column(name = "show_as")
    @Enumerated(EnumType.STRING)
    private FreeBusyStatus showAs;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private EventType type;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP")
    private ZonedDateTime createdAt;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "organizer_fk")
    private Participant organizer;

    @OneToMany(mappedBy = "event")
    @Singular
    private List<Participant> attendees;
}
