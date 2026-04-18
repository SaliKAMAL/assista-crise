package com.assistacrise.web.rest;

import com.assistacrise.repository.CriseRepository;
import com.assistacrise.service.CriseQueryService;
import com.assistacrise.service.CriseService;
import com.assistacrise.service.criteria.CriseCriteria;
import com.assistacrise.service.dto.CriseDTO;
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
 * REST controller for managing {@link com.assistacrise.domain.Crise}.
 */
@RestController
@RequestMapping("/api/crises")
public class CriseResource {

    private static final Logger LOG = LoggerFactory.getLogger(CriseResource.class);

    private static final String ENTITY_NAME = "crise";

    @Value("${jhipster.clientApp.name:assistaCrise}")
    private String applicationName;

    private final CriseService criseService;

    private final CriseRepository criseRepository;

    private final CriseQueryService criseQueryService;

    public CriseResource(CriseService criseService, CriseRepository criseRepository, CriseQueryService criseQueryService) {
        this.criseService = criseService;
        this.criseRepository = criseRepository;
        this.criseQueryService = criseQueryService;
    }

    /**
     * {@code POST  /crises} : Create a new crise.
     *
     * @param criseDTO the criseDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new criseDTO, or with status {@code 400 (Bad Request)} if the crise has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CriseDTO> createCrise(@Valid @RequestBody CriseDTO criseDTO) throws URISyntaxException {
        LOG.debug("REST request to save Crise : {}", criseDTO);
        if (criseDTO.getId() != null) {
            throw new BadRequestAlertException("A new crise cannot already have an ID", ENTITY_NAME, "idexists");
        }
        criseDTO = criseService.save(criseDTO);
        return ResponseEntity.created(new URI("/api/crises/" + criseDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, criseDTO.getId().toString()))
            .body(criseDTO);
    }

    /**
     * {@code PUT  /crises/:id} : Updates an existing crise.
     *
     * @param id the id of the criseDTO to save.
     * @param criseDTO the criseDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated criseDTO,
     * or with status {@code 400 (Bad Request)} if the criseDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the criseDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CriseDTO> updateCrise(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CriseDTO criseDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Crise : {}, {}", id, criseDTO);
        if (criseDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, criseDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!criseRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        criseDTO = criseService.update(criseDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, criseDTO.getId().toString()))
            .body(criseDTO);
    }

    /**
     * {@code PATCH  /crises/:id} : Partial updates given fields of an existing crise, field will ignore if it is null
     *
     * @param id the id of the criseDTO to save.
     * @param criseDTO the criseDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated criseDTO,
     * or with status {@code 400 (Bad Request)} if the criseDTO is not valid,
     * or with status {@code 404 (Not Found)} if the criseDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the criseDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CriseDTO> partialUpdateCrise(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CriseDTO criseDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Crise partially : {}, {}", id, criseDTO);
        if (criseDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, criseDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!criseRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CriseDTO> result = criseService.partialUpdate(criseDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, criseDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /crises} : get all the Crises.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Crises in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CriseDTO>> getAllCrises(
        CriseCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Crises by criteria: {}", criteria);

        Page<CriseDTO> page = criseQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /crises/count} : count all the crises.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countCrises(CriseCriteria criteria) {
        LOG.debug("REST request to count Crises by criteria: {}", criteria);
        return ResponseEntity.ok().body(criseQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /crises/:id} : get the "id" crise.
     *
     * @param id the id of the criseDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the criseDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CriseDTO> getCrise(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Crise : {}", id);
        Optional<CriseDTO> criseDTO = criseService.findOne(id);
        return ResponseUtil.wrapOrNotFound(criseDTO);
    }

    /**
     * {@code DELETE  /crises/:id} : delete the "id" crise.
     *
     * @param id the id of the criseDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCrise(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Crise : {}", id);
        criseService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
