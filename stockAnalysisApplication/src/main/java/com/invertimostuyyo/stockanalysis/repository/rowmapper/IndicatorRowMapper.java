package com.invertimostuyyo.stockanalysis.repository.rowmapper;

import com.invertimostuyyo.stockanalysis.domain.Indicator;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Indicator}, with proper type conversions.
 */
@Service
public class IndicatorRowMapper implements BiFunction<Row, String, Indicator> {

    private final ColumnConverter converter;

    public IndicatorRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Indicator} stored in the database.
     */
    @Override
    public Indicator apply(Row row, String prefix) {
        Indicator entity = new Indicator();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setAnalysisId(converter.fromRow(row, prefix + "_analysis_id", Long.class));
        return entity;
    }
}
