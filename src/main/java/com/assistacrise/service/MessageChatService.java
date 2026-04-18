package com.assistacrise.service;

import com.assistacrise.service.dto.MessageChatDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.assistacrise.domain.MessageChat}.
 */
public interface MessageChatService {
    /**
     * Save a messageChat.
     *
     * @param messageChatDTO the entity to save.
     * @return the persisted entity.
     */
    MessageChatDTO save(MessageChatDTO messageChatDTO);

    /**
     * Updates a messageChat.
     *
     * @param messageChatDTO the entity to update.
     * @return the persisted entity.
     */
    MessageChatDTO update(MessageChatDTO messageChatDTO);

    /**
     * Partially updates a messageChat.
     *
     * @param messageChatDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<MessageChatDTO> partialUpdate(MessageChatDTO messageChatDTO);

    /**
     * Get all the messageChats.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<MessageChatDTO> findAll(Pageable pageable);

    /**
     * Get all the messageChats with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<MessageChatDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" messageChat.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<MessageChatDTO> findOne(Long id);

    /**
     * Delete the "id" messageChat.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
