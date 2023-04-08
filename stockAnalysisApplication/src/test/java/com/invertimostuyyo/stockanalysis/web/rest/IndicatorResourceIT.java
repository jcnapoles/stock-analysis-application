package com.invertimostuyyo.stockanalysis.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.invertimostuyyo.stockanalysis.IntegrationTest;
import com.invertimostuyyo.stockanalysis.domain.Indicator;
import com.invertimostuyyo.stockanalysis.repository.EntityManager;
import com.invertimostuyyo.stockanalysis.repository.IndicatorRepository;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link IndicatorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
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
    private WebTestClient webTestClient;

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

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Indicator.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        indicator = createEntity(em);
    }

    @Test
    void createIndicator() throws Exception {
        int databaseSizeBeforeCreate = indicatorRepository.findAll().collectList().block().size();
        // Create the Indicator
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(indicator))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Indicator in the database
        List<Indicator> indicatorList = indicatorRepository.findAll().collectList().block();
        assertThat(indicatorList).hasSize(databaseSizeBeforeCreate + 1);
        Indicator testIndicator = indicatorList.get(indicatorList.size() - 1);
        assertThat(testIndicator.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testIndicator.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void createIndicatorWithExistingId() throws Exception {
        // Create the Indicator with an existing ID
        indicator.setId(1L);

        int databaseSizeBeforeCreate = indicatorRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(indicator))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Indicator in the database
        List<Indicator> indicatorList = indicatorRepository.findAll().collectList().block();
        assertThat(indicatorList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = indicatorRepository.findAll().collectList().block().size();
        // set the field null
        indicator.setName(null);

        // Create the Indicator, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(indicator))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Indicator> indicatorList = indicatorRepository.findAll().collectList().block();
        assertThat(indicatorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllIndicatorsAsStream() {
        // Initialize the database
        indicatorRepository.save(indicator).block();

        List<Indicator> indicatorList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Indicator.class)
            .getResponseBody()
            .filter(indicator::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(indicatorList).isNotNull();
        assertThat(indicatorList).hasSize(1);
        Indicator testIndicator = indicatorList.get(0);
        assertThat(testIndicator.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testIndicator.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void getAllIndicators() {
        // Initialize the database
        indicatorRepository.save(indicator).block();

        // Get all the indicatorList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(indicator.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));
    }

    @Test
    void getIndicator() {
        // Initialize the database
        indicatorRepository.save(indicator).block();

        // Get the indicator
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, indicator.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(indicator.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION));
    }

    @Test
    void getNonExistingIndicator() {
        // Get the indicator
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingIndicator() throws Exception {
        // Initialize the database
        indicatorRepository.save(indicator).block();

        int databaseSizeBeforeUpdate = indicatorRepository.findAll().collectList().block().size();

        // Update the indicator
        Indicator updatedIndicator = indicatorRepository.findById(indicator.getId()).block();
        updatedIndicator.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedIndicator.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedIndicator))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Indicator in the database
        List<Indicator> indicatorList = indicatorRepository.findAll().collectList().block();
        assertThat(indicatorList).hasSize(databaseSizeBeforeUpdate);
        Indicator testIndicator = indicatorList.get(indicatorList.size() - 1);
        assertThat(testIndicator.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testIndicator.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void putNonExistingIndicator() throws Exception {
        int databaseSizeBeforeUpdate = indicatorRepository.findAll().collectList().block().size();
        indicator.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, indicator.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(indicator))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Indicator in the database
        List<Indicator> indicatorList = indicatorRepository.findAll().collectList().block();
        assertThat(indicatorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchIndicator() throws Exception {
        int databaseSizeBeforeUpdate = indicatorRepository.findAll().collectList().block().size();
        indicator.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(indicator))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Indicator in the database
        List<Indicator> indicatorList = indicatorRepository.findAll().collectList().block();
        assertThat(indicatorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamIndicator() throws Exception {
        int databaseSizeBeforeUpdate = indicatorRepository.findAll().collectList().block().size();
        indicator.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(indicator))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Indicator in the database
        List<Indicator> indicatorList = indicatorRepository.findAll().collectList().block();
        assertThat(indicatorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateIndicatorWithPatch() throws Exception {
        // Initialize the database
        indicatorRepository.save(indicator).block();

        int databaseSizeBeforeUpdate = indicatorRepository.findAll().collectList().block().size();

        // Update the indicator using partial update
        Indicator partialUpdatedIndicator = new Indicator();
        partialUpdatedIndicator.setId(indicator.getId());

        partialUpdatedIndicator.name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedIndicator.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedIndicator))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Indicator in the database
        List<Indicator> indicatorList = indicatorRepository.findAll().collectList().block();
        assertThat(indicatorList).hasSize(databaseSizeBeforeUpdate);
        Indicator testIndicator = indicatorList.get(indicatorList.size() - 1);
        assertThat(testIndicator.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testIndicator.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void fullUpdateIndicatorWithPatch() throws Exception {
        // Initialize the database
        indicatorRepository.save(indicator).block();

        int databaseSizeBeforeUpdate = indicatorRepository.findAll().collectList().block().size();

        // Update the indicator using partial update
        Indicator partialUpdatedIndicator = new Indicator();
        partialUpdatedIndicator.setId(indicator.getId());

        partialUpdatedIndicator.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedIndicator.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedIndicator))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Indicator in the database
        List<Indicator> indicatorList = indicatorRepository.findAll().collectList().block();
        assertThat(indicatorList).hasSize(databaseSizeBeforeUpdate);
        Indicator testIndicator = indicatorList.get(indicatorList.size() - 1);
        assertThat(testIndicator.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testIndicator.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void patchNonExistingIndicator() throws Exception {
        int databaseSizeBeforeUpdate = indicatorRepository.findAll().collectList().block().size();
        indicator.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, indicator.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(indicator))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Indicator in the database
        List<Indicator> indicatorList = indicatorRepository.findAll().collectList().block();
        assertThat(indicatorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchIndicator() throws Exception {
        int databaseSizeBeforeUpdate = indicatorRepository.findAll().collectList().block().size();
        indicator.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(indicator))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Indicator in the database
        List<Indicator> indicatorList = indicatorRepository.findAll().collectList().block();
        assertThat(indicatorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamIndicator() throws Exception {
        int databaseSizeBeforeUpdate = indicatorRepository.findAll().collectList().block().size();
        indicator.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(indicator))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Indicator in the database
        List<Indicator> indicatorList = indicatorRepository.findAll().collectList().block();
        assertThat(indicatorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteIndicator() {
        // Initialize the database
        indicatorRepository.save(indicator).block();

        int databaseSizeBeforeDelete = indicatorRepository.findAll().collectList().block().size();

        // Delete the indicator
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, indicator.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Indicator> indicatorList = indicatorRepository.findAll().collectList().block();
        assertThat(indicatorList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
