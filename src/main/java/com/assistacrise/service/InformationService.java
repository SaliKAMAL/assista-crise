package com.assistacrise.service;

import com.assistacrise.service.dto.InformationDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.assistacrise.domain.Information}.
 */
public interface InformationService {
    /**
     * Save a information.
     *
     * @param informationDTO the entity to save.
     * @return the persisted entity.
     */
    InformationDTO save(InformationDTO informationDTO);

    /**
     * Updates a information.
     *
     * @param informationDTO the entity to update.
     * @return the persisted entity.
     */
    InformationDTO update(InformationDTO informationDTO);

    /**
     * Partially updates a information.
     *
     * @param informationDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<InformationDTO> partialUpdate(InformationDTO informationDTO);

    /**
     * Get all the informations with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<InformationDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" information.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<InformationDTO> findOne(Long id);

    /**
     * Delete the "id" information.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
