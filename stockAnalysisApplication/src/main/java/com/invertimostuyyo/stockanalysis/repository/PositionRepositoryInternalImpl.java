package com.invertimostuyyo.stockanalysis.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.invertimostuyyo.stockanalysis.domain.Position;
import com.invertimostuyyo.stockanalysis.repository.rowmapper.PortfolioRowMapper;
import com.invertimostuyyo.stockanalysis.repository.rowmapper.PositionRowMapper;
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
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Position entity.
 */
@SuppressWarnings("unused")
class PositionRepositoryInternalImpl extends SimpleR2dbcRepository<Position, Long> implements PositionRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final PortfolioRowMapper portfolioMapper;
    private final PositionRowMapper positionMapper;

    private static final Table entityTable = Table.aliased("position", EntityManager.ENTITY_ALIAS);
    private static final Table portfolioTable = Table.aliased("portfolio", "portfolio");

    public PositionRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        PortfolioRowMapper portfolioMapper,
        PositionRowMapper positionMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Position.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.portfolioMapper = portfolioMapper;
        this.positionMapper = positionMapper;
    }

    @Override
    public Flux<Position> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Position> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = PositionSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(PortfolioSqlHelper.getColumns(portfolioTable, "portfolio"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(portfolioTable)
            .on(Column.create("portfolio_id", entityTable))
            .equals(Column.create("id", portfolioTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Position.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Position> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Position> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Position process(Row row, RowMetadata metadata) {
        Position entity = positionMapper.apply(row, "e");
        entity.setPortfolio(portfolioMapper.apply(row, "portfolio"));
        return entity;
    }

    @Override
    public <S extends Position> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
