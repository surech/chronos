package ch.surech.chronos.leecher.mapper;

import com.microsoft.graph.models.DateTimeTimeZone;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;

public final class DateTimeTimeZoneMapper {

    private DateTimeTimeZoneMapper() {
    }

    public static ZonedDateTime toZonedDateTime(DateTimeTimeZone dateTimeTimeZone) {
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeTimeZone.dateTime);
        ZonedDateTime result = ZonedDateTime.of(dateTime, ZoneId.of(dateTimeTimeZone.timeZone));
        return result;
    }

    public static ZonedDateTime toZonedDateTime(Calendar createdDateTime) {
        return ZonedDateTime.ofInstant(createdDateTime.toInstant(), ZoneId.of("UTC"));
    }
}
