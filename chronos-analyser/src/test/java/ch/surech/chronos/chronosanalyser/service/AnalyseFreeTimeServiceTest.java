package ch.surech.chronos.chronosanalyser.service;

import static org.junit.jupiter.api.Assertions.*;

import ch.surech.chronos.analyser.persistence.model.ImportedEvent;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import java.time.LocalDate;
import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AnalyseFreeTimeServiceTest {

    private AnalyseFreeTimeService sut;

    @BeforeEach
    void setUp() {
        sut = new AnalyseFreeTimeService();
    }

    @Test
    void generateDateList() {
        LocalDate start = LocalDate.of(2022, Month.MARCH, 8);
        LocalDate end = LocalDate.of(2022, Month.MARCH, 12);
        List<LocalDate> result = sut.generateDateList(start, end);

        assertEquals(5, result.size());
        assertEquals(LocalDate.of(2022, Month.MARCH, 8), result.get(0));
        assertEquals(LocalDate.of(2022, Month.MARCH, 9), result.get(1));
        assertEquals(LocalDate.of(2022, Month.MARCH, 10), result.get(2));
        assertEquals(LocalDate.of(2022, Month.MARCH, 11), result.get(3));
        assertEquals(LocalDate.of(2022, Month.MARCH, 12), result.get(4));
    }
}