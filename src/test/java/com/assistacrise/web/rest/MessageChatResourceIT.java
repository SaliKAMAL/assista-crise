package com.assistacrise.web.rest;

import static com.assistacrise.domain.MessageChatAsserts.*;
import static com.assistacrise.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.assistacrise.IntegrationTest;
import com.assistacrise.domain.Demande;
import com.assistacrise.domain.MessageChat;
import com.assistacrise.domain.User;
import com.assistacrise.repository.MessageChatRepository;
import com.assistacrise.repository.UserRepository;
import com.assistacrise.service.MessageChatService;
import com.assistacrise.service.dto.MessageChatDTO;
import com.assistacrise.service.mapper.MessageChatMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link MessageChatResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class MessageChatResourceIT {

    private static final String DEFAULT_CONTENU = "AAAAAAAAAA";
    private static final String UPDATED_CONTENU = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_ENVOI = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_ENVOI = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/message-chats";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MessageChatRepository messageChatRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private MessageChatRepository messageChatRepositoryMock;

    @Autowired
    private MessageChatMapper messageChatMapper;

    @Mock
    private MessageChatService messageChatServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMessageChatMockMvc;

    private MessageChat messageChat;

    private MessageChat insertedMessageChat;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MessageChat createEntity(EntityManager em) {
        MessageChat messageChat = new MessageChat().contenu(DEFAULT_CONTENU).dateEnvoi(DEFAULT_DATE_ENVOI);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        messageChat.setAuteur(user);
        // Add required entity
        Demande demande;
        if (TestUtil.findAll(em, Demande.class).isEmpty()) {
            demande = DemandeResourceIT.createEntity(em);
            em.persist(demande);
            em.flush();
        } else {
            demande = TestUtil.findAll(em, Demande.class).get(0);
        }
        messageChat.setDemande(demande);
        return messageChat;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MessageChat createUpdatedEntity(EntityManager em) {
        MessageChat updatedMessageChat = new MessageChat().contenu(UPDATED_CONTENU).dateEnvoi(UPDATED_DATE_ENVOI);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedMessageChat.setAuteur(user);
        // Add required entity
        Demande demande;
        if (TestUtil.findAll(em, Demande.class).isEmpty()) {
            demande = DemandeResourceIT.createUpdatedEntity(em);
            em.persist(demande);
            em.flush();
        } else {
            demande = TestUtil.findAll(em, Demande.class).get(0);
        }
        updatedMessageChat.setDemande(demande);
        return updatedMessageChat;
    }

    @BeforeEach
    void initTest() {
        messageChat = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedMessageChat != null) {
            messageChatRepository.delete(insertedMessageChat);
            insertedMessageChat = null;
        }
    }

    @Test
    @Transactional
    void createMessageChat() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the MessageChat
        MessageChatDTO messageChatDTO = messageChatMapper.toDto(messageChat);
        var returnedMessageChatDTO = om.readValue(
            restMessageChatMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(messageChatDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MessageChatDTO.class
        );

        // Validate the MessageChat in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMessageChat = messageChatMapper.toEntity(returnedMessageChatDTO);
        assertMessageChatUpdatableFieldsEquals(returnedMessageChat, getPersistedMessageChat(returnedMessageChat));

        insertedMessageChat = returnedMessageChat;
    }

    @Test
    @Transactional
    void createMessageChatWithExistingId() throws Exception {
        // Create the MessageChat with an existing ID
        messageChat.setId(1L);
        MessageChatDTO messageChatDTO = messageChatMapper.toDto(messageChat);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMessageChatMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(messageChatDTO)))
            .andExpect(status().isBadRequest());

        // Validate the MessageChat in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkContenuIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        messageChat.setContenu(null);

        // Create the MessageChat, which fails.
        MessageChatDTO messageChatDTO = messageChatMapper.toDto(messageChat);

        restMessageChatMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(messageChatDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateEnvoiIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        messageChat.setDateEnvoi(null);

        // Create the MessageChat, which fails.
        MessageChatDTO messageChatDTO = messageChatMapper.toDto(messageChat);

        restMessageChatMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(messageChatDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMessageChats() throws Exception {
        // Initialize the database
        insertedMessageChat = messageChatRepository.saveAndFlush(messageChat);

        // Get all the messageChatList
        restMessageChatMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(messageChat.getId().intValue())))
            .andExpect(jsonPath("$.[*].contenu").value(hasItem(DEFAULT_CONTENU)))
            .andExpect(jsonPath("$.[*].dateEnvoi").value(hasItem(DEFAULT_DATE_ENVOI.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMessageChatsWithEagerRelationshipsIsEnabled() throws Exception {
        when(messageChatServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMessageChatMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(messageChatServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMessageChatsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(messageChatServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMessageChatMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(messageChatRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getMessageChat() throws Exception {
        // Initialize the database
        insertedMessageChat = messageChatRepository.saveAndFlush(messageChat);

        // Get the messageChat
        restMessageChatMockMvc
            .perform(get(ENTITY_API_URL_ID, messageChat.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(messageChat.getId().intValue()))
            .andExpect(jsonPath("$.contenu").value(DEFAULT_CONTENU))
            .andExpect(jsonPath("$.dateEnvoi").value(DEFAULT_DATE_ENVOI.toString()));
    }

    @Test
    @Transactional
    void getNonExistingMessageChat() throws Exception {
        // Get the messageChat
        restMessageChatMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMessageChat() throws Exception {
        // Initialize the database
        insertedMessageChat = messageChatRepository.saveAndFlush(messageChat);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the messageChat
        MessageChat updatedMessageChat = messageChatRepository.findById(messageChat.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMessageChat are not directly saved in db
        em.detach(updatedMessageChat);
        updatedMessageChat.contenu(UPDATED_CONTENU).dateEnvoi(UPDATED_DATE_ENVOI);
        MessageChatDTO messageChatDTO = messageChatMapper.toDto(updatedMessageChat);

        restMessageChatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, messageChatDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(messageChatDTO))
            )
            .andExpect(status().isOk());

        // Validate the MessageChat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMessageChatToMatchAllProperties(updatedMessageChat);
    }

    @Test
    @Transactional
    void putNonExistingMessageChat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        messageChat.setId(longCount.incrementAndGet());

        // Create the MessageChat
        MessageChatDTO messageChatDTO = messageChatMapper.toDto(messageChat);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMessageChatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, messageChatDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(messageChatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MessageChat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMessageChat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        messageChat.setId(longCount.incrementAndGet());

        // Create the MessageChat
        MessageChatDTO messageChatDTO = messageChatMapper.toDto(messageChat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMessageChatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(messageChatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MessageChat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMessageChat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        messageChat.setId(longCount.incrementAndGet());

        // Create the MessageChat
        MessageChatDTO messageChatDTO = messageChatMapper.toDto(messageChat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMessageChatMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(messageChatDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MessageChat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMessageChatWithPatch() throws Exception {
        // Initialize the database
        insertedMessageChat = messageChatRepository.saveAndFlush(messageChat);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the messageChat using partial update
        MessageChat partialUpdatedMessageChat = new MessageChat();
        partialUpdatedMessageChat.setId(messageChat.getId());

        partialUpdatedMessageChat.contenu(UPDATED_CONTENU).dateEnvoi(UPDATED_DATE_ENVOI);

        restMessageChatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMessageChat.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMessageChat))
            )
            .andExpect(status().isOk());

        // Validate the MessageChat in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMessageChatUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMessageChat, messageChat),
            getPersistedMessageChat(messageChat)
        );
    }

    @Test
    @Transactional
    void fullUpdateMessageChatWithPatch() throws Exception {
        // Initialize the database
        insertedMessageChat = messageChatRepository.saveAndFlush(messageChat);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the messageChat using partial update
        MessageChat partialUpdatedMessageChat = new MessageChat();
        partialUpdatedMessageChat.setId(messageChat.getId());

        partialUpdatedMessageChat.contenu(UPDATED_CONTENU).dateEnvoi(UPDATED_DATE_ENVOI);

        restMessageChatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMessageChat.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMessageChat))
            )
            .andExpect(status().isOk());

        // Validate the MessageChat in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMessageChatUpdatableFieldsEquals(partialUpdatedMessageChat, getPersistedMessageChat(partialUpdatedMessageChat));
    }

    @Test
    @Transactional
    void patchNonExistingMessageChat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        messageChat.setId(longCount.incrementAndGet());

        // Create the MessageChat
        MessageChatDTO messageChatDTO = messageChatMapper.toDto(messageChat);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMessageChatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, messageChatDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(messageChatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MessageChat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMessageChat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        messageChat.setId(longCount.incrementAndGet());

        // Create the MessageChat
        MessageChatDTO messageChatDTO = messageChatMapper.toDto(messageChat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMessageChatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(messageChatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MessageChat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMessageChat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        messageChat.setId(longCount.incrementAndGet());

        // Create the MessageChat
        MessageChatDTO messageChatDTO = messageChatMapper.toDto(messageChat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMessageChatMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(messageChatDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MessageChat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMessageChat() throws Exception {
        // Initialize the database
        insertedMessageChat = messageChatRepository.saveAndFlush(messageChat);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the messageChat
        restMessageChatMockMvc
            .perform(delete(ENTITY_API_URL_ID, messageChat.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return messageChatRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected MessageChat getPersistedMessageChat(MessageChat messageChat) {
        return messageChatRepository.findById(messageChat.getId()).orElseThrow();
    }

    protected void assertPersistedMessageChatToMatchAllProperties(MessageChat expectedMessageChat) {
        assertMessageChatAllPropertiesEquals(expectedMessageChat, getPersistedMessageChat(expectedMessageChat));
    }

    protected void assertPersistedMessageChatToMatchUpdatableProperties(MessageChat expectedMessageChat) {
        assertMessageChatAllUpdatablePropertiesEquals(expectedMessageChat, getPersistedMessageChat(expectedMessageChat));
    }
}
