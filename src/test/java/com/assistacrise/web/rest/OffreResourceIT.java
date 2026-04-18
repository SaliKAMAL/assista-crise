package com.assistacrise.web.rest;

import static com.assistacrise.domain.OffreAsserts.*;
import static com.assistacrise.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.assistacrise.IntegrationTest;
import com.assistacrise.domain.Crise;
import com.assistacrise.domain.Demande;
import com.assistacrise.domain.Offre;
import com.assistacrise.domain.User;
import com.assistacrise.repository.OffreRepository;
import com.assistacrise.repository.UserRepository;
import com.assistacrise.service.OffreService;
import com.assistacrise.service.dto.OffreDTO;
import com.assistacrise.service.mapper.OffreMapper;
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
 * Integration tests for the {@link OffreResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class OffreResourceIT {

    private static final String DEFAULT_TITRE = "AAAAAAAAAA";
    private static final String UPDATED_TITRE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_CREATION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_CREATION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_DATE_MISE_A_JOUR = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_MISE_A_JOUR = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Double DEFAULT_LATITUDE = -90D;
    private static final Double UPDATED_LATITUDE = -89D;
    private static final Double SMALLER_LATITUDE = -90D - 1D;

    private static final Double DEFAULT_LONGITUDE = -180D;
    private static final Double UPDATED_LONGITUDE = -179D;
    private static final Double SMALLER_LONGITUDE = -180D - 1D;

    private static final Boolean DEFAULT_ARCHIVEE = false;
    private static final Boolean UPDATED_ARCHIVEE = true;

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/offres";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OffreRepository offreRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private OffreRepository offreRepositoryMock;

    @Autowired
    private OffreMapper offreMapper;

    @Mock
    private OffreService offreServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOffreMockMvc;

    private Offre offre;

    private Offre insertedOffre;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Offre createEntity(EntityManager em) {
        Offre offre = new Offre()
            .titre(DEFAULT_TITRE)
            .description(DEFAULT_DESCRIPTION)
            .dateCreation(DEFAULT_DATE_CREATION)
            .dateMiseAJour(DEFAULT_DATE_MISE_A_JOUR)
            .latitude(DEFAULT_LATITUDE)
            .longitude(DEFAULT_LONGITUDE)
            .archivee(DEFAULT_ARCHIVEE)
            .active(DEFAULT_ACTIVE);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        offre.setCitoyen(user);
        // Add required entity
        Crise crise;
        if (TestUtil.findAll(em, Crise.class).isEmpty()) {
            crise = CriseResourceIT.createEntity(em);
            em.persist(crise);
            em.flush();
        } else {
            crise = TestUtil.findAll(em, Crise.class).get(0);
        }
        offre.setCrise(crise);
        return offre;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Offre createUpdatedEntity(EntityManager em) {
        Offre updatedOffre = new Offre()
            .titre(UPDATED_TITRE)
            .description(UPDATED_DESCRIPTION)
            .dateCreation(UPDATED_DATE_CREATION)
            .dateMiseAJour(UPDATED_DATE_MISE_A_JOUR)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .archivee(UPDATED_ARCHIVEE)
            .active(UPDATED_ACTIVE);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedOffre.setCitoyen(user);
        // Add required entity
        Crise crise;
        if (TestUtil.findAll(em, Crise.class).isEmpty()) {
            crise = CriseResourceIT.createUpdatedEntity(em);
            em.persist(crise);
            em.flush();
        } else {
            crise = TestUtil.findAll(em, Crise.class).get(0);
        }
        updatedOffre.setCrise(crise);
        return updatedOffre;
    }

    @BeforeEach
    void initTest() {
        offre = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedOffre != null) {
            offreRepository.delete(insertedOffre);
            insertedOffre = null;
        }
    }

    @Test
    @Transactional
    void createOffre() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Offre
        OffreDTO offreDTO = offreMapper.toDto(offre);
        var returnedOffreDTO = om.readValue(
            restOffreMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(offreDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            OffreDTO.class
        );

        // Validate the Offre in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedOffre = offreMapper.toEntity(returnedOffreDTO);
        assertOffreUpdatableFieldsEquals(returnedOffre, getPersistedOffre(returnedOffre));

        insertedOffre = returnedOffre;
    }

    @Test
    @Transactional
    void createOffreWithExistingId() throws Exception {
        // Create the Offre with an existing ID
        offre.setId(1L);
        OffreDTO offreDTO = offreMapper.toDto(offre);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOffreMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(offreDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Offre in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        offre.setTitre(null);

        // Create the Offre, which fails.
        OffreDTO offreDTO = offreMapper.toDto(offre);

        restOffreMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(offreDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        offre.setDescription(null);

        // Create the Offre, which fails.
        OffreDTO offreDTO = offreMapper.toDto(offre);

        restOffreMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(offreDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateCreationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        offre.setDateCreation(null);

        // Create the Offre, which fails.
        OffreDTO offreDTO = offreMapper.toDto(offre);

        restOffreMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(offreDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkArchiveeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        offre.setArchivee(null);

        // Create the Offre, which fails.
        OffreDTO offreDTO = offreMapper.toDto(offre);

        restOffreMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(offreDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        offre.setActive(null);

        // Create the Offre, which fails.
        OffreDTO offreDTO = offreMapper.toDto(offre);

        restOffreMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(offreDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllOffres() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList
        restOffreMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(offre.getId().intValue())))
            .andExpect(jsonPath("$.[*].titre").value(hasItem(DEFAULT_TITRE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].dateCreation").value(hasItem(DEFAULT_DATE_CREATION.toString())))
            .andExpect(jsonPath("$.[*].dateMiseAJour").value(hasItem(DEFAULT_DATE_MISE_A_JOUR.toString())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE)))
            .andExpect(jsonPath("$.[*].archivee").value(hasItem(DEFAULT_ARCHIVEE)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOffresWithEagerRelationshipsIsEnabled() throws Exception {
        when(offreServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOffreMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(offreServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOffresWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(offreServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOffreMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(offreRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getOffre() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get the offre
        restOffreMockMvc
            .perform(get(ENTITY_API_URL_ID, offre.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(offre.getId().intValue()))
            .andExpect(jsonPath("$.titre").value(DEFAULT_TITRE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.dateCreation").value(DEFAULT_DATE_CREATION.toString()))
            .andExpect(jsonPath("$.dateMiseAJour").value(DEFAULT_DATE_MISE_A_JOUR.toString()))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE))
            .andExpect(jsonPath("$.archivee").value(DEFAULT_ARCHIVEE))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE));
    }

    @Test
    @Transactional
    void getOffresByIdFiltering() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        Long id = offre.getId();

        defaultOffreFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultOffreFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultOffreFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllOffresByTitreIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList where titre equals to
        defaultOffreFiltering("titre.equals=" + DEFAULT_TITRE, "titre.equals=" + UPDATED_TITRE);
    }

    @Test
    @Transactional
    void getAllOffresByTitreIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList where titre in
        defaultOffreFiltering("titre.in=" + DEFAULT_TITRE + "," + UPDATED_TITRE, "titre.in=" + UPDATED_TITRE);
    }

    @Test
    @Transactional
    void getAllOffresByTitreIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList where titre is not null
        defaultOffreFiltering("titre.specified=true", "titre.specified=false");
    }

    @Test
    @Transactional
    void getAllOffresByTitreContainsSomething() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList where titre contains
        defaultOffreFiltering("titre.contains=" + DEFAULT_TITRE, "titre.contains=" + UPDATED_TITRE);
    }

    @Test
    @Transactional
    void getAllOffresByTitreNotContainsSomething() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList where titre does not contain
        defaultOffreFiltering("titre.doesNotContain=" + UPDATED_TITRE, "titre.doesNotContain=" + DEFAULT_TITRE);
    }

    @Test
    @Transactional
    void getAllOffresByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList where description equals to
        defaultOffreFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllOffresByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList where description in
        defaultOffreFiltering("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION, "description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllOffresByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList where description is not null
        defaultOffreFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllOffresByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList where description contains
        defaultOffreFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllOffresByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList where description does not contain
        defaultOffreFiltering("description.doesNotContain=" + UPDATED_DESCRIPTION, "description.doesNotContain=" + DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllOffresByDateCreationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList where dateCreation equals to
        defaultOffreFiltering("dateCreation.equals=" + DEFAULT_DATE_CREATION, "dateCreation.equals=" + UPDATED_DATE_CREATION);
    }

    @Test
    @Transactional
    void getAllOffresByDateCreationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList where dateCreation in
        defaultOffreFiltering(
            "dateCreation.in=" + DEFAULT_DATE_CREATION + "," + UPDATED_DATE_CREATION,
            "dateCreation.in=" + UPDATED_DATE_CREATION
        );
    }

    @Test
    @Transactional
    void getAllOffresByDateCreationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList where dateCreation is not null
        defaultOffreFiltering("dateCreation.specified=true", "dateCreation.specified=false");
    }

    @Test
    @Transactional
    void getAllOffresByDateMiseAJourIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList where dateMiseAJour equals to
        defaultOffreFiltering("dateMiseAJour.equals=" + DEFAULT_DATE_MISE_A_JOUR, "dateMiseAJour.equals=" + UPDATED_DATE_MISE_A_JOUR);
    }

    @Test
    @Transactional
    void getAllOffresByDateMiseAJourIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList where dateMiseAJour in
        defaultOffreFiltering(
            "dateMiseAJour.in=" + DEFAULT_DATE_MISE_A_JOUR + "," + UPDATED_DATE_MISE_A_JOUR,
            "dateMiseAJour.in=" + UPDATED_DATE_MISE_A_JOUR
        );
    }

    @Test
    @Transactional
    void getAllOffresByDateMiseAJourIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList where dateMiseAJour is not null
        defaultOffreFiltering("dateMiseAJour.specified=true", "dateMiseAJour.specified=false");
    }

    @Test
    @Transactional
    void getAllOffresByLatitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList where latitude equals to
        defaultOffreFiltering("latitude.equals=" + DEFAULT_LATITUDE, "latitude.equals=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllOffresByLatitudeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList where latitude in
        defaultOffreFiltering("latitude.in=" + DEFAULT_LATITUDE + "," + UPDATED_LATITUDE, "latitude.in=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllOffresByLatitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList where latitude is not null
        defaultOffreFiltering("latitude.specified=true", "latitude.specified=false");
    }

    @Test
    @Transactional
    void getAllOffresByLatitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList where latitude is greater than or equal to
        defaultOffreFiltering("latitude.greaterThanOrEqual=" + DEFAULT_LATITUDE, "latitude.greaterThanOrEqual=" + (DEFAULT_LATITUDE + 1));
    }

    @Test
    @Transactional
    void getAllOffresByLatitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList where latitude is less than or equal to
        defaultOffreFiltering("latitude.lessThanOrEqual=" + DEFAULT_LATITUDE, "latitude.lessThanOrEqual=" + SMALLER_LATITUDE);
    }

    @Test
    @Transactional
    void getAllOffresByLatitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList where latitude is less than
        defaultOffreFiltering("latitude.lessThan=" + (DEFAULT_LATITUDE + 1), "latitude.lessThan=" + DEFAULT_LATITUDE);
    }

    @Test
    @Transactional
    void getAllOffresByLatitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList where latitude is greater than
        defaultOffreFiltering("latitude.greaterThan=" + SMALLER_LATITUDE, "latitude.greaterThan=" + DEFAULT_LATITUDE);
    }

    @Test
    @Transactional
    void getAllOffresByLongitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList where longitude equals to
        defaultOffreFiltering("longitude.equals=" + DEFAULT_LONGITUDE, "longitude.equals=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllOffresByLongitudeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList where longitude in
        defaultOffreFiltering("longitude.in=" + DEFAULT_LONGITUDE + "," + UPDATED_LONGITUDE, "longitude.in=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllOffresByLongitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList where longitude is not null
        defaultOffreFiltering("longitude.specified=true", "longitude.specified=false");
    }

    @Test
    @Transactional
    void getAllOffresByLongitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList where longitude is greater than or equal to
        defaultOffreFiltering(
            "longitude.greaterThanOrEqual=" + DEFAULT_LONGITUDE,
            "longitude.greaterThanOrEqual=" + (DEFAULT_LONGITUDE + 1)
        );
    }

    @Test
    @Transactional
    void getAllOffresByLongitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList where longitude is less than or equal to
        defaultOffreFiltering("longitude.lessThanOrEqual=" + DEFAULT_LONGITUDE, "longitude.lessThanOrEqual=" + SMALLER_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllOffresByLongitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList where longitude is less than
        defaultOffreFiltering("longitude.lessThan=" + (DEFAULT_LONGITUDE + 1), "longitude.lessThan=" + DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllOffresByLongitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList where longitude is greater than
        defaultOffreFiltering("longitude.greaterThan=" + SMALLER_LONGITUDE, "longitude.greaterThan=" + DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllOffresByArchiveeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList where archivee equals to
        defaultOffreFiltering("archivee.equals=" + DEFAULT_ARCHIVEE, "archivee.equals=" + UPDATED_ARCHIVEE);
    }

    @Test
    @Transactional
    void getAllOffresByArchiveeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList where archivee in
        defaultOffreFiltering("archivee.in=" + DEFAULT_ARCHIVEE + "," + UPDATED_ARCHIVEE, "archivee.in=" + UPDATED_ARCHIVEE);
    }

    @Test
    @Transactional
    void getAllOffresByArchiveeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList where archivee is not null
        defaultOffreFiltering("archivee.specified=true", "archivee.specified=false");
    }

    @Test
    @Transactional
    void getAllOffresByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList where active equals to
        defaultOffreFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllOffresByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList where active in
        defaultOffreFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllOffresByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        // Get all the offreList where active is not null
        defaultOffreFiltering("active.specified=true", "active.specified=false");
    }

    @Test
    @Transactional
    void getAllOffresByCitoyenIsEqualToSomething() throws Exception {
        User citoyen;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            offreRepository.saveAndFlush(offre);
            citoyen = UserResourceIT.createEntity();
        } else {
            citoyen = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(citoyen);
        em.flush();
        offre.setCitoyen(citoyen);
        offreRepository.saveAndFlush(offre);
        Long citoyenId = citoyen.getId();
        // Get all the offreList where citoyen equals to citoyenId
        defaultOffreShouldBeFound("citoyenId.equals=" + citoyenId);

        // Get all the offreList where citoyen equals to (citoyenId + 1)
        defaultOffreShouldNotBeFound("citoyenId.equals=" + (citoyenId + 1));
    }

    @Test
    @Transactional
    void getAllOffresByCriseIsEqualToSomething() throws Exception {
        Crise crise;
        if (TestUtil.findAll(em, Crise.class).isEmpty()) {
            offreRepository.saveAndFlush(offre);
            crise = CriseResourceIT.createEntity(em);
        } else {
            crise = TestUtil.findAll(em, Crise.class).get(0);
        }
        em.persist(crise);
        em.flush();
        offre.setCrise(crise);
        offreRepository.saveAndFlush(offre);
        Long criseId = crise.getId();
        // Get all the offreList where crise equals to criseId
        defaultOffreShouldBeFound("criseId.equals=" + criseId);

        // Get all the offreList where crise equals to (criseId + 1)
        defaultOffreShouldNotBeFound("criseId.equals=" + (criseId + 1));
    }

    @Test
    @Transactional
    void getAllOffresByDemandeIsEqualToSomething() throws Exception {
        Demande demande;
        if (TestUtil.findAll(em, Demande.class).isEmpty()) {
            offreRepository.saveAndFlush(offre);
            demande = DemandeResourceIT.createEntity(em);
        } else {
            demande = TestUtil.findAll(em, Demande.class).get(0);
        }
        em.persist(demande);
        em.flush();
        offre.addDemande(demande);
        offreRepository.saveAndFlush(offre);
        Long demandeId = demande.getId();
        // Get all the offreList where demande equals to demandeId
        defaultOffreShouldBeFound("demandeId.equals=" + demandeId);

        // Get all the offreList where demande equals to (demandeId + 1)
        defaultOffreShouldNotBeFound("demandeId.equals=" + (demandeId + 1));
    }

    private void defaultOffreFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultOffreShouldBeFound(shouldBeFound);
        defaultOffreShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOffreShouldBeFound(String filter) throws Exception {
        restOffreMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(offre.getId().intValue())))
            .andExpect(jsonPath("$.[*].titre").value(hasItem(DEFAULT_TITRE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].dateCreation").value(hasItem(DEFAULT_DATE_CREATION.toString())))
            .andExpect(jsonPath("$.[*].dateMiseAJour").value(hasItem(DEFAULT_DATE_MISE_A_JOUR.toString())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE)))
            .andExpect(jsonPath("$.[*].archivee").value(hasItem(DEFAULT_ARCHIVEE)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));

        // Check, that the count call also returns 1
        restOffreMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultOffreShouldNotBeFound(String filter) throws Exception {
        restOffreMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restOffreMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingOffre() throws Exception {
        // Get the offre
        restOffreMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOffre() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the offre
        Offre updatedOffre = offreRepository.findById(offre.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedOffre are not directly saved in db
        em.detach(updatedOffre);
        updatedOffre
            .titre(UPDATED_TITRE)
            .description(UPDATED_DESCRIPTION)
            .dateCreation(UPDATED_DATE_CREATION)
            .dateMiseAJour(UPDATED_DATE_MISE_A_JOUR)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .archivee(UPDATED_ARCHIVEE)
            .active(UPDATED_ACTIVE);
        OffreDTO offreDTO = offreMapper.toDto(updatedOffre);

        restOffreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, offreDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(offreDTO))
            )
            .andExpect(status().isOk());

        // Validate the Offre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOffreToMatchAllProperties(updatedOffre);
    }

    @Test
    @Transactional
    void putNonExistingOffre() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offre.setId(longCount.incrementAndGet());

        // Create the Offre
        OffreDTO offreDTO = offreMapper.toDto(offre);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOffreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, offreDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(offreDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Offre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOffre() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offre.setId(longCount.incrementAndGet());

        // Create the Offre
        OffreDTO offreDTO = offreMapper.toDto(offre);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOffreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(offreDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Offre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOffre() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offre.setId(longCount.incrementAndGet());

        // Create the Offre
        OffreDTO offreDTO = offreMapper.toDto(offre);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOffreMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(offreDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Offre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOffreWithPatch() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the offre using partial update
        Offre partialUpdatedOffre = new Offre();
        partialUpdatedOffre.setId(offre.getId());

        partialUpdatedOffre
            .titre(UPDATED_TITRE)
            .description(UPDATED_DESCRIPTION)
            .dateCreation(UPDATED_DATE_CREATION)
            .dateMiseAJour(UPDATED_DATE_MISE_A_JOUR)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .archivee(UPDATED_ARCHIVEE)
            .active(UPDATED_ACTIVE);

        restOffreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOffre.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOffre))
            )
            .andExpect(status().isOk());

        // Validate the Offre in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOffreUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedOffre, offre), getPersistedOffre(offre));
    }

    @Test
    @Transactional
    void fullUpdateOffreWithPatch() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the offre using partial update
        Offre partialUpdatedOffre = new Offre();
        partialUpdatedOffre.setId(offre.getId());

        partialUpdatedOffre
            .titre(UPDATED_TITRE)
            .description(UPDATED_DESCRIPTION)
            .dateCreation(UPDATED_DATE_CREATION)
            .dateMiseAJour(UPDATED_DATE_MISE_A_JOUR)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .archivee(UPDATED_ARCHIVEE)
            .active(UPDATED_ACTIVE);

        restOffreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOffre.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOffre))
            )
            .andExpect(status().isOk());

        // Validate the Offre in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOffreUpdatableFieldsEquals(partialUpdatedOffre, getPersistedOffre(partialUpdatedOffre));
    }

    @Test
    @Transactional
    void patchNonExistingOffre() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offre.setId(longCount.incrementAndGet());

        // Create the Offre
        OffreDTO offreDTO = offreMapper.toDto(offre);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOffreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, offreDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(offreDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Offre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOffre() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offre.setId(longCount.incrementAndGet());

        // Create the Offre
        OffreDTO offreDTO = offreMapper.toDto(offre);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOffreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(offreDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Offre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOffre() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offre.setId(longCount.incrementAndGet());

        // Create the Offre
        OffreDTO offreDTO = offreMapper.toDto(offre);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOffreMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(offreDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Offre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOffre() throws Exception {
        // Initialize the database
        insertedOffre = offreRepository.saveAndFlush(offre);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the offre
        restOffreMockMvc
            .perform(delete(ENTITY_API_URL_ID, offre.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return offreRepository.count();
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

    protected Offre getPersistedOffre(Offre offre) {
        return offreRepository.findById(offre.getId()).orElseThrow();
    }

    protected void assertPersistedOffreToMatchAllProperties(Offre expectedOffre) {
        assertOffreAllPropertiesEquals(expectedOffre, getPersistedOffre(expectedOffre));
    }

    protected void assertPersistedOffreToMatchUpdatableProperties(Offre expectedOffre) {
        assertOffreAllUpdatablePropertiesEquals(expectedOffre, getPersistedOffre(expectedOffre));
    }
}
