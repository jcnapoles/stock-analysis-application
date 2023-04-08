package com.invertimostuyyo.stockanalysis.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.invertimostuyyo.stockanalysis.IntegrationTest;
import com.invertimostuyyo.stockanalysis.domain.Position;
import com.invertimostuyyo.stockanalysis.repository.EntityManager;
import com.invertimostuyyo.stockanalysis.repository.PositionRepository;
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
 * Integration tests for the {@link PositionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PositionResourceIT {

    private static final Double DEFAULT_AMOUNT = 1D;
    private static final Double UPDATED_AMOUNT = 2D;

    private static final Double DEFAULT_PRICE = 1D;
    private static final Double UPDATED_PRICE = 2D;

    private static final String ENTITY_API_URL = "/api/positions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Position position;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Position createEntity(EntityManager em) {
        Position position = new Position().amount(DEFAULT_AMOUNT).price(DEFAULT_PRICE);
        return position;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Position createUpdatedEntity(EntityManager em) {
        Position position = new Position().amount(UPDATED_AMOUNT).price(UPDATED_PRICE);
        return position;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Position.class).block();
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
        position = createEntity(em);
    }

    @Test
    void createPosition() throws Exception {
        int databaseSizeBeforeCreate = positionRepository.findAll().collectList().block().size();
        // Create the Position
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(position))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Position in the database
        List<Position> positionList = positionRepository.findAll().collectList().block();
        assertThat(positionList).hasSize(databaseSizeBeforeCreate + 1);
        Position testPosition = positionList.get(positionList.size() - 1);
        assertThat(testPosition.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testPosition.getPrice()).isEqualTo(DEFAULT_PRICE);
    }

    @Test
    void createPositionWithExistingId() throws Exception {
        // Create the Position with an existing ID
        position.setId(1L);

        int databaseSizeBeforeCreate = positionRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(position))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Position in the database
        List<Position> positionList = positionRepository.findAll().collectList().block();
        assertThat(positionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = positionRepository.findAll().collectList().block().size();
        // set the field null
        position.setAmount(null);

        // Create the Position, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(position))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Position> positionList = positionRepository.findAll().collectList().block();
        assertThat(positionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = positionRepository.findAll().collectList().block().size();
        // set the field null
        position.setPrice(null);

        // Create the Position, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(position))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Position> positionList = positionRepository.findAll().collectList().block();
        assertThat(positionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllPositionsAsStream() {
        // Initialize the database
        positionRepository.save(position).block();

        List<Position> positionList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Position.class)
            .getResponseBody()
            .filter(position::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(positionList).isNotNull();
        assertThat(positionList).hasSize(1);
        Position testPosition = positionList.get(0);
        assertThat(testPosition.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testPosition.getPrice()).isEqualTo(DEFAULT_PRICE);
    }

    @Test
    void getAllPositions() {
        // Initialize the database
        positionRepository.save(position).block();

        // Get all the positionList
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
            .value(hasItem(position.getId().intValue()))
            .jsonPath("$.[*].amount")
            .value(hasItem(DEFAULT_AMOUNT.doubleValue()))
            .jsonPath("$.[*].price")
            .value(hasItem(DEFAULT_PRICE.doubleValue()));
    }

    @Test
    void getPosition() {
        // Initialize the database
        positionRepository.save(position).block();

        // Get the position
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, position.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(position.getId().intValue()))
            .jsonPath("$.amount")
            .value(is(DEFAULT_AMOUNT.doubleValue()))
            .jsonPath("$.price")
            .value(is(DEFAULT_PRICE.doubleValue()));
    }

    @Test
    void getNonExistingPosition() {
        // Get the position
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingPosition() throws Exception {
        // Initialize the database
        positionRepository.save(position).block();

        int databaseSizeBeforeUpdate = positionRepository.findAll().collectList().block().size();

        // Update the position
        Position updatedPosition = positionRepository.findById(position.getId()).block();
        updatedPosition.amount(UPDATED_AMOUNT).price(UPDATED_PRICE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedPosition.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedPosition))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Position in the database
        List<Position> positionList = positionRepository.findAll().collectList().block();
        assertThat(positionList).hasSize(databaseSizeBeforeUpdate);
        Position testPosition = positionList.get(positionList.size() - 1);
        assertThat(testPosition.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testPosition.getPrice()).isEqualTo(UPDATED_PRICE);
    }

    @Test
    void putNonExistingPosition() throws Exception {
        int databaseSizeBeforeUpdate = positionRepository.findAll().collectList().block().size();
        position.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, position.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(position))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Position in the database
        List<Position> positionList = positionRepository.findAll().collectList().block();
        assertThat(positionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPosition() throws Exception {
        int databaseSizeBeforeUpdate = positionRepository.findAll().collectList().block().size();
        position.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(position))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Position in the database
        List<Position> positionList = positionRepository.findAll().collectList().block();
        assertThat(positionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPosition() throws Exception {
        int databaseSizeBeforeUpdate = positionRepository.findAll().collectList().block().size();
        position.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(position))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Position in the database
        List<Position> positionList = positionRepository.findAll().collectList().block();
        assertThat(positionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePositionWithPatch() throws Exception {
        // Initialize the database
        positionRepository.save(position).block();

        int databaseSizeBeforeUpdate = positionRepository.findAll().collectList().block().size();

        // Update the position using partial update
        Position partialUpdatedPosition = new Position();
        partialUpdatedPosition.setId(position.getId());

        partialUpdatedPosition.price(UPDATED_PRICE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPosition.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPosition))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Position in the database
        List<Position> positionList = positionRepository.findAll().collectList().block();
        assertThat(positionList).hasSize(databaseSizeBeforeUpdate);
        Position testPosition = positionList.get(positionList.size() - 1);
        assertThat(testPosition.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testPosition.getPrice()).isEqualTo(UPDATED_PRICE);
    }

    @Test
    void fullUpdatePositionWithPatch() throws Exception {
        // Initialize the database
        positionRepository.save(position).block();

        int databaseSizeBeforeUpdate = positionRepository.findAll().collectList().block().size();

        // Update the position using partial update
        Position partialUpdatedPosition = new Position();
        partialUpdatedPosition.setId(position.getId());

        partialUpdatedPosition.amount(UPDATED_AMOUNT).price(UPDATED_PRICE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPosition.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPosition))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Position in the database
        List<Position> positionList = positionRepository.findAll().collectList().block();
        assertThat(positionList).hasSize(databaseSizeBeforeUpdate);
        Position testPosition = positionList.get(positionList.size() - 1);
        assertThat(testPosition.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testPosition.getPrice()).isEqualTo(UPDATED_PRICE);
    }

    @Test
    void patchNonExistingPosition() throws Exception {
        int databaseSizeBeforeUpdate = positionRepository.findAll().collectList().block().size();
        position.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, position.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(position))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Position in the database
        List<Position> positionList = positionRepository.findAll().collectList().block();
        assertThat(positionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPosition() throws Exception {
        int databaseSizeBeforeUpdate = positionRepository.findAll().collectList().block().size();
        position.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(position))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Position in the database
        List<Position> positionList = positionRepository.findAll().collectList().block();
        assertThat(positionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPosition() throws Exception {
        int databaseSizeBeforeUpdate = positionRepository.findAll().collectList().block().size();
        position.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(position))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Position in the database
        List<Position> positionList = positionRepository.findAll().collectList().block();
        assertThat(positionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePosition() {
        // Initialize the database
        positionRepository.save(position).block();

        int databaseSizeBeforeDelete = positionRepository.findAll().collectList().block().size();

        // Delete the position
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, position.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Position> positionList = positionRepository.findAll().collectList().block();
        assertThat(positionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
