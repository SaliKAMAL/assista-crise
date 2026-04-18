package com.assistacrise.service.dto;

import com.assistacrise.domain.enumeration.StatutCrise;
import com.assistacrise.domain.enumeration.TypeCrise;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.assistacrise.domain.Crise} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CriseDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 3, max = 200)
    private String titre;

    @Size(max = 2000)
    private String description;

    @NotNull
    private TypeCrise type;

    @NotNull
    private Instant dateDebut;

    private Instant dateFermeture;

    @NotNull
    private StatutCrise statut;

    @NotNull
    @DecimalMin(value = "-90")
    @DecimalMax(value = "90")
    private Double latitude;

    @NotNull
    @DecimalMin(value = "-180")
    @DecimalMax(value = "180")
    private Double longitude;

    @NotNull
    private UserDTO declarant;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TypeCrise getType() {
        return type;
    }

    public void setType(TypeCrise type) {
        this.type = type;
    }

    public Instant getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Instant dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Instant getDateFermeture() {
        return dateFermeture;
    }

    public void setDateFermeture(Instant dateFermeture) {
        this.dateFermeture = dateFermeture;
    }

    public StatutCrise getStatut() {
        return statut;
    }

    public void setStatut(StatutCrise statut) {
        this.statut = statut;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public UserDTO getDeclarant() {
        return declarant;
    }

    public void setDeclarant(UserDTO declarant) {
        this.declarant = declarant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CriseDTO)) {
            return false;
        }

        CriseDTO criseDTO = (CriseDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, criseDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CriseDTO{" +
            "id=" + getId() +
            ", titre='" + getTitre() + "'" +
            ", description='" + getDescription() + "'" +
            ", type='" + getType() + "'" +
            ", dateDebut='" + getDateDebut() + "'" +
            ", dateFermeture='" + getDateFermeture() + "'" +
            ", statut='" + getStatut() + "'" +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            ", declarant=" + getDeclarant() +
            "}";
    }
}
