package com.assistacrise.service;

import com.assistacrise.domain.*; // for static metamodels
import com.assistacrise.domain.Crise;
import com.assistacrise.repository.CriseRepository;
import com.assistacrise.service.criteria.CriseCriteria;
import com.assistacrise.service.dto.CriseDTO;
import com.assistacrise.service.mapper.CriseMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Crise} entities in the database.
 * The main input is a {@link CriseCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CriseDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CriseQueryService extends QueryService<Crise> {

    private static final Logger LOG = LoggerFactory.getLogger(CriseQueryService.class);

    private final CriseRepository criseRepository;

    private final CriseMapper criseMapper;

    public CriseQueryService(CriseRepository criseRepository, CriseMapper criseMapper) {
        this.criseRepository = criseRepository;
        this.criseMapper = criseMapper;
    }

    /**
     * Return a {@link Page} of {@link CriseDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CriseDTO> findByCriteria(CriseCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Crise> specification = createSpecification(criteria);
        return criseRepository.findAll(specification, page).map(criseMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CriseCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Crise> specification = createSpecification(criteria);
        return criseRepository.count(specification);
    }

    /**
     * Function to convert {@link CriseCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Crise> createSpecification(CriseCriteria criteria) {
        Specification<Crise> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), Crise_.id),
                buildStringSpecification(criteria.getTitre(), Crise_.titre),
                buildStringSpecification(criteria.getDescription(), Crise_.description),
                buildSpecification(criteria.getType(), Crise_.type),
                buildRangeSpecification(criteria.getDateDebut(), Crise_.dateDebut),
                buildRangeSpecification(criteria.getDateFermeture(), Crise_.dateFermeture),
                buildSpecification(criteria.getStatut(), Crise_.statut),
                buildRangeSpecification(criteria.getLatitude(), Crise_.latitude),
                buildRangeSpecification(criteria.getLongitude(), Crise_.longitude),
                buildSpecification(criteria.getDeclarantId(), root -> root.join(Crise_.declarant, JoinType.LEFT).get(User_.id))
            );
        }
        return specification;
    }
}
