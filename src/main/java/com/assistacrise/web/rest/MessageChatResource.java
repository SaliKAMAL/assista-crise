package com.assistacrise.web.rest;

import com.assistacrise.repository.MessageChatRepository;
import com.assistacrise.service.MessageChatService;
import com.assistacrise.service.dto.MessageChatDTO;
import com.assistacrise.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.assistacrise.domain.MessageChat}.
 */
@RestController
@RequestMapping("/api/message-chats")
public class MessageChatResource {

    private static final Logger LOG = LoggerFactory.getLogger(MessageChatResource.class);

    private static final String ENTITY_NAME = "messageChat";

    @Value("${jhipster.clientApp.name:assistaCrise}")
    private String applicationName;

    private final MessageChatService messageChatService;

    private final MessageChatRepository messageChatRepository;

    public MessageChatResource(MessageChatService messageChatService, MessageChatRepository messageChatRepository) {
        this.messageChatService = messageChatService;
        this.messageChatRepository = messageChatRepository;
    }

    /**
     * {@code POST  /message-chats} : Create a new messageChat.
     *
     * @param messageChatDTO the messageChatDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new messageChatDTO, or with status {@code 400 (Bad Request)} if the messageChat has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MessageChatDTO> createMessageChat(@Valid @RequestBody MessageChatDTO messageChatDTO) throws URISyntaxException {
        LOG.debug("REST request to save MessageChat : {}", messageChatDTO);
        if (messageChatDTO.getId() != null) {
            throw new BadRequestAlertException("A new messageChat cannot already have an ID", ENTITY_NAME, "idexists");
        }
        messageChatDTO = messageChatService.save(messageChatDTO);
        return ResponseEntity.created(new URI("/api/message-chats/" + messageChatDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, messageChatDTO.getId().toString()))
            .body(messageChatDTO);
    }

    /**
     * {@code PUT  /message-chats/:id} : Updates an existing messageChat.
     *
     * @param id the id of the messageChatDTO to save.
     * @param messageChatDTO the messageChatDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated messageChatDTO,
     * or with status {@code 400 (Bad Request)} if the messageChatDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the messageChatDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MessageChatDTO> updateMessageChat(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MessageChatDTO messageChatDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MessageChat : {}, {}", id, messageChatDTO);
        if (messageChatDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, messageChatDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!messageChatRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        messageChatDTO = messageChatService.update(messageChatDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, messageChatDTO.getId().toString()))
            .body(messageChatDTO);
    }

    /**
     * {@code PATCH  /message-chats/:id} : Partial updates given fields of an existing messageChat, field will ignore if it is null
     *
     * @param id the id of the messageChatDTO to save.
     * @param messageChatDTO the messageChatDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated messageChatDTO,
     * or with status {@code 400 (Bad Request)} if the messageChatDTO is not valid,
     * or with status {@code 404 (Not Found)} if the messageChatDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the messageChatDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MessageChatDTO> partialUpdateMessageChat(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MessageChatDTO messageChatDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MessageChat partially : {}, {}", id, messageChatDTO);
        if (messageChatDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, messageChatDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!messageChatRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MessageChatDTO> result = messageChatService.partialUpdate(messageChatDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, messageChatDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /message-chats} : get all the Message Chats.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Message Chats in body.
     */
    @GetMapping("")
    public ResponseEntity<List<MessageChatDTO>> getAllMessageChats(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of MessageChats");
        Page<MessageChatDTO> page;
        if (eagerload) {
            page = messageChatService.findAllWithEagerRelationships(pageable);
        } else {
            page = messageChatService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /message-chats/:id} : get the "id" messageChat.
     *
     * @param id the id of the messageChatDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the messageChatDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MessageChatDTO> getMessageChat(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MessageChat : {}", id);
        Optional<MessageChatDTO> messageChatDTO = messageChatService.findOne(id);
        return ResponseUtil.wrapOrNotFound(messageChatDTO);
    }

    /**
     * {@code DELETE  /message-chats/:id} : delete the "id" messageChat.
     *
     * @param id the id of the messageChatDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessageChat(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MessageChat : {}", id);
        messageChatService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
