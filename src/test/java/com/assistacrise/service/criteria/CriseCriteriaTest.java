package com.assistacrise.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CriseCriteriaTest {

    @Test
    void newCriseCriteriaHasAllFiltersNullTest() {
        var criseCriteria = new CriseCriteria();
        assertThat(criseCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void criseCriteriaFluentMethodsCreatesFiltersTest() {
        var criseCriteria = new CriseCriteria();

        setAllFilters(criseCriteria);

        assertThat(criseCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void criseCriteriaCopyCreatesNullFilterTest() {
        var criseCriteria = new CriseCriteria();
        var copy = criseCriteria.copy();

        assertThat(criseCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(criseCriteria)
        );
    }

    @Test
    void criseCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var criseCriteria = new CriseCriteria();
        setAllFilters(criseCriteria);

        var copy = criseCriteria.copy();

        assertThat(criseCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(criseCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var criseCriteria = new CriseCriteria();

        assertThat(criseCriteria).hasToString("CriseCriteria{}");
    }

    private static void setAllFilters(CriseCriteria criseCriteria) {
        criseCriteria.id();
        criseCriteria.titre();
        criseCriteria.description();
        criseCriteria.type();
        criseCriteria.dateDebut();
        criseCriteria.dateFermeture();
        criseCriteria.statut();
        criseCriteria.latitude();
        criseCriteria.longitude();
        criseCriteria.declarantId();
        criseCriteria.distinct();
    }

    private static Condition<CriseCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTitre()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getType()) &&
                condition.apply(criteria.getDateDebut()) &&
                condition.apply(criteria.getDateFermeture()) &&
                condition.apply(criteria.getStatut()) &&
                condition.apply(criteria.getLatitude()) &&
                condition.apply(criteria.getLongitude()) &&
                condition.apply(criteria.getDeclarantId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CriseCriteria> copyFiltersAre(CriseCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTitre(), copy.getTitre()) &&
                condition.apply(criteria.getDescription(), copy.getDescription()) &&
                condition.apply(criteria.getType(), copy.getType()) &&
                condition.apply(criteria.getDateDebut(), copy.getDateDebut()) &&
                condition.apply(criteria.getDateFermeture(), copy.getDateFermeture()) &&
                condition.apply(criteria.getStatut(), copy.getStatut()) &&
                condition.apply(criteria.getLatitude(), copy.getLatitude()) &&
                condition.apply(criteria.getLongitude(), copy.getLongitude()) &&
                condition.apply(criteria.getDeclarantId(), copy.getDeclarantId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
