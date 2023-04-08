package com.invertimostuyyo.web.rest;

import com.invertimostuyyo.domain.Analysis;
import com.invertimostuyyo.repository.AnalysisRepository;
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
 * REST controller for managing {@link com.invertimostuyyo.domain.Analysis}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AnalysisResource {

    private final Logger log = LoggerFactory.getLogger(AnalysisResource.class);

    private static final String ENTITY_NAME = "tradingAnalysisAnalysis";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AnalysisRepository analysisRepository;

    public AnalysisResource(AnalysisRepository analysisRepository) {
        this.analysisRepository = analysisRepository;
    }

    /**
     * {@code POST  /analyses} : Create a new analysis.
     *
     * @param analysis the analysis to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new analysis, or with status {@code 400 (Bad Request)} if the analysis has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/analyses")
    public ResponseEntity<Analysis> createAnalysis(@Valid @RequestBody Analysis analysis) throws URISyntaxException {
        log.debug("REST request to save Analysis : {}", analysis);
        if (analysis.getId() != null) {
            throw new BadRequestAlertException("A new analysis cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Analysis result = analysisRepository.save(analysis);
        return ResponseEntity
            .created(new URI("/api/analyses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /analyses/:id} : Updates an existing analysis.
     *
     * @param id the id of the analysis to save.
     * @param analysis the analysis to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated analysis,
     * or with status {@code 400 (Bad Request)} if the analysis is not valid,
     * or with status {@code 500 (Internal Server Error)} if the analysis couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/analyses/{id}")
    public ResponseEntity<Analysis> updateAnalysis(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Analysis analysis
    ) throws URISyntaxException {
        log.debug("REST request to update Analysis : {}, {}", id, analysis);
        if (analysis.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, analysis.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!analysisRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Analysis result = analysisRepository.save(analysis);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, analysis.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /analyses/:id} : Partial updates given fields of an existing analysis, field will ignore if it is null
     *
     * @param id the id of the analysis to save.
     * @param analysis the analysis to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated analysis,
     * or with status {@code 400 (Bad Request)} if the analysis is not valid,
     * or with status {@code 404 (Not Found)} if the analysis is not found,
     * or with status {@code 500 (Internal Server Error)} if the analysis couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/analyses/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Analysis> partialUpdateAnalysis(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Analysis analysis
    ) throws URISyntaxException {
        log.debug("REST request to partial update Analysis partially : {}, {}", id, analysis);
        if (analysis.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, analysis.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!analysisRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Analysis> result = analysisRepository
            .findById(analysis.getId())
            .map(existingAnalysis -> {
                if (analysis.getDate() != null) {
                    existingAnalysis.setDate(analysis.getDate());
                }
                if (analysis.getDescription() != null) {
                    existingAnalysis.setDescription(analysis.getDescription());
                }

                return existingAnalysis;
            })
            .map(analysisRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, analysis.getId().toString())
        );
    }

    /**
     * {@code GET  /analyses} : get all the analyses.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of analyses in body.
     */
    @GetMapping("/analyses")
    public List<Analysis> getAllAnalyses() {
        log.debug("REST request to get all Analyses");
        return analysisRepository.findAll();
    }

    /**
     * {@code GET  /analyses/:id} : get the "id" analysis.
     *
     * @param id the id of the analysis to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the analysis, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/analyses/{id}")
    public ResponseEntity<Analysis> getAnalysis(@PathVariable Long id) {
        log.debug("REST request to get Analysis : {}", id);
        Optional<Analysis> analysis = analysisRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(analysis);
    }

    /**
     * {@code DELETE  /analyses/:id} : delete the "id" analysis.
     *
     * @param id the id of the analysis to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/analyses/{id}")
    public ResponseEntity<Void> deleteAnalysis(@PathVariable Long id) {
        log.debug("REST request to delete Analysis : {}", id);
        analysisRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
