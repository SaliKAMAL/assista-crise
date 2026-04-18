package com.assistacrise.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.assistacrise.domain.Information} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InformationDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 3, max = 200)
    private String titre;

    @NotNull
    @Size(max = 5000)
    private String contenu;

    @NotNull
    private Instant datePublication;

    @DecimalMin(value = "-90")
    @DecimalMax(value = "90")
    private Double latitude;

    @DecimalMin(value = "-180")
    @DecimalMax(value = "180")
    private Double longitude;

    @NotNull
    private UserDTO auteur;

    @NotNull
    private CriseDTO crise;

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

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Instant getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(Instant datePublication) {
        this.datePublication = datePublication;
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

    public UserDTO getAuteur() {
        return auteur;
    }

    public void setAuteur(UserDTO auteur) {
        this.auteur = auteur;
    }

    public CriseDTO getCrise() {
        return crise;
    }

    public void setCrise(CriseDTO crise) {
        this.crise = crise;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InformationDTO)) {
            return false;
        }

        InformationDTO informationDTO = (InformationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, informationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InformationDTO{" +
            "id=" + getId() +
            ", titre='" + getTitre() + "'" +
            ", contenu='" + getContenu() + "'" +
            ", datePublication='" + getDatePublication() + "'" +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            ", auteur=" + getAuteur() +
            ", crise=" + getCrise() +
            "}";
    }
}
