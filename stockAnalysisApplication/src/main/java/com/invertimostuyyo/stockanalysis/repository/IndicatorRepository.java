package com.invertimostuyyo.stockanalysis.repository;

import com.invertimostuyyo.stockanalysis.domain.Indicator;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Indicator entity.
 */
@SuppressWarnings("unused")
@Repository
public interface IndicatorRepository extends ReactiveCrudRepository<Indicator, Long>, IndicatorRepositoryInternal {
    @Query("SELECT * FROM indicator entity WHERE entity.analysis_id = :id")
    Flux<Indicator> findByAnalysis(Long id);

    @Query("SELECT * FROM indicator entity WHERE entity.analysis_id IS NULL")
    Flux<Indicator> findAllWhereAnalysisIsNull();

    @Override
    <S extends Indicator> Mono<S> save(S entity);

    @Override
    Flux<Indicator> findAll();

    @Override
    Mono<Indicator> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface IndicatorRepositoryInternal {
    <S extends Indicator> Mono<S> save(S entity);

    Flux<Indicator> findAllBy(Pageable pageable);

    Flux<Indicator> findAll();

    Mono<Indicator> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Indicator> findAllBy(Pageable pageable, Criteria criteria);

}
