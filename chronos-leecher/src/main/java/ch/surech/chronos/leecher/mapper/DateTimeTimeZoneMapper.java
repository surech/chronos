package ch.surech.chronos.leecher.mapper;

import com.microsoft.graph.models.DateTimeTimeZone;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;

public final class DateTimeTimeZoneMapper {

    private DateTimeTimeZoneMapper() {
    }

    public static LocalDateTime toZonedDateTime(DateTimeTimeZone dateTimeTimeZone) {
        // Parse date and time
        LocalDateTime result = LocalDateTime.parse(dateTimeTimeZone.dateTime);

        // Apply the zone information
        ZonedDateTime zonedDateTime = result.atZone(ZoneId.of(dateTimeTimeZone.timeZone));

        // Convert to our timezone
        return zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDateTime toZonedDateTime(Calendar createdDateTime) {
        return LocalDateTime.ofInstant(createdDateTime.toInstant(), ZoneId.systemDefault());
    }
}
