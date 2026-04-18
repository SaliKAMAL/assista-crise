package com.assistacrise.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class DemandeCriteriaTest {

    @Test
    void newDemandeCriteriaHasAllFiltersNullTest() {
        var demandeCriteria = new DemandeCriteria();
        assertThat(demandeCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void demandeCriteriaFluentMethodsCreatesFiltersTest() {
        var demandeCriteria = new DemandeCriteria();

        setAllFilters(demandeCriteria);

        assertThat(demandeCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void demandeCriteriaCopyCreatesNullFilterTest() {
        var demandeCriteria = new DemandeCriteria();
        var copy = demandeCriteria.copy();

        assertThat(demandeCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(demandeCriteria)
        );
    }

    @Test
    void demandeCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var demandeCriteria = new DemandeCriteria();
        setAllFilters(demandeCriteria);

        var copy = demandeCriteria.copy();

        assertThat(demandeCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(demandeCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var demandeCriteria = new DemandeCriteria();

        assertThat(demandeCriteria).hasToString("DemandeCriteria{}");
    }

    private static void setAllFilters(DemandeCriteria demandeCriteria) {
        demandeCriteria.id();
        demandeCriteria.titre();
        demandeCriteria.description();
        demandeCriteria.statut();
        demandeCriteria.dateCreation();
        demandeCriteria.dateMiseAJour();
        demandeCriteria.latitude();
        demandeCriteria.longitude();
        demandeCriteria.archivee();
        demandeCriteria.sinistreId();
        demandeCriteria.criseId();
        demandeCriteria.offreId();
        demandeCriteria.distinct();
    }

    private static Condition<DemandeCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTitre()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getStatut()) &&
                condition.apply(criteria.getDateCreation()) &&
                condition.apply(criteria.getDateMiseAJour()) &&
                condition.apply(criteria.getLatitude()) &&
                condition.apply(criteria.getLongitude()) &&
                condition.apply(criteria.getArchivee()) &&
                condition.apply(criteria.getSinistreId()) &&
                condition.apply(criteria.getCriseId()) &&
                condition.apply(criteria.getOffreId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<DemandeCriteria> copyFiltersAre(DemandeCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTitre(), copy.getTitre()) &&
                condition.apply(criteria.getDescription(), copy.getDescription()) &&
                condition.apply(criteria.getStatut(), copy.getStatut()) &&
                condition.apply(criteria.getDateCreation(), copy.getDateCreation()) &&
                condition.apply(criteria.getDateMiseAJour(), copy.getDateMiseAJour()) &&
                condition.apply(criteria.getLatitude(), copy.getLatitude()) &&
                condition.apply(criteria.getLongitude(), copy.getLongitude()) &&
                condition.apply(criteria.getArchivee(), copy.getArchivee()) &&
                condition.apply(criteria.getSinistreId(), copy.getSinistreId()) &&
                condition.apply(criteria.getCriseId(), copy.getCriseId()) &&
                condition.apply(criteria.getOffreId(), copy.getOffreId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
