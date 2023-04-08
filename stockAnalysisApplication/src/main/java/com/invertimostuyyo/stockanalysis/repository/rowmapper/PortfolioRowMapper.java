package com.invertimostuyyo.stockanalysis.repository.rowmapper;

import com.invertimostuyyo.stockanalysis.domain.Portfolio;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Portfolio}, with proper type conversions.
 */
@Service
public class PortfolioRowMapper implements BiFunction<Row, String, Portfolio> {

    private final ColumnConverter converter;

    public PortfolioRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Portfolio} stored in the database.
     */
    @Override
    public Portfolio apply(Row row, String prefix) {
        Portfolio entity = new Portfolio();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        return entity;
    }
}
