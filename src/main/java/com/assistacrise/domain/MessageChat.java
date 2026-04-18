package com.assistacrise.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A MessageChat.
 */
@Entity
@Table(name = "message_chat")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MessageChat implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 1, max = 1000)
    @Column(name = "contenu", length = 1000, nullable = false)
    private String contenu;

    @NotNull
    @Column(name = "date_envoi", nullable = false)
    private Instant dateEnvoi;

    @ManyToOne(optional = false)
    @NotNull
    private User auteur;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "sinistre", "crise", "offres" }, allowSetters = true)
    private Demande demande;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MessageChat id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContenu() {
        return this.contenu;
    }

    public MessageChat contenu(String contenu) {
        this.setContenu(contenu);
        return this;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Instant getDateEnvoi() {
        return this.dateEnvoi;
    }

    public MessageChat dateEnvoi(Instant dateEnvoi) {
        this.setDateEnvoi(dateEnvoi);
        return this;
    }

    public void setDateEnvoi(Instant dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }

    public User getAuteur() {
        return this.auteur;
    }

    public void setAuteur(User user) {
        this.auteur = user;
    }

    public MessageChat auteur(User user) {
        this.setAuteur(user);
        return this;
    }

    public Demande getDemande() {
        return this.demande;
    }

    public void setDemande(Demande demande) {
        this.demande = demande;
    }

    public MessageChat demande(Demande demande) {
        this.setDemande(demande);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MessageChat)) {
            return false;
        }
        return getId() != null && getId().equals(((MessageChat) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MessageChat{" +
            "id=" + getId() +
            ", contenu='" + getContenu() + "'" +
            ", dateEnvoi='" + getDateEnvoi() + "'" +
            "}";
    }
}
