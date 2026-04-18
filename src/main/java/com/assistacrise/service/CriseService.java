package com.assistacrise.service;

import com.assistacrise.service.dto.CriseDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.assistacrise.domain.Crise}.
 */
public interface CriseService {
    /**
     * Save a crise.
     *
     * @param criseDTO the entity to save.
     * @return the persisted entity.
     */
    CriseDTO save(CriseDTO criseDTO);

    /**
     * Updates a crise.
     *
     * @param criseDTO the entity to update.
     * @return the persisted entity.
     */
    CriseDTO update(CriseDTO criseDTO);

    /**
     * Partially updates a crise.
     *
     * @param criseDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CriseDTO> partialUpdate(CriseDTO criseDTO);

    /**
     * Get all the crises with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CriseDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" crise.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CriseDTO> findOne(Long id);

    /**
     * Delete the "id" crise.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
