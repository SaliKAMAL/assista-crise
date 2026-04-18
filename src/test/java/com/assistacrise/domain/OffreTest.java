package com.assistacrise.domain;

import static com.assistacrise.domain.CriseTestSamples.*;
import static com.assistacrise.domain.DemandeTestSamples.*;
import static com.assistacrise.domain.OffreTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.assistacrise.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class OffreTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Offre.class);
        Offre offre1 = getOffreSample1();
        Offre offre2 = new Offre();
        assertThat(offre1).isNotEqualTo(offre2);

        offre2.setId(offre1.getId());
        assertThat(offre1).isEqualTo(offre2);

        offre2 = getOffreSample2();
        assertThat(offre1).isNotEqualTo(offre2);
    }

    @Test
    void criseTest() {
        Offre offre = getOffreRandomSampleGenerator();
        Crise criseBack = getCriseRandomSampleGenerator();

        offre.setCrise(criseBack);
        assertThat(offre.getCrise()).isEqualTo(criseBack);

        offre.crise(null);
        assertThat(offre.getCrise()).isNull();
    }

    @Test
    void demandeTest() {
        Offre offre = getOffreRandomSampleGenerator();
        Demande demandeBack = getDemandeRandomSampleGenerator();

        offre.addDemande(demandeBack);
        assertThat(offre.getDemandes()).containsOnly(demandeBack);

        offre.removeDemande(demandeBack);
        assertThat(offre.getDemandes()).doesNotContain(demandeBack);

        offre.demandes(new HashSet<>(Set.of(demandeBack)));
        assertThat(offre.getDemandes()).containsOnly(demandeBack);

        offre.setDemandes(new HashSet<>());
        assertThat(offre.getDemandes()).doesNotContain(demandeBack);
    }
}
