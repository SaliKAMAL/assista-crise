package com.assistacrise.web.rest;

import static com.assistacrise.domain.DemandeAsserts.*;
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
import com.assistacrise.domain.enumeration.StatutDemande;
import com.assistacrise.repository.DemandeRepository;
import com.assistacrise.repository.UserRepository;
import com.assistacrise.service.DemandeService;
import com.assistacrise.service.dto.DemandeDTO;
import com.assistacrise.service.mapper.DemandeMapper;
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
 * Integration tests for the {@link DemandeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DemandeResourceIT {

    private static final String DEFAULT_TITRE = "AAAAAAAAAA";
    private static final String UPDATED_TITRE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final StatutDemande DEFAULT_STATUT = StatutDemande.OUVERTE;
    private static final StatutDemande UPDATED_STATUT = StatutDemande.EN_COURS;

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

    private static final String ENTITY_API_URL = "/api/demandes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DemandeRepository demandeRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private DemandeRepository demandeRepositoryMock;

    @Autowired
    private DemandeMapper demandeMapper;

    @Mock
    private DemandeService demandeServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDemandeMockMvc;

    private Demande demande;

    private Demande insertedDemande;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Demande createEntity(EntityManager em) {
        Demande demande = new Demande()
            .titre(DEFAULT_TITRE)
            .description(DEFAULT_DESCRIPTION)
            .statut(DEFAULT_STATUT)
            .dateCreation(DEFAULT_DATE_CREATION)
            .dateMiseAJour(DEFAULT_DATE_MISE_A_JOUR)
            .latitude(DEFAULT_LATITUDE)
            .longitude(DEFAULT_LONGITUDE)
            .archivee(DEFAULT_ARCHIVEE);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        demande.setSinistre(user);
        // Add required entity
        Crise crise;
        if (TestUtil.findAll(em, Crise.class).isEmpty()) {
            crise = CriseResourceIT.createEntity(em);
            em.persist(crise);
            em.flush();
        } else {
            crise = TestUtil.findAll(em, Crise.class).get(0);
        }
        demande.setCrise(crise);
        return demande;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Demande createUpdatedEntity(EntityManager em) {
        Demande updatedDemande = new Demande()
            .titre(UPDATED_TITRE)
            .description(UPDATED_DESCRIPTION)
            .statut(UPDATED_STATUT)
            .dateCreation(UPDATED_DATE_CREATION)
            .dateMiseAJour(UPDATED_DATE_MISE_A_JOUR)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .archivee(UPDATED_ARCHIVEE);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedDemande.setSinistre(user);
        // Add required entity
        Crise crise;
        if (TestUtil.findAll(em, Crise.class).isEmpty()) {
            crise = CriseResourceIT.createUpdatedEntity(em);
            em.persist(crise);
            em.flush();
        } else {
            crise = TestUtil.findAll(em, Crise.class).get(0);
        }
        updatedDemande.setCrise(crise);
        return updatedDemande;
    }

    @BeforeEach
    void initTest() {
        demande = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedDemande != null) {
            demandeRepository.delete(insertedDemande);
            insertedDemande = null;
        }
    }

    @Test
    @Transactional
    void createDemande() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Demande
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);
        var returnedDemandeDTO = om.readValue(
            restDemandeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(demandeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DemandeDTO.class
        );

        // Validate the Demande in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDemande = demandeMapper.toEntity(returnedDemandeDTO);
        assertDemandeUpdatableFieldsEquals(returnedDemande, getPersistedDemande(returnedDemande));

        insertedDemande = returnedDemande;
    }

    @Test
    @Transactional
    void createDemandeWithExistingId() throws Exception {
        // Create the Demande with an existing ID
        demande.setId(1L);
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDemandeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(demandeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Demande in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        demande.setTitre(null);

        // Create the Demande, which fails.
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);

        restDemandeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(demandeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        demande.setDescription(null);

        // Create the Demande, which fails.
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);

        restDemandeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(demandeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatutIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        demande.setStatut(null);

        // Create the Demande, which fails.
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);

        restDemandeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(demandeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateCreationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        demande.setDateCreation(null);

        // Create the Demande, which fails.
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);

        restDemandeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(demandeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkArchiveeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        demande.setArchivee(null);

        // Create the Demande, which fails.
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);

        restDemandeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(demandeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDemandes() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList
        restDemandeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(demande.getId().intValue())))
            .andExpect(jsonPath("$.[*].titre").value(hasItem(DEFAULT_TITRE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())))
            .andExpect(jsonPath("$.[*].dateCreation").value(hasItem(DEFAULT_DATE_CREATION.toString())))
            .andExpect(jsonPath("$.[*].dateMiseAJour").value(hasItem(DEFAULT_DATE_MISE_A_JOUR.toString())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE)))
            .andExpect(jsonPath("$.[*].archivee").value(hasItem(DEFAULT_ARCHIVEE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDemandesWithEagerRelationshipsIsEnabled() throws Exception {
        when(demandeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDemandeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(demandeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDemandesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(demandeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDemandeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(demandeRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getDemande() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get the demande
        restDemandeMockMvc
            .perform(get(ENTITY_API_URL_ID, demande.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(demande.getId().intValue()))
            .andExpect(jsonPath("$.titre").value(DEFAULT_TITRE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.statut").value(DEFAULT_STATUT.toString()))
            .andExpect(jsonPath("$.dateCreation").value(DEFAULT_DATE_CREATION.toString()))
            .andExpect(jsonPath("$.dateMiseAJour").value(DEFAULT_DATE_MISE_A_JOUR.toString()))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE))
            .andExpect(jsonPath("$.archivee").value(DEFAULT_ARCHIVEE));
    }

    @Test
    @Transactional
    void getDemandesByIdFiltering() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        Long id = demande.getId();

        defaultDemandeFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultDemandeFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultDemandeFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDemandesByTitreIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where titre equals to
        defaultDemandeFiltering("titre.equals=" + DEFAULT_TITRE, "titre.equals=" + UPDATED_TITRE);
    }

    @Test
    @Transactional
    void getAllDemandesByTitreIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where titre in
        defaultDemandeFiltering("titre.in=" + DEFAULT_TITRE + "," + UPDATED_TITRE, "titre.in=" + UPDATED_TITRE);
    }

    @Test
    @Transactional
    void getAllDemandesByTitreIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where titre is not null
        defaultDemandeFiltering("titre.specified=true", "titre.specified=false");
    }

    @Test
    @Transactional
    void getAllDemandesByTitreContainsSomething() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where titre contains
        defaultDemandeFiltering("titre.contains=" + DEFAULT_TITRE, "titre.contains=" + UPDATED_TITRE);
    }

    @Test
    @Transactional
    void getAllDemandesByTitreNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where titre does not contain
        defaultDemandeFiltering("titre.doesNotContain=" + UPDATED_TITRE, "titre.doesNotContain=" + DEFAULT_TITRE);
    }

    @Test
    @Transactional
    void getAllDemandesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where description equals to
        defaultDemandeFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllDemandesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where description in
        defaultDemandeFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllDemandesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where description is not null
        defaultDemandeFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllDemandesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where description contains
        defaultDemandeFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllDemandesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where description does not contain
        defaultDemandeFiltering("description.doesNotContain=" + UPDATED_DESCRIPTION, "description.doesNotContain=" + DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllDemandesByStatutIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where statut equals to
        defaultDemandeFiltering("statut.equals=" + DEFAULT_STATUT, "statut.equals=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllDemandesByStatutIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where statut in
        defaultDemandeFiltering("statut.in=" + DEFAULT_STATUT + "," + UPDATED_STATUT, "statut.in=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllDemandesByStatutIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where statut is not null
        defaultDemandeFiltering("statut.specified=true", "statut.specified=false");
    }

    @Test
    @Transactional
    void getAllDemandesByDateCreationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where dateCreation equals to
        defaultDemandeFiltering("dateCreation.equals=" + DEFAULT_DATE_CREATION, "dateCreation.equals=" + UPDATED_DATE_CREATION);
    }

    @Test
    @Transactional
    void getAllDemandesByDateCreationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where dateCreation in
        defaultDemandeFiltering(
            "dateCreation.in=" + DEFAULT_DATE_CREATION + "," + UPDATED_DATE_CREATION,
            "dateCreation.in=" + UPDATED_DATE_CREATION
        );
    }

    @Test
    @Transactional
    void getAllDemandesByDateCreationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where dateCreation is not null
        defaultDemandeFiltering("dateCreation.specified=true", "dateCreation.specified=false");
    }

    @Test
    @Transactional
    void getAllDemandesByDateMiseAJourIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where dateMiseAJour equals to
        defaultDemandeFiltering("dateMiseAJour.equals=" + DEFAULT_DATE_MISE_A_JOUR, "dateMiseAJour.equals=" + UPDATED_DATE_MISE_A_JOUR);
    }

    @Test
    @Transactional
    void getAllDemandesByDateMiseAJourIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where dateMiseAJour in
        defaultDemandeFiltering(
            "dateMiseAJour.in=" + DEFAULT_DATE_MISE_A_JOUR + "," + UPDATED_DATE_MISE_A_JOUR,
            "dateMiseAJour.in=" + UPDATED_DATE_MISE_A_JOUR
        );
    }

    @Test
    @Transactional
    void getAllDemandesByDateMiseAJourIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where dateMiseAJour is not null
        defaultDemandeFiltering("dateMiseAJour.specified=true", "dateMiseAJour.specified=false");
    }

    @Test
    @Transactional
    void getAllDemandesByLatitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where latitude equals to
        defaultDemandeFiltering("latitude.equals=" + DEFAULT_LATITUDE, "latitude.equals=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllDemandesByLatitudeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where latitude in
        defaultDemandeFiltering("latitude.in=" + DEFAULT_LATITUDE + "," + UPDATED_LATITUDE, "latitude.in=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllDemandesByLatitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where latitude is not null
        defaultDemandeFiltering("latitude.specified=true", "latitude.specified=false");
    }

    @Test
    @Transactional
    void getAllDemandesByLatitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where latitude is greater than or equal to
        defaultDemandeFiltering("latitude.greaterThanOrEqual=" + DEFAULT_LATITUDE, "latitude.greaterThanOrEqual=" + (DEFAULT_LATITUDE + 1));
    }

    @Test
    @Transactional
    void getAllDemandesByLatitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where latitude is less than or equal to
        defaultDemandeFiltering("latitude.lessThanOrEqual=" + DEFAULT_LATITUDE, "latitude.lessThanOrEqual=" + SMALLER_LATITUDE);
    }

    @Test
    @Transactional
    void getAllDemandesByLatitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where latitude is less than
        defaultDemandeFiltering("latitude.lessThan=" + (DEFAULT_LATITUDE + 1), "latitude.lessThan=" + DEFAULT_LATITUDE);
    }

    @Test
    @Transactional
    void getAllDemandesByLatitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where latitude is greater than
        defaultDemandeFiltering("latitude.greaterThan=" + SMALLER_LATITUDE, "latitude.greaterThan=" + DEFAULT_LATITUDE);
    }

    @Test
    @Transactional
    void getAllDemandesByLongitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where longitude equals to
        defaultDemandeFiltering("longitude.equals=" + DEFAULT_LONGITUDE, "longitude.equals=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllDemandesByLongitudeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where longitude in
        defaultDemandeFiltering("longitude.in=" + DEFAULT_LONGITUDE + "," + UPDATED_LONGITUDE, "longitude.in=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllDemandesByLongitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where longitude is not null
        defaultDemandeFiltering("longitude.specified=true", "longitude.specified=false");
    }

    @Test
    @Transactional
    void getAllDemandesByLongitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where longitude is greater than or equal to
        defaultDemandeFiltering(
            "longitude.greaterThanOrEqual=" + DEFAULT_LONGITUDE,
            "longitude.greaterThanOrEqual=" + (DEFAULT_LONGITUDE + 1)
        );
    }

    @Test
    @Transactional
    void getAllDemandesByLongitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where longitude is less than or equal to
        defaultDemandeFiltering("longitude.lessThanOrEqual=" + DEFAULT_LONGITUDE, "longitude.lessThanOrEqual=" + SMALLER_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllDemandesByLongitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where longitude is less than
        defaultDemandeFiltering("longitude.lessThan=" + (DEFAULT_LONGITUDE + 1), "longitude.lessThan=" + DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllDemandesByLongitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where longitude is greater than
        defaultDemandeFiltering("longitude.greaterThan=" + SMALLER_LONGITUDE, "longitude.greaterThan=" + DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllDemandesByArchiveeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where archivee equals to
        defaultDemandeFiltering("archivee.equals=" + DEFAULT_ARCHIVEE, "archivee.equals=" + UPDATED_ARCHIVEE);
    }

    @Test
    @Transactional
    void getAllDemandesByArchiveeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where archivee in
        defaultDemandeFiltering("archivee.in=" + DEFAULT_ARCHIVEE + "," + UPDATED_ARCHIVEE, "archivee.in=" + UPDATED_ARCHIVEE);
    }

    @Test
    @Transactional
    void getAllDemandesByArchiveeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where archivee is not null
        defaultDemandeFiltering("archivee.specified=true", "archivee.specified=false");
    }

    @Test
    @Transactional
    void getAllDemandesBySinistreIsEqualToSomething() throws Exception {
        User sinistre;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            demandeRepository.saveAndFlush(demande);
            sinistre = UserResourceIT.createEntity();
        } else {
            sinistre = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(sinistre);
        em.flush();
        demande.setSinistre(sinistre);
        demandeRepository.saveAndFlush(demande);
        Long sinistreId = sinistre.getId();
        // Get all the demandeList where sinistre equals to sinistreId
        defaultDemandeShouldBeFound("sinistreId.equals=" + sinistreId);

        // Get all the demandeList where sinistre equals to (sinistreId + 1)
        defaultDemandeShouldNotBeFound("sinistreId.equals=" + (sinistreId + 1));
    }

    @Test
    @Transactional
    void getAllDemandesByCriseIsEqualToSomething() throws Exception {
        Crise crise;
        if (TestUtil.findAll(em, Crise.class).isEmpty()) {
            demandeRepository.saveAndFlush(demande);
            crise = CriseResourceIT.createEntity(em);
        } else {
            crise = TestUtil.findAll(em, Crise.class).get(0);
        }
        em.persist(crise);
        em.flush();
        demande.setCrise(crise);
        demandeRepository.saveAndFlush(demande);
        Long criseId = crise.getId();
        // Get all the demandeList where crise equals to criseId
        defaultDemandeShouldBeFound("criseId.equals=" + criseId);

        // Get all the demandeList where crise equals to (criseId + 1)
        defaultDemandeShouldNotBeFound("criseId.equals=" + (criseId + 1));
    }

    @Test
    @Transactional
    void getAllDemandesByOffreIsEqualToSomething() throws Exception {
        Offre offre;
        if (TestUtil.findAll(em, Offre.class).isEmpty()) {
            demandeRepository.saveAndFlush(demande);
            offre = OffreResourceIT.createEntity(em);
        } else {
            offre = TestUtil.findAll(em, Offre.class).get(0);
        }
        em.persist(offre);
        em.flush();
        demande.addOffre(offre);
        demandeRepository.saveAndFlush(demande);
        Long offreId = offre.getId();
        // Get all the demandeList where offre equals to offreId
        defaultDemandeShouldBeFound("offreId.equals=" + offreId);

        // Get all the demandeList where offre equals to (offreId + 1)
        defaultDemandeShouldNotBeFound("offreId.equals=" + (offreId + 1));
    }

    private void defaultDemandeFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultDemandeShouldBeFound(shouldBeFound);
        defaultDemandeShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDemandeShouldBeFound(String filter) throws Exception {
        restDemandeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(demande.getId().intValue())))
            .andExpect(jsonPath("$.[*].titre").value(hasItem(DEFAULT_TITRE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())))
            .andExpect(jsonPath("$.[*].dateCreation").value(hasItem(DEFAULT_DATE_CREATION.toString())))
            .andExpect(jsonPath("$.[*].dateMiseAJour").value(hasItem(DEFAULT_DATE_MISE_A_JOUR.toString())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE)))
            .andExpect(jsonPath("$.[*].archivee").value(hasItem(DEFAULT_ARCHIVEE)));

        // Check, that the count call also returns 1
        restDemandeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDemandeShouldNotBeFound(String filter) throws Exception {
        restDemandeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDemandeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDemande() throws Exception {
        // Get the demande
        restDemandeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDemande() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the demande
        Demande updatedDemande = demandeRepository.findById(demande.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDemande are not directly saved in db
        em.detach(updatedDemande);
        updatedDemande
            .titre(UPDATED_TITRE)
            .description(UPDATED_DESCRIPTION)
            .statut(UPDATED_STATUT)
            .dateCreation(UPDATED_DATE_CREATION)
            .dateMiseAJour(UPDATED_DATE_MISE_A_JOUR)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .archivee(UPDATED_ARCHIVEE);
        DemandeDTO demandeDTO = demandeMapper.toDto(updatedDemande);

        restDemandeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, demandeDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(demandeDTO))
            )
            .andExpect(status().isOk());

        // Validate the Demande in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDemandeToMatchAllProperties(updatedDemande);
    }

    @Test
    @Transactional
    void putNonExistingDemande() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        demande.setId(longCount.incrementAndGet());

        // Create the Demande
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDemandeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, demandeDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(demandeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Demande in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDemande() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        demande.setId(longCount.incrementAndGet());

        // Create the Demande
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDemandeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(demandeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Demande in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDemande() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        demande.setId(longCount.incrementAndGet());

        // Create the Demande
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDemandeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(demandeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Demande in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDemandeWithPatch() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the demande using partial update
        Demande partialUpdatedDemande = new Demande();
        partialUpdatedDemande.setId(demande.getId());

        partialUpdatedDemande.description(UPDATED_DESCRIPTION).latitude(UPDATED_LATITUDE);

        restDemandeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDemande.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDemande))
            )
            .andExpect(status().isOk());

        // Validate the Demande in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDemandeUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedDemande, demande), getPersistedDemande(demande));
    }

    @Test
    @Transactional
    void fullUpdateDemandeWithPatch() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the demande using partial update
        Demande partialUpdatedDemande = new Demande();
        partialUpdatedDemande.setId(demande.getId());

        partialUpdatedDemande
            .titre(UPDATED_TITRE)
            .description(UPDATED_DESCRIPTION)
            .statut(UPDATED_STATUT)
            .dateCreation(UPDATED_DATE_CREATION)
            .dateMiseAJour(UPDATED_DATE_MISE_A_JOUR)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .archivee(UPDATED_ARCHIVEE);

        restDemandeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDemande.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDemande))
            )
            .andExpect(status().isOk());

        // Validate the Demande in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDemandeUpdatableFieldsEquals(partialUpdatedDemande, getPersistedDemande(partialUpdatedDemande));
    }

    @Test
    @Transactional
    void patchNonExistingDemande() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        demande.setId(longCount.incrementAndGet());

        // Create the Demande
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDemandeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, demandeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(demandeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Demande in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDemande() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        demande.setId(longCount.incrementAndGet());

        // Create the Demande
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDemandeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(demandeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Demande in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDemande() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        demande.setId(longCount.incrementAndGet());

        // Create the Demande
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDemandeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(demandeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Demande in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDemande() throws Exception {
        // Initialize the database
        insertedDemande = demandeRepository.saveAndFlush(demande);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the demande
        restDemandeMockMvc
            .perform(delete(ENTITY_API_URL_ID, demande.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return demandeRepository.count();
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

    protected Demande getPersistedDemande(Demande demande) {
        return demandeRepository.findById(demande.getId()).orElseThrow();
    }

    protected void assertPersistedDemandeToMatchAllProperties(Demande expectedDemande) {
        assertDemandeAllPropertiesEquals(expectedDemande, getPersistedDemande(expectedDemande));
    }

    protected void assertPersistedDemandeToMatchUpdatableProperties(Demande expectedDemande) {
        assertDemandeAllUpdatablePropertiesEquals(expectedDemande, getPersistedDemande(expectedDemande));
    }
}
