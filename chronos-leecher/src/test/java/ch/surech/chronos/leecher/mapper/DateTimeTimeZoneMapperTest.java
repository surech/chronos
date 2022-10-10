package ch.surech.chronos.leecher.mapper;

import com.microsoft.graph.models.DateTimeTimeZone;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DateTimeTimeZoneMapperTest {

    @Test
    void testToZonedDateTime() {
        DateTimeTimeZone dttz = new DateTimeTimeZone();
        dttz.dateTime = "2022-10-11T11:00:00.0000000";
        dttz.timeZone = "UTC";

        LocalDateTime result = DateTimeTimeZoneMapper.toZonedDateTime(dttz);
        assertEquals(13, result.getHour());
        assertEquals(0, result.getMinute());

    }
}