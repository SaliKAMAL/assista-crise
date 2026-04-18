package com.assistacrise.web.rest;

import static com.assistacrise.domain.InformationAsserts.*;
import static com.assistacrise.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.assistacrise.IntegrationTest;
import com.assistacrise.domain.Crise;
import com.assistacrise.domain.Information;
import com.assistacrise.domain.User;
import com.assistacrise.repository.InformationRepository;
import com.assistacrise.repository.UserRepository;
import com.assistacrise.service.InformationService;
import com.assistacrise.service.dto.InformationDTO;
import com.assistacrise.service.mapper.InformationMapper;
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
 * Integration tests for the {@link InformationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class InformationResourceIT {

    private static final String DEFAULT_TITRE = "AAAAAAAAAA";
    private static final String UPDATED_TITRE = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENU = "AAAAAAAAAA";
    private static final String UPDATED_CONTENU = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_PUBLICATION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_PUBLICATION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Double DEFAULT_LATITUDE = -90D;
    private static final Double UPDATED_LATITUDE = -89D;
    private static final Double SMALLER_LATITUDE = -90D - 1D;

    private static final Double DEFAULT_LONGITUDE = -180D;
    private static final Double UPDATED_LONGITUDE = -179D;
    private static final Double SMALLER_LONGITUDE = -180D - 1D;

    private static final String ENTITY_API_URL = "/api/information";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InformationRepository informationRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private InformationRepository informationRepositoryMock;

    @Autowired
    private InformationMapper informationMapper;

    @Mock
    private InformationService informationServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInformationMockMvc;

    private Information information;

    private Information insertedInformation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Information createEntity(EntityManager em) {
        Information information = new Information()
            .titre(DEFAULT_TITRE)
            .contenu(DEFAULT_CONTENU)
            .datePublication(DEFAULT_DATE_PUBLICATION)
            .latitude(DEFAULT_LATITUDE)
            .longitude(DEFAULT_LONGITUDE);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        information.setAuteur(user);
        // Add required entity
        Crise crise;
        if (TestUtil.findAll(em, Crise.class).isEmpty()) {
            crise = CriseResourceIT.createEntity(em);
            em.persist(crise);
            em.flush();
        } else {
            crise = TestUtil.findAll(em, Crise.class).get(0);
        }
        information.setCrise(crise);
        return information;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Information createUpdatedEntity(EntityManager em) {
        Information updatedInformation = new Information()
            .titre(UPDATED_TITRE)
            .contenu(UPDATED_CONTENU)
            .datePublication(UPDATED_DATE_PUBLICATION)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedInformation.setAuteur(user);
        // Add required entity
        Crise crise;
        if (TestUtil.findAll(em, Crise.class).isEmpty()) {
            crise = CriseResourceIT.createUpdatedEntity(em);
            em.persist(crise);
            em.flush();
        } else {
            crise = TestUtil.findAll(em, Crise.class).get(0);
        }
        updatedInformation.setCrise(crise);
        return updatedInformation;
    }

    @BeforeEach
    void initTest() {
        information = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedInformation != null) {
            informationRepository.delete(insertedInformation);
            insertedInformation = null;
        }
    }

    @Test
    @Transactional
    void createInformation() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Information
        InformationDTO informationDTO = informationMapper.toDto(information);
        var returnedInformationDTO = om.readValue(
            restInformationMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(informationDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            InformationDTO.class
        );

        // Validate the Information in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedInformation = informationMapper.toEntity(returnedInformationDTO);
        assertInformationUpdatableFieldsEquals(returnedInformation, getPersistedInformation(returnedInformation));

        insertedInformation = returnedInformation;
    }

    @Test
    @Transactional
    void createInformationWithExistingId() throws Exception {
        // Create the Information with an existing ID
        information.setId(1L);
        InformationDTO informationDTO = informationMapper.toDto(information);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInformationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(informationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Information in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        information.setTitre(null);

        // Create the Information, which fails.
        InformationDTO informationDTO = informationMapper.toDto(information);

        restInformationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(informationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkContenuIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        information.setContenu(null);

        // Create the Information, which fails.
        InformationDTO informationDTO = informationMapper.toDto(information);

        restInformationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(informationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDatePublicationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        information.setDatePublication(null);

        // Create the Information, which fails.
        InformationDTO informationDTO = informationMapper.toDto(information);

        restInformationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(informationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllInformations() throws Exception {
        // Initialize the database
        insertedInformation = informationRepository.saveAndFlush(information);

        // Get all the informationList
        restInformationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(information.getId().intValue())))
            .andExpect(jsonPath("$.[*].titre").value(hasItem(DEFAULT_TITRE)))
            .andExpect(jsonPath("$.[*].contenu").value(hasItem(DEFAULT_CONTENU)))
            .andExpect(jsonPath("$.[*].datePublication").value(hasItem(DEFAULT_DATE_PUBLICATION.toString())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInformationsWithEagerRelationshipsIsEnabled() throws Exception {
        when(informationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInformationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(informationServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInformationsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(informationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInformationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(informationRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getInformation() throws Exception {
        // Initialize the database
        insertedInformation = informationRepository.saveAndFlush(information);

        // Get the information
        restInformationMockMvc
            .perform(get(ENTITY_API_URL_ID, information.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(information.getId().intValue()))
            .andExpect(jsonPath("$.titre").value(DEFAULT_TITRE))
            .andExpect(jsonPath("$.contenu").value(DEFAULT_CONTENU))
            .andExpect(jsonPath("$.datePublication").value(DEFAULT_DATE_PUBLICATION.toString()))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE));
    }

    @Test
    @Transactional
    void getInformationsByIdFiltering() throws Exception {
        // Initialize the database
        insertedInformation = informationRepository.saveAndFlush(information);

        Long id = information.getId();

        defaultInformationFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultInformationFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultInformationFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllInformationsByTitreIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInformation = informationRepository.saveAndFlush(information);

        // Get all the informationList where titre equals to
        defaultInformationFiltering("titre.equals=" + DEFAULT_TITRE, "titre.equals=" + UPDATED_TITRE);
    }

    @Test
    @Transactional
    void getAllInformationsByTitreIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInformation = informationRepository.saveAndFlush(information);

        // Get all the informationList where titre in
        defaultInformationFiltering("titre.in=" + DEFAULT_TITRE + "," + UPDATED_TITRE, "titre.in=" + UPDATED_TITRE);
    }

    @Test
    @Transactional
    void getAllInformationsByTitreIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInformation = informationRepository.saveAndFlush(information);

        // Get all the informationList where titre is not null
        defaultInformationFiltering("titre.specified=true", "titre.specified=false");
    }

    @Test
    @Transactional
    void getAllInformationsByTitreContainsSomething() throws Exception {
        // Initialize the database
        insertedInformation = informationRepository.saveAndFlush(information);

        // Get all the informationList where titre contains
        defaultInformationFiltering("titre.contains=" + DEFAULT_TITRE, "titre.contains=" + UPDATED_TITRE);
    }

    @Test
    @Transactional
    void getAllInformationsByTitreNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInformation = informationRepository.saveAndFlush(information);

        // Get all the informationList where titre does not contain
        defaultInformationFiltering("titre.doesNotContain=" + UPDATED_TITRE, "titre.doesNotContain=" + DEFAULT_TITRE);
    }

    @Test
    @Transactional
    void getAllInformationsByContenuIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInformation = informationRepository.saveAndFlush(information);

        // Get all the informationList where contenu equals to
        defaultInformationFiltering("contenu.equals=" + DEFAULT_CONTENU, "contenu.equals=" + UPDATED_CONTENU);
    }

    @Test
    @Transactional
    void getAllInformationsByContenuIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInformation = informationRepository.saveAndFlush(information);

        // Get all the informationList where contenu in
        defaultInformationFiltering("contenu.in=" + DEFAULT_CONTENU + "," + UPDATED_CONTENU, "contenu.in=" + UPDATED_CONTENU);
    }

    @Test
    @Transactional
    void getAllInformationsByContenuIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInformation = informationRepository.saveAndFlush(information);

        // Get all the informationList where contenu is not null
        defaultInformationFiltering("contenu.specified=true", "contenu.specified=false");
    }

    @Test
    @Transactional
    void getAllInformationsByContenuContainsSomething() throws Exception {
        // Initialize the database
        insertedInformation = informationRepository.saveAndFlush(information);

        // Get all the informationList where contenu contains
        defaultInformationFiltering("contenu.contains=" + DEFAULT_CONTENU, "contenu.contains=" + UPDATED_CONTENU);
    }

    @Test
    @Transactional
    void getAllInformationsByContenuNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInformation = informationRepository.saveAndFlush(information);

        // Get all the informationList where contenu does not contain
        defaultInformationFiltering("contenu.doesNotContain=" + UPDATED_CONTENU, "contenu.doesNotContain=" + DEFAULT_CONTENU);
    }

    @Test
    @Transactional
    void getAllInformationsByDatePublicationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInformation = informationRepository.saveAndFlush(information);

        // Get all the informationList where datePublication equals to
        defaultInformationFiltering(
            "datePublication.equals=" + DEFAULT_DATE_PUBLICATION,
            "datePublication.equals=" + UPDATED_DATE_PUBLICATION
        );
    }

    @Test
    @Transactional
    void getAllInformationsByDatePublicationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInformation = informationRepository.saveAndFlush(information);

        // Get all the informationList where datePublication in
        defaultInformationFiltering(
            "datePublication.in=" + DEFAULT_DATE_PUBLICATION + "," + UPDATED_DATE_PUBLICATION,
            "datePublication.in=" + UPDATED_DATE_PUBLICATION
        );
    }

    @Test
    @Transactional
    void getAllInformationsByDatePublicationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInformation = informationRepository.saveAndFlush(information);

        // Get all the informationList where datePublication is not null
        defaultInformationFiltering("datePublication.specified=true", "datePublication.specified=false");
    }

    @Test
    @Transactional
    void getAllInformationsByLatitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInformation = informationRepository.saveAndFlush(information);

        // Get all the informationList where latitude equals to
        defaultInformationFiltering("latitude.equals=" + DEFAULT_LATITUDE, "latitude.equals=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllInformationsByLatitudeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInformation = informationRepository.saveAndFlush(information);

        // Get all the informationList where latitude in
        defaultInformationFiltering("latitude.in=" + DEFAULT_LATITUDE + "," + UPDATED_LATITUDE, "latitude.in=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllInformationsByLatitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInformation = informationRepository.saveAndFlush(information);

        // Get all the informationList where latitude is not null
        defaultInformationFiltering("latitude.specified=true", "latitude.specified=false");
    }

    @Test
    @Transactional
    void getAllInformationsByLatitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInformation = informationRepository.saveAndFlush(information);

        // Get all the informationList where latitude is greater than or equal to
        defaultInformationFiltering(
            "latitude.greaterThanOrEqual=" + DEFAULT_LATITUDE,
            "latitude.greaterThanOrEqual=" + (DEFAULT_LATITUDE + 1)
        );
    }

    @Test
    @Transactional
    void getAllInformationsByLatitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInformation = informationRepository.saveAndFlush(information);

        // Get all the informationList where latitude is less than or equal to
        defaultInformationFiltering("latitude.lessThanOrEqual=" + DEFAULT_LATITUDE, "latitude.lessThanOrEqual=" + SMALLER_LATITUDE);
    }

    @Test
    @Transactional
    void getAllInformationsByLatitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedInformation = informationRepository.saveAndFlush(information);

        // Get all the informationList where latitude is less than
        defaultInformationFiltering("latitude.lessThan=" + (DEFAULT_LATITUDE + 1), "latitude.lessThan=" + DEFAULT_LATITUDE);
    }

    @Test
    @Transactional
    void getAllInformationsByLatitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedInformation = informationRepository.saveAndFlush(information);

        // Get all the informationList where latitude is greater than
        defaultInformationFiltering("latitude.greaterThan=" + SMALLER_LATITUDE, "latitude.greaterThan=" + DEFAULT_LATITUDE);
    }

    @Test
    @Transactional
    void getAllInformationsByLongitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInformation = informationRepository.saveAndFlush(information);

        // Get all the informationList where longitude equals to
        defaultInformationFiltering("longitude.equals=" + DEFAULT_LONGITUDE, "longitude.equals=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllInformationsByLongitudeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInformation = informationRepository.saveAndFlush(information);

        // Get all the informationList where longitude in
        defaultInformationFiltering("longitude.in=" + DEFAULT_LONGITUDE + "," + UPDATED_LONGITUDE, "longitude.in=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllInformationsByLongitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInformation = informationRepository.saveAndFlush(information);

        // Get all the informationList where longitude is not null
        defaultInformationFiltering("longitude.specified=true", "longitude.specified=false");
    }

    @Test
    @Transactional
    void getAllInformationsByLongitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInformation = informationRepository.saveAndFlush(information);

        // Get all the informationList where longitude is greater than or equal to
        defaultInformationFiltering(
            "longitude.greaterThanOrEqual=" + DEFAULT_LONGITUDE,
            "longitude.greaterThanOrEqual=" + (DEFAULT_LONGITUDE + 1)
        );
    }

    @Test
    @Transactional
    void getAllInformationsByLongitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedInformation = informationRepository.saveAndFlush(information);

        // Get all the informationList where longitude is less than or equal to
        defaultInformationFiltering("longitude.lessThanOrEqual=" + DEFAULT_LONGITUDE, "longitude.lessThanOrEqual=" + SMALLER_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllInformationsByLongitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedInformation = informationRepository.saveAndFlush(information);

        // Get all the informationList where longitude is less than
        defaultInformationFiltering("longitude.lessThan=" + (DEFAULT_LONGITUDE + 1), "longitude.lessThan=" + DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllInformationsByLongitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedInformation = informationRepository.saveAndFlush(information);

        // Get all the informationList where longitude is greater than
        defaultInformationFiltering("longitude.greaterThan=" + SMALLER_LONGITUDE, "longitude.greaterThan=" + DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllInformationsByAuteurIsEqualToSomething() throws Exception {
        User auteur;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            informationRepository.saveAndFlush(information);
            auteur = UserResourceIT.createEntity();
        } else {
            auteur = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(auteur);
        em.flush();
        information.setAuteur(auteur);
        informationRepository.saveAndFlush(information);
        Long auteurId = auteur.getId();
        // Get all the informationList where auteur equals to auteurId
        defaultInformationShouldBeFound("auteurId.equals=" + auteurId);

        // Get all the informationList where auteur equals to (auteurId + 1)
        defaultInformationShouldNotBeFound("auteurId.equals=" + (auteurId + 1));
    }

    @Test
    @Transactional
    void getAllInformationsByCriseIsEqualToSomething() throws Exception {
        Crise crise;
        if (TestUtil.findAll(em, Crise.class).isEmpty()) {
            informationRepository.saveAndFlush(information);
            crise = CriseResourceIT.createEntity(em);
        } else {
            crise = TestUtil.findAll(em, Crise.class).get(0);
        }
        em.persist(crise);
        em.flush();
        information.setCrise(crise);
        informationRepository.saveAndFlush(information);
        Long criseId = crise.getId();
        // Get all the informationList where crise equals to criseId
        defaultInformationShouldBeFound("criseId.equals=" + criseId);

        // Get all the informationList where crise equals to (criseId + 1)
        defaultInformationShouldNotBeFound("criseId.equals=" + (criseId + 1));
    }

    private void defaultInformationFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultInformationShouldBeFound(shouldBeFound);
        defaultInformationShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultInformationShouldBeFound(String filter) throws Exception {
        restInformationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(information.getId().intValue())))
            .andExpect(jsonPath("$.[*].titre").value(hasItem(DEFAULT_TITRE)))
            .andExpect(jsonPath("$.[*].contenu").value(hasItem(DEFAULT_CONTENU)))
            .andExpect(jsonPath("$.[*].datePublication").value(hasItem(DEFAULT_DATE_PUBLICATION.toString())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE)));

        // Check, that the count call also returns 1
        restInformationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultInformationShouldNotBeFound(String filter) throws Exception {
        restInformationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restInformationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingInformation() throws Exception {
        // Get the information
        restInformationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInformation() throws Exception {
        // Initialize the database
        insertedInformation = informationRepository.saveAndFlush(information);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the information
        Information updatedInformation = informationRepository.findById(information.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedInformation are not directly saved in db
        em.detach(updatedInformation);
        updatedInformation
            .titre(UPDATED_TITRE)
            .contenu(UPDATED_CONTENU)
            .datePublication(UPDATED_DATE_PUBLICATION)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE);
        InformationDTO informationDTO = informationMapper.toDto(updatedInformation);

        restInformationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, informationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(informationDTO))
            )
            .andExpect(status().isOk());

        // Validate the Information in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInformationToMatchAllProperties(updatedInformation);
    }

    @Test
    @Transactional
    void putNonExistingInformation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        information.setId(longCount.incrementAndGet());

        // Create the Information
        InformationDTO informationDTO = informationMapper.toDto(information);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInformationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, informationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(informationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Information in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInformation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        information.setId(longCount.incrementAndGet());

        // Create the Information
        InformationDTO informationDTO = informationMapper.toDto(information);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInformationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(informationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Information in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInformation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        information.setId(longCount.incrementAndGet());

        // Create the Information
        InformationDTO informationDTO = informationMapper.toDto(information);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInformationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(informationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Information in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInformationWithPatch() throws Exception {
        // Initialize the database
        insertedInformation = informationRepository.saveAndFlush(information);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the information using partial update
        Information partialUpdatedInformation = new Information();
        partialUpdatedInformation.setId(information.getId());

        partialUpdatedInformation.latitude(UPDATED_LATITUDE);

        restInformationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInformation.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInformation))
            )
            .andExpect(status().isOk());

        // Validate the Information in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInformationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedInformation, information),
            getPersistedInformation(information)
        );
    }

    @Test
    @Transactional
    void fullUpdateInformationWithPatch() throws Exception {
        // Initialize the database
        insertedInformation = informationRepository.saveAndFlush(information);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the information using partial update
        Information partialUpdatedInformation = new Information();
        partialUpdatedInformation.setId(information.getId());

        partialUpdatedInformation
            .titre(UPDATED_TITRE)
            .contenu(UPDATED_CONTENU)
            .datePublication(UPDATED_DATE_PUBLICATION)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE);

        restInformationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInformation.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInformation))
            )
            .andExpect(status().isOk());

        // Validate the Information in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInformationUpdatableFieldsEquals(partialUpdatedInformation, getPersistedInformation(partialUpdatedInformation));
    }

    @Test
    @Transactional
    void patchNonExistingInformation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        information.setId(longCount.incrementAndGet());

        // Create the Information
        InformationDTO informationDTO = informationMapper.toDto(information);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInformationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, informationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(informationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Information in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInformation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        information.setId(longCount.incrementAndGet());

        // Create the Information
        InformationDTO informationDTO = informationMapper.toDto(information);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInformationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(informationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Information in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInformation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        information.setId(longCount.incrementAndGet());

        // Create the Information
        InformationDTO informationDTO = informationMapper.toDto(information);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInformationMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(informationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Information in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInformation() throws Exception {
        // Initialize the database
        insertedInformation = informationRepository.saveAndFlush(information);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the information
        restInformationMockMvc
            .perform(delete(ENTITY_API_URL_ID, information.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return informationRepository.count();
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

    protected Information getPersistedInformation(Information information) {
        return informationRepository.findById(information.getId()).orElseThrow();
    }

    protected void assertPersistedInformationToMatchAllProperties(Information expectedInformation) {
        assertInformationAllPropertiesEquals(expectedInformation, getPersistedInformation(expectedInformation));
    }

    protected void assertPersistedInformationToMatchUpdatableProperties(Information expectedInformation) {
        assertInformationAllUpdatablePropertiesEquals(expectedInformation, getPersistedInformation(expectedInformation));
    }
}
