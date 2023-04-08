package com.invertimostuyyo.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.invertimostuyyo.IntegrationTest;
import com.invertimostuyyo.domain.Stock;
import com.invertimostuyyo.repository.StockRepository;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link StockResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
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
    private MockMvc restStockMockMvc;

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

    @BeforeEach
    public void initTest() {
        stock = createEntity(em);
    }

    @Test
    @Transactional
    void createStock() throws Exception {
        int databaseSizeBeforeCreate = stockRepository.findAll().size();
        // Create the Stock
        restStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stock)))
            .andExpect(status().isCreated());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
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
    @Transactional
    void createStockWithExistingId() throws Exception {
        // Create the Stock with an existing ID
        stock.setId(1L);

        int databaseSizeBeforeCreate = stockRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stock)))
            .andExpect(status().isBadRequest());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockRepository.findAll().size();
        // set the field null
        stock.setName(null);

        // Create the Stock, which fails.

        restStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stock)))
            .andExpect(status().isBadRequest());

        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSectorIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockRepository.findAll().size();
        // set the field null
        stock.setSector(null);

        // Create the Stock, which fails.

        restStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stock)))
            .andExpect(status().isBadRequest());

        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStocks() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stockList
        restStockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stock.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].sector").value(hasItem(DEFAULT_SECTOR)))
            .andExpect(jsonPath("$.[*].fundation").value(hasItem(DEFAULT_FUNDATION.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].icnome").value(hasItem(DEFAULT_ICNOME.doubleValue())))
            .andExpect(jsonPath("$.[*].expenses").value(hasItem(DEFAULT_EXPENSES.doubleValue())))
            .andExpect(jsonPath("$.[*].capitalization").value(hasItem(DEFAULT_CAPITALIZATION.doubleValue())))
            .andExpect(jsonPath("$.[*].employees").value(hasItem(DEFAULT_EMPLOYEES)));
    }

    @Test
    @Transactional
    void getStock() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get the stock
        restStockMockMvc
            .perform(get(ENTITY_API_URL_ID, stock.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(stock.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.sector").value(DEFAULT_SECTOR))
            .andExpect(jsonPath("$.fundation").value(DEFAULT_FUNDATION.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.icnome").value(DEFAULT_ICNOME.doubleValue()))
            .andExpect(jsonPath("$.expenses").value(DEFAULT_EXPENSES.doubleValue()))
            .andExpect(jsonPath("$.capitalization").value(DEFAULT_CAPITALIZATION.doubleValue()))
            .andExpect(jsonPath("$.employees").value(DEFAULT_EMPLOYEES));
    }

    @Test
    @Transactional
    void getNonExistingStock() throws Exception {
        // Get the stock
        restStockMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStock() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        int databaseSizeBeforeUpdate = stockRepository.findAll().size();

        // Update the stock
        Stock updatedStock = stockRepository.findById(stock.getId()).get();
        // Disconnect from session so that the updates on updatedStock are not directly saved in db
        em.detach(updatedStock);
        updatedStock
            .name(UPDATED_NAME)
            .sector(UPDATED_SECTOR)
            .fundation(UPDATED_FUNDATION)
            .description(UPDATED_DESCRIPTION)
            .icnome(UPDATED_ICNOME)
            .expenses(UPDATED_EXPENSES)
            .capitalization(UPDATED_CAPITALIZATION)
            .employees(UPDATED_EMPLOYEES);

        restStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedStock.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedStock))
            )
            .andExpect(status().isOk());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
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
    @Transactional
    void putNonExistingStock() throws Exception {
        int databaseSizeBeforeUpdate = stockRepository.findAll().size();
        stock.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stock.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stock))
            )
            .andExpect(status().isBadRequest());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStock() throws Exception {
        int databaseSizeBeforeUpdate = stockRepository.findAll().size();
        stock.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stock))
            )
            .andExpect(status().isBadRequest());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStock() throws Exception {
        int databaseSizeBeforeUpdate = stockRepository.findAll().size();
        stock.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stock)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStockWithPatch() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        int databaseSizeBeforeUpdate = stockRepository.findAll().size();

        // Update the stock using partial update
        Stock partialUpdatedStock = new Stock();
        partialUpdatedStock.setId(stock.getId());

        partialUpdatedStock.name(UPDATED_NAME).sector(UPDATED_SECTOR).capitalization(UPDATED_CAPITALIZATION);

        restStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStock.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStock))
            )
            .andExpect(status().isOk());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
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
    @Transactional
    void fullUpdateStockWithPatch() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        int databaseSizeBeforeUpdate = stockRepository.findAll().size();

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

        restStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStock.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStock))
            )
            .andExpect(status().isOk());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
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
    @Transactional
    void patchNonExistingStock() throws Exception {
        int databaseSizeBeforeUpdate = stockRepository.findAll().size();
        stock.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, stock.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stock))
            )
            .andExpect(status().isBadRequest());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStock() throws Exception {
        int databaseSizeBeforeUpdate = stockRepository.findAll().size();
        stock.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stock))
            )
            .andExpect(status().isBadRequest());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStock() throws Exception {
        int databaseSizeBeforeUpdate = stockRepository.findAll().size();
        stock.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(stock)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Stock in the database
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStock() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        int databaseSizeBeforeDelete = stockRepository.findAll().size();

        // Delete the stock
        restStockMockMvc
            .perform(delete(ENTITY_API_URL_ID, stock.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
