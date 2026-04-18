package com.assistacrise.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Offre.
 */
@Entity
@Table(name = "offre")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Offre implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 3, max = 200)
    @Column(name = "titre", length = 200, nullable = false)
    private String titre;

    @NotNull
    @Size(max = 2000)
    @Column(name = "description", length = 2000, nullable = false)
    private String description;

    @NotNull
    @Column(name = "date_creation", nullable = false)
    private Instant dateCreation;

    @Column(name = "date_mise_a_jour")
    private Instant dateMiseAJour;

    @DecimalMin(value = "-90")
    @DecimalMax(value = "90")
    @Column(name = "latitude")
    private Double latitude;

    @DecimalMin(value = "-180")
    @DecimalMax(value = "180")
    @Column(name = "longitude")
    private Double longitude;

    @NotNull
    @Column(name = "archivee", nullable = false)
    private Boolean archivee;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @ManyToOne(optional = false)
    @NotNull
    private User citoyen;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "declarant" }, allowSetters = true)
    private Crise crise;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_offre__demande",
        joinColumns = @JoinColumn(name = "offre_id"),
        inverseJoinColumns = @JoinColumn(name = "demande_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "sinistre", "crise", "offres" }, allowSetters = true)
    private Set<Demande> demandes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Offre id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return this.titre;
    }

    public Offre titre(String titre) {
        this.setTitre(titre);
        return this;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return this.description;
    }

    public Offre description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getDateCreation() {
        return this.dateCreation;
    }

    public Offre dateCreation(Instant dateCreation) {
        this.setDateCreation(dateCreation);
        return this;
    }

    public void setDateCreation(Instant dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Instant getDateMiseAJour() {
        return this.dateMiseAJour;
    }

    public Offre dateMiseAJour(Instant dateMiseAJour) {
        this.setDateMiseAJour(dateMiseAJour);
        return this;
    }

    public void setDateMiseAJour(Instant dateMiseAJour) {
        this.dateMiseAJour = dateMiseAJour;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Offre latitude(Double latitude) {
        this.setLatitude(latitude);
        return this;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public Offre longitude(Double longitude) {
        this.setLongitude(longitude);
        return this;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Boolean getArchivee() {
        return this.archivee;
    }

    public Offre archivee(Boolean archivee) {
        this.setArchivee(archivee);
        return this;
    }

    public void setArchivee(Boolean archivee) {
        this.archivee = archivee;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Offre active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public User getCitoyen() {
        return this.citoyen;
    }

    public void setCitoyen(User user) {
        this.citoyen = user;
    }

    public Offre citoyen(User user) {
        this.setCitoyen(user);
        return this;
    }

    public Crise getCrise() {
        return this.crise;
    }

    public void setCrise(Crise crise) {
        this.crise = crise;
    }

    public Offre crise(Crise crise) {
        this.setCrise(crise);
        return this;
    }

    public Set<Demande> getDemandes() {
        return this.demandes;
    }

    public void setDemandes(Set<Demande> demandes) {
        this.demandes = demandes;
    }

    public Offre demandes(Set<Demande> demandes) {
        this.setDemandes(demandes);
        return this;
    }

    public Offre addDemande(Demande demande) {
        this.demandes.add(demande);
        return this;
    }

    public Offre removeDemande(Demande demande) {
        this.demandes.remove(demande);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Offre)) {
            return false;
        }
        return getId() != null && getId().equals(((Offre) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Offre{" +
            "id=" + getId() +
            ", titre='" + getTitre() + "'" +
            ", description='" + getDescription() + "'" +
            ", dateCreation='" + getDateCreation() + "'" +
            ", dateMiseAJour='" + getDateMiseAJour() + "'" +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            ", archivee='" + getArchivee() + "'" +
            ", active='" + getActive() + "'" +
            "}";
    }
}
