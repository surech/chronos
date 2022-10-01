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
        LocalDateTime result = LocalDateTime.parse(dateTimeTimeZone.dateTime);
        return result;
    }

    public static LocalDateTime toZonedDateTime(Calendar createdDateTime) {
        return LocalDateTime.ofInstant(createdDateTime.toInstant(), ZoneId.systemDefault());
    }
}
