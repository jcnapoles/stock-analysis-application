package com.invertimostuyyo.stockanalysis.repository;

import com.invertimostuyyo.stockanalysis.domain.Portfolio;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Portfolio entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PortfolioRepository extends ReactiveCrudRepository<Portfolio, Long>, PortfolioRepositoryInternal {
    @Override
    <S extends Portfolio> Mono<S> save(S entity);

    @Override
    Flux<Portfolio> findAll();

    @Override
    Mono<Portfolio> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface PortfolioRepositoryInternal {
    <S extends Portfolio> Mono<S> save(S entity);

    Flux<Portfolio> findAllBy(Pageable pageable);

    Flux<Portfolio> findAll();

    Mono<Portfolio> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Portfolio> findAllBy(Pageable pageable, Criteria criteria);

}
