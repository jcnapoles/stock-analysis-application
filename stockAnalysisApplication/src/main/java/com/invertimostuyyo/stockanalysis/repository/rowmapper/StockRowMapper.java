package com.invertimostuyyo.stockanalysis.repository.rowmapper;

import com.invertimostuyyo.stockanalysis.domain.Stock;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Stock}, with proper type conversions.
 */
@Service
public class StockRowMapper implements BiFunction<Row, String, Stock> {

    private final ColumnConverter converter;

    public StockRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Stock} stored in the database.
     */
    @Override
    public Stock apply(Row row, String prefix) {
        Stock entity = new Stock();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setSector(converter.fromRow(row, prefix + "_sector", String.class));
        entity.setFundation(converter.fromRow(row, prefix + "_fundation", LocalDate.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setIcnome(converter.fromRow(row, prefix + "_icnome", Double.class));
        entity.setExpenses(converter.fromRow(row, prefix + "_expenses", Double.class));
        entity.setCapitalization(converter.fromRow(row, prefix + "_capitalization", Double.class));
        entity.setEmployees(converter.fromRow(row, prefix + "_employees", Integer.class));
        return entity;
    }
}
