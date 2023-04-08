package com.invertimostuyyo.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.invertimostuyyo.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AnalysisTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Analysis.class);
        Analysis analysis1 = new Analysis();
        analysis1.setId(1L);
        Analysis analysis2 = new Analysis();
        analysis2.setId(analysis1.getId());
        assertThat(analysis1).isEqualTo(analysis2);
        analysis2.setId(2L);
        assertThat(analysis1).isNotEqualTo(analysis2);
        analysis1.setId(null);
        assertThat(analysis1).isNotEqualTo(analysis2);
    }
}
