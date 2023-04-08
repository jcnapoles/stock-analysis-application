package com.invertimostuyyo.web.rest;

import com.invertimostuyyo.domain.Indicator;
import com.invertimostuyyo.repository.IndicatorRepository;
import com.invertimostuyyo.web.rest.errors.BadRequestAlertException;
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
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.invertimostuyyo.domain.Indicator}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class IndicatorResource {

    private final Logger log = LoggerFactory.getLogger(IndicatorResource.class);

    private static final String ENTITY_NAME = "tradingAnalysisIndicator";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final IndicatorRepository indicatorRepository;

    public IndicatorResource(IndicatorRepository indicatorRepository) {
        this.indicatorRepository = indicatorRepository;
    }

    /**
     * {@code POST  /indicators} : Create a new indicator.
     *
     * @param indicator the indicator to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new indicator, or with status {@code 400 (Bad Request)} if the indicator has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/indicators")
    public ResponseEntity<Indicator> createIndicator(@Valid @RequestBody Indicator indicator) throws URISyntaxException {
        log.debug("REST request to save Indicator : {}", indicator);
        if (indicator.getId() != null) {
            throw new BadRequestAlertException("A new indicator cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Indicator result = indicatorRepository.save(indicator);
        return ResponseEntity
            .created(new URI("/api/indicators/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /indicators/:id} : Updates an existing indicator.
     *
     * @param id the id of the indicator to save.
     * @param indicator the indicator to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated indicator,
     * or with status {@code 400 (Bad Request)} if the indicator is not valid,
     * or with status {@code 500 (Internal Server Error)} if the indicator couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/indicators/{id}")
    public ResponseEntity<Indicator> updateIndicator(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Indicator indicator
    ) throws URISyntaxException {
        log.debug("REST request to update Indicator : {}, {}", id, indicator);
        if (indicator.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, indicator.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!indicatorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Indicator result = indicatorRepository.save(indicator);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, indicator.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /indicators/:id} : Partial updates given fields of an existing indicator, field will ignore if it is null
     *
     * @param id the id of the indicator to save.
     * @param indicator the indicator to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated indicator,
     * or with status {@code 400 (Bad Request)} if the indicator is not valid,
     * or with status {@code 404 (Not Found)} if the indicator is not found,
     * or with status {@code 500 (Internal Server Error)} if the indicator couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/indicators/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Indicator> partialUpdateIndicator(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Indicator indicator
    ) throws URISyntaxException {
        log.debug("REST request to partial update Indicator partially : {}, {}", id, indicator);
        if (indicator.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, indicator.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!indicatorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Indicator> result = indicatorRepository
            .findById(indicator.getId())
            .map(existingIndicator -> {
                if (indicator.getName() != null) {
                    existingIndicator.setName(indicator.getName());
                }
                if (indicator.getDescription() != null) {
                    existingIndicator.setDescription(indicator.getDescription());
                }

                return existingIndicator;
            })
            .map(indicatorRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, indicator.getId().toString())
        );
    }

    /**
     * {@code GET  /indicators} : get all the indicators.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of indicators in body.
     */
    @GetMapping("/indicators")
    public List<Indicator> getAllIndicators() {
        log.debug("REST request to get all Indicators");
        return indicatorRepository.findAll();
    }

    /**
     * {@code GET  /indicators/:id} : get the "id" indicator.
     *
     * @param id the id of the indicator to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the indicator, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/indicators/{id}")
    public ResponseEntity<Indicator> getIndicator(@PathVariable Long id) {
        log.debug("REST request to get Indicator : {}", id);
        Optional<Indicator> indicator = indicatorRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(indicator);
    }

    /**
     * {@code DELETE  /indicators/:id} : delete the "id" indicator.
     *
     * @param id the id of the indicator to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/indicators/{id}")
    public ResponseEntity<Void> deleteIndicator(@PathVariable Long id) {
        log.debug("REST request to delete Indicator : {}", id);
        indicatorRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
