package com.invertimostuyyo.stockanalysis.web.rest;

import com.invertimostuyyo.stockanalysis.domain.Stock;
import com.invertimostuyyo.stockanalysis.repository.StockRepository;
import com.invertimostuyyo.stockanalysis.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.invertimostuyyo.stockanalysis.domain.Stock}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class StockResource {

    private final Logger log = LoggerFactory.getLogger(StockResource.class);

    private static final String ENTITY_NAME = "stock";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StockRepository stockRepository;

    public StockResource(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    /**
     * {@code POST  /stocks} : Create a new stock.
     *
     * @param stock the stock to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stock, or with status {@code 400 (Bad Request)} if the stock has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/stocks")
    public Mono<ResponseEntity<Stock>> createStock(@Valid @RequestBody Stock stock) throws URISyntaxException {
        log.debug("REST request to save Stock : {}", stock);
        if (stock.getId() != null) {
            throw new BadRequestAlertException("A new stock cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return stockRepository
            .save(stock)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/stocks/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /stocks/:id} : Updates an existing stock.
     *
     * @param id the id of the stock to save.
     * @param stock the stock to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stock,
     * or with status {@code 400 (Bad Request)} if the stock is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stock couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/stocks/{id}")
    public Mono<ResponseEntity<Stock>> updateStock(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Stock stock
    ) throws URISyntaxException {
        log.debug("REST request to update Stock : {}, {}", id, stock);
        if (stock.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stock.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return stockRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return stockRepository
                    .save(stock)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /stocks/:id} : Partial updates given fields of an existing stock, field will ignore if it is null
     *
     * @param id the id of the stock to save.
     * @param stock the stock to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stock,
     * or with status {@code 400 (Bad Request)} if the stock is not valid,
     * or with status {@code 404 (Not Found)} if the stock is not found,
     * or with status {@code 500 (Internal Server Error)} if the stock couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/stocks/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Stock>> partialUpdateStock(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Stock stock
    ) throws URISyntaxException {
        log.debug("REST request to partial update Stock partially : {}, {}", id, stock);
        if (stock.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stock.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return stockRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Stock> result = stockRepository
                    .findById(stock.getId())
                    .map(existingStock -> {
                        if (stock.getName() != null) {
                            existingStock.setName(stock.getName());
                        }
                        if (stock.getSector() != null) {
                            existingStock.setSector(stock.getSector());
                        }
                        if (stock.getFundation() != null) {
                            existingStock.setFundation(stock.getFundation());
                        }
                        if (stock.getDescription() != null) {
                            existingStock.setDescription(stock.getDescription());
                        }
                        if (stock.getIcnome() != null) {
                            existingStock.setIcnome(stock.getIcnome());
                        }
                        if (stock.getExpenses() != null) {
                            existingStock.setExpenses(stock.getExpenses());
                        }
                        if (stock.getCapitalization() != null) {
                            existingStock.setCapitalization(stock.getCapitalization());
                        }
                        if (stock.getEmployees() != null) {
                            existingStock.setEmployees(stock.getEmployees());
                        }

                        return existingStock;
                    })
                    .flatMap(stockRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /stocks} : get all the stocks.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of stocks in body.
     */
    @GetMapping("/stocks")
    public Mono<List<Stock>> getAllStocks() {
        log.debug("REST request to get all Stocks");
        return stockRepository.findAll().collectList();
    }

    /**
     * {@code GET  /stocks} : get all the stocks as a stream.
     * @return the {@link Flux} of stocks.
     */
    @GetMapping(value = "/stocks", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Stock> getAllStocksAsStream() {
        log.debug("REST request to get all Stocks as a stream");
        return stockRepository.findAll();
    }

    /**
     * {@code GET  /stocks/:id} : get the "id" stock.
     *
     * @param id the id of the stock to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stock, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/stocks/{id}")
    public Mono<ResponseEntity<Stock>> getStock(@PathVariable Long id) {
        log.debug("REST request to get Stock : {}", id);
        Mono<Stock> stock = stockRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(stock);
    }

    /**
     * {@code DELETE  /stocks/:id} : delete the "id" stock.
     *
     * @param id the id of the stock to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/stocks/{id}")
    public Mono<ResponseEntity<Void>> deleteStock(@PathVariable Long id) {
        log.debug("REST request to delete Stock : {}", id);
        return stockRepository
            .deleteById(id)
            .then(
                Mono.just(
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
