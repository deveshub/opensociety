package com.bits.opensociety.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.bits.opensociety.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FlatTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Flat.class);
        Flat flat1 = new Flat();
        flat1.setId(1L);
        Flat flat2 = new Flat();
        flat2.setId(flat1.getId());
        assertThat(flat1).isEqualTo(flat2);
        flat2.setId(2L);
        assertThat(flat1).isNotEqualTo(flat2);
        flat1.setId(null);
        assertThat(flat1).isNotEqualTo(flat2);
    }
}
