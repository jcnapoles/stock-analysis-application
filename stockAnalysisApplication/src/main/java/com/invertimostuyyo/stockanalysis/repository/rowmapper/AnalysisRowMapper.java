package com.invertimostuyyo.stockanalysis.repository.rowmapper;

import com.invertimostuyyo.stockanalysis.domain.Analysis;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Analysis}, with proper type conversions.
 */
@Service
public class AnalysisRowMapper implements BiFunction<Row, String, Analysis> {

    private final ColumnConverter converter;

    public AnalysisRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Analysis} stored in the database.
     */
    @Override
    public Analysis apply(Row row, String prefix) {
        Analysis entity = new Analysis();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDate(converter.fromRow(row, prefix + "_date", LocalDate.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setStockId(converter.fromRow(row, prefix + "_stock_id", Long.class));
        return entity;
    }
}
