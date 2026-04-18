package com.assistacrise.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.assistacrise.domain.MessageChat} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MessageChatDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 1, max = 1000)
    private String contenu;

    @NotNull
    private Instant dateEnvoi;

    @NotNull
    private UserDTO auteur;

    @NotNull
    private DemandeDTO demande;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Instant getDateEnvoi() {
        return dateEnvoi;
    }

    public void setDateEnvoi(Instant dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }

    public UserDTO getAuteur() {
        return auteur;
    }

    public void setAuteur(UserDTO auteur) {
        this.auteur = auteur;
    }

    public DemandeDTO getDemande() {
        return demande;
    }

    public void setDemande(DemandeDTO demande) {
        this.demande = demande;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MessageChatDTO)) {
            return false;
        }

        MessageChatDTO messageChatDTO = (MessageChatDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, messageChatDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MessageChatDTO{" +
            "id=" + getId() +
            ", contenu='" + getContenu() + "'" +
            ", dateEnvoi='" + getDateEnvoi() + "'" +
            ", auteur=" + getAuteur() +
            ", demande=" + getDemande() +
            "}";
    }
}
