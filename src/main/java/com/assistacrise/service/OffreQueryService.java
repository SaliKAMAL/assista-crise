package com.assistacrise.service;

import com.assistacrise.domain.*; // for static metamodels
import com.assistacrise.domain.Offre;
import com.assistacrise.repository.OffreRepository;
import com.assistacrise.service.criteria.OffreCriteria;
import com.assistacrise.service.dto.OffreDTO;
import com.assistacrise.service.mapper.OffreMapper;
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
 * Service for executing complex queries for {@link Offre} entities in the database.
 * The main input is a {@link OffreCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link OffreDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OffreQueryService extends QueryService<Offre> {

    private static final Logger LOG = LoggerFactory.getLogger(OffreQueryService.class);

    private final OffreRepository offreRepository;

    private final OffreMapper offreMapper;

    public OffreQueryService(OffreRepository offreRepository, OffreMapper offreMapper) {
        this.offreRepository = offreRepository;
        this.offreMapper = offreMapper;
    }

    /**
     * Return a {@link Page} of {@link OffreDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<OffreDTO> findByCriteria(OffreCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Offre> specification = createSpecification(criteria);
        return offreRepository.fetchBagRelationships(offreRepository.findAll(specification, page)).map(offreMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(OffreCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Offre> specification = createSpecification(criteria);
        return offreRepository.count(specification);
    }

    /**
     * Function to convert {@link OffreCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Offre> createSpecification(OffreCriteria criteria) {
        Specification<Offre> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), Offre_.id),
                buildStringSpecification(criteria.getTitre(), Offre_.titre),
                buildStringSpecification(criteria.getDescription(), Offre_.description),
                buildRangeSpecification(criteria.getDateCreation(), Offre_.dateCreation),
                buildRangeSpecification(criteria.getDateMiseAJour(), Offre_.dateMiseAJour),
                buildRangeSpecification(criteria.getLatitude(), Offre_.latitude),
                buildRangeSpecification(criteria.getLongitude(), Offre_.longitude),
                buildSpecification(criteria.getArchivee(), Offre_.archivee),
                buildSpecification(criteria.getActive(), Offre_.active),
                buildSpecification(criteria.getCitoyenId(), root -> root.join(Offre_.citoyen, JoinType.LEFT).get(User_.id)),
                buildSpecification(criteria.getCriseId(), root -> root.join(Offre_.crise, JoinType.LEFT).get(Crise_.id)),
                buildSpecification(criteria.getDemandeId(), root -> root.join(Offre_.demandes, JoinType.LEFT).get(Demande_.id))
            );
        }
        return specification;
    }
}
