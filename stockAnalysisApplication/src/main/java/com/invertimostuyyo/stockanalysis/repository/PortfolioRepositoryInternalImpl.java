package com.invertimostuyyo.stockanalysis.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.invertimostuyyo.stockanalysis.domain.Portfolio;
import com.invertimostuyyo.stockanalysis.repository.rowmapper.PortfolioRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Portfolio entity.
 */
@SuppressWarnings("unused")
class PortfolioRepositoryInternalImpl extends SimpleR2dbcRepository<Portfolio, Long> implements PortfolioRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final PortfolioRowMapper portfolioMapper;

    private static final Table entityTable = Table.aliased("portfolio", EntityManager.ENTITY_ALIAS);

    public PortfolioRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        PortfolioRowMapper portfolioMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Portfolio.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.portfolioMapper = portfolioMapper;
    }

    @Override
    public Flux<Portfolio> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Portfolio> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = PortfolioSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Portfolio.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Portfolio> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Portfolio> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Portfolio process(Row row, RowMetadata metadata) {
        Portfolio entity = portfolioMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends Portfolio> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
