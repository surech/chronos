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
import org.apache.commons.lang3.StringUtils;

@Entity
@Table(name = "importedevent")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportedEvent extends TimeRange {

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

    public void setSubject(String subject) {
        this.subject = StringUtils.substring(subject, 0, 200);
    }

    public void setBodyPreview(String bodyPreview) {
        this.bodyPreview = StringUtils.substring(bodyPreview, 0, 200);
    }

    public void setLocationName(String locationName) {
        this.locationName = StringUtils.substring(locationName, 0, 200);
    }

    public void setLocationId(String locationId) {
        this.locationId = StringUtils.substring(locationId, 0, 200);
    }

    public void setLocationUri(String locationUri) {
        this.locationUri = locationUri;
    }

    public static class ImportedEventBuilder{
        private String subject;
        private String bodyPreview;

        private String locationName;

        private String locationId;

        private String locationUri;

        public ImportedEventBuilder subject(String subject){
            this.subject = StringUtils.substring(subject, 0, 200);
            return this;
        }

        public ImportedEventBuilder bodyPreview(String bodyPreview){
            this.bodyPreview = StringUtils.substring(bodyPreview, 0, 200);
            return this;
        }

        public ImportedEventBuilder locationName(String locationName){
            this.locationName = StringUtils.substring(locationName, 0, 200);
            return this;
        }

        public ImportedEventBuilder locationId(String locationId){
            this.locationId = StringUtils.substring(locationId, 0, 200);
            return this;
        }

        public ImportedEventBuilder locationUri(String locationUri){
            this.locationUri = StringUtils.substring(locationUri, 0, 200);
            return this;
        }
    }
}
