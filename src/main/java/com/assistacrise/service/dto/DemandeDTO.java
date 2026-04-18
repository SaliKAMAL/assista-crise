package com.assistacrise.service.dto;

import com.assistacrise.domain.enumeration.StatutDemande;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.assistacrise.domain.Demande} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DemandeDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 3, max = 200)
    private String titre;

    @NotNull
    @Size(max = 2000)
    private String description;

    @NotNull
    private StatutDemande statut;

    @NotNull
    private Instant dateCreation;

    private Instant dateMiseAJour;

    @DecimalMin(value = "-90")
    @DecimalMax(value = "90")
    private Double latitude;

    @DecimalMin(value = "-180")
    @DecimalMax(value = "180")
    private Double longitude;

    @NotNull
    private Boolean archivee;

    @NotNull
    private UserDTO sinistre;

    @NotNull
    private CriseDTO crise;

    private Set<OffreDTO> offres = new HashSet<>();

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

    public StatutDemande getStatut() {
        return statut;
    }

    public void setStatut(StatutDemande statut) {
        this.statut = statut;
    }

    public Instant getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Instant dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Instant getDateMiseAJour() {
        return dateMiseAJour;
    }

    public void setDateMiseAJour(Instant dateMiseAJour) {
        this.dateMiseAJour = dateMiseAJour;
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

    public Boolean getArchivee() {
        return archivee;
    }

    public void setArchivee(Boolean archivee) {
        this.archivee = archivee;
    }

    public UserDTO getSinistre() {
        return sinistre;
    }

    public void setSinistre(UserDTO sinistre) {
        this.sinistre = sinistre;
    }

    public CriseDTO getCrise() {
        return crise;
    }

    public void setCrise(CriseDTO crise) {
        this.crise = crise;
    }

    public Set<OffreDTO> getOffres() {
        return offres;
    }

    public void setOffres(Set<OffreDTO> offres) {
        this.offres = offres;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DemandeDTO)) {
            return false;
        }

        DemandeDTO demandeDTO = (DemandeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, demandeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DemandeDTO{" +
            "id=" + getId() +
            ", titre='" + getTitre() + "'" +
            ", description='" + getDescription() + "'" +
            ", statut='" + getStatut() + "'" +
            ", dateCreation='" + getDateCreation() + "'" +
            ", dateMiseAJour='" + getDateMiseAJour() + "'" +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            ", archivee='" + getArchivee() + "'" +
            ", sinistre=" + getSinistre() +
            ", crise=" + getCrise() +
            ", offres=" + getOffres() +
            "}";
    }
}
