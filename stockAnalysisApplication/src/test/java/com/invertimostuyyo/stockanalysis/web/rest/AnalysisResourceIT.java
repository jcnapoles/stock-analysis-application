package com.invertimostuyyo.stockanalysis.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.invertimostuyyo.stockanalysis.IntegrationTest;
import com.invertimostuyyo.stockanalysis.domain.Analysis;
import com.invertimostuyyo.stockanalysis.repository.AnalysisRepository;
import com.invertimostuyyo.stockanalysis.repository.EntityManager;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link AnalysisResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AnalysisResourceIT {

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/analyses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AnalysisRepository analysisRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Analysis analysis;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Analysis createEntity(EntityManager em) {
        Analysis analysis = new Analysis().date(DEFAULT_DATE).description(DEFAULT_DESCRIPTION);
        return analysis;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Analysis createUpdatedEntity(EntityManager em) {
        Analysis analysis = new Analysis().date(UPDATED_DATE).description(UPDATED_DESCRIPTION);
        return analysis;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Analysis.class).block();
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
        analysis = createEntity(em);
    }

    @Test
    void createAnalysis() throws Exception {
        int databaseSizeBeforeCreate = analysisRepository.findAll().collectList().block().size();
        // Create the Analysis
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(analysis))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Analysis in the database
        List<Analysis> analysisList = analysisRepository.findAll().collectList().block();
        assertThat(analysisList).hasSize(databaseSizeBeforeCreate + 1);
        Analysis testAnalysis = analysisList.get(analysisList.size() - 1);
        assertThat(testAnalysis.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testAnalysis.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void createAnalysisWithExistingId() throws Exception {
        // Create the Analysis with an existing ID
        analysis.setId(1L);

        int databaseSizeBeforeCreate = analysisRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(analysis))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Analysis in the database
        List<Analysis> analysisList = analysisRepository.findAll().collectList().block();
        assertThat(analysisList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = analysisRepository.findAll().collectList().block().size();
        // set the field null
        analysis.setDate(null);

        // Create the Analysis, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(analysis))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Analysis> analysisList = analysisRepository.findAll().collectList().block();
        assertThat(analysisList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllAnalysesAsStream() {
        // Initialize the database
        analysisRepository.save(analysis).block();

        List<Analysis> analysisList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Analysis.class)
            .getResponseBody()
            .filter(analysis::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(analysisList).isNotNull();
        assertThat(analysisList).hasSize(1);
        Analysis testAnalysis = analysisList.get(0);
        assertThat(testAnalysis.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testAnalysis.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void getAllAnalyses() {
        // Initialize the database
        analysisRepository.save(analysis).block();

        // Get all the analysisList
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
            .value(hasItem(analysis.getId().intValue()))
            .jsonPath("$.[*].date")
            .value(hasItem(DEFAULT_DATE.toString()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));
    }

    @Test
    void getAnalysis() {
        // Initialize the database
        analysisRepository.save(analysis).block();

        // Get the analysis
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, analysis.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(analysis.getId().intValue()))
            .jsonPath("$.date")
            .value(is(DEFAULT_DATE.toString()))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION));
    }

    @Test
    void getNonExistingAnalysis() {
        // Get the analysis
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingAnalysis() throws Exception {
        // Initialize the database
        analysisRepository.save(analysis).block();

        int databaseSizeBeforeUpdate = analysisRepository.findAll().collectList().block().size();

        // Update the analysis
        Analysis updatedAnalysis = analysisRepository.findById(analysis.getId()).block();
        updatedAnalysis.date(UPDATED_DATE).description(UPDATED_DESCRIPTION);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedAnalysis.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedAnalysis))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Analysis in the database
        List<Analysis> analysisList = analysisRepository.findAll().collectList().block();
        assertThat(analysisList).hasSize(databaseSizeBeforeUpdate);
        Analysis testAnalysis = analysisList.get(analysisList.size() - 1);
        assertThat(testAnalysis.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testAnalysis.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void putNonExistingAnalysis() throws Exception {
        int databaseSizeBeforeUpdate = analysisRepository.findAll().collectList().block().size();
        analysis.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, analysis.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(analysis))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Analysis in the database
        List<Analysis> analysisList = analysisRepository.findAll().collectList().block();
        assertThat(analysisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAnalysis() throws Exception {
        int databaseSizeBeforeUpdate = analysisRepository.findAll().collectList().block().size();
        analysis.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(analysis))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Analysis in the database
        List<Analysis> analysisList = analysisRepository.findAll().collectList().block();
        assertThat(analysisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAnalysis() throws Exception {
        int databaseSizeBeforeUpdate = analysisRepository.findAll().collectList().block().size();
        analysis.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(analysis))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Analysis in the database
        List<Analysis> analysisList = analysisRepository.findAll().collectList().block();
        assertThat(analysisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAnalysisWithPatch() throws Exception {
        // Initialize the database
        analysisRepository.save(analysis).block();

        int databaseSizeBeforeUpdate = analysisRepository.findAll().collectList().block().size();

        // Update the analysis using partial update
        Analysis partialUpdatedAnalysis = new Analysis();
        partialUpdatedAnalysis.setId(analysis.getId());

        partialUpdatedAnalysis.description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAnalysis.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAnalysis))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Analysis in the database
        List<Analysis> analysisList = analysisRepository.findAll().collectList().block();
        assertThat(analysisList).hasSize(databaseSizeBeforeUpdate);
        Analysis testAnalysis = analysisList.get(analysisList.size() - 1);
        assertThat(testAnalysis.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testAnalysis.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void fullUpdateAnalysisWithPatch() throws Exception {
        // Initialize the database
        analysisRepository.save(analysis).block();

        int databaseSizeBeforeUpdate = analysisRepository.findAll().collectList().block().size();

        // Update the analysis using partial update
        Analysis partialUpdatedAnalysis = new Analysis();
        partialUpdatedAnalysis.setId(analysis.getId());

        partialUpdatedAnalysis.date(UPDATED_DATE).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAnalysis.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAnalysis))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Analysis in the database
        List<Analysis> analysisList = analysisRepository.findAll().collectList().block();
        assertThat(analysisList).hasSize(databaseSizeBeforeUpdate);
        Analysis testAnalysis = analysisList.get(analysisList.size() - 1);
        assertThat(testAnalysis.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testAnalysis.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void patchNonExistingAnalysis() throws Exception {
        int databaseSizeBeforeUpdate = analysisRepository.findAll().collectList().block().size();
        analysis.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, analysis.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(analysis))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Analysis in the database
        List<Analysis> analysisList = analysisRepository.findAll().collectList().block();
        assertThat(analysisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAnalysis() throws Exception {
        int databaseSizeBeforeUpdate = analysisRepository.findAll().collectList().block().size();
        analysis.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(analysis))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Analysis in the database
        List<Analysis> analysisList = analysisRepository.findAll().collectList().block();
        assertThat(analysisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAnalysis() throws Exception {
        int databaseSizeBeforeUpdate = analysisRepository.findAll().collectList().block().size();
        analysis.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(analysis))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Analysis in the database
        List<Analysis> analysisList = analysisRepository.findAll().collectList().block();
        assertThat(analysisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAnalysis() {
        // Initialize the database
        analysisRepository.save(analysis).block();

        int databaseSizeBeforeDelete = analysisRepository.findAll().collectList().block().size();

        // Delete the analysis
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, analysis.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Analysis> analysisList = analysisRepository.findAll().collectList().block();
        assertThat(analysisList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
