package ch.surech.chronos.leecher.mapper;

import ch.surech.chronos.leecher.model.ImportedEvent;
import com.microsoft.graph.models.Event;
import java.time.ZonedDateTime;

public final class ImportedEventMapper {

    private ImportedEventMapper() {
    }

    public static ImportedEvent toModel(Event event){
        ImportedEvent.ImportedEventBuilder builder = ImportedEvent.builder();
        builder.iCalUId(event.iCalUId);
        builder.subject(event.subject);
        builder.bodyPreview(event.bodyPreview);
        builder.importance(event.importance);
        builder.start(DateTimeTimeZoneMapper.toZonedDateTime(event.start));
        builder.end(DateTimeTimeZoneMapper.toZonedDateTime(event.end));
        if(event.location != null) {
            builder.locationName(event.location.displayName);
            builder.locationUri(event.location.locationUri);
            builder.locationType(event.location.locationType);
            builder.locationId(event.location.uniqueId);
        }
        builder.isAllDay(nc(event.isAllDay));
        builder.isCancelled(nc(event.isCancelled));
        builder.isOrganizer(nc(event.isOrganizer));
        builder.seriesMasterId(event.seriesMasterId);
        builder.showAs(event.showAs);
        builder.type(event.type);
        builder.organizer(ParticipantMapper.toParticipant(event.organizer));
        if (event.attendees != null) {
            event.attendees.stream().map(ParticipantMapper::toParticipant).forEach(builder::attendee);
        }
//        builder.createdAt(DateTimeTimeZoneMapper.toZonedDateTime(event.createdDateTime));
        builder.createdAt(ZonedDateTime.now());

        return builder.build();
    }

    private static boolean nc(Boolean value){
        return nc(value, false);
    }

    private static boolean nc(Boolean value, boolean defaultValue){
        if(value == null){
            return defaultValue;
        } else {
            return value.booleanValue();
        }
    }
}
