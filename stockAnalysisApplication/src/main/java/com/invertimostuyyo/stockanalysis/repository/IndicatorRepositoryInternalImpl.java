package com.invertimostuyyo.stockanalysis.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.invertimostuyyo.stockanalysis.domain.Indicator;
import com.invertimostuyyo.stockanalysis.repository.rowmapper.AnalysisRowMapper;
import com.invertimostuyyo.stockanalysis.repository.rowmapper.IndicatorRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Indicator entity.
 */
@SuppressWarnings("unused")
class IndicatorRepositoryInternalImpl extends SimpleR2dbcRepository<Indicator, Long> implements IndicatorRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final AnalysisRowMapper analysisMapper;
    private final IndicatorRowMapper indicatorMapper;

    private static final Table entityTable = Table.aliased("indicator", EntityManager.ENTITY_ALIAS);
    private static final Table analysisTable = Table.aliased("analysis", "analysis");

    public IndicatorRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        AnalysisRowMapper analysisMapper,
        IndicatorRowMapper indicatorMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Indicator.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.analysisMapper = analysisMapper;
        this.indicatorMapper = indicatorMapper;
    }

    @Override
    public Flux<Indicator> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Indicator> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = IndicatorSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(AnalysisSqlHelper.getColumns(analysisTable, "analysis"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(analysisTable)
            .on(Column.create("analysis_id", entityTable))
            .equals(Column.create("id", analysisTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Indicator.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Indicator> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Indicator> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Indicator process(Row row, RowMetadata metadata) {
        Indicator entity = indicatorMapper.apply(row, "e");
        entity.setAnalysis(analysisMapper.apply(row, "analysis"));
        return entity;
    }

    @Override
    public <S extends Indicator> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
