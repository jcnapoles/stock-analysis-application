package com.invertimostuyyo.stockanalysis.repository;

import com.invertimostuyyo.stockanalysis.domain.Position;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Position entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PositionRepository extends ReactiveCrudRepository<Position, Long>, PositionRepositoryInternal {
    @Query("SELECT * FROM position entity WHERE entity.portfolio_id = :id")
    Flux<Position> findByPortfolio(Long id);

    @Query("SELECT * FROM position entity WHERE entity.portfolio_id IS NULL")
    Flux<Position> findAllWherePortfolioIsNull();

    @Override
    <S extends Position> Mono<S> save(S entity);

    @Override
    Flux<Position> findAll();

    @Override
    Mono<Position> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface PositionRepositoryInternal {
    <S extends Position> Mono<S> save(S entity);

    Flux<Position> findAllBy(Pageable pageable);

    Flux<Position> findAll();

    Mono<Position> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Position> findAllBy(Pageable pageable, Criteria criteria);

}
