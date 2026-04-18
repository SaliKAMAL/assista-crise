package com.assistacrise.domain;

import static com.assistacrise.domain.CriseTestSamples.*;
import static com.assistacrise.domain.DemandeTestSamples.*;
import static com.assistacrise.domain.OffreTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.assistacrise.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DemandeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Demande.class);
        Demande demande1 = getDemandeSample1();
        Demande demande2 = new Demande();
        assertThat(demande1).isNotEqualTo(demande2);

        demande2.setId(demande1.getId());
        assertThat(demande1).isEqualTo(demande2);

        demande2 = getDemandeSample2();
        assertThat(demande1).isNotEqualTo(demande2);
    }

    @Test
    void criseTest() {
        Demande demande = getDemandeRandomSampleGenerator();
        Crise criseBack = getCriseRandomSampleGenerator();

        demande.setCrise(criseBack);
        assertThat(demande.getCrise()).isEqualTo(criseBack);

        demande.crise(null);
        assertThat(demande.getCrise()).isNull();
    }

    @Test
    void offreTest() {
        Demande demande = getDemandeRandomSampleGenerator();
        Offre offreBack = getOffreRandomSampleGenerator();

        demande.addOffre(offreBack);
        assertThat(demande.getOffres()).containsOnly(offreBack);
        assertThat(offreBack.getDemandes()).containsOnly(demande);

        demande.removeOffre(offreBack);
        assertThat(demande.getOffres()).doesNotContain(offreBack);
        assertThat(offreBack.getDemandes()).doesNotContain(demande);

        demande.offres(new HashSet<>(Set.of(offreBack)));
        assertThat(demande.getOffres()).containsOnly(offreBack);
        assertThat(offreBack.getDemandes()).containsOnly(demande);

        demande.setOffres(new HashSet<>());
        assertThat(demande.getOffres()).doesNotContain(offreBack);
        assertThat(offreBack.getDemandes()).doesNotContain(demande);
    }
}
