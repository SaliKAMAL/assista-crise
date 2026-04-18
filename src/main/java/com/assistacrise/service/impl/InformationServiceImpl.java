package com.assistacrise.service.impl;

import com.assistacrise.domain.Information;
import com.assistacrise.repository.InformationRepository;
import com.assistacrise.service.InformationService;
import com.assistacrise.service.dto.InformationDTO;
import com.assistacrise.service.mapper.InformationMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.assistacrise.domain.Information}.
 */
@Service
@Transactional
public class InformationServiceImpl implements InformationService {

    private static final Logger LOG = LoggerFactory.getLogger(InformationServiceImpl.class);

    private final InformationRepository informationRepository;

    private final InformationMapper informationMapper;

    public InformationServiceImpl(InformationRepository informationRepository, InformationMapper informationMapper) {
        this.informationRepository = informationRepository;
        this.informationMapper = informationMapper;
    }

    @Override
    public InformationDTO save(InformationDTO informationDTO) {
        LOG.debug("Request to save Information : {}", informationDTO);
        Information information = informationMapper.toEntity(informationDTO);
        information = informationRepository.save(information);
        return informationMapper.toDto(information);
    }

    @Override
    public InformationDTO update(InformationDTO informationDTO) {
        LOG.debug("Request to update Information : {}", informationDTO);
        Information information = informationMapper.toEntity(informationDTO);
        information = informationRepository.save(information);
        return informationMapper.toDto(information);
    }

    @Override
    public Optional<InformationDTO> partialUpdate(InformationDTO informationDTO) {
        LOG.debug("Request to partially update Information : {}", informationDTO);

        return informationRepository
            .findById(informationDTO.getId())
            .map(existingInformation -> {
                informationMapper.partialUpdate(existingInformation, informationDTO);

                return existingInformation;
            })
            .map(informationRepository::save)
            .map(informationMapper::toDto);
    }

    public Page<InformationDTO> findAllWithEagerRelationships(Pageable pageable) {
        return informationRepository.findAllWithEagerRelationships(pageable).map(informationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InformationDTO> findOne(Long id) {
        LOG.debug("Request to get Information : {}", id);
        return informationRepository.findOneWithEagerRelationships(id).map(informationMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Information : {}", id);
        informationRepository.deleteById(id);
    }
}
