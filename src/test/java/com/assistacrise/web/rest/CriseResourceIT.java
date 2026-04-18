package com.assistacrise.web.rest;

import static com.assistacrise.domain.CriseAsserts.*;
import static com.assistacrise.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.assistacrise.IntegrationTest;
import com.assistacrise.domain.Crise;
import com.assistacrise.domain.User;
import com.assistacrise.domain.enumeration.StatutCrise;
import com.assistacrise.domain.enumeration.TypeCrise;
import com.assistacrise.repository.CriseRepository;
import com.assistacrise.repository.UserRepository;
import com.assistacrise.service.CriseService;
import com.assistacrise.service.dto.CriseDTO;
import com.assistacrise.service.mapper.CriseMapper;
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
 * Integration tests for the {@link CriseResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CriseResourceIT {

    private static final String DEFAULT_TITRE = "AAAAAAAAAA";
    private static final String UPDATED_TITRE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final TypeCrise DEFAULT_TYPE = TypeCrise.FEU_FORET;
    private static final TypeCrise UPDATED_TYPE = TypeCrise.INONDATION;

    private static final Instant DEFAULT_DATE_DEBUT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_DEBUT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_DATE_FERMETURE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_FERMETURE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final StatutCrise DEFAULT_STATUT = StatutCrise.ACTIVE;
    private static final StatutCrise UPDATED_STATUT = StatutCrise.FERMEE;

    private static final Double DEFAULT_LATITUDE = -90D;
    private static final Double UPDATED_LATITUDE = -89D;
    private static final Double SMALLER_LATITUDE = -90D - 1D;

    private static final Double DEFAULT_LONGITUDE = -180D;
    private static final Double UPDATED_LONGITUDE = -179D;
    private static final Double SMALLER_LONGITUDE = -180D - 1D;

    private static final String ENTITY_API_URL = "/api/crises";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CriseRepository criseRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private CriseRepository criseRepositoryMock;

    @Autowired
    private CriseMapper criseMapper;

    @Mock
    private CriseService criseServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCriseMockMvc;

    private Crise crise;

    private Crise insertedCrise;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Crise createEntity(EntityManager em) {
        Crise crise = new Crise()
            .titre(DEFAULT_TITRE)
            .description(DEFAULT_DESCRIPTION)
            .type(DEFAULT_TYPE)
            .dateDebut(DEFAULT_DATE_DEBUT)
            .dateFermeture(DEFAULT_DATE_FERMETURE)
            .statut(DEFAULT_STATUT)
            .latitude(DEFAULT_LATITUDE)
            .longitude(DEFAULT_LONGITUDE);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        crise.setDeclarant(user);
        return crise;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Crise createUpdatedEntity(EntityManager em) {
        Crise updatedCrise = new Crise()
            .titre(UPDATED_TITRE)
            .description(UPDATED_DESCRIPTION)
            .type(UPDATED_TYPE)
            .dateDebut(UPDATED_DATE_DEBUT)
            .dateFermeture(UPDATED_DATE_FERMETURE)
            .statut(UPDATED_STATUT)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedCrise.setDeclarant(user);
        return updatedCrise;
    }

    @BeforeEach
    void initTest() {
        crise = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedCrise != null) {
            criseRepository.delete(insertedCrise);
            insertedCrise = null;
        }
    }

    @Test
    @Transactional
    void createCrise() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Crise
        CriseDTO criseDTO = criseMapper.toDto(crise);
        var returnedCriseDTO = om.readValue(
            restCriseMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(criseDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CriseDTO.class
        );

        // Validate the Crise in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCrise = criseMapper.toEntity(returnedCriseDTO);
        assertCriseUpdatableFieldsEquals(returnedCrise, getPersistedCrise(returnedCrise));

        insertedCrise = returnedCrise;
    }

    @Test
    @Transactional
    void createCriseWithExistingId() throws Exception {
        // Create the Crise with an existing ID
        crise.setId(1L);
        CriseDTO criseDTO = criseMapper.toDto(crise);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCriseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(criseDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Crise in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        crise.setTitre(null);

        // Create the Crise, which fails.
        CriseDTO criseDTO = criseMapper.toDto(crise);

        restCriseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(criseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        crise.setType(null);

        // Create the Crise, which fails.
        CriseDTO criseDTO = criseMapper.toDto(crise);

        restCriseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(criseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateDebutIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        crise.setDateDebut(null);

        // Create the Crise, which fails.
        CriseDTO criseDTO = criseMapper.toDto(crise);

        restCriseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(criseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatutIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        crise.setStatut(null);

        // Create the Crise, which fails.
        CriseDTO criseDTO = criseMapper.toDto(crise);

        restCriseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(criseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLatitudeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        crise.setLatitude(null);

        // Create the Crise, which fails.
        CriseDTO criseDTO = criseMapper.toDto(crise);

        restCriseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(criseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLongitudeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        crise.setLongitude(null);

        // Create the Crise, which fails.
        CriseDTO criseDTO = criseMapper.toDto(crise);

        restCriseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(criseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCrises() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList
        restCriseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(crise.getId().intValue())))
            .andExpect(jsonPath("$.[*].titre").value(hasItem(DEFAULT_TITRE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].dateDebut").value(hasItem(DEFAULT_DATE_DEBUT.toString())))
            .andExpect(jsonPath("$.[*].dateFermeture").value(hasItem(DEFAULT_DATE_FERMETURE.toString())))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCrisesWithEagerRelationshipsIsEnabled() throws Exception {
        when(criseServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCriseMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(criseServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCrisesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(criseServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCriseMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(criseRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCrise() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get the crise
        restCriseMockMvc
            .perform(get(ENTITY_API_URL_ID, crise.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(crise.getId().intValue()))
            .andExpect(jsonPath("$.titre").value(DEFAULT_TITRE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.dateDebut").value(DEFAULT_DATE_DEBUT.toString()))
            .andExpect(jsonPath("$.dateFermeture").value(DEFAULT_DATE_FERMETURE.toString()))
            .andExpect(jsonPath("$.statut").value(DEFAULT_STATUT.toString()))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE));
    }

    @Test
    @Transactional
    void getCrisesByIdFiltering() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        Long id = crise.getId();

        defaultCriseFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCriseFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCriseFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCrisesByTitreIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList where titre equals to
        defaultCriseFiltering("titre.equals=" + DEFAULT_TITRE, "titre.equals=" + UPDATED_TITRE);
    }

    @Test
    @Transactional
    void getAllCrisesByTitreIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList where titre in
        defaultCriseFiltering("titre.in=" + DEFAULT_TITRE + "," + UPDATED_TITRE, "titre.in=" + UPDATED_TITRE);
    }

    @Test
    @Transactional
    void getAllCrisesByTitreIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList where titre is not null
        defaultCriseFiltering("titre.specified=true", "titre.specified=false");
    }

    @Test
    @Transactional
    void getAllCrisesByTitreContainsSomething() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList where titre contains
        defaultCriseFiltering("titre.contains=" + DEFAULT_TITRE, "titre.contains=" + UPDATED_TITRE);
    }

    @Test
    @Transactional
    void getAllCrisesByTitreNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList where titre does not contain
        defaultCriseFiltering("titre.doesNotContain=" + UPDATED_TITRE, "titre.doesNotContain=" + DEFAULT_TITRE);
    }

    @Test
    @Transactional
    void getAllCrisesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList where description equals to
        defaultCriseFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllCrisesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList where description in
        defaultCriseFiltering("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION, "description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllCrisesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList where description is not null
        defaultCriseFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllCrisesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList where description contains
        defaultCriseFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllCrisesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList where description does not contain
        defaultCriseFiltering("description.doesNotContain=" + UPDATED_DESCRIPTION, "description.doesNotContain=" + DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllCrisesByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList where type equals to
        defaultCriseFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllCrisesByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList where type in
        defaultCriseFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllCrisesByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList where type is not null
        defaultCriseFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    @Transactional
    void getAllCrisesByDateDebutIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList where dateDebut equals to
        defaultCriseFiltering("dateDebut.equals=" + DEFAULT_DATE_DEBUT, "dateDebut.equals=" + UPDATED_DATE_DEBUT);
    }

    @Test
    @Transactional
    void getAllCrisesByDateDebutIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList where dateDebut in
        defaultCriseFiltering("dateDebut.in=" + DEFAULT_DATE_DEBUT + "," + UPDATED_DATE_DEBUT, "dateDebut.in=" + UPDATED_DATE_DEBUT);
    }

    @Test
    @Transactional
    void getAllCrisesByDateDebutIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList where dateDebut is not null
        defaultCriseFiltering("dateDebut.specified=true", "dateDebut.specified=false");
    }

    @Test
    @Transactional
    void getAllCrisesByDateFermetureIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList where dateFermeture equals to
        defaultCriseFiltering("dateFermeture.equals=" + DEFAULT_DATE_FERMETURE, "dateFermeture.equals=" + UPDATED_DATE_FERMETURE);
    }

    @Test
    @Transactional
    void getAllCrisesByDateFermetureIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList where dateFermeture in
        defaultCriseFiltering(
            "dateFermeture.in=" + DEFAULT_DATE_FERMETURE + "," + UPDATED_DATE_FERMETURE,
            "dateFermeture.in=" + UPDATED_DATE_FERMETURE
        );
    }

    @Test
    @Transactional
    void getAllCrisesByDateFermetureIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList where dateFermeture is not null
        defaultCriseFiltering("dateFermeture.specified=true", "dateFermeture.specified=false");
    }

    @Test
    @Transactional
    void getAllCrisesByStatutIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList where statut equals to
        defaultCriseFiltering("statut.equals=" + DEFAULT_STATUT, "statut.equals=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllCrisesByStatutIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList where statut in
        defaultCriseFiltering("statut.in=" + DEFAULT_STATUT + "," + UPDATED_STATUT, "statut.in=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllCrisesByStatutIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList where statut is not null
        defaultCriseFiltering("statut.specified=true", "statut.specified=false");
    }

    @Test
    @Transactional
    void getAllCrisesByLatitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList where latitude equals to
        defaultCriseFiltering("latitude.equals=" + DEFAULT_LATITUDE, "latitude.equals=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllCrisesByLatitudeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList where latitude in
        defaultCriseFiltering("latitude.in=" + DEFAULT_LATITUDE + "," + UPDATED_LATITUDE, "latitude.in=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllCrisesByLatitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList where latitude is not null
        defaultCriseFiltering("latitude.specified=true", "latitude.specified=false");
    }

    @Test
    @Transactional
    void getAllCrisesByLatitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList where latitude is greater than or equal to
        defaultCriseFiltering("latitude.greaterThanOrEqual=" + DEFAULT_LATITUDE, "latitude.greaterThanOrEqual=" + (DEFAULT_LATITUDE + 1));
    }

    @Test
    @Transactional
    void getAllCrisesByLatitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList where latitude is less than or equal to
        defaultCriseFiltering("latitude.lessThanOrEqual=" + DEFAULT_LATITUDE, "latitude.lessThanOrEqual=" + SMALLER_LATITUDE);
    }

    @Test
    @Transactional
    void getAllCrisesByLatitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList where latitude is less than
        defaultCriseFiltering("latitude.lessThan=" + (DEFAULT_LATITUDE + 1), "latitude.lessThan=" + DEFAULT_LATITUDE);
    }

    @Test
    @Transactional
    void getAllCrisesByLatitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList where latitude is greater than
        defaultCriseFiltering("latitude.greaterThan=" + SMALLER_LATITUDE, "latitude.greaterThan=" + DEFAULT_LATITUDE);
    }

    @Test
    @Transactional
    void getAllCrisesByLongitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList where longitude equals to
        defaultCriseFiltering("longitude.equals=" + DEFAULT_LONGITUDE, "longitude.equals=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllCrisesByLongitudeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList where longitude in
        defaultCriseFiltering("longitude.in=" + DEFAULT_LONGITUDE + "," + UPDATED_LONGITUDE, "longitude.in=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllCrisesByLongitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList where longitude is not null
        defaultCriseFiltering("longitude.specified=true", "longitude.specified=false");
    }

    @Test
    @Transactional
    void getAllCrisesByLongitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList where longitude is greater than or equal to
        defaultCriseFiltering(
            "longitude.greaterThanOrEqual=" + DEFAULT_LONGITUDE,
            "longitude.greaterThanOrEqual=" + (DEFAULT_LONGITUDE + 1)
        );
    }

    @Test
    @Transactional
    void getAllCrisesByLongitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList where longitude is less than or equal to
        defaultCriseFiltering("longitude.lessThanOrEqual=" + DEFAULT_LONGITUDE, "longitude.lessThanOrEqual=" + SMALLER_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllCrisesByLongitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList where longitude is less than
        defaultCriseFiltering("longitude.lessThan=" + (DEFAULT_LONGITUDE + 1), "longitude.lessThan=" + DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllCrisesByLongitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        // Get all the criseList where longitude is greater than
        defaultCriseFiltering("longitude.greaterThan=" + SMALLER_LONGITUDE, "longitude.greaterThan=" + DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllCrisesByDeclarantIsEqualToSomething() throws Exception {
        User declarant;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            criseRepository.saveAndFlush(crise);
            declarant = UserResourceIT.createEntity();
        } else {
            declarant = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(declarant);
        em.flush();
        crise.setDeclarant(declarant);
        criseRepository.saveAndFlush(crise);
        Long declarantId = declarant.getId();
        // Get all the criseList where declarant equals to declarantId
        defaultCriseShouldBeFound("declarantId.equals=" + declarantId);

        // Get all the criseList where declarant equals to (declarantId + 1)
        defaultCriseShouldNotBeFound("declarantId.equals=" + (declarantId + 1));
    }

    private void defaultCriseFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCriseShouldBeFound(shouldBeFound);
        defaultCriseShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCriseShouldBeFound(String filter) throws Exception {
        restCriseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(crise.getId().intValue())))
            .andExpect(jsonPath("$.[*].titre").value(hasItem(DEFAULT_TITRE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].dateDebut").value(hasItem(DEFAULT_DATE_DEBUT.toString())))
            .andExpect(jsonPath("$.[*].dateFermeture").value(hasItem(DEFAULT_DATE_FERMETURE.toString())))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE)));

        // Check, that the count call also returns 1
        restCriseMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCriseShouldNotBeFound(String filter) throws Exception {
        restCriseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCriseMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCrise() throws Exception {
        // Get the crise
        restCriseMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCrise() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the crise
        Crise updatedCrise = criseRepository.findById(crise.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCrise are not directly saved in db
        em.detach(updatedCrise);
        updatedCrise
            .titre(UPDATED_TITRE)
            .description(UPDATED_DESCRIPTION)
            .type(UPDATED_TYPE)
            .dateDebut(UPDATED_DATE_DEBUT)
            .dateFermeture(UPDATED_DATE_FERMETURE)
            .statut(UPDATED_STATUT)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE);
        CriseDTO criseDTO = criseMapper.toDto(updatedCrise);

        restCriseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, criseDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(criseDTO))
            )
            .andExpect(status().isOk());

        // Validate the Crise in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCriseToMatchAllProperties(updatedCrise);
    }

    @Test
    @Transactional
    void putNonExistingCrise() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        crise.setId(longCount.incrementAndGet());

        // Create the Crise
        CriseDTO criseDTO = criseMapper.toDto(crise);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCriseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, criseDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(criseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Crise in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCrise() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        crise.setId(longCount.incrementAndGet());

        // Create the Crise
        CriseDTO criseDTO = criseMapper.toDto(crise);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCriseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(criseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Crise in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCrise() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        crise.setId(longCount.incrementAndGet());

        // Create the Crise
        CriseDTO criseDTO = criseMapper.toDto(crise);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCriseMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(criseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Crise in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCriseWithPatch() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the crise using partial update
        Crise partialUpdatedCrise = new Crise();
        partialUpdatedCrise.setId(crise.getId());

        partialUpdatedCrise
            .titre(UPDATED_TITRE)
            .description(UPDATED_DESCRIPTION)
            .type(UPDATED_TYPE)
            .dateDebut(UPDATED_DATE_DEBUT)
            .dateFermeture(UPDATED_DATE_FERMETURE)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE);

        restCriseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCrise.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCrise))
            )
            .andExpect(status().isOk());

        // Validate the Crise in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCriseUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCrise, crise), getPersistedCrise(crise));
    }

    @Test
    @Transactional
    void fullUpdateCriseWithPatch() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the crise using partial update
        Crise partialUpdatedCrise = new Crise();
        partialUpdatedCrise.setId(crise.getId());

        partialUpdatedCrise
            .titre(UPDATED_TITRE)
            .description(UPDATED_DESCRIPTION)
            .type(UPDATED_TYPE)
            .dateDebut(UPDATED_DATE_DEBUT)
            .dateFermeture(UPDATED_DATE_FERMETURE)
            .statut(UPDATED_STATUT)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE);

        restCriseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCrise.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCrise))
            )
            .andExpect(status().isOk());

        // Validate the Crise in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCriseUpdatableFieldsEquals(partialUpdatedCrise, getPersistedCrise(partialUpdatedCrise));
    }

    @Test
    @Transactional
    void patchNonExistingCrise() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        crise.setId(longCount.incrementAndGet());

        // Create the Crise
        CriseDTO criseDTO = criseMapper.toDto(crise);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCriseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, criseDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(criseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Crise in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCrise() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        crise.setId(longCount.incrementAndGet());

        // Create the Crise
        CriseDTO criseDTO = criseMapper.toDto(crise);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCriseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(criseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Crise in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCrise() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        crise.setId(longCount.incrementAndGet());

        // Create the Crise
        CriseDTO criseDTO = criseMapper.toDto(crise);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCriseMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(criseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Crise in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCrise() throws Exception {
        // Initialize the database
        insertedCrise = criseRepository.saveAndFlush(crise);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the crise
        restCriseMockMvc
            .perform(delete(ENTITY_API_URL_ID, crise.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return criseRepository.count();
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

    protected Crise getPersistedCrise(Crise crise) {
        return criseRepository.findById(crise.getId()).orElseThrow();
    }

    protected void assertPersistedCriseToMatchAllProperties(Crise expectedCrise) {
        assertCriseAllPropertiesEquals(expectedCrise, getPersistedCrise(expectedCrise));
    }

    protected void assertPersistedCriseToMatchUpdatableProperties(Crise expectedCrise) {
        assertCriseAllUpdatablePropertiesEquals(expectedCrise, getPersistedCrise(expectedCrise));
    }
}
