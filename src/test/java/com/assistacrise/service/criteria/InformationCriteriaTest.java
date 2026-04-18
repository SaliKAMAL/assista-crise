package com.assistacrise.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class InformationCriteriaTest {

    @Test
    void newInformationCriteriaHasAllFiltersNullTest() {
        var informationCriteria = new InformationCriteria();
        assertThat(informationCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void informationCriteriaFluentMethodsCreatesFiltersTest() {
        var informationCriteria = new InformationCriteria();

        setAllFilters(informationCriteria);

        assertThat(informationCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void informationCriteriaCopyCreatesNullFilterTest() {
        var informationCriteria = new InformationCriteria();
        var copy = informationCriteria.copy();

        assertThat(informationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(informationCriteria)
        );
    }

    @Test
    void informationCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var informationCriteria = new InformationCriteria();
        setAllFilters(informationCriteria);

        var copy = informationCriteria.copy();

        assertThat(informationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(informationCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var informationCriteria = new InformationCriteria();

        assertThat(informationCriteria).hasToString("InformationCriteria{}");
    }

    private static void setAllFilters(InformationCriteria informationCriteria) {
        informationCriteria.id();
        informationCriteria.titre();
        informationCriteria.contenu();
        informationCriteria.datePublication();
        informationCriteria.latitude();
        informationCriteria.longitude();
        informationCriteria.auteurId();
        informationCriteria.criseId();
        informationCriteria.distinct();
    }

    private static Condition<InformationCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTitre()) &&
                condition.apply(criteria.getContenu()) &&
                condition.apply(criteria.getDatePublication()) &&
                condition.apply(criteria.getLatitude()) &&
                condition.apply(criteria.getLongitude()) &&
                condition.apply(criteria.getAuteurId()) &&
                condition.apply(criteria.getCriseId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<InformationCriteria> copyFiltersAre(InformationCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTitre(), copy.getTitre()) &&
                condition.apply(criteria.getContenu(), copy.getContenu()) &&
                condition.apply(criteria.getDatePublication(), copy.getDatePublication()) &&
                condition.apply(criteria.getLatitude(), copy.getLatitude()) &&
                condition.apply(criteria.getLongitude(), copy.getLongitude()) &&
                condition.apply(criteria.getAuteurId(), copy.getAuteurId()) &&
                condition.apply(criteria.getCriseId(), copy.getCriseId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
