package com.invertimostuyyo.stockanalysis.repository.rowmapper;

import com.invertimostuyyo.stockanalysis.domain.Position;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Position}, with proper type conversions.
 */
@Service
public class PositionRowMapper implements BiFunction<Row, String, Position> {

    private final ColumnConverter converter;

    public PositionRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Position} stored in the database.
     */
    @Override
    public Position apply(Row row, String prefix) {
        Position entity = new Position();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setAmount(converter.fromRow(row, prefix + "_amount", Double.class));
        entity.setPrice(converter.fromRow(row, prefix + "_price", Double.class));
        entity.setPortfolioId(converter.fromRow(row, prefix + "_portfolio_id", Long.class));
        return entity;
    }
}
