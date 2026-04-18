package com.assistacrise.web.rest;

import com.assistacrise.repository.InformationRepository;
import com.assistacrise.service.InformationQueryService;
import com.assistacrise.service.InformationService;
import com.assistacrise.service.criteria.InformationCriteria;
import com.assistacrise.service.dto.InformationDTO;
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
 * REST controller for managing {@link com.assistacrise.domain.Information}.
 */
@RestController
@RequestMapping("/api/information")
public class InformationResource {

    private static final Logger LOG = LoggerFactory.getLogger(InformationResource.class);

    private static final String ENTITY_NAME = "information";

    @Value("${jhipster.clientApp.name:assistaCrise}")
    private String applicationName;

    private final InformationService informationService;

    private final InformationRepository informationRepository;

    private final InformationQueryService informationQueryService;

    public InformationResource(
        InformationService informationService,
        InformationRepository informationRepository,
        InformationQueryService informationQueryService
    ) {
        this.informationService = informationService;
        this.informationRepository = informationRepository;
        this.informationQueryService = informationQueryService;
    }

    /**
     * {@code POST  /information} : Create a new information.
     *
     * @param informationDTO the informationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new informationDTO, or with status {@code 400 (Bad Request)} if the information has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<InformationDTO> createInformation(@Valid @RequestBody InformationDTO informationDTO) throws URISyntaxException {
        LOG.debug("REST request to save Information : {}", informationDTO);
        if (informationDTO.getId() != null) {
            throw new BadRequestAlertException("A new information cannot already have an ID", ENTITY_NAME, "idexists");
        }
        informationDTO = informationService.save(informationDTO);
        return ResponseEntity.created(new URI("/api/information/" + informationDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, informationDTO.getId().toString()))
            .body(informationDTO);
    }

    /**
     * {@code PUT  /information/:id} : Updates an existing information.
     *
     * @param id the id of the informationDTO to save.
     * @param informationDTO the informationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated informationDTO,
     * or with status {@code 400 (Bad Request)} if the informationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the informationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<InformationDTO> updateInformation(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody InformationDTO informationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Information : {}, {}", id, informationDTO);
        if (informationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, informationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!informationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        informationDTO = informationService.update(informationDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, informationDTO.getId().toString()))
            .body(informationDTO);
    }

    /**
     * {@code PATCH  /information/:id} : Partial updates given fields of an existing information, field will ignore if it is null
     *
     * @param id the id of the informationDTO to save.
     * @param informationDTO the informationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated informationDTO,
     * or with status {@code 400 (Bad Request)} if the informationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the informationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the informationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<InformationDTO> partialUpdateInformation(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody InformationDTO informationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Information partially : {}, {}", id, informationDTO);
        if (informationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, informationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!informationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<InformationDTO> result = informationService.partialUpdate(informationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, informationDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /information} : get all the Information.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Information in body.
     */
    @GetMapping("")
    public ResponseEntity<List<InformationDTO>> getAllInformations(
        InformationCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Informations by criteria: {}", criteria);

        Page<InformationDTO> page = informationQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /information/count} : count all the informations.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countInformations(InformationCriteria criteria) {
        LOG.debug("REST request to count Informations by criteria: {}", criteria);
        return ResponseEntity.ok().body(informationQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /information/:id} : get the "id" information.
     *
     * @param id the id of the informationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the informationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<InformationDTO> getInformation(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Information : {}", id);
        Optional<InformationDTO> informationDTO = informationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(informationDTO);
    }

    /**
     * {@code DELETE  /information/:id} : delete the "id" information.
     *
     * @param id the id of the informationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInformation(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Information : {}", id);
        informationService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
