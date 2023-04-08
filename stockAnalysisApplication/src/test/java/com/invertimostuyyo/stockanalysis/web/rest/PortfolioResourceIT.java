package com.invertimostuyyo.stockanalysis.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.invertimostuyyo.stockanalysis.IntegrationTest;
import com.invertimostuyyo.stockanalysis.domain.Portfolio;
import com.invertimostuyyo.stockanalysis.repository.EntityManager;
import com.invertimostuyyo.stockanalysis.repository.PortfolioRepository;
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
 * Integration tests for the {@link PortfolioResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PortfolioResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/portfolios";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Portfolio portfolio;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Portfolio createEntity(EntityManager em) {
        Portfolio portfolio = new Portfolio().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
        return portfolio;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Portfolio createUpdatedEntity(EntityManager em) {
        Portfolio portfolio = new Portfolio().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        return portfolio;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Portfolio.class).block();
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
        portfolio = createEntity(em);
    }

    @Test
    void createPortfolio() throws Exception {
        int databaseSizeBeforeCreate = portfolioRepository.findAll().collectList().block().size();
        // Create the Portfolio
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(portfolio))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Portfolio in the database
        List<Portfolio> portfolioList = portfolioRepository.findAll().collectList().block();
        assertThat(portfolioList).hasSize(databaseSizeBeforeCreate + 1);
        Portfolio testPortfolio = portfolioList.get(portfolioList.size() - 1);
        assertThat(testPortfolio.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPortfolio.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void createPortfolioWithExistingId() throws Exception {
        // Create the Portfolio with an existing ID
        portfolio.setId(1L);

        int databaseSizeBeforeCreate = portfolioRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(portfolio))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Portfolio in the database
        List<Portfolio> portfolioList = portfolioRepository.findAll().collectList().block();
        assertThat(portfolioList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = portfolioRepository.findAll().collectList().block().size();
        // set the field null
        portfolio.setName(null);

        // Create the Portfolio, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(portfolio))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Portfolio> portfolioList = portfolioRepository.findAll().collectList().block();
        assertThat(portfolioList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllPortfoliosAsStream() {
        // Initialize the database
        portfolioRepository.save(portfolio).block();

        List<Portfolio> portfolioList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Portfolio.class)
            .getResponseBody()
            .filter(portfolio::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(portfolioList).isNotNull();
        assertThat(portfolioList).hasSize(1);
        Portfolio testPortfolio = portfolioList.get(0);
        assertThat(testPortfolio.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPortfolio.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void getAllPortfolios() {
        // Initialize the database
        portfolioRepository.save(portfolio).block();

        // Get all the portfolioList
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
            .value(hasItem(portfolio.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));
    }

    @Test
    void getPortfolio() {
        // Initialize the database
        portfolioRepository.save(portfolio).block();

        // Get the portfolio
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, portfolio.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(portfolio.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION));
    }

    @Test
    void getNonExistingPortfolio() {
        // Get the portfolio
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingPortfolio() throws Exception {
        // Initialize the database
        portfolioRepository.save(portfolio).block();

        int databaseSizeBeforeUpdate = portfolioRepository.findAll().collectList().block().size();

        // Update the portfolio
        Portfolio updatedPortfolio = portfolioRepository.findById(portfolio.getId()).block();
        updatedPortfolio.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedPortfolio.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedPortfolio))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Portfolio in the database
        List<Portfolio> portfolioList = portfolioRepository.findAll().collectList().block();
        assertThat(portfolioList).hasSize(databaseSizeBeforeUpdate);
        Portfolio testPortfolio = portfolioList.get(portfolioList.size() - 1);
        assertThat(testPortfolio.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPortfolio.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void putNonExistingPortfolio() throws Exception {
        int databaseSizeBeforeUpdate = portfolioRepository.findAll().collectList().block().size();
        portfolio.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, portfolio.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(portfolio))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Portfolio in the database
        List<Portfolio> portfolioList = portfolioRepository.findAll().collectList().block();
        assertThat(portfolioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPortfolio() throws Exception {
        int databaseSizeBeforeUpdate = portfolioRepository.findAll().collectList().block().size();
        portfolio.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(portfolio))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Portfolio in the database
        List<Portfolio> portfolioList = portfolioRepository.findAll().collectList().block();
        assertThat(portfolioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPortfolio() throws Exception {
        int databaseSizeBeforeUpdate = portfolioRepository.findAll().collectList().block().size();
        portfolio.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(portfolio))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Portfolio in the database
        List<Portfolio> portfolioList = portfolioRepository.findAll().collectList().block();
        assertThat(portfolioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePortfolioWithPatch() throws Exception {
        // Initialize the database
        portfolioRepository.save(portfolio).block();

        int databaseSizeBeforeUpdate = portfolioRepository.findAll().collectList().block().size();

        // Update the portfolio using partial update
        Portfolio partialUpdatedPortfolio = new Portfolio();
        partialUpdatedPortfolio.setId(portfolio.getId());

        partialUpdatedPortfolio.name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPortfolio.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPortfolio))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Portfolio in the database
        List<Portfolio> portfolioList = portfolioRepository.findAll().collectList().block();
        assertThat(portfolioList).hasSize(databaseSizeBeforeUpdate);
        Portfolio testPortfolio = portfolioList.get(portfolioList.size() - 1);
        assertThat(testPortfolio.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPortfolio.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void fullUpdatePortfolioWithPatch() throws Exception {
        // Initialize the database
        portfolioRepository.save(portfolio).block();

        int databaseSizeBeforeUpdate = portfolioRepository.findAll().collectList().block().size();

        // Update the portfolio using partial update
        Portfolio partialUpdatedPortfolio = new Portfolio();
        partialUpdatedPortfolio.setId(portfolio.getId());

        partialUpdatedPortfolio.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPortfolio.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPortfolio))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Portfolio in the database
        List<Portfolio> portfolioList = portfolioRepository.findAll().collectList().block();
        assertThat(portfolioList).hasSize(databaseSizeBeforeUpdate);
        Portfolio testPortfolio = portfolioList.get(portfolioList.size() - 1);
        assertThat(testPortfolio.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPortfolio.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void patchNonExistingPortfolio() throws Exception {
        int databaseSizeBeforeUpdate = portfolioRepository.findAll().collectList().block().size();
        portfolio.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, portfolio.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(portfolio))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Portfolio in the database
        List<Portfolio> portfolioList = portfolioRepository.findAll().collectList().block();
        assertThat(portfolioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPortfolio() throws Exception {
        int databaseSizeBeforeUpdate = portfolioRepository.findAll().collectList().block().size();
        portfolio.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(portfolio))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Portfolio in the database
        List<Portfolio> portfolioList = portfolioRepository.findAll().collectList().block();
        assertThat(portfolioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPortfolio() throws Exception {
        int databaseSizeBeforeUpdate = portfolioRepository.findAll().collectList().block().size();
        portfolio.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(portfolio))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Portfolio in the database
        List<Portfolio> portfolioList = portfolioRepository.findAll().collectList().block();
        assertThat(portfolioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePortfolio() {
        // Initialize the database
        portfolioRepository.save(portfolio).block();

        int databaseSizeBeforeDelete = portfolioRepository.findAll().collectList().block().size();

        // Delete the portfolio
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, portfolio.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Portfolio> portfolioList = portfolioRepository.findAll().collectList().block();
        assertThat(portfolioList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
