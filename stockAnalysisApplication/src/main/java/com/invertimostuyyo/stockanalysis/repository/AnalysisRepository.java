package com.invertimostuyyo.stockanalysis.repository;

import com.invertimostuyyo.stockanalysis.domain.Analysis;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Analysis entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AnalysisRepository extends ReactiveCrudRepository<Analysis, Long>, AnalysisRepositoryInternal {
    @Query("SELECT * FROM analysis entity WHERE entity.stock_id = :id")
    Flux<Analysis> findByStock(Long id);

    @Query("SELECT * FROM analysis entity WHERE entity.stock_id IS NULL")
    Flux<Analysis> findAllWhereStockIsNull();

    @Override
    <S extends Analysis> Mono<S> save(S entity);

    @Override
    Flux<Analysis> findAll();

    @Override
    Mono<Analysis> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface AnalysisRepositoryInternal {
    <S extends Analysis> Mono<S> save(S entity);

    Flux<Analysis> findAllBy(Pageable pageable);

    Flux<Analysis> findAll();

    Mono<Analysis> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Analysis> findAllBy(Pageable pageable, Criteria criteria);

}
