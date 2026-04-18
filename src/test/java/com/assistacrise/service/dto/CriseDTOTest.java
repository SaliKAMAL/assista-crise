package com.assistacrise.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.assistacrise.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CriseDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CriseDTO.class);
        CriseDTO criseDTO1 = new CriseDTO();
        criseDTO1.setId(1L);
        CriseDTO criseDTO2 = new CriseDTO();
        assertThat(criseDTO1).isNotEqualTo(criseDTO2);
        criseDTO2.setId(criseDTO1.getId());
        assertThat(criseDTO1).isEqualTo(criseDTO2);
        criseDTO2.setId(2L);
        assertThat(criseDTO1).isNotEqualTo(criseDTO2);
        criseDTO1.setId(null);
        assertThat(criseDTO1).isNotEqualTo(criseDTO2);
    }
}
