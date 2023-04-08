package com.invertimostuyyo.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.invertimostuyyo.IntegrationTest;
import com.invertimostuyyo.domain.Indicator;
import com.invertimostuyyo.repository.IndicatorRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link IndicatorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class IndicatorResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/indicators";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private IndicatorRepository indicatorRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restIndicatorMockMvc;

    private Indicator indicator;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Indicator createEntity(EntityManager em) {
        Indicator indicator = new Indicator().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
        return indicator;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Indicator createUpdatedEntity(EntityManager em) {
        Indicator indicator = new Indicator().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        return indicator;
    }

    @BeforeEach
    public void initTest() {
        indicator = createEntity(em);
    }

    @Test
    @Transactional
    void createIndicator() throws Exception {
        int databaseSizeBeforeCreate = indicatorRepository.findAll().size();
        // Create the Indicator
        restIndicatorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(indicator)))
            .andExpect(status().isCreated());

        // Validate the Indicator in the database
        List<Indicator> indicatorList = indicatorRepository.findAll();
        assertThat(indicatorList).hasSize(databaseSizeBeforeCreate + 1);
        Indicator testIndicator = indicatorList.get(indicatorList.size() - 1);
        assertThat(testIndicator.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testIndicator.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createIndicatorWithExistingId() throws Exception {
        // Create the Indicator with an existing ID
        indicator.setId(1L);

        int databaseSizeBeforeCreate = indicatorRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restIndicatorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(indicator)))
            .andExpect(status().isBadRequest());

        // Validate the Indicator in the database
        List<Indicator> indicatorList = indicatorRepository.findAll();
        assertThat(indicatorList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = indicatorRepository.findAll().size();
        // set the field null
        indicator.setName(null);

        // Create the Indicator, which fails.

        restIndicatorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(indicator)))
            .andExpect(status().isBadRequest());

        List<Indicator> indicatorList = indicatorRepository.findAll();
        assertThat(indicatorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllIndicators() throws Exception {
        // Initialize the database
        indicatorRepository.saveAndFlush(indicator);

        // Get all the indicatorList
        restIndicatorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(indicator.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getIndicator() throws Exception {
        // Initialize the database
        indicatorRepository.saveAndFlush(indicator);

        // Get the indicator
        restIndicatorMockMvc
            .perform(get(ENTITY_API_URL_ID, indicator.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(indicator.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingIndicator() throws Exception {
        // Get the indicator
        restIndicatorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingIndicator() throws Exception {
        // Initialize the database
        indicatorRepository.saveAndFlush(indicator);

        int databaseSizeBeforeUpdate = indicatorRepository.findAll().size();

        // Update the indicator
        Indicator updatedIndicator = indicatorRepository.findById(indicator.getId()).get();
        // Disconnect from session so that the updates on updatedIndicator are not directly saved in db
        em.detach(updatedIndicator);
        updatedIndicator.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restIndicatorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedIndicator.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedIndicator))
            )
            .andExpect(status().isOk());

        // Validate the Indicator in the database
        List<Indicator> indicatorList = indicatorRepository.findAll();
        assertThat(indicatorList).hasSize(databaseSizeBeforeUpdate);
        Indicator testIndicator = indicatorList.get(indicatorList.size() - 1);
        assertThat(testIndicator.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testIndicator.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingIndicator() throws Exception {
        int databaseSizeBeforeUpdate = indicatorRepository.findAll().size();
        indicator.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIndicatorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, indicator.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(indicator))
            )
            .andExpect(status().isBadRequest());

        // Validate the Indicator in the database
        List<Indicator> indicatorList = indicatorRepository.findAll();
        assertThat(indicatorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchIndicator() throws Exception {
        int databaseSizeBeforeUpdate = indicatorRepository.findAll().size();
        indicator.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIndicatorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(indicator))
            )
            .andExpect(status().isBadRequest());

        // Validate the Indicator in the database
        List<Indicator> indicatorList = indicatorRepository.findAll();
        assertThat(indicatorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamIndicator() throws Exception {
        int databaseSizeBeforeUpdate = indicatorRepository.findAll().size();
        indicator.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIndicatorMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(indicator)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Indicator in the database
        List<Indicator> indicatorList = indicatorRepository.findAll();
        assertThat(indicatorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateIndicatorWithPatch() throws Exception {
        // Initialize the database
        indicatorRepository.saveAndFlush(indicator);

        int databaseSizeBeforeUpdate = indicatorRepository.findAll().size();

        // Update the indicator using partial update
        Indicator partialUpdatedIndicator = new Indicator();
        partialUpdatedIndicator.setId(indicator.getId());

        partialUpdatedIndicator.name(UPDATED_NAME);

        restIndicatorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIndicator.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedIndicator))
            )
            .andExpect(status().isOk());

        // Validate the Indicator in the database
        List<Indicator> indicatorList = indicatorRepository.findAll();
        assertThat(indicatorList).hasSize(databaseSizeBeforeUpdate);
        Indicator testIndicator = indicatorList.get(indicatorList.size() - 1);
        assertThat(testIndicator.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testIndicator.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateIndicatorWithPatch() throws Exception {
        // Initialize the database
        indicatorRepository.saveAndFlush(indicator);

        int databaseSizeBeforeUpdate = indicatorRepository.findAll().size();

        // Update the indicator using partial update
        Indicator partialUpdatedIndicator = new Indicator();
        partialUpdatedIndicator.setId(indicator.getId());

        partialUpdatedIndicator.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restIndicatorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIndicator.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedIndicator))
            )
            .andExpect(status().isOk());

        // Validate the Indicator in the database
        List<Indicator> indicatorList = indicatorRepository.findAll();
        assertThat(indicatorList).hasSize(databaseSizeBeforeUpdate);
        Indicator testIndicator = indicatorList.get(indicatorList.size() - 1);
        assertThat(testIndicator.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testIndicator.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingIndicator() throws Exception {
        int databaseSizeBeforeUpdate = indicatorRepository.findAll().size();
        indicator.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIndicatorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, indicator.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(indicator))
            )
            .andExpect(status().isBadRequest());

        // Validate the Indicator in the database
        List<Indicator> indicatorList = indicatorRepository.findAll();
        assertThat(indicatorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchIndicator() throws Exception {
        int databaseSizeBeforeUpdate = indicatorRepository.findAll().size();
        indicator.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIndicatorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(indicator))
            )
            .andExpect(status().isBadRequest());

        // Validate the Indicator in the database
        List<Indicator> indicatorList = indicatorRepository.findAll();
        assertThat(indicatorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamIndicator() throws Exception {
        int databaseSizeBeforeUpdate = indicatorRepository.findAll().size();
        indicator.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIndicatorMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(indicator))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Indicator in the database
        List<Indicator> indicatorList = indicatorRepository.findAll();
        assertThat(indicatorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteIndicator() throws Exception {
        // Initialize the database
        indicatorRepository.saveAndFlush(indicator);

        int databaseSizeBeforeDelete = indicatorRepository.findAll().size();

        // Delete the indicator
        restIndicatorMockMvc
            .perform(delete(ENTITY_API_URL_ID, indicator.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Indicator> indicatorList = indicatorRepository.findAll();
        assertThat(indicatorList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
