package com.assistacrise.service;

import com.assistacrise.domain.*; // for static metamodels
import com.assistacrise.domain.Demande;
import com.assistacrise.repository.DemandeRepository;
import com.assistacrise.service.criteria.DemandeCriteria;
import com.assistacrise.service.dto.DemandeDTO;
import com.assistacrise.service.mapper.DemandeMapper;
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
 * Service for executing complex queries for {@link Demande} entities in the database.
 * The main input is a {@link DemandeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link DemandeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DemandeQueryService extends QueryService<Demande> {

    private static final Logger LOG = LoggerFactory.getLogger(DemandeQueryService.class);

    private final DemandeRepository demandeRepository;

    private final DemandeMapper demandeMapper;

    public DemandeQueryService(DemandeRepository demandeRepository, DemandeMapper demandeMapper) {
        this.demandeRepository = demandeRepository;
        this.demandeMapper = demandeMapper;
    }

    /**
     * Return a {@link Page} of {@link DemandeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DemandeDTO> findByCriteria(DemandeCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Demande> specification = createSpecification(criteria);
        return demandeRepository.findAll(specification, page).map(demandeMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DemandeCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Demande> specification = createSpecification(criteria);
        return demandeRepository.count(specification);
    }

    /**
     * Function to convert {@link DemandeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Demande> createSpecification(DemandeCriteria criteria) {
        Specification<Demande> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), Demande_.id),
                buildStringSpecification(criteria.getTitre(), Demande_.titre),
                buildStringSpecification(criteria.getDescription(), Demande_.description),
                buildSpecification(criteria.getStatut(), Demande_.statut),
                buildRangeSpecification(criteria.getDateCreation(), Demande_.dateCreation),
                buildRangeSpecification(criteria.getDateMiseAJour(), Demande_.dateMiseAJour),
                buildRangeSpecification(criteria.getLatitude(), Demande_.latitude),
                buildRangeSpecification(criteria.getLongitude(), Demande_.longitude),
                buildSpecification(criteria.getArchivee(), Demande_.archivee),
                buildSpecification(criteria.getSinistreId(), root -> root.join(Demande_.sinistre, JoinType.LEFT).get(User_.id)),
                buildSpecification(criteria.getCriseId(), root -> root.join(Demande_.crise, JoinType.LEFT).get(Crise_.id)),
                buildSpecification(criteria.getOffreId(), root -> root.join(Demande_.offres, JoinType.LEFT).get(Offre_.id))
            );
        }
        return specification;
    }
}
