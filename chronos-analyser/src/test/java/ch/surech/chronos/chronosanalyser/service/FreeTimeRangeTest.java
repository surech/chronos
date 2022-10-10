package ch.surech.chronos.chronosanalyser.service;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class FreeTimeRangeTest {
    @Test
    void testCalculateUsefulness() {
        FreeTimeRange sut = new FreeTimeRange(getTime(13, 0), getTime(17, 0));
        assertEquals(8d, sut.calculateUsefulness());

        sut = new FreeTimeRange(getTime(8, 0), getTime(8, 30));
        assertEquals(0.125d, sut.calculateUsefulness());
    }

    @NotNull
    private LocalDateTime getTime(int hour, int minute) {
        LocalDate today = LocalDate.now();
        return LocalDateTime.of(today, LocalTime.of(hour, minute));
    }
}