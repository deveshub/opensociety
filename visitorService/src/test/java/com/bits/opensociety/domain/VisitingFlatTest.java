package com.bits.opensociety.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.bits.opensociety.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VisitingFlatTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(VisitingFlat.class);
        VisitingFlat visitingFlat1 = new VisitingFlat();
        visitingFlat1.setId(1L);
        VisitingFlat visitingFlat2 = new VisitingFlat();
        visitingFlat2.setId(visitingFlat1.getId());
        assertThat(visitingFlat1).isEqualTo(visitingFlat2);
        visitingFlat2.setId(2L);
        assertThat(visitingFlat1).isNotEqualTo(visitingFlat2);
        visitingFlat1.setId(null);
        assertThat(visitingFlat1).isNotEqualTo(visitingFlat2);
    }
}
