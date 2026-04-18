package com.assistacrise.service.mapper;

import static com.assistacrise.domain.InformationAsserts.*;
import static com.assistacrise.domain.InformationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InformationMapperTest {

    private InformationMapper informationMapper;

    @BeforeEach
    void setUp() {
        informationMapper = new InformationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getInformationSample1();
        var actual = informationMapper.toEntity(informationMapper.toDto(expected));
        assertInformationAllPropertiesEquals(expected, actual);
    }
}
