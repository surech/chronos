package ch.surech.chronos.leecher.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class PersonServiceTest {

    private PersonService sut;

    @BeforeEach
    void setUp() {
        sut = new PersonService();
    }

    @Test
    void extractOrganisationFromName() {
        String org = sut.extractOrganisationFromName("Brunner Daniel (MP-VSV-BEV-VMG)");
        assertEquals("MP-VSV-BEV-VMG", org);

        org = sut.extractOrganisationFromName("Muster Alexandra (IT-PTR-EXT - Extern)");
        assertEquals("IT-PTR-EXT", org);

        org = sut.extractOrganisationFromName("Tester Daniel (IT-PTR-CEN2-BDE2 - Extern)");
        assertEquals("IT-PTR-CEN2-BDE2", org);

        org = sut.extractOrganisationFromName("Jochen Decker (IT)");
        assertEquals("IT", org);

        org = sut.extractOrganisationFromName("Hans Fehler");
        assertNull(org);
    }

    @Test
    void isExternal() {
        boolean external = sut.isExternal("Brunner Daniel (MP-VSV-BEV-VMG)");
        assertFalse(external);

        external = sut.isExternal("Muster Alexandra (IT-PTR-EXT - Extern)");
        assertTrue(external);

        external = sut.isExternal("Jochen Decker (IT)");
        assertFalse(external);
    }
}