package com.assistacrise.service.impl;

import com.assistacrise.domain.Crise;
import com.assistacrise.repository.CriseRepository;
import com.assistacrise.service.CriseService;
import com.assistacrise.service.dto.CriseDTO;
import com.assistacrise.service.mapper.CriseMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.assistacrise.domain.Crise}.
 */
@Service
@Transactional
public class CriseServiceImpl implements CriseService {

    private static final Logger LOG = LoggerFactory.getLogger(CriseServiceImpl.class);

    private final CriseRepository criseRepository;

    private final CriseMapper criseMapper;

    public CriseServiceImpl(CriseRepository criseRepository, CriseMapper criseMapper) {
        this.criseRepository = criseRepository;
        this.criseMapper = criseMapper;
    }

    @Override
    public CriseDTO save(CriseDTO criseDTO) {
        LOG.debug("Request to save Crise : {}", criseDTO);
        Crise crise = criseMapper.toEntity(criseDTO);
        crise = criseRepository.save(crise);
        return criseMapper.toDto(crise);
    }

    @Override
    public CriseDTO update(CriseDTO criseDTO) {
        LOG.debug("Request to update Crise : {}", criseDTO);
        Crise crise = criseMapper.toEntity(criseDTO);
        crise = criseRepository.save(crise);
        return criseMapper.toDto(crise);
    }

    @Override
    public Optional<CriseDTO> partialUpdate(CriseDTO criseDTO) {
        LOG.debug("Request to partially update Crise : {}", criseDTO);

        return criseRepository
            .findById(criseDTO.getId())
            .map(existingCrise -> {
                criseMapper.partialUpdate(existingCrise, criseDTO);

                return existingCrise;
            })
            .map(criseRepository::save)
            .map(criseMapper::toDto);
    }

    public Page<CriseDTO> findAllWithEagerRelationships(Pageable pageable) {
        return criseRepository.findAllWithEagerRelationships(pageable).map(criseMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CriseDTO> findOne(Long id) {
        LOG.debug("Request to get Crise : {}", id);
        return criseRepository.findOneWithEagerRelationships(id).map(criseMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Crise : {}", id);
        criseRepository.deleteById(id);
    }
}
