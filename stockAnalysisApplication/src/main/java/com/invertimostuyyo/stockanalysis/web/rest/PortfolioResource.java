package com.invertimostuyyo.stockanalysis.web.rest;

import com.invertimostuyyo.stockanalysis.domain.Portfolio;
import com.invertimostuyyo.stockanalysis.repository.PortfolioRepository;
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
 * REST controller for managing {@link com.invertimostuyyo.stockanalysis.domain.Portfolio}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class PortfolioResource {

    private final Logger log = LoggerFactory.getLogger(PortfolioResource.class);

    private static final String ENTITY_NAME = "portfolio";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PortfolioRepository portfolioRepository;

    public PortfolioResource(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    /**
     * {@code POST  /portfolios} : Create a new portfolio.
     *
     * @param portfolio the portfolio to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new portfolio, or with status {@code 400 (Bad Request)} if the portfolio has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/portfolios")
    public Mono<ResponseEntity<Portfolio>> createPortfolio(@Valid @RequestBody Portfolio portfolio) throws URISyntaxException {
        log.debug("REST request to save Portfolio : {}", portfolio);
        if (portfolio.getId() != null) {
            throw new BadRequestAlertException("A new portfolio cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return portfolioRepository
            .save(portfolio)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/portfolios/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /portfolios/:id} : Updates an existing portfolio.
     *
     * @param id the id of the portfolio to save.
     * @param portfolio the portfolio to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated portfolio,
     * or with status {@code 400 (Bad Request)} if the portfolio is not valid,
     * or with status {@code 500 (Internal Server Error)} if the portfolio couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/portfolios/{id}")
    public Mono<ResponseEntity<Portfolio>> updatePortfolio(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Portfolio portfolio
    ) throws URISyntaxException {
        log.debug("REST request to update Portfolio : {}, {}", id, portfolio);
        if (portfolio.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, portfolio.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return portfolioRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return portfolioRepository
                    .save(portfolio)
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
     * {@code PATCH  /portfolios/:id} : Partial updates given fields of an existing portfolio, field will ignore if it is null
     *
     * @param id the id of the portfolio to save.
     * @param portfolio the portfolio to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated portfolio,
     * or with status {@code 400 (Bad Request)} if the portfolio is not valid,
     * or with status {@code 404 (Not Found)} if the portfolio is not found,
     * or with status {@code 500 (Internal Server Error)} if the portfolio couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/portfolios/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Portfolio>> partialUpdatePortfolio(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Portfolio portfolio
    ) throws URISyntaxException {
        log.debug("REST request to partial update Portfolio partially : {}, {}", id, portfolio);
        if (portfolio.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, portfolio.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return portfolioRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Portfolio> result = portfolioRepository
                    .findById(portfolio.getId())
                    .map(existingPortfolio -> {
                        if (portfolio.getName() != null) {
                            existingPortfolio.setName(portfolio.getName());
                        }
                        if (portfolio.getDescription() != null) {
                            existingPortfolio.setDescription(portfolio.getDescription());
                        }

                        return existingPortfolio;
                    })
                    .flatMap(portfolioRepository::save);

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
     * {@code GET  /portfolios} : get all the portfolios.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of portfolios in body.
     */
    @GetMapping("/portfolios")
    public Mono<List<Portfolio>> getAllPortfolios() {
        log.debug("REST request to get all Portfolios");
        return portfolioRepository.findAll().collectList();
    }

    /**
     * {@code GET  /portfolios} : get all the portfolios as a stream.
     * @return the {@link Flux} of portfolios.
     */
    @GetMapping(value = "/portfolios", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Portfolio> getAllPortfoliosAsStream() {
        log.debug("REST request to get all Portfolios as a stream");
        return portfolioRepository.findAll();
    }

    /**
     * {@code GET  /portfolios/:id} : get the "id" portfolio.
     *
     * @param id the id of the portfolio to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the portfolio, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/portfolios/{id}")
    public Mono<ResponseEntity<Portfolio>> getPortfolio(@PathVariable Long id) {
        log.debug("REST request to get Portfolio : {}", id);
        Mono<Portfolio> portfolio = portfolioRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(portfolio);
    }

    /**
     * {@code DELETE  /portfolios/:id} : delete the "id" portfolio.
     *
     * @param id the id of the portfolio to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/portfolios/{id}")
    public Mono<ResponseEntity<Void>> deletePortfolio(@PathVariable Long id) {
        log.debug("REST request to delete Portfolio : {}", id);
        return portfolioRepository
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
