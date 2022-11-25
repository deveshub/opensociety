package com.demo.opensociety.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.demo.opensociety.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NoticeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Notice.class);
        Notice notice1 = new Notice();
        notice1.setId("id1");
        Notice notice2 = new Notice();
        notice2.setId(notice1.getId());
        assertThat(notice1).isEqualTo(notice2);
        notice2.setId("id2");
        assertThat(notice1).isNotEqualTo(notice2);
        notice1.setId(null);
        assertThat(notice1).isNotEqualTo(notice2);
    }
}
