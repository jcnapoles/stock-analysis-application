package com.invertimostuyyo.stockanalysis.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.invertimostuyyo.stockanalysis.IntegrationTest;
import com.invertimostuyyo.stockanalysis.domain.Stock;
import com.invertimostuyyo.stockanalysis.repository.EntityManager;
import com.invertimostuyyo.stockanalysis.repository.StockRepository;
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
 * Integration tests for the {@link StockResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class StockResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SECTOR = "AAAAAAAAAA";
    private static final String UPDATED_SECTOR = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_FUNDATION = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FUNDATION = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Double DEFAULT_ICNOME = 1D;
    private static final Double UPDATED_ICNOME = 2D;

    private static final Double DEFAULT_EXPENSES = 1D;
    private static final Double UPDATED_EXPENSES = 2D;

    private static final Double DEFAULT_CAPITALIZATION = 1D;
    private static final Double UPDATED_CAPITALIZATION = 2D;

    private static final Integer DEFAULT_EMPLOYEES = 1;
    private static final Integer UPDATED_EMPLOYEES = 2;

    private static final String ENTITY_API_URL = "/api/stocks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Stock stock;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Stock createEntity(EntityManager em) {
        Stock stock = new Stock()
            .name(DEFAULT_NAME)
            .sector(DEFAULT_SECTOR)
            .fundation(DEFAULT_FUNDATION)
            .description(DEFAULT_DESCRIPTION)
            .icnome(DEFAULT_ICNOME)
            .expenses(DEFAULT_EXPENSES)
            .capitalization(DEFAULT_CAPITALIZATION)
            .employees(DEFAULT_EMPLOYEES);
        return stock;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Stock createUpdatedEntity(EntityManager em) {
        Stock stock = new Stock()
            .name(UPDATED_NAME)
            .sector(UPDATED_SECTOR)
            .fundation(UPDATED_FUNDATION)
            .description(UPDATED_DESCRIPTION)
            .icnome(UPDATED_ICNOME)
            .expenses(UPDATED_EXPENSES)
            .capitalization(UPDATED_CAPITALIZATION)
            .employees(UPDATED_EMPLOYEES);
        return stock;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Stock.class).block();
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
        stock = createEntity(em);
    }

    @Test
    void createStock() throws Exception {
        int databaseSizeBeforeCreate = stockRepository.findAll().collectList().block().size();
        // Create the Stock
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(stock))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll().collectList().block();
        assertThat(stockList).hasSize(databaseSizeBeforeCreate + 1);
        Stock testStock = stockList.get(stockList.size() - 1);
        assertThat(testStock.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testStock.getSector()).isEqualTo(DEFAULT_SECTOR);
        assertThat(testStock.getFundation()).isEqualTo(DEFAULT_FUNDATION);
        assertThat(testStock.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testStock.getIcnome()).isEqualTo(DEFAULT_ICNOME);
        assertThat(testStock.getExpenses()).isEqualTo(DEFAULT_EXPENSES);
        assertThat(testStock.getCapitalization()).isEqualTo(DEFAULT_CAPITALIZATION);
        assertThat(testStock.getEmployees()).isEqualTo(DEFAULT_EMPLOYEES);
    }

    @Test
    void createStockWithExistingId() throws Exception {
        // Create the Stock with an existing ID
        stock.setId(1L);

        int databaseSizeBeforeCreate = stockRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(stock))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll().collectList().block();
        assertThat(stockList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockRepository.findAll().collectList().block().size();
        // set the field null
        stock.setName(null);

        // Create the Stock, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(stock))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Stock> stockList = stockRepository.findAll().collectList().block();
        assertThat(stockList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkSectorIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockRepository.findAll().collectList().block().size();
        // set the field null
        stock.setSector(null);

        // Create the Stock, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(stock))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Stock> stockList = stockRepository.findAll().collectList().block();
        assertThat(stockList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllStocksAsStream() {
        // Initialize the database
        stockRepository.save(stock).block();

        List<Stock> stockList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Stock.class)
            .getResponseBody()
            .filter(stock::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(stockList).isNotNull();
        assertThat(stockList).hasSize(1);
        Stock testStock = stockList.get(0);
        assertThat(testStock.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testStock.getSector()).isEqualTo(DEFAULT_SECTOR);
        assertThat(testStock.getFundation()).isEqualTo(DEFAULT_FUNDATION);
        assertThat(testStock.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testStock.getIcnome()).isEqualTo(DEFAULT_ICNOME);
        assertThat(testStock.getExpenses()).isEqualTo(DEFAULT_EXPENSES);
        assertThat(testStock.getCapitalization()).isEqualTo(DEFAULT_CAPITALIZATION);
        assertThat(testStock.getEmployees()).isEqualTo(DEFAULT_EMPLOYEES);
    }

    @Test
    void getAllStocks() {
        // Initialize the database
        stockRepository.save(stock).block();

        // Get all the stockList
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
            .value(hasItem(stock.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].sector")
            .value(hasItem(DEFAULT_SECTOR))
            .jsonPath("$.[*].fundation")
            .value(hasItem(DEFAULT_FUNDATION.toString()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].icnome")
            .value(hasItem(DEFAULT_ICNOME.doubleValue()))
            .jsonPath("$.[*].expenses")
            .value(hasItem(DEFAULT_EXPENSES.doubleValue()))
            .jsonPath("$.[*].capitalization")
            .value(hasItem(DEFAULT_CAPITALIZATION.doubleValue()))
            .jsonPath("$.[*].employees")
            .value(hasItem(DEFAULT_EMPLOYEES));
    }

    @Test
    void getStock() {
        // Initialize the database
        stockRepository.save(stock).block();

        // Get the stock
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, stock.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(stock.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.sector")
            .value(is(DEFAULT_SECTOR))
            .jsonPath("$.fundation")
            .value(is(DEFAULT_FUNDATION.toString()))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.icnome")
            .value(is(DEFAULT_ICNOME.doubleValue()))
            .jsonPath("$.expenses")
            .value(is(DEFAULT_EXPENSES.doubleValue()))
            .jsonPath("$.capitalization")
            .value(is(DEFAULT_CAPITALIZATION.doubleValue()))
            .jsonPath("$.employees")
            .value(is(DEFAULT_EMPLOYEES));
    }

    @Test
    void getNonExistingStock() {
        // Get the stock
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingStock() throws Exception {
        // Initialize the database
        stockRepository.save(stock).block();

        int databaseSizeBeforeUpdate = stockRepository.findAll().collectList().block().size();

        // Update the stock
        Stock updatedStock = stockRepository.findById(stock.getId()).block();
        updatedStock
            .name(UPDATED_NAME)
            .sector(UPDATED_SECTOR)
            .fundation(UPDATED_FUNDATION)
            .description(UPDATED_DESCRIPTION)
            .icnome(UPDATED_ICNOME)
            .expenses(UPDATED_EXPENSES)
            .capitalization(UPDATED_CAPITALIZATION)
            .employees(UPDATED_EMPLOYEES);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedStock.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedStock))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll().collectList().block();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
        Stock testStock = stockList.get(stockList.size() - 1);
        assertThat(testStock.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testStock.getSector()).isEqualTo(UPDATED_SECTOR);
        assertThat(testStock.getFundation()).isEqualTo(UPDATED_FUNDATION);
        assertThat(testStock.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testStock.getIcnome()).isEqualTo(UPDATED_ICNOME);
        assertThat(testStock.getExpenses()).isEqualTo(UPDATED_EXPENSES);
        assertThat(testStock.getCapitalization()).isEqualTo(UPDATED_CAPITALIZATION);
        assertThat(testStock.getEmployees()).isEqualTo(UPDATED_EMPLOYEES);
    }

    @Test
    void putNonExistingStock() throws Exception {
        int databaseSizeBeforeUpdate = stockRepository.findAll().collectList().block().size();
        stock.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, stock.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(stock))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll().collectList().block();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchStock() throws Exception {
        int databaseSizeBeforeUpdate = stockRepository.findAll().collectList().block().size();
        stock.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(stock))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll().collectList().block();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamStock() throws Exception {
        int databaseSizeBeforeUpdate = stockRepository.findAll().collectList().block().size();
        stock.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(stock))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll().collectList().block();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateStockWithPatch() throws Exception {
        // Initialize the database
        stockRepository.save(stock).block();

        int databaseSizeBeforeUpdate = stockRepository.findAll().collectList().block().size();

        // Update the stock using partial update
        Stock partialUpdatedStock = new Stock();
        partialUpdatedStock.setId(stock.getId());

        partialUpdatedStock.name(UPDATED_NAME).sector(UPDATED_SECTOR).capitalization(UPDATED_CAPITALIZATION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedStock.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedStock))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll().collectList().block();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
        Stock testStock = stockList.get(stockList.size() - 1);
        assertThat(testStock.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testStock.getSector()).isEqualTo(UPDATED_SECTOR);
        assertThat(testStock.getFundation()).isEqualTo(DEFAULT_FUNDATION);
        assertThat(testStock.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testStock.getIcnome()).isEqualTo(DEFAULT_ICNOME);
        assertThat(testStock.getExpenses()).isEqualTo(DEFAULT_EXPENSES);
        assertThat(testStock.getCapitalization()).isEqualTo(UPDATED_CAPITALIZATION);
        assertThat(testStock.getEmployees()).isEqualTo(DEFAULT_EMPLOYEES);
    }

    @Test
    void fullUpdateStockWithPatch() throws Exception {
        // Initialize the database
        stockRepository.save(stock).block();

        int databaseSizeBeforeUpdate = stockRepository.findAll().collectList().block().size();

        // Update the stock using partial update
        Stock partialUpdatedStock = new Stock();
        partialUpdatedStock.setId(stock.getId());

        partialUpdatedStock
            .name(UPDATED_NAME)
            .sector(UPDATED_SECTOR)
            .fundation(UPDATED_FUNDATION)
            .description(UPDATED_DESCRIPTION)
            .icnome(UPDATED_ICNOME)
            .expenses(UPDATED_EXPENSES)
            .capitalization(UPDATED_CAPITALIZATION)
            .employees(UPDATED_EMPLOYEES);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedStock.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedStock))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll().collectList().block();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
        Stock testStock = stockList.get(stockList.size() - 1);
        assertThat(testStock.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testStock.getSector()).isEqualTo(UPDATED_SECTOR);
        assertThat(testStock.getFundation()).isEqualTo(UPDATED_FUNDATION);
        assertThat(testStock.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testStock.getIcnome()).isEqualTo(UPDATED_ICNOME);
        assertThat(testStock.getExpenses()).isEqualTo(UPDATED_EXPENSES);
        assertThat(testStock.getCapitalization()).isEqualTo(UPDATED_CAPITALIZATION);
        assertThat(testStock.getEmployees()).isEqualTo(UPDATED_EMPLOYEES);
    }

    @Test
    void patchNonExistingStock() throws Exception {
        int databaseSizeBeforeUpdate = stockRepository.findAll().collectList().block().size();
        stock.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, stock.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(stock))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll().collectList().block();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchStock() throws Exception {
        int databaseSizeBeforeUpdate = stockRepository.findAll().collectList().block().size();
        stock.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(stock))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll().collectList().block();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamStock() throws Exception {
        int databaseSizeBeforeUpdate = stockRepository.findAll().collectList().block().size();
        stock.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(stock))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll().collectList().block();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteStock() {
        // Initialize the database
        stockRepository.save(stock).block();

        int databaseSizeBeforeDelete = stockRepository.findAll().collectList().block().size();

        // Delete the stock
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, stock.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Stock> stockList = stockRepository.findAll().collectList().block();
        assertThat(stockList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
