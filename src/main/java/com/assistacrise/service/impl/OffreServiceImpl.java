package com.assistacrise.service.impl;

import com.assistacrise.domain.Offre;
import com.assistacrise.repository.OffreRepository;
import com.assistacrise.service.OffreService;
import com.assistacrise.service.dto.OffreDTO;
import com.assistacrise.service.mapper.OffreMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.assistacrise.domain.Offre}.
 */
@Service
@Transactional
public class OffreServiceImpl implements OffreService {

    private static final Logger LOG = LoggerFactory.getLogger(OffreServiceImpl.class);

    private final OffreRepository offreRepository;

    private final OffreMapper offreMapper;

    public OffreServiceImpl(OffreRepository offreRepository, OffreMapper offreMapper) {
        this.offreRepository = offreRepository;
        this.offreMapper = offreMapper;
    }

    @Override
    public OffreDTO save(OffreDTO offreDTO) {
        LOG.debug("Request to save Offre : {}", offreDTO);
        Offre offre = offreMapper.toEntity(offreDTO);
        offre = offreRepository.save(offre);
        return offreMapper.toDto(offre);
    }

    @Override
    public OffreDTO update(OffreDTO offreDTO) {
        LOG.debug("Request to update Offre : {}", offreDTO);
        Offre offre = offreMapper.toEntity(offreDTO);
        offre = offreRepository.save(offre);
        return offreMapper.toDto(offre);
    }

    @Override
    public Optional<OffreDTO> partialUpdate(OffreDTO offreDTO) {
        LOG.debug("Request to partially update Offre : {}", offreDTO);

        return offreRepository
            .findById(offreDTO.getId())
            .map(existingOffre -> {
                offreMapper.partialUpdate(existingOffre, offreDTO);

                return existingOffre;
            })
            .map(offreRepository::save)
            .map(offreMapper::toDto);
    }

    public Page<OffreDTO> findAllWithEagerRelationships(Pageable pageable) {
        return offreRepository.findAllWithEagerRelationships(pageable).map(offreMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OffreDTO> findOne(Long id) {
        LOG.debug("Request to get Offre : {}", id);
        return offreRepository.findOneWithEagerRelationships(id).map(offreMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Offre : {}", id);
        offreRepository.deleteById(id);
    }
}
