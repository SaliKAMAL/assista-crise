package com.assistacrise.service.criteria;

import com.assistacrise.domain.enumeration.StatutCrise;
import com.assistacrise.domain.enumeration.TypeCrise;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.assistacrise.domain.Crise} entity. This class is used
 * in {@link com.assistacrise.web.rest.CriseResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /crises?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CriseCriteria implements Serializable, Criteria {

    /**
     * Class for filtering TypeCrise
     */
    public static class TypeCriseFilter extends Filter<TypeCrise> {

        public TypeCriseFilter() {}

        public TypeCriseFilter(TypeCriseFilter filter) {
            super(filter);
        }

        @Override
        public TypeCriseFilter copy() {
            return new TypeCriseFilter(this);
        }
    }

    /**
     * Class for filtering StatutCrise
     */
    public static class StatutCriseFilter extends Filter<StatutCrise> {

        public StatutCriseFilter() {}

        public StatutCriseFilter(StatutCriseFilter filter) {
            super(filter);
        }

        @Override
        public StatutCriseFilter copy() {
            return new StatutCriseFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter titre;

    private StringFilter description;

    private TypeCriseFilter type;

    private InstantFilter dateDebut;

    private InstantFilter dateFermeture;

    private StatutCriseFilter statut;

    private DoubleFilter latitude;

    private DoubleFilter longitude;

    private LongFilter declarantId;

    private Boolean distinct;

    public CriseCriteria() {}

    public CriseCriteria(CriseCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.titre = other.optionalTitre().map(StringFilter::copy).orElse(null);
        this.description = other.optionalDescription().map(StringFilter::copy).orElse(null);
        this.type = other.optionalType().map(TypeCriseFilter::copy).orElse(null);
        this.dateDebut = other.optionalDateDebut().map(InstantFilter::copy).orElse(null);
        this.dateFermeture = other.optionalDateFermeture().map(InstantFilter::copy).orElse(null);
        this.statut = other.optionalStatut().map(StatutCriseFilter::copy).orElse(null);
        this.latitude = other.optionalLatitude().map(DoubleFilter::copy).orElse(null);
        this.longitude = other.optionalLongitude().map(DoubleFilter::copy).orElse(null);
        this.declarantId = other.optionalDeclarantId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CriseCriteria copy() {
        return new CriseCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getTitre() {
        return titre;
    }

    public Optional<StringFilter> optionalTitre() {
        return Optional.ofNullable(titre);
    }

    public StringFilter titre() {
        if (titre == null) {
            setTitre(new StringFilter());
        }
        return titre;
    }

    public void setTitre(StringFilter titre) {
        this.titre = titre;
    }

    public StringFilter getDescription() {
        return description;
    }

    public Optional<StringFilter> optionalDescription() {
        return Optional.ofNullable(description);
    }

    public StringFilter description() {
        if (description == null) {
            setDescription(new StringFilter());
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public TypeCriseFilter getType() {
        return type;
    }

    public Optional<TypeCriseFilter> optionalType() {
        return Optional.ofNullable(type);
    }

    public TypeCriseFilter type() {
        if (type == null) {
            setType(new TypeCriseFilter());
        }
        return type;
    }

    public void setType(TypeCriseFilter type) {
        this.type = type;
    }

    public InstantFilter getDateDebut() {
        return dateDebut;
    }

    public Optional<InstantFilter> optionalDateDebut() {
        return Optional.ofNullable(dateDebut);
    }

    public InstantFilter dateDebut() {
        if (dateDebut == null) {
            setDateDebut(new InstantFilter());
        }
        return dateDebut;
    }

    public void setDateDebut(InstantFilter dateDebut) {
        this.dateDebut = dateDebut;
    }

    public InstantFilter getDateFermeture() {
        return dateFermeture;
    }

    public Optional<InstantFilter> optionalDateFermeture() {
        return Optional.ofNullable(dateFermeture);
    }

    public InstantFilter dateFermeture() {
        if (dateFermeture == null) {
            setDateFermeture(new InstantFilter());
        }
        return dateFermeture;
    }

    public void setDateFermeture(InstantFilter dateFermeture) {
        this.dateFermeture = dateFermeture;
    }

    public StatutCriseFilter getStatut() {
        return statut;
    }

    public Optional<StatutCriseFilter> optionalStatut() {
        return Optional.ofNullable(statut);
    }

    public StatutCriseFilter statut() {
        if (statut == null) {
            setStatut(new StatutCriseFilter());
        }
        return statut;
    }

    public void setStatut(StatutCriseFilter statut) {
        this.statut = statut;
    }

    public DoubleFilter getLatitude() {
        return latitude;
    }

    public Optional<DoubleFilter> optionalLatitude() {
        return Optional.ofNullable(latitude);
    }

    public DoubleFilter latitude() {
        if (latitude == null) {
            setLatitude(new DoubleFilter());
        }
        return latitude;
    }

    public void setLatitude(DoubleFilter latitude) {
        this.latitude = latitude;
    }

    public DoubleFilter getLongitude() {
        return longitude;
    }

    public Optional<DoubleFilter> optionalLongitude() {
        return Optional.ofNullable(longitude);
    }

    public DoubleFilter longitude() {
        if (longitude == null) {
            setLongitude(new DoubleFilter());
        }
        return longitude;
    }

    public void setLongitude(DoubleFilter longitude) {
        this.longitude = longitude;
    }

    public LongFilter getDeclarantId() {
        return declarantId;
    }

    public Optional<LongFilter> optionalDeclarantId() {
        return Optional.ofNullable(declarantId);
    }

    public LongFilter declarantId() {
        if (declarantId == null) {
            setDeclarantId(new LongFilter());
        }
        return declarantId;
    }

    public void setDeclarantId(LongFilter declarantId) {
        this.declarantId = declarantId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CriseCriteria that = (CriseCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(titre, that.titre) &&
            Objects.equals(description, that.description) &&
            Objects.equals(type, that.type) &&
            Objects.equals(dateDebut, that.dateDebut) &&
            Objects.equals(dateFermeture, that.dateFermeture) &&
            Objects.equals(statut, that.statut) &&
            Objects.equals(latitude, that.latitude) &&
            Objects.equals(longitude, that.longitude) &&
            Objects.equals(declarantId, that.declarantId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, titre, description, type, dateDebut, dateFermeture, statut, latitude, longitude, declarantId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CriseCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTitre().map(f -> "titre=" + f + ", ").orElse("") +
            optionalDescription().map(f -> "description=" + f + ", ").orElse("") +
            optionalType().map(f -> "type=" + f + ", ").orElse("") +
            optionalDateDebut().map(f -> "dateDebut=" + f + ", ").orElse("") +
            optionalDateFermeture().map(f -> "dateFermeture=" + f + ", ").orElse("") +
            optionalStatut().map(f -> "statut=" + f + ", ").orElse("") +
            optionalLatitude().map(f -> "latitude=" + f + ", ").orElse("") +
            optionalLongitude().map(f -> "longitude=" + f + ", ").orElse("") +
            optionalDeclarantId().map(f -> "declarantId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
