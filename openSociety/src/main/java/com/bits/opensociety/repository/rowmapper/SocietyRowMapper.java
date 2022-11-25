package com.bits.opensociety.repository.rowmapper;

import com.bits.opensociety.domain.Society;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Society}, with proper type conversions.
 */
@Service
public class SocietyRowMapper implements BiFunction<Row, String, Society> {

    private final ColumnConverter converter;

    public SocietyRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Society} stored in the database.
     */
    @Override
    public Society apply(Row row, String prefix) {
        Society entity = new Society();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        return entity;
    }
}
