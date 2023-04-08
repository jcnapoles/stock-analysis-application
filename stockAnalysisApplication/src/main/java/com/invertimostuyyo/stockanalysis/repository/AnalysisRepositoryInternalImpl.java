package com.invertimostuyyo.stockanalysis.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.invertimostuyyo.stockanalysis.domain.Analysis;
import com.invertimostuyyo.stockanalysis.repository.rowmapper.AnalysisRowMapper;
import com.invertimostuyyo.stockanalysis.repository.rowmapper.StockRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.LocalDate;
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
 * Spring Data R2DBC custom repository implementation for the Analysis entity.
 */
@SuppressWarnings("unused")
class AnalysisRepositoryInternalImpl extends SimpleR2dbcRepository<Analysis, Long> implements AnalysisRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final StockRowMapper stockMapper;
    private final AnalysisRowMapper analysisMapper;

    private static final Table entityTable = Table.aliased("analysis", EntityManager.ENTITY_ALIAS);
    private static final Table stockTable = Table.aliased("stock", "stock");

    public AnalysisRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        StockRowMapper stockMapper,
        AnalysisRowMapper analysisMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Analysis.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.stockMapper = stockMapper;
        this.analysisMapper = analysisMapper;
    }

    @Override
    public Flux<Analysis> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Analysis> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = AnalysisSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(StockSqlHelper.getColumns(stockTable, "stock"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(stockTable)
            .on(Column.create("stock_id", entityTable))
            .equals(Column.create("id", stockTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Analysis.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Analysis> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Analysis> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Analysis process(Row row, RowMetadata metadata) {
        Analysis entity = analysisMapper.apply(row, "e");
        entity.setStock(stockMapper.apply(row, "stock"));
        return entity;
    }

    @Override
    public <S extends Analysis> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
